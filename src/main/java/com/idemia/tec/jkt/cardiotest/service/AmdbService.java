package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.AmmendmentB;
import com.idemia.tec.jkt.cardiotest.model.MinimumSecurityLevel;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmdbService {

    static Logger logger = Logger.getLogger(AmdbService.class);

    @Autowired private RootLayoutController root;
    private String scriptsDirectory;
    private int exitVal;

    public boolean checkOpenChannel() {
        scriptsDirectory = root.getRunSettings().getProjectPath() + "\\scripts\\";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(scriptsDirectory + "check_open_channel.txt"))) {
            bw.append(generateOpenChannel(root.getRunSettings().getAmmendmentB()));
        }
        catch (IOException e) { logger.error("Failed writing check_open_channel script"); }
        runShellCommand("pcomconsole", scriptsDirectory + "check_open_channel.txt");
        return exitVal == 0;
    }

    private StringBuilder generateOpenChannel(AmmendmentB ammendmentB) {
        StringBuilder openChannelBuffer = new StringBuilder();
        openChannelBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
            + ".LOAD dll\\Calcul.dll\n"
            + ".LOAD dll\\OTA2.dll\n"
            + ".LOAD dll\\DLL_TLS_PSK.dll\n"
        );
        // create counter and initialize for first-time run
        File counterBin = new File(root.getRunSettings().getProjectPath() + "\\scripts\\COUNTER.bin");
        if (!counterBin.exists()) {
            openChannelBuffer.append(
                "\n; initialize counter\n"
                + ".SET_BUFFER L 00 00 00 00 00\n"
                + ".EXPORT_BUFFER L COUNTER.bin\n"
            );
        }
        // load anti-replay counter
        openChannelBuffer.append(
            "\n; buffer L contains the anti-replay counter for OTA message\n"
            + ".SET_BUFFER L\n"
            + ".IMPORT_BUFFER L COUNTER.bin\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
            + ".DISPLAY L\n"
        );
        // enable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            openChannelBuffer.append("\nA0 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n");
        openChannelBuffer.append(
            "\n.DEFINE %SD_TAR " + ammendmentB.getSdTar() + "\n"
            + ".DEFINE %CONNECTION_PARAMETERS 830E 840C 3C0302215F 3E05217F000001\n" // TODO
            + "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + ammendmentB.getScp80CipherKeyset().getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + ammendmentB.getScp80AuthKeyset().getKidValuation() + "\n"
            + ".SET_BUFFER M " + ammendmentB.getScp80CipherKeyset().getComputedKic() + "\n"
            + ".SET_BUFFER N " + ammendmentB.getScp80AuthKeyset().getComputedKid() + "\n"
            + ".INIT_ENV_0348\n"
            + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            openChannelBuffer.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        openChannelBuffer.append(
            ".CHANGE_TAR %SD_TAR\n"
            + ".CHANGE_COUNTER L\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
            + "\n; SD MSL = " + ammendmentB.getSdMsl().getComputedMsl() + "\n"
            + ".SET_DLKEY_KIC O\n"
            + ".SET_DLKEY_KID Q\n"
            + ".CHANGE_KIC M\n"
            + ".CHANGE_KID N\n"
            + spiConfigurator(ammendmentB.getSdMsl(), ammendmentB.getScp80CipherKeyset(), ammendmentB.getScp80AuthKeyset())
        );
        if (ammendmentB.getScp80AuthKeyset().getKidMode().equals("AES - CMAC"))
            openChannelBuffer.append(".SET_CMAC_LENGTH " + String.format("%02X", ammendmentB.getScp80AuthKeyset().getCmacLength()) + "\n");
        openChannelBuffer.append(
            ".CHANGE_POR_FORMAT 01\n"
            + ".CHANGE_TP_DCS F6\n"
            + ".PUSH_SMS %CONNECTION_PARAMETERS\n"
            + ".END_MESSAGE I J\n"
            + "A0 C2 00 00 I J (91XX)\n"
            + "; PoR as SMS-submit\n"
            + ".MESSAGE #CHECK001:Get PoR as SMS-submit\n"
            + "A0 12 00 00 2F [D02D8103011300820281838500060280018B1C410005811250F300F612027100000D0A %SD_TAR XXXXXXXXXX00009000] (9000)\n"
            + "A0 14 00 00 0C 810301130002028281030100 (91XX)\n"
            + "; check for open channel\n"
            + ".MESSAGE #CHECK002:Fetch open channel\n"
            + "A0 12 00 00 W(2:2) (9000)\n"
        );
        openChannelBuffer.append(
            "; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
            + "; save counter state\n"
            + ".EXPORT_BUFFER L COUNTER.bin\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            openChannelBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");
        openChannelBuffer.append(
            "\n.UNLOAD Calcul.dll\n"
            + ".UNLOAD OTA2.dll\n"
            + ".UNLOAD DLL_TLS_PSK.dll\n"
            + "\n.POWER_OFF\n"
        );
        // TODO
        return openChannelBuffer;
    }

    private String proactiveInitialization() {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; proactive initialization\n"
            + "A010000013 FFFFFFFF7F3F00DFFF00001FE28A0D02030900 (9XXX)\n"
            + ".BEGIN_LOOP\n"
            + "\t.SWITCH W(1:1)\n"
            + "\t.CASE 91\n"
            + "\t\tA0 12 00 00 W(2:2) ; fetch\n"
            + "\t\tA0 14 00 00 0C 010301 R(6;1) 00 02028281 030100 ; terminal response\n"
            + "\t\t.BREAK\n"
            + "\t.DEFAULT\n"
            + "\t\t.QUITLOOP\n"
            + "\t\t.BREAK\n"
            + "\t.ENDSWITCH\n"
            + ".LOOP 100\n"
        );
        return routine.toString();
    }

    private String spiConfigurator(MinimumSecurityLevel msl, SCP80Keyset cipherKeyset, SCP80Keyset authKeyset) {
        StringBuilder spiConf = new StringBuilder();

        if (msl.getAuthVerification().equals("No verification"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 00 ; No verification\n");
        if (msl.getAuthVerification().equals("Redundancy Check"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 01 ; Redundancy Check\n");
        if (msl.getAuthVerification().equals("Cryptographic Checksum"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 02 ; Cryptographic Checksum\n");
        if (msl.getAuthVerification().equals("Digital Signature"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 03 ; Digital Signature\n");

        String signingAlgo;
        if (msl.getSigningAlgo().equals("as defined in keyset"))
            signingAlgo = authKeyset.getKidMode();
        else
            signingAlgo = msl.getSigningAlgo();
        if (signingAlgo.equals("no algorithm"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 00 ; no algorithm\n");
        if (signingAlgo.equals("DES - CBC"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 01 ; DES - CBC\n");
        if (signingAlgo.equals("AES - CMAC"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 02 ; AES - CMAC\n");
        if (signingAlgo.equals("XOR"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 03 ; XOR\n");
        if (signingAlgo.equals("3DES - CBC 2 keys"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 05 ; 3DES - CBC 2 keys\n");
        if (signingAlgo.equals("3DES - CBC 3 keys"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 09 ; 3DES - CBC 3 keys\n");
        if (signingAlgo.equals("DES - ECB"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0D ; DES - ECB\n");
        if (signingAlgo.equals("CRC32 (may be X5h)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0B ; CRC32 (may be X5h)\n");
        if (signingAlgo.equals("CRC32 (may be X0h)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0C ; CRC32 (may be X0h)\n");
        if (signingAlgo.equals("ISO9797 Algo 3 (auth value 8 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0F ; ISO9797 Algo 3 (auth value 8 byte)\n");
        if (signingAlgo.equals("ISO9797 Algo 3 (auth value 4 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 10 ; ISO9797 Algo 3 (auth value 4 byte)\n");
        if (signingAlgo.equals("ISO9797 Algo 4 (auth value 4 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 11 ; ISO9797 Algo 4 (auth value 4 byte)\n");
        if (signingAlgo.equals("ISO9797 Algo 4 (auth value 8 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 12 ; ISO9797 Algo 4 (auth value 8 byte)\n");
        if (signingAlgo.equals("CRC16"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 13 ; CRC16\n");

        if (msl.isUseCipher())
            spiConf.append(".CHANGE_CIPHER 01 ; use cipher\n");
        else
            spiConf.append(".CHANGE_CIPHER 00 ; no cipher\n");

        String cipherAlgo;
        if (msl.getCipherAlgo().equals("as defined in keyset"))
            cipherAlgo = cipherKeyset.getKicMode();
        else
            cipherAlgo = msl.getCipherAlgo();
        if (cipherAlgo.equals("no cipher"))
            spiConf.append(".CHANGE_ALGO_CIPHER 00 ; no cipher\n");
        if (cipherAlgo.equals("DES - CBC"))
            spiConf.append(".CHANGE_ALGO_CIPHER 01 ; DES - CBC\n");
        if (cipherAlgo.equals("AES - CBC"))
            spiConf.append(".CHANGE_ALGO_CIPHER 02 ; AES - CBC\n");
        if (cipherAlgo.equals("XOR"))
            spiConf.append(".CHANGE_ALGO_CIPHER 03 ; XOR\n");
        if (cipherAlgo.equals("3DES - CBC 2 keys"))
            spiConf.append(".CHANGE_ALGO_CIPHER 05 ; 3DES - CBC 2 keys\n");
        if (cipherAlgo.equals("3DES - CBC 3 keys"))
            spiConf.append(".CHANGE_ALGO_CIPHER 09 ; 3DES - CBC 3 keys\n");
        if (cipherAlgo.equals("DES - ECB"))
            spiConf.append(".CHANGE_ALGO_CIPHER 0D ; DES - ECB\n");

        if (msl.getCounterChecking().equals("No counter available"))
            spiConf.append(".CHANGE_CNT_CHK 00 ; No counter available\n");
        if (msl.getCounterChecking().equals("Counter available no checking"))
            spiConf.append(".CHANGE_CNT_CHK 01 ; Counter available no checking\n");
        if (msl.getCounterChecking().equals("Counter must be higher"))
            spiConf.append(".CHANGE_CNT_CHK 02 ; Counter must be higher\n");
        if (msl.getCounterChecking().equals("Counter must be one higher"))
            spiConf.append(".CHANGE_CNT_CHK 03 ; Counter must be one higher\n");

        if (msl.getPorRequirement().equals("No PoR"))
            spiConf.append(".CHANGE_POR 00 ; No PoR\n");
        if (msl.getPorRequirement().equals("PoR required"))
            spiConf.append(".CHANGE_POR 01 ; PoR required\n");
        if (msl.getPorRequirement().equals("PoR only if error"))
            spiConf.append(".CHANGE_POR 02 ; PoR only if error\n");

        if (msl.getPorSecurity().equals("response with no security"))
            spiConf.append(".CHANGE_POR_SECURITY 00 ; response with no security\n");
        if (msl.getPorSecurity().equals("response with RC"))
            spiConf.append(".CHANGE_POR_SECURITY 01 ; response with RC\n");
        if (msl.getPorSecurity().equals("response with CC"))
            spiConf.append(".CHANGE_POR_SECURITY 02 ; response with CC\n");
        if (msl.getPorSecurity().equals("response with DS"))
            spiConf.append(".CHANGE_POR_SECURITY 03 ; response with DS\n");

        if (msl.isCipherPor())
            spiConf.append(".CHANGE_POR_CIPHER 01\n");
        else
            spiConf.append(".CHANGE_POR_CIPHER 00\n");

        return spiConf.toString();
    }

    private void launchProcess(List<String> cmdArray) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        exitVal = process.waitFor();
    }

    private void runShellCommand(String pcomExecutable, String scriptName) {
        String readerName = "";
        try {
            List<CardTerminal> terminals = root.getTerminalFactory().terminals().list();
            readerName = terminals.get(root.getRunSettings().getReaderNumber()).getName();
        }
        catch (CardException e) { logger.error(e.getMessage()); }
        List<String> cmdArray = new ArrayList<>();
        cmdArray.add(pcomExecutable);
        cmdArray.add("-script");
        cmdArray.add(scriptName);
        cmdArray.add("-xmllogpath");
        cmdArray.add(".");
        cmdArray.add("-logpath");
        cmdArray.add(".");
        cmdArray.add("-reader");
        cmdArray.add(readerName);
        cmdArray.add("-protocol");
        cmdArray.add("TPDUMode");
        if (root.getRunSettings().isStopOnError()) cmdArray.add("-stoponerror");
        try {
//            logger.info("Executing.. " + scriptName);
            launchProcess(cmdArray);
//            logger.info("Exit val: " + exitVal);
        }
        catch (IOException | InterruptedException e) { e.printStackTrace(); }
    }

}
