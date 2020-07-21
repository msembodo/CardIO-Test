package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ScriptGeneratorServiceImpl implements ScriptGeneratorService {

    Logger logger = Logger.getLogger(ScriptGeneratorServiceImpl.class);

    @Autowired
    private RootLayoutController root;

    @Override
    public StringBuilder generateAtr() {
        String composeAtrScript = ".CALL Mapping.txt\n"
            + ".CALL Options.txt\n\n"
            + ".POWER_ON /PROTOCOL_ON /NEGOTIATE_PROTOCOL [%ATR] (" + root.getRunSettings().getAtr().getStatus() + ")\n"
            + ".GET_PROTOCOL_PARAMETERS\n"
            + ".POWER_OFF";
        
        return new StringBuilder().append(composeAtrScript);
    }

    @Override
    public StringBuilder generateMapping() {
        StringBuilder mappings = new StringBuilder();
        mappings.append(".CALL ..\\variables.txt /LIST_OFF\n\n");
        for (VariableMapping mapping : root.getRunSettings().getVariableMappings()) {
            if (mapping.isFixed())
                mappings.append(String.format(".DEFINE %%%s  %s\n", mapping.getMappedVariable(), mapping.getValue()));
            else
                mappings.append(String.format(".DEFINE %%%s  %%%s\n", mapping.getMappedVariable(), mapping.getMccVariable()));
        }
        return mappings;
    }

    @Override
    public StringBuilder generateMilenageDeltaTest(Authentication authentication) {
        StringBuilder deltaTestBuffer = new StringBuilder();
        deltaTestBuffer.append(
            ".SET_BUFFER L " + authentication.getSqn() + "\n\n"
            + ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".DEFINE %RAND " + authentication.getRand() + "\n\n"
            + ".POWER_ON\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            deltaTestBuffer.append(
                "00 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n\n"
            );
        }
        deltaTestBuffer.append(
            ".DEFINE %DELTA " + authentication.getDelta() + "\n\n"
        );
        if (authentication.isIsimAuth()) {
            deltaTestBuffer.append(
                "; ISIM auth\n"
                + ".LOAD dll\\Milenage_AKA.dll\n"
                + ".LOAD dll\\Calcul.dll\n"
                + "; Case 1: AUTHENTICATE 3G CONTEXT AGAIN WITH CURRENT SQN (SEQ = 1, IND = 1)\n"
                + ".POWER_ON\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgo3gAgainCurrSqn(authentication)
                + "; Case 2: INCREASE SEQUENCE (SEQ = 2, IND = 1)\n"
                + ".POWER_ON\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgoSqnOkIsim(authentication)
                + "; Case 3: SYNC FAILED (SEQ = 3 + Delta, IND = 1)\n"
                + ".POWER_ON\n"
                + ".INCREASE_BUFFER L %DELTA\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgoSqnNokIsim(authentication)
                + "; Case 4: 3G ME in 3G context (Resync)\n"
                + ".POWER_ON\n"
                + ".DECREASE_BUFFER L %DELTA\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgoSqnOkIsim(authentication)
                + ".UNLOAD Milenage_AKA.dll\n"
                + ".UNLOAD Calcul.dll\n\n"
            );
        }
        deltaTestBuffer.append(
            "; Milenage Algo\n"
            + ".LOAD dll\\Milenage_AKA.dll\n"
            + ".LOAD dll\\Calcul.dll\n"
            + "; Case 1: AUTHENTICATE 3G CONTEXT AGAIN WITH CURRENT SQN (SEQ = 1, IND = 1)\n"
            + ".POWER_ON\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgo3gAgainCurrSqn(authentication)
            + "; Case 2: INCREASE SEQUENCE (SEQ = 2, IND = 1)\n"
            + ".POWER_ON\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgoSqnOk(authentication)
            + "; Case 3: SYNC FAILED (SEQ = 3 + Delta, IND = 1)\n"
            + ".POWER_ON\n"
            + ".INCREASE_BUFFER L %DELTA\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgoSqnNok(authentication)
            + "; Case 4: 3G ME in 3G context (Resync)\n"
            + ".POWER_ON\n"
            + ".DECREASE_BUFFER L %DELTA\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgoSqnOk(authentication)
            + ".UNLOAD Milenage_AKA.dll\n"
            + ".UNLOAD Calcul.dll\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            deltaTestBuffer.append(
                "\n00 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n\n"
            );
        }
        deltaTestBuffer.append(".POWER_OFF\n");
        return deltaTestBuffer;
    }

    @Override
    public StringBuilder generateMilenageSqnMax(Authentication authentication) {
        StringBuilder sqnMaxBuffer = new StringBuilder();
        sqnMaxBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".DEFINE %RAND " + authentication.getRand() + "\n\n"
            + ".POWER_ON\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            sqnMaxBuffer.append(
                "00 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n\n"
            );
        }
        if (authentication.isMilenage()) {
            sqnMaxBuffer.append(
                "; Milenage Algo\n"
                + ".LOAD dll\\Milenage_AKA.dll\n"
                + ".LOAD dll\\Calcul.dll\n"
                + ".DEFINE %SQN " + authentication.getSqnMax() + "\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + ".POWER_ON\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
                + "; set variables for calculating AKA1 quintets\n"
                + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
                + ".SET_RI\n"
                + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
                authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
                authentication.getAkaC5())
                + ".SET_CI\n"
                + ".SET_BUFFER I %" + authentication.getKi() + "\n"
                + ".SET_K1\n"
                + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
                + ".SET_OPC\n"
                + ".SET_BUFFER I %RAND\n"
                + ".SET_RAND\n"
                + ".SET_BUFFER I %SQN\n"
                + ".SET_SQN\n"
                + ".SET_BUFFER I %AMF\n"
                + ".SET_AMF\n"
                + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
                + ".INIT_AKA1 ; calculate AKA1 quintets\n"
                + ".GET_MAC ; get quintets and triplets\n"
                + ".DISPLAY O\n"
                + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
                + ".DISPLAY O\n"
                + ".DEFINE %RES O\n"
                + ".GET_SRES\n"
                + ".DISPLAY O\n"
                + ".DEFINE %SRES O\n"
                + ".GET_CK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %CK O\n"
                + ".GET_IK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %IK O\n"
                + ".GET_AK\n"
                + ".DISPLAY O\n"
                + ".SET_KEY O(1;6) 0000\n"
                + ".GET_AUTN\n"
                + ".DISPLAY O\n"
                + ".DEFINE %AUTN O\n"
                + ".SET_DATA O(1;6) 0000\n"
                + ".XOR I 00 /P\n"
                + ".DISPLAY I (1;6)\n"
                + ".GET_KC ; 2G Kc\n"
                + ".DISPLAY O\n"
                + ".DEFINE %Kc O\n"
            );
            if (authentication.isGsmAlgo()) {
                sqnMaxBuffer.append(
                    "; run GSM algo (2G)\n"
                    + ".POWER_ON\n"
                    + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                    + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                    + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                    + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                    + ".POWER_ON\n"
                );
            }
            sqnMaxBuffer.append(
                "; authenticate in 3G mode with 2G context\n"
                + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
                + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
                + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
                + "; authenticate with 3G context\n"
                + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
                + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
                + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK 08 %Kc ] (9000) ; check result\n"
                + ".SET_BUFFER I R(3;14)\n"
                + ".GET_SQN_MAX\n"
                + ".DISPLAY O\n"
                + ".UNDEFINE %RES\n"
                + ".UNDEFINE %SRES\n"
                + ".UNDEFINE %CK\n"
                + ".UNDEFINE %IK\n"
                + ".UNDEFINE %AUTN\n"
                + ".UNDEFINE %Kc\n"
                + ".UNDEFINE %SQN\n"
                + ".UNDEFINE %AMF\n"
                + ".UNLOAD Milenage_AKA.dll\n"
                + ".UNLOAD Calcul.dll\n"
            );
        }
        if (authentication.isIsimAuth()) {
            sqnMaxBuffer.append(
                "; ISIM auth\n"
                + ".LOAD dll\\Milenage_AKA.dll\n"
                + ".LOAD dll\\Calcul.dll\n"
                + ".DEFINE %SQN " + authentication.getSqnMax() + "\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + ".POWER_ON\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
                + "; set variables for calculating AKA1 quintets\n"
                + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
                + ".SET_RI\n"
                + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
                authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
                authentication.getAkaC5())
                + ".SET_CI\n"
                + ".SET_BUFFER I %" + authentication.getKi() + "\n"
                + ".SET_K1\n"
                + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
                + ".SET_OPC\n"
                + ".SET_BUFFER I %RAND\n"
                + ".SET_RAND\n"
                + ".SET_BUFFER I %SQN\n"
                + ".SET_SQN\n"
                + ".SET_BUFFER I %AMF\n"
                + ".SET_AMF\n"
                + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
                + ".INIT_AKA1 ; calculate AKA1 quintets\n"
                + ".GET_MAC ; get quintets and triplets\n"
                + ".DISPLAY O\n"
                + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
                + ".DISPLAY O\n"
                + ".DEFINE %RES O\n"
                + ".GET_SRES\n"
                + ".DISPLAY O\n"
                + ".DEFINE %SRES O\n"
                + ".GET_CK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %CK O\n"
                + ".GET_IK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %IK O\n"
                + ".GET_AK\n"
                + ".DISPLAY O\n"
                + ".SET_KEY O(1;6) 0000\n"
                + ".GET_AUTN\n"
                + ".DISPLAY O\n"
                + ".DEFINE %AUTN O\n"
                + ".SET_DATA O(1;6) 0000\n"
                + ".XOR I 00 /P\n"
                + ".DISPLAY I (1;6)\n"
                + "; authenticate with 3G context\n"
                + "00 A4 04 0C <?> %ISIM_AID (9000) ; select ISIM AID\n"
                + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
                + "00 C0 00 00 W(2;1) [DB 08 %RES 10 %CK 10 %IK] (9000) ; return result\n"
                + ".SET_BUFFER I R(3;14)\n"
                + ".GET_SQN_MAX\n"
                + ".DISPLAY O\n"
                + ".UNDEFINE %RES\n"
                + ".UNDEFINE %SRES\n"
                + ".UNDEFINE %CK\n"
                + ".UNDEFINE %IK\n"
                + ".UNDEFINE %AUTN\n"
                + ".UNDEFINE %Kc\n"
                + ".UNDEFINE %SQN\n"
                + ".UNDEFINE %AMF\n"
                + ".UNLOAD Milenage_AKA.dll\n"
                + ".UNLOAD Calcul.dll\n"
            );
        }
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            sqnMaxBuffer.append(
                "\n00 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n\n"
            );
        }
        sqnMaxBuffer.append(".POWER_OFF\n");
        return sqnMaxBuffer;
    }

    @Override
    public StringBuilder generateRfmUsim(RfmUsim rfmUsim) {
        StringBuilder rfmUsimBuffer = new StringBuilder();
        // call mappings and load DLLs
        rfmUsimBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
            + ".LOAD dll\\Calcul.dll\n"
            + ".LOAD dll\\OTA2.dll\n"
            + ".LOAD dll\\Var_Reader.dll\n"
        );
        // create counter and initialize for first-time run
        File counterBin = new File(root.getRunSettings().getProjectPath() + "\\scripts\\COUNTER.bin");
        if (!counterBin.exists()) {
            rfmUsimBuffer.append(
                "\n; initialize counter\n"
                + ".SET_BUFFER L 00 00 00 00 00\n"
                + ".EXPORT_BUFFER L COUNTER.bin\n"
            );
        }
        // load anti-replay counter
        rfmUsimBuffer.append(
            "\n; buffer L contains the anti-replay counter for OTA message\n"
            + ".SET_BUFFER L\n"
            + ".IMPORT_BUFFER L COUNTER.bin\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
            + ".DISPLAY L\n"
            + "\n; setup TAR\n"
            + ".DEFINE %TAR " + rfmUsim.getTar() + "\n"
        );
        // enable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            rfmUsimBuffer.append(
                "\nA0 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n"
            );
        }
        // define target files
        rfmUsimBuffer.append(
            "\n.DEFINE %EF_ID " + rfmUsim.getTargetEf() + "\n"
            + ".DEFINE %EF_ID_ERR " + rfmUsim.getTargetEfBadCase() + "\n"
            + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfUsim() + "\n"
        );
        // case 1
        rfmUsimBuffer.append(
            "\n; CASE 1: RFM USIM with correct security settings\n"
            + "\n.POWER_ON\n"
            + "; check initial content of EF\n"
            + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
            + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9F22)\n"
            + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
            + "A0 B0 00 00 01 (9000)\n"
            + ".DEFINE %EF_CONTENT R\n"
        );

        // TODO: if not full access; define target files as in access domain

        // some TAR may be configured with specific keyset or use all available keysets
        if (rfmUsim.isUseSpecificKeyset())
            rfmUsimBuffer.append(rfmUsimCase1(rfmUsim.getCipheringKeyset(), rfmUsim.getAuthKeyset(), rfmUsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmUsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmUsimBuffer.append(rfmUsimCase1(keyset, keyset, rfmUsim.getMinimumSecurityLevel()));
            }
        }
        rfmUsimBuffer.append("\n.UNDEFINE %EF_CONTENT\n");
        // case 2
        rfmUsimBuffer.append("\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in USIM TAR\n");
        if (rfmUsim.isUseSpecificKeyset())
            rfmUsimBuffer.append(rfmUsimCase2(rfmUsim.getCipheringKeyset(), rfmUsim.getAuthKeyset(), rfmUsim.getMinimumSecurityLevel()));
        else
            // only use first keyset
            rfmUsimBuffer.append(rfmUsimCase2(root.getRunSettings().getScp80Keysets().get(0), root.getRunSettings().getScp80Keysets().get(0), rfmUsim.getMinimumSecurityLevel()));
        // case 3
        rfmUsimBuffer.append("\n; CASE 3: (Bad Case) send 2G command to USIM TAR\n");
        if (rfmUsim.isUseSpecificKeyset())
            rfmUsimBuffer.append(rfmUsimCase3(rfmUsim.getCipheringKeyset(), rfmUsim.getAuthKeyset(), rfmUsim.getMinimumSecurityLevel()));
        else
            // only use first keyset
            rfmUsimBuffer.append(rfmUsimCase3(root.getRunSettings().getScp80Keysets().get(0), root.getRunSettings().getScp80Keysets().get(0), rfmUsim.getMinimumSecurityLevel()));
        // TODO: case 4
        // TODO: case 5
        // TODO: case 6
        // TODO: case 7

        // save counter
        rfmUsimBuffer.append(
            "\n; save counter state\n"
            + ".EXPORT_BUFFER L COUNTER.bin\n"
        );
        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            rfmUsimBuffer.append(
                "\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n"
            );
        }
        // unload DLLs
        rfmUsimBuffer.append(
            "\n.UNLOAD Calcul.dll\n"
            + ".UNLOAD OTA2.dll\n"
            + ".UNLOAD Var_Reader.dll\n"
            + "\n.POWER_OFF\n"
        );
        return rfmUsimBuffer;
    }

    private String rfmUsimCase1(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
            + ".INIT_ENV_0348\n"
            + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
            + ".CHANGE_TAR %TAR\n"
            + ".CHANGE_COUNTER L\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
            + "\n; MSL = " + msl.getComputedMsl() + "\n"
            + ".SET_DLKEY_KIC O\n"
            + ".SET_DLKEY_KID Q\n"
            + ".CHANGE_KIC M\n"
            + ".CHANGE_KID N\n"
            + spiConfigurator(msl)
            + "\n; command(s) sent via OTA\n"
            + ".SET_BUFFER J 00 A4 00 00 02 %EF_ID ; select EF\n"
            + ".APPEND_SCRIPT J\n"
            + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
            + ".APPEND_SCRIPT J\n"
            // TODO: if not full access
            + ".END_MESSAGE G J\n"
            + "; show OTA message details\n"
            + ".DISPLAY_MESSAGE J\n"
            + "; send envelope\n"
            + "A0 C2 00 00 G J (9FXX)\n"
            + ".CLEAR_SCRIPT\n"
            + "; check PoR\n"
            + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 90 00] (9000) ; PoR OK\n"
            + "\n; check update has been done on EF\n"
            + ".POWER_ON\n"
            + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
            + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9F22)\n"
            + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
            + "A0 B0 00 00 01 [AA] (9000)\n"
            + "\n; restore initial content of EF\n"
            + ".POWER_ON\n"
            + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
            + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9F22)\n"
            + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
            + "A0 D6 00 00 01 %EF_CONTENT (9000)\n"
            + "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmUsimCase2(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O " + createFakeCipherKey(cipherKeyset) + " ; bad key\n"
            + ".SET_BUFFER Q " + createFakeAuthKey(authKeyset) + " ; bad key\n"
            + ".SET_BUFFER M 99 ; bad keyset\n"
            + ".SET_BUFFER N 99 ; bad keyset\n"
            + ".INIT_ENV_0348\n"
            + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
            + ".CHANGE_TAR %TAR\n"
            + ".CHANGE_COUNTER L\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
            + "\n; MSL = " + msl.getComputedMsl() + "\n"
            + ".SET_DLKEY_KIC O\n"
            + ".SET_DLKEY_KID Q\n"
            + ".CHANGE_KIC M\n"
            + ".CHANGE_KID N\n"
            + spiConfigurator(msl)
            + "\n; command(s) sent via OTA\n"
            + ".SET_BUFFER J 00 A4 00 00 02 3F00\n"
            + ".APPEND_SCRIPT J\n"
            + ".END_MESSAGE G J\n"
            + "; send envelope\n"
            + "A0 C2 00 00 G J (9XXX)\n"
            + ".CLEAR_SCRIPT\n"
            + "; check PoR\n"
            + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 06] (9000) ; unidentified security error\n"
            + "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmUsimCase3(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
            + ".INIT_ENV_0348\n"
            + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
            + ".CHANGE_TAR %TAR\n"
            + ".CHANGE_COUNTER L\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
            + "\n; MSL = " + msl.getComputedMsl() + "\n"
            + ".SET_DLKEY_KIC O\n"
            + ".SET_DLKEY_KID Q\n"
            + ".CHANGE_KIC M\n"
            + ".CHANGE_KID N\n"
            + spiConfigurator(msl)
            + "\n; command(s) sent via OTA\n"
            + ".SET_BUFFER J A0 A4 00 00 02 3F00 ; this command isn't supported by USIM\n"
            + ".APPEND_SCRIPT J\n"
            + ".END_MESSAGE G J\n"
            + "; send envelope\n"
            + "A0 C2 00 00 G J (9XXX)\n"
            + ".CLEAR_SCRIPT\n"
            + "; check PoR\n"
            + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 6E 00] (9000) ; PoR returns '6E00' (class not supported)\n"
            + "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String createFakeCipherKey(SCP80Keyset keyset) {
        if (keyset.getKicMode().equals("3DES - CBC 2 keys"))
            return "0102030405060708090A0B0C0D0E0F10";
        if (keyset.getKicMode().equals("3DES - CBC 3 keys"))
            return "0102030405060708090A0B0C0D0E0F101112131415161718";
        else
            return "0102030405060708";
    }

    private String createFakeAuthKey(SCP80Keyset keyset) {
        if (keyset.getKidMode().equals("3DES - CBC 2 keys"))
            return "0102030405060708090A0B0C0D0E0F10";
        if (keyset.getKidMode().equals("3DES - CBC 3 keys"))
            return "0102030405060708090A0B0C0D0E0F101112131415161718";
        else
            return "0102030405060708";
    }

    private String spiConfigurator(MinimumSecurityLevel msl) {
        StringBuilder spiConf = new StringBuilder();

        if (msl.getAuthVerification().equals("No verification"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 00 ; No verification\n");
        if (msl.getAuthVerification().equals("Redundancy Check"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 01 ; Redundancy Check\n");
        if (msl.getAuthVerification().equals("Cryptographic Checksum"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 02 ; Cryptographic Checksum\n");
        if (msl.getAuthVerification().equals("Digital Signature"))
            spiConf.append(".CHANGE_CRYPTO_VERIF 03 ; Digital Signature\n");

        if (msl.getSigningAlgo().equals("no algorithm"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 00 ; no algorithm\n");
        if (msl.getSigningAlgo().equals("DES - CBC"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 01 ; DES - CBC\n");
        if (msl.getSigningAlgo().equals("AES - CMAC"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 02 ; AES - CMAC\n");
        if (msl.getSigningAlgo().equals("XOR"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 03 ; XOR\n");
        if (msl.getSigningAlgo().equals("3DES - CBC 2 keys"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 05 ; 3DES - CBC 2 keys\n");
        if (msl.getSigningAlgo().equals("3DES - CBC 3 keys"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 09 ; 3DES - CBC 3 keys\n");
        if (msl.getSigningAlgo().equals("DES - ECB"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0D ; DES - ECB\n");
        if (msl.getSigningAlgo().equals("CRC32 (may be X5h)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0B ; CRC32 (may be X5h)\n");
        if (msl.getSigningAlgo().equals("CRC32 (may be X0h)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0C ; CRC32 (may be X0h)\n");
        if (msl.getSigningAlgo().equals("ISO9797 Algo 3 (auth value 8 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 0F ; ISO9797 Algo 3 (auth value 8 byte)\n");
        if (msl.getSigningAlgo().equals("ISO9797 Algo 3 (auth value 4 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 10 ; ISO9797 Algo 3 (auth value 4 byte)\n");
        if (msl.getSigningAlgo().equals("ISO9797 Algo 4 (auth value 4 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 11 ; ISO9797 Algo 4 (auth value 4 byte)\n");
        if (msl.getSigningAlgo().equals("ISO9797 Algo 4 (auth value 8 byte)"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 12 ; ISO9797 Algo 4 (auth value 8 byte)\n");
        if (msl.getSigningAlgo().equals("CRC16"))
            spiConf.append(".CHANGE_ALGO_CRYPTO_VERIF 13 ; CRC16\n");

        if (msl.isUseCipher())
            spiConf.append(".CHANGE_CIPHER 01 ; use cipher\n");
        else
            spiConf.append(".CHANGE_CIPHER 00 ; no cipher\n");

        if (msl.getCipherAlgo().equals("no cipher"))
            spiConf.append(".CHANGE_ALGO_CIPHER 00 ; no cipher\n");
        if (msl.getCipherAlgo().equals("DES - CBC"))
            spiConf.append(".CHANGE_ALGO_CIPHER 01 ; DES - CBC\n");
        if (msl.getCipherAlgo().equals("AES - CBC"))
            spiConf.append(".CHANGE_ALGO_CIPHER 02 ; AES - CBC\n");
        if (msl.getCipherAlgo().equals("XOR"))
            spiConf.append(".CHANGE_ALGO_CIPHER 03 ; XOR\n");
        if (msl.getCipherAlgo().equals("3DES - CBC 2 keys"))
            spiConf.append(".CHANGE_ALGO_CIPHER 05 ; 3DES - CBC 2 keys\n");
        if (msl.getCipherAlgo().equals("3DES - CBC 3 keys"))
            spiConf.append(".CHANGE_ALGO_CIPHER 09 ; 3DES - CBC 3 keys\n");
        if (msl.getCipherAlgo().equals("DES - ECB"))
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

    @Override
    public StringBuilder generateRfmUsimUpdateRecord(RfmUsim rfmUsim) {
        StringBuilder rfmUsimUpdateRecordBuffer = new StringBuilder();
        rfmUsimUpdateRecordBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
        );

        // TODO
        rfmUsimUpdateRecordBuffer.append("; TODO\n\n");

        rfmUsimUpdateRecordBuffer.append(".POWER_OFF\n");
        return rfmUsimUpdateRecordBuffer;
    }

    @Override
    public StringBuilder generateRfmUsimExpandedMode(RfmUsim rfmUsim) {
        StringBuilder rfmUsimExpandedModeBuffer = new StringBuilder();
        rfmUsimExpandedModeBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
        );

        // TODO
        rfmUsimExpandedModeBuffer.append("; TODO\n\n");

        rfmUsimExpandedModeBuffer.append(".POWER_OFF\n");
        return rfmUsimExpandedModeBuffer;
    }

    @Override
    public StringBuilder generateSecretCodes2g(SecretCodes secretCodes) {
        StringBuilder codes2gBuffer = new StringBuilder();
        codes2gBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
        );
        if (secretCodes.isPin1disabled()) {
            codes2gBuffer.append(
                "A0 28 00 01 08 %" + secretCodes.getChv1() + " (9000) ; enable CHV1\n\n"
            );
        }
        if (secretCodes.isPin2disabled()) {
            codes2gBuffer.append(
                "A0 28 00 02 08 %" + secretCodes.getChv1() + " (9000) ; enable CHV2\n\n"
            );
        }
        codes2gBuffer.append(
            "; CHV1\n"
            + "; check CHV1 remaining attempts\n"
            + "A0 A4 00 00 02 3F00 (9FXX)\n"
            + "A0 C0 00 00 W(2;1) (9000) [XXXX XXXX 3F00 XX XX XXXXXX XX 15 XX XX XX XX XX (80 | " + String.format("%02X", secretCodes.getChv1Retries()) + ") XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX] ; remaining attempts for CHV1\n"
            + "; verify CHV1\n"
            + "A0 20 00 01 08 %" + secretCodes.getChv1() + " (9000)\n"
            + "; disable CHV1 and verify\n"
            + "A0 26 00 01 08 %" + secretCodes.getChv1() + " (9000)\n"
            + "A0 20 00 01 08 %" + secretCodes.getChv1() + " (9808) ; verification failed\n"
            + "; enable CHV1 and verify\n"
            + "A0 28 00 01 08 %" + secretCodes.getChv1() + " (9000)\n"
            + "A0 20 00 01 08 %" + secretCodes.getChv1() + " (9000)\n"
            + "; change CHV1 and verify new value\n"
            + "A0 24 00 01 10 %" + secretCodes.getChv1() + " %" + secretCodes.getChv2() + " (9000)\n"
            + "A0 20 00 01 08 %" + secretCodes.getChv2() + " (9000)\n"
            + "; restore initial CHV1 and verify\n"
            + "A0 24 00 01 10 %" + secretCodes.getChv2() + " %" + secretCodes.getChv1() + " (9000)\n"
            + "A0 20 00 01 08 %" + secretCodes.getChv1() + " (9000)\n\n"
            + "; PUK1\n"
            + "; block CHV1 and verify\n"
            + "; try verifying with false code " + secretCodes.getChv1Retries() + " times\n"
        );
        for (int i = 0; i < secretCodes.getChv1Retries(); i++)
            codes2gBuffer.append("A0 20 00 01 08 FFFFFFFFFFFFFFFF (98XX) ; try: " + (i + 1) + "\n");
        codes2gBuffer.append(
            "A0 20 00 01 08 %" + secretCodes.getChv1() + " (9840)\n"
            + "; unblock CHV1 using PUK1 and verify\n"
            + "A0 2C 00 00 10 %" + secretCodes.getPuk1() + " %" + secretCodes.getChv1() + " (9000)\n"
            + "A0 20 00 01 08 %" + secretCodes.getChv1() + " (9000)\n"
            + "; unblock CHV1 using wrong PUK1\n"
            + "A0 2C 00 00 10 FFFFFFFFFFFFFFFF %" + secretCodes.getChv1() + " (9804) ; verification failed\n"
            + "; check PUK1 remaining verifications\n"
            + "A0 A4 00 00 02 3F00 (9FXX)\n"
            + "A0 C0 00 00 W(2;1) (9000) [XXXX XXXX 3F00 XX XX XXXXXX XX 15 XX XX XX XX XX XX (80 | " + String.format("%02X", secretCodes.getPuk1Retries()) + ") XX XX XX XX XX XX XX XX XX XX XX XX XX XX] ; remaining attempts for PUK1 because of wrong verification in previous command\n\n"
            + "; CHV2\n"
            + "; check CHV2 remaining verifications\n"
            + "A0 A4 00 00 02 3F00 (9FXX)\n"
            + "A0 C0 00 00 W(2;1) (9000) [XXXX XXXX 3F00 XX XX XXXXXX XX 15 XX XX XX XX XX XX XX (80 | " + String.format("%02X", secretCodes.getChv2Retries()) + ") XX XX XX XX XX XX XX XX XX XX XX XX XX] ; remaining attempts for CHV2\n"
            + "; verify CHV2\n"
            + "A0 20 00 02 08 %" + secretCodes.getChv2() + " (9000)\n"
            + "; change CHV2 and verify new value\n"
            + "A0 24 00 02 10 %" + secretCodes.getChv2() + " %" + secretCodes.getChv1() + " (9000)\n"
            + "A0 20 00 02 08 %" + secretCodes.getChv1() + " (9000)\n"
            + "; restore initial CHV2 and verify\n"
            + "A0 24 00 02 10 %" + secretCodes.getChv1() + " %" + secretCodes.getChv2() + " (9000)\n"
            + "A0 20 00 02 08 %" + secretCodes.getChv2() + " (9000)\n\n"
            + "; PUK2\n"
            + "; block CHV2 and verify\n"
            + "; try verifying with false code " + secretCodes.getChv2Retries() + " times\n"
        );
        for (int i = 0; i < secretCodes.getChv2Retries(); i++)
            codes2gBuffer.append("A0 20 00 02 08 FFFFFFFFFFFFFFFF (98XX) ; try: " + (i + 1) + "\n");
        codes2gBuffer.append(
            "A0 20 00 02 08 %" + secretCodes.getChv2() + " (9840)\n"
            + "; unblock CHV2 using PUK2 and verify\n"
            + "A0 2C 00 02 10 %" + secretCodes.getPuk2() + " %" + secretCodes.getChv2() + " (9000)\n"
            + "A0 20 00 02 08 %" + secretCodes.getChv2() + " (9000)\n"
            + "; unblock CHV2 using wrong PUK2\n"
            + "A0 2C 00 02 10 FFFFFFFFFFFFFFFF %" + secretCodes.getChv2() + " (9804) ; verification failed\n"
            + "; check PUK2 remaining verifications\n"
            + "A0 A4 00 00 02 3F00 (9FXX)\n"
            + "A0 C0 00 00 W(2;1) (9000) [XXXX XXXX 3F00 XX XX XXXXXX XX 15 XX XX XX XX XX XX XX XX (80 | " + String.format("%02X", secretCodes.getPuk2Retries()) + ") XX XX XX XX XX XX XX XX XX XX XX XX] ; remaining attempts for PUK2 because of wrong verification in previous command\n\n"
            + "; ADM1\n"
            + "; verify ADM1\n"
            + "A0 20 00 00 08 %" + secretCodes.getIsc1() + " (9000)\n"
            + "; change ADM1 and verify\n"
            + "A0 24 00 00 10 %" + secretCodes.getIsc1() + " 0123456789ABCDEF (9000)\n"
            + "A0 20 00 00 08 0123456789ABCDEF (9000)\n"
            + "; restore initial ADM1 and verify\n"
            + "A0 24 00 00 10 0123456789ABCDEF %" + secretCodes.getIsc1() + " (9000)\n"
            + "A0 20 00 00 08 %" + secretCodes.getIsc1() + " (9000)\n\n"
        );
        if (secretCodes.isUseIsc2()) {
            codes2gBuffer.append(
                "; ADM2\n"
                + "; verify ADM2\n"
                + "A0 20 00 05 08 %" + secretCodes.getIsc2() + " (9000)\n"
                + "; block ADM2 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc2Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc2Retries(); i++)
                codes2gBuffer.append("A0 20 00 05 08 FFFFFFFFFFFFFFFF (98XX) ; try: " + (i + 1) + "\n");
            codes2gBuffer.append(
                "A0 20 00 05 08 %" + secretCodes.getIsc2() + " (9840) ; verification failed\n"
                + "; unblock ADM2 using ADM1 and verify\n"
                + "A0 2C 00 05 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc2() + " (9000)\n"
                + "A0 20 00 05 08 %" + secretCodes.getIsc2() + " (9000)\n"
                + "; change ADM2 and verify\n"
                + "A0 24 00 05 10 %" + secretCodes.getIsc2() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "A0 20 00 05 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM2 and verify\n"
                + "A0 24 00 05 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc2() + " (9000)\n"
                + "A0 20 00 05 08 %" + secretCodes.getIsc2() + " (9000)\n\n"
            );
        }
        if (secretCodes.isUseIsc3()) {
            codes2gBuffer.append(
                "; ADM3\n"
                + "; verify ADM3\n"
                + "A0 20 00 06 08 %" + secretCodes.getIsc3() + " (9000)\n"
                + "; block ADM3 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc3Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc3Retries(); i++)
                codes2gBuffer.append("A0 20 00 06 08 FFFFFFFFFFFFFFFF (98XX) ; try: " + (i + 1) + "\n");
            codes2gBuffer.append(
                "A0 20 00 06 08 %" + secretCodes.getIsc3() + " (9840) ; verification failed\n"
                + "; unblock ADM3 using ADM1 and verify\n"
                + "A0 2C 00 06 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc3() + " (9000)\n"
                + "A0 20 00 06 08 %" + secretCodes.getIsc3() + " (9000)\n"
                + "; change ADM3 and verify\n"
                + "A0 24 00 06 10 %" + secretCodes.getIsc3() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "A0 20 00 06 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM3 and verify\n"
                + "A0 24 00 06 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc3() + " (9000)\n"
                + "A0 20 00 06 08 %" + secretCodes.getIsc3() + " (9000)\n\n"
            );
        }
        if (secretCodes.isUseIsc4()) {
            codes2gBuffer.append(
                "; ADM4\n"
                + "; verify ADM4\n"
                + "A0 20 00 07 08 %" + secretCodes.getIsc4() + " (9000)\n"
                + "; block ADM4 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc4Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc4Retries(); i++)
                codes2gBuffer.append("A0 20 00 07 08 FFFFFFFFFFFFFFFF (98XX) ; try: " + (i + 1) + "\n");
            codes2gBuffer.append(
                "A0 20 00 07 08 %" + secretCodes.getIsc4() + " (9840) ; verification failed\n"
                + "; unblock ADM4 using ADM1 and verify\n"
                + "A0 2C 00 07 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc4() + " (9000)\n"
                + "A0 20 00 07 08 %" + secretCodes.getIsc4() + " (9000)\n"
                + "; change ADM4 and verify\n"
                + "A0 24 00 07 10 %" + secretCodes.getIsc4() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "A0 20 00 07 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM4 and verify\n"
                + "A0 24 00 07 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc4() + " (9000)\n"
                + "A0 20 00 07 08 %" + secretCodes.getIsc4() + " (9000)\n\n"
            );
        }
        if (secretCodes.isBlockPuk1()) {
            codes2gBuffer.append(
                "; Block PUK1\n"
                + "; verify CHV1\n"
                + "A0 2000 01 08 %" + secretCodes.getChv1() + " (9000)\n"
                + "; verify wrong CHV1\n"
            );
            for (int i = 0; i < secretCodes.getChv1Retries(); i++)
                codes2gBuffer.append("A0 20 00 01 08 3A3A3A3A3A3A3A3A (98XX) ; try: " + (i + 1) + "\n");
            codes2gBuffer.append("; unblock with wrong PUK code\n");
            for (int i = 0; i < secretCodes.getPuk1Retries(); i++)
                codes2gBuffer.append("A0 2C 00 01 10 3A3A3A3A3A3A3A3A39393939FFFFFFFF (98XX) ; try: " + (i + 1) + "\n");
            codes2gBuffer.append(
                "; unblock CHV1 by PUK1 fails as PUK1 blocked\n"
                + "A0 2C 00 00 10 %" + secretCodes.getPuk1() + " %" + secretCodes.getChv1() + " (98XX)\n\n"
            );
        }
        if (secretCodes.isBlockPuk2()) {
            codes2gBuffer.append(
                "; Block PUK2\n"
                + "; verify CHV2\n"
                + "A0 20 00 02 08 %" + secretCodes.getChv2() + " (9000)\n"
                + "; verify wrong CHV2\n"
            );
            for (int i = 0; i < secretCodes.getChv2Retries(); i++)
                codes2gBuffer.append("A0 20 00 02 08 3A3A3A3A3A3A3A3A (98XX) ; try: " + (i + 1) + "\n");
            codes2gBuffer.append("; unblock with wrong PUK code\n");
            for (int i = 0; i < secretCodes.getPuk2Retries(); i++)
                codes2gBuffer.append("A0 2C 00 02 10 3A3A3A3A3A3A3A3A39393939FFFFFFFF (98XX) ; try: " + (i + 1) + "\n");
            codes2gBuffer.append(
                "; unblock CHV2 by PUK2 fails as PUK2 blocked\n"
                + "A0 2C 00 02 10 %" + secretCodes.getPuk2() + " %" + secretCodes.getChv2() + " (98XX)\n\n"
            );
        }
        if (secretCodes.isPin1disabled()) {
            codes2gBuffer.append(
                "A0 26 00 01 08 %" + secretCodes.getChv1() + " (9000) ; disable CHV1\n\n"
            );
        }
        if (secretCodes.isPin2disabled()) {
            codes2gBuffer.append(
                "A0 26 00 02 08 %" + secretCodes.getChv1() + " (9000) ; disable CHV2\n\n"
            );
        }
        codes2gBuffer.append(".POWER_OFF\n");
        return codes2gBuffer;
    }

    @Override
    public StringBuilder generateSecretCodes3g(SecretCodes secretCodes) {
        StringBuilder codes3gBuffer = new StringBuilder();
        codes3gBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
        );
        if (secretCodes.isPin1disabled()) {
            codes3gBuffer.append(
                "00 28 00 01 08 %" + secretCodes.getGpin() + " (9000) ; enable GPIN1\n\n"
            );
        }
        if (secretCodes.isPin2disabled()) {
            codes3gBuffer.append(
                "; enable LPIN1\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 28 00 81 08 %" + secretCodes.getLpin() + " (9000)\n\n"
            );
        }
        codes3gBuffer.append(
            "; Global PIN\n"
            + "; check GPIN1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID (9000)\n"
            + "00 20 00 01 00 (63CX) ; remaining attempts for GPIN1\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; disable GPIN1 and verify\n"
            + "00 26 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (6984) ; verification failed\n"
            + "; enable GPIN1 and verify\n"
            + "00 28 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; change GPIN1 and verify new value\n"
            + "00 24 00 01 10 %" + secretCodes.getGpin() + " %" + secretCodes.getLpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getLpin() + " (9000)\n"
            + "; restore initial GPIN1 and verify\n"
            + "00 24 00 01 10 %" + secretCodes.getLpin() + " %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n\n"
            + "; Global PUK\n"
            + "; block GPIN1 and verify\n"
            + "; try verifying with false code " + secretCodes.getGpinRetries() + " times\n"
        );
        for (int i = 0; i < secretCodes.getGpinRetries(); i++)
            codes3gBuffer.append("00 20 00 01 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
        codes3gBuffer.append(
            "00 20 00 01 08 %" + secretCodes.getGpin() + " (6983) ; verification failed\n"
            + "; unblock GPIN1 using GPUK1 and verify\n"
            + "00 2C 00 01 10 %" + secretCodes.getGpuk() + " %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; unblock GPIN1 using wrong GPUK1\n"
            + "00 2C 00 01 10 FFFFFFFFFFFFFFFF %" + secretCodes.getGpin() + " (63CX) ; verification failed\n"
            + "; check GPUK1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID\n"
            + "00 2C 00 01 00 (63CX) ; remaining attempts for GPUK1 because of wrong verification in previous command\n\n"
            + "; Local PIN\n"
            + "; verify LPIN1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID (9000)\n"
            + "00 20 00 81 00 (63CX) ; remaining attempts for LPIN1\n"
            + "00 20 00 81 08 %" + secretCodes.getLpin() + " (9000)\n"
            + "; change LPIN1 and verify new value\n"
            + "00 24 00 81 10 %" + secretCodes.getLpin() + " %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 81 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; restore initial LPIN1 and verify\n"
            + "00 24 00 81 10 %" + secretCodes.getGpin() + " %" + secretCodes.getLpin() + " (9000)\n"
            + "00 20 00 81 08 %" + secretCodes.getLpin() + " (9000)\n\n"
            + "; Local PUK\n"
            + "; block LPIN1 and verify\n"
            + "; try verifying with false code " + secretCodes.getLpinRetries() + " times\n"
        );
        for (int i = 0; i < secretCodes.getLpinRetries(); i++)
            codes3gBuffer.append("00 20 00 81 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
        codes3gBuffer.append(
            "00 20 00 81 08 %" + secretCodes.getLpin() + " (6983) ; verification failed\n"
            + "; unblock LPIN1 using LPUK1 and verify\n"
            + "00 2C 00 81 10 %" + secretCodes.getLpuk() + " %" + secretCodes.getLpin() + " (9000)\n"
            + "00 20 00 81 08 %" + secretCodes.getLpin() + " (9000)\n"
            + "; unblock LPIN1 using wrong LPUK1\n"
            + "00 2C 00 81 10 FFFFFFFFFFFFFFFF %" + secretCodes.getLpin() + " (63CX) ; verification failed\n"
            + "; check LPUK1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID\n"
            + "00 2C 00 81 00 (63CX) ; remaining attempts for LPUK1 because of wrong verification in previous command\n\n"
            + "; Issuer Security Code 1\n"
            + "; verify ADM1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID (9000)\n"
            + "00 20 00 0A 00 (63CX) ; remaining attempts for ADM1\n"
            + "; verify ADM1\n"
            + "00 20 00 0A 08 %" + secretCodes.getIsc1() + " (9000)\n"
            + "; change ADM1 and verify new value\n"
            + "00 24 00 0A 10 %" + secretCodes.getIsc1() + " 0123456789ABCDEF (9000)\n"
            + "00 20 00 0A 08 0123456789ABCDEF (9000)\n"
            + "; restore initial ADM1 and verify\n"
            + "00 24 00 0A 10 0123456789ABCDEF %" + secretCodes.getIsc1() + " (9000)\n"
            + "00 20 00 0A 08 %" + secretCodes.getIsc1() + " (9000)\n\n"
        );
        if (secretCodes.isUseIsc2()) {
            codes3gBuffer.append(
                "; Issuer Security Code 2\n"
                + "; check ADM2 remaining verifications\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 20 00 0B 00 (63CX) ; remaining attempts for ADM2\n"
                + "; verify ADM2\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (9000)\n"
                + "; block ADM2 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc2Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc2Retries(); i++)
                codes3gBuffer.append("00 20 00 0B 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (6983) ; verification failed\n"
                + "; unblock ADM2 using ADM1 and verify\n"
                + "00 2C 00 0B 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc2() + " (9000)\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (9000)\n"
                + "; change ADM2 and verify new value\n"
                + "00 24 00 0B 10 %" + secretCodes.getIsc2() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM2 and verify\n"
                + "00 24 00 0B 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc2() + " (9000)\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (9000)\n\n"
            );
        }
        if (secretCodes.isUseIsc3()) {
            codes3gBuffer.append(
                "; Issuer Security Code 3\n"
                + "; check ADM3 remaining verifications\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 20 00 0C 00 (63CX) ; remaining attempts for ADM3\n"
                + "; verify ADM3\n"
                + "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (9000)\n"
                + "; block ADM3 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc3Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc3Retries(); i++)
                codes3gBuffer.append("00 20 00 0C 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (6983) ; verification failed\n"
                + "; unblock ADM3 using ADM1 and verify\n"
                + "00 2C 00 0C 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc3() + " (9000)\n"
                + "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (9000)\n"
                + "; change ADM3 and verify new value\n"
                + "00 24 00 0C 10 %" + secretCodes.getIsc3() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "00 20 00 0C 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM3 and verify\n"
                + "00 24 00 0C 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc3() + " (9000)\n"
                + "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (9000)\n\n"
            );
        }
        if (secretCodes.isUseIsc4()) {
            codes3gBuffer.append(
                "; Issuer Security Code 4\n"
                + "; check ADM4 remaining verifications\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 20 00 0D 00 (63CX) ; remaining attempts for ADM4\n"
                + "; verify ADM4\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (9000)\n"
                + "; block ADM4 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc4Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc4Retries(); i++)
                codes3gBuffer.append("00 20 00 0D 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (6983) ; verification failed\n"
                + "; unblock ADM4 using ADM1 and verify\n"
                + "00 2C 00 0D 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc4() + " (9000)\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (9000)\n"
                + "; change ADM4 and verify new value\n"
                + "00 24 00 0D 10 %" + secretCodes.getIsc4() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM4 and verify\n"
                + "00 24 00 0D 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc4() + " (9000)\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (9000)\n\n"
            );
        }
        if (secretCodes.isBlockGpuk()) {
            codes3gBuffer.append(
                "; Block GPUK\n"
                + "; verify GPIN1\n"
                + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
                + "; verify wrong GPIN1\n"
            );
            for (int i = 0; i < secretCodes.getGpinRetries(); i++)
                codes3gBuffer.append("00 20 00 01 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append("; unblock with wrong PUK code\n");
            for (int i = 0; i < secretCodes.getGpukRetries(); i++)
                codes3gBuffer.append("00 2C 00 01 10 3A3A3A3A3A3A3A3A39393939FFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "; unblock GPIN1 by GPUK1 fails as PUK1 blocked\n"
                + "00 2C 00 01 10 %" + secretCodes.getGpuk() + " %" + secretCodes.getGpin() + " (69XX)\n\n"
            );
        }
        if (secretCodes.isBlockLpuk()) {
            codes3gBuffer.append(
                "; Block LPUK\n"
                + "; verify LPIN1\n"
                + "00 20 00 81 08 %" + secretCodes.getLpin() + " (9000)\n"
                + "; verify wrong LPIN1\n"
            );
            for (int i = 0; i < secretCodes.getLpinRetries(); i++)
                codes3gBuffer.append("00 20 00 81 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append("; unblock with wrong PUK code\n");
            for (int i = 0; i < secretCodes.getLpinRetries(); i++)
                codes3gBuffer.append("00 2C 00 81 10 3A3A3A3A3A3A3A3A39393939FFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "; unblock LPIN1 by LPUK1 fails as PUK2 blocked\n"
                + "00 2C 00 81 10 %" + secretCodes.getLpuk() + " %" + secretCodes.getLpin() + " (69XX)\n\n"
            );
        }
        if (secretCodes.isPin1disabled()) {
            codes3gBuffer.append(
                "00 26 00 01 08 %" + secretCodes.getGpin() + " (9000) ; disable GPIN1\n\n"
            );
        }
        if (secretCodes.isPin2disabled()) {
            codes3gBuffer.append(
                "; disable LPIN1\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 26 00 81 08 %" + secretCodes.getLpin() + " (9000)\n\n"
            );
        }
        codes3gBuffer.append(".POWER_OFF\n");
        return codes3gBuffer;
    }

    private String runAlgo3gAgainCurrSqn(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo; check result DC\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
        );
        if (authentication.isGsmAlgo()) {
            routine.append(
                "; run GSM algo (2G)\n"
                + ".POWER_ON\n"
                + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK 08 %Kc ] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + "; authenticate 3G context again with current SQN\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 00 00 W(2;1) [DC] (9000) ; check result\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnOkIsim(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo, check result --> OK\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %ISIM_AID (9000) ; select ISIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK ] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnNokIsim(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo failed\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
            + ".POWER_ON\n"
            + "00 A4 04 0C <?> %ISIM_AID (9000) ; select ISIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 00 00 W(2;1) [DC] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnOk(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo, check result --> OK\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
        );
        if (authentication.isGsmAlgo()) {
            routine.append(
                "; run GSM algo (2G)\n"
                + ".POWER_ON\n"
                + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK 08 %Kc ] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnNok(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo failed\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
        );
        if (authentication.isGsmAlgo()) {
            routine.append(
                "; run GSM algo (2G)\n"
                + ".POWER_ON\n"
                + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [DC] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String proactiveInitialization() {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n; proactive initialization\n"
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

}
