package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.MinimumSecurityLevel;
import com.idemia.tec.jkt.cardiotest.model.Isd;
import com.idemia.tec.jkt.cardiotest.model.Ram;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RamService {

    @Autowired
    private RootLayoutController root;

    public StringBuilder generateRam(Ram ram) {
        StringBuilder ramBuffer = new StringBuilder();
        // call mappings and load DLLs
        ramBuffer.append(
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
            ramBuffer.append(
                    "\n; initialize counter\n"
                            + ".SET_BUFFER L 00 00 00 00 00\n"
                            + ".EXPORT_BUFFER L COUNTER.bin\n"
            );
        }
        // load anti-replay counter
        ramBuffer.append(
                "\n; buffer L contains the anti-replay counter for OTA message\n"
                        + ".SET_BUFFER L\n"
                        + ".IMPORT_BUFFER L COUNTER.bin\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + ".DISPLAY L\n"
                        + "\n; setup TAR\n"
                        + ".DEFINE %TAR " + ram.getTar() + "\n"
        );
        // enable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            ramBuffer.append("\nA0 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n");
        // case 1
        ramBuffer.append("\n*********\n; CASE 1: Install Applet via OTA by sending single SMS one by one\n*********\n");
        // define target files
//        if (ram.isFullAccess()) {
//            ramBuffer.append(
//                    "\n; TAR is configured for full access\n"
//                            + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
//                            + ".DEFINE %EF_ID " + ram.getTargetEf() + "\n"
//                            + ".DEFINE %EF_ID_ERR " + ram.getTargetEfBadCase() + "\n"
//            );
//        } else {
//            ramBuffer.append(
//                    "\n; TAR is configured with access domain\n"
//                            + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
//                            + ".DEFINE %EF_ID " + ram.getCustomTargetEf() + "; EF protected by " + ram.getCustomTargetAcc() +  "\n"
//                            + ".DEFINE %EF_ID_ERR " + ram.getCustomTargetEfBadCase() + "; (negative test) EF protected by " + ram.getCustomTargetAccBadCase() +  "\n"
//            );
//        }
        ramBuffer.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            ramBuffer.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            ramBuffer.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            ramBuffer.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        ramBuffer.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT R\n"
        );
        // some TAR may be configured with specific keyset or use all available keysets
        if (ram.isUseSpecificKeyset())
            ramBuffer.append(ramCase1(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel(), ram.getIsd()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                ramBuffer.append(ramCase1(keyset, keyset, ram.getMinimumSecurityLevel(), ram.getIsd()));
            }
        }
        ramBuffer.append("\n.UNDEFINE %EF_CONTENT\n");
        // perform negative test if not full access
//        if (!ram.isFullAccess()) {
//            ramBuffer.append(
//                    "\n; perform negative test: updating " + ram.getCustomTargetEfBadCase() + " (" + ram.getCustomTargetAccBadCase() + ")\n"
//                            + "\n.POWER_ON\n"
//                            + "; check initial content of EF\n"
//                            + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
//            );
//            if (root.getRunSettings().getSecretCodes().isUseIsc2())
//                ramBuffer.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
//            if (root.getRunSettings().getSecretCodes().isUseIsc3())
//                ramBuffer.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
//            if (root.getRunSettings().getSecretCodes().isUseIsc4())
//                ramBuffer.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
//            ramBuffer.append(
//                    "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
//                            + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
//                            + "A0 A4 00 00 02 %DF_ID (9F22)\n"
//                            + select2gWithAbsolutePath(ram.getCustomTargetEfBadCase())
//                            + "A0 B0 00 00 01 (9000)\n"
//                            + ".DEFINE %EF_CONTENT R\n"
//            );
//            if (ram.isUseSpecificKeyset())
//                ramBuffer.append(ramCase1NegativeTest(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel()));
//            else {
//                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
//                    ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
//                    ramBuffer.append(ramCase1NegativeTest(keyset, keyset, ram.getMinimumSecurityLevel()));
//                }
//            }
//            ramBuffer.append("\n.UNDEFINE %EF_CONTENT\n");
//        }
        // case 2
        ramBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in ISIM TAR\n*********\n");
        if (ram.isUseSpecificKeyset())
            ramBuffer.append(ramCase2(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                ramBuffer.append(ramCase2(keyset, keyset, ram.getMinimumSecurityLevel()));
            }
        }
        // case 3
        ramBuffer.append("\n*********\n; CASE 3: (Bad Case) send 2G command to ISIM TAR\n*********\n");
        if (ram.isUseSpecificKeyset())
            ramBuffer.append(ramCase3(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                ramBuffer.append(ramCase3(keyset, keyset, ram.getMinimumSecurityLevel()));
            }
        }
        // case 4
        ramBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
        if (ram.isUseSpecificKeyset())
            ramBuffer.append(ramCase4(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                ramBuffer.append(ramCase4(keyset, keyset, ram.getMinimumSecurityLevel()));
            }
        }
        // case 5
        ramBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
        if (Integer.parseInt(ram.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
            ramBuffer.append("\n; MSL: " + ram.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
        else {
            if (ram.isUseSpecificKeyset())
                ramBuffer.append(ramCase5(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    ramBuffer.append(ramCase5(keyset, keyset, ram.getMinimumSecurityLevel()));
                }
            }
        }
        // case 6
        ramBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
        if (ram.isUseSpecificKeyset())
            ramBuffer.append(ramCase6(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                ramBuffer.append(ramCase6(keyset, keyset, ram.getMinimumSecurityLevel()));
            }
        }
        // case 7
        ramBuffer.append("\n*********\n; CASE 7: (Bad Case) insufficient MSL\n*********\n");
        if (ram.getMinimumSecurityLevel().getComputedMsl().equals("00"))
            ramBuffer.append("\n; MSL: " + ram.getMinimumSecurityLevel().getComputedMsl() + " -- case 7 is not executed\n");
        else {
            MinimumSecurityLevel lowMsl = new MinimumSecurityLevel(false, "No verification", "No counter available");
            lowMsl.setSigningAlgo("no algorithm");
            lowMsl.setCipherAlgo("no cipher");
            lowMsl.setPorRequirement("PoR required");
            lowMsl.setPorSecurity("response with no security");
            lowMsl.setCipherPor(false);
            if (ram.isUseSpecificKeyset())
                ramBuffer.append(ramCase7(ram.getCipheringKeyset(), ram.getAuthKeyset(), lowMsl));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    ramBuffer.append(ramCase7(keyset, keyset, lowMsl));
                }
            }
        }
        // case 8
        ramBuffer.append("\n*********\n; CASE 8: Bad TP-OA value\n*********\n");
        if (!root.getRunSettings().getSmsUpdate().isUseWhiteList())
            ramBuffer.append("\n; profile does not have white list configuration -- case 8 is not executed\n");
        else {
            if (ram.isUseSpecificKeyset())
                ramBuffer.append(ramCase8(ram.getCipheringKeyset(), ram.getAuthKeyset(), ram.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    ramBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    ramBuffer.append(ramCase8(keyset, keyset, ram.getMinimumSecurityLevel()));
                }
            }
        }
        // save counter
        ramBuffer.append(
                "\n; save counter state\n"
                        + ".EXPORT_BUFFER L COUNTER.bin\n"
        );
        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            ramBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");
        // unload DLLs
        ramBuffer.append(
                "\n.UNLOAD Calcul.dll\n"
                        + ".UNLOAD OTA2.dll\n"
                        + ".UNLOAD Var_Reader.dll\n"
                        + "\n.POWER_OFF\n"
        );
        return ramBuffer;
    }

    public StringBuilder generateRamUpdateRecord(Ram ram) {
        StringBuilder ramUpdateRecordBuffer = new StringBuilder();
        // TODO
        return ramUpdateRecordBuffer;
    }

    public StringBuilder generateRamExpandedMode(Ram ram) {
        StringBuilder ramExpandedModeBuffer = new StringBuilder();
        // TODO
        return ramExpandedModeBuffer;
    }

    private String ramCase1(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Isd isd) {
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
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        routine.append(
                ".CHANGE_TAR %TAR\n"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");

        routine.append(
                "\n; command(s) sent via OTA\n"
                        + "SMS 1: INSTALL FOR LOAD"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + ".SET_BUFFER J 80E602001F0CA0000000185302000000001000000EEF0CC6020000C8020000C702000000\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + "A0 C2 00 00 G J (9FXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "A0 C0 00 00 W(2;1) [XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX0161XX]"
                        + "SMS 2: LOAD PACKAGE"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + ".SET_BUFFER J 80E8000067C48201CC010016DECAFFED01020400020CA0000000185302000000001002001F0016001F0010001E0046001A00C6000A001E0000009400000000000002010004001E02000107A0000000620101010210A0000000090003FFFFFFFF8910710002030010010CA000\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + getNextMessage(msl)
                        + "A0 C2 00 00 G J (9FXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "A0 C0 00 00 W(2;1) [XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX0161XX]"
                        + "SMS 3: LOAD PACKAGE"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + ".SET_BUFFER J 80E800016700001853020000000110001F06001A43800300FF00070300000035003800A4800200810101088100000700C6020048803E008800060093800B00A000060210188C00008D0001058B00027A05318F00033D8C00042E1B181D0441181D258B00057A00207A03221D\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + getNextMessage(msl)
                        + "A0 C2 00 00 G J (9FXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "A0 C0 00 00 W(2;1) [XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX0161XX]"
                        + "SMS 4: LOAD PACKAGE"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + ".SET_BUFFER J 80E800026775006800020002000D001300588D00072E1B8B0008311B1E8B000910F06B4B1B1E04418B0009100C6B401B1E05418B000961371B1E06418B000910126B2C1B1E07418B00096123188B000A701D3B8D0001103C8B000B7012188B000A8D0001038B000B70053B70\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + getNextMessage(msl)
                        + "A0 C2 00 00 G J (9FXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "A0 C0 00 00 W(2;1) [XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX0161XX]"
                        + "SMS 5: LOAD PACKAGE"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + ".SET_BUFFER J 80E8000367027A041110178D000C601A8D000D2C19040310828B000E198B000F10206B06058D00107A08000A00000000000000000000050046001106800300068109000381090901000000060000110380030201810700068108000381080D03810204030000090381090C06\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + getNextMessage(msl)
                        + "A0 C2 00 00 G J (9FXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "A0 C0 00 00 W(2;1) [XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX0161XX]"
                        + "SMS 6: LOAD PACKAGE"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + ".SET_BUFFER J 80E880043481030006810A0003810A1503810A160681070009001E0000001A070806030406040C1705060B0B090B0606050603040D05090408\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + "A0 C2 00 00 G J (9FXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "A0 C0 00 00 W(2;1) [XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX0161XX]"
                        + ".EXPORT_BUFFER L COUNTER.bin\n\n\n"
                        + " CHECK LOADED PACKAGE\n"
                        + ".POWER_ON\n"
                        + proactiveInitialization()
                        + checkLoadedPackage(isd)
                        + deleteLoadedPackage(isd)
        );
        return routine.toString();
    }

    private String ramCase2(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        routine.append(
                ".CHANGE_TAR %TAR\n"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");
        routine.append(
                "\n; command(s) sent via OTA\n"
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

    private String ramCase3(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        routine.append(
                ".CHANGE_TAR %TAR\n"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");
        routine.append(
                "\n; command(s) sent via OTA\n"
                        + ".SET_BUFFER J A0 A4 00 00 02 3F00 ; this command isn't supported by ISIM\n"
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

    private String ramCase4(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        routine.append(
                ".CHANGE_TAR B0FFFF ; this TAR isn't registered in profile\n"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");
        routine.append(
                "\n; command(s) sent via OTA\n"
                        + ".SET_BUFFER J 00 A4 00 00 02 3F00\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + "; send envelope\n"
                        + "A0 C2 00 00 G J (9XXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "; check PoR\n"
                        + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX B0 FF FF XX XX XX XX XX XX 09] (9000) ; TAR unknown\n"
                        + "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String ramCase5(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        routine.append(
                ".CHANGE_TAR %TAR\n"
                        + ".CHANGE_COUNTER 0000000001 ; this value is lower than previous case\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");
        routine.append(
                "\n; command(s) sent via OTA\n"
                        + ".SET_BUFFER J 00 A4 00 00 02 3F00\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + "; send envelope\n"
                        + "A0 C2 00 00 G J (9XXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "; check PoR\n"
                        + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 02] (9000) ; low counter\n"
                        + "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String ramCase6(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                        + proactiveInitialization()
                        + "\n; SPI settings\n"
                        + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                        + ".SET_BUFFER Q " + createFakeAuthKey(authKeyset) + " ; bad authentication key\n"
                        + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                        + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                        + ".INIT_ENV_0348\n"
                        + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        routine.append(
                ".CHANGE_TAR %TAR\n"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");
        routine.append(
                "\n; command(s) sent via OTA\n"
                        + ".SET_BUFFER J 00 A4 00 00 02 3F00\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + "; send envelope\n"
                        + "A0 C2 00 00 G J (9XXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "; check PoR\n"
                        + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 01] (9000) ; RC/CC/DS failed\n"
                        + "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String ramCase7(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA " + root.getRunSettings().getSmsUpdate().getTpOa() + "\n");
        routine.append(
                ".CHANGE_TAR %TAR\n"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");
        routine.append(
                "\n; command(s) sent via OTA\n"
                        + ".SET_BUFFER J 00 A4 00 00 02 3F00\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + "; send envelope\n"
                        + "A0 C2 00 00 G J (9XXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "; check PoR\n"
                        + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 0A] (9000) ; insufficient MSL\n"
                        + "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String ramCase8(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
        );
        if (root.getRunSettings().getSmsUpdate().isUseWhiteList())
            routine.append(".CHANGE_TP_OA FFFFFFFFFF ; bad TP-OA value\n");
        routine.append(
                ".CHANGE_TAR %TAR\n"
                        + ".CHANGE_COUNTER L\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
                        + "\n; MSL = " + msl.getComputedMsl() + "\n"
                        + ".SET_DLKEY_KIC O\n"
                        + ".SET_DLKEY_KID Q\n"
                        + ".CHANGE_KIC M\n"
                        + ".CHANGE_KID N\n"
                        + spiConfigurator(msl, cipherKeyset, authKeyset)
        );
        if (authKeyset.getKidMode().equals("AES - CMAC"))
            routine.append(".SET_CMAC_LENGTH " + String.format("%02X", authKeyset.getCmacLength()) + "\n");
        routine.append(
                "\n; command(s) sent via OTA\n"
                        + ".SET_BUFFER J 00 A4 00 00 02 3F00\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
                        + "; send envelope\n"
                        + "A0 C2 00 00 G J (9000) ; no PoR returned\n"
                        + ".CLEAR_SCRIPT\n"
                        + "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
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

    private String getNextMessage(MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        String auth_verif = msl.getAuthVerification();
        if(auth_verif.equals("Redundancy Check") || auth_verif.equals("Cryptographic Checksum")) {
            routine.append(
                    "A0 C2 00 00 G J (9000)\n"
                        + ".GET_NEXT_MESSAGE G J \n"
            );
        }
        return routine.toString();
    }

    private String openChannel(Isd isd) {
        StringBuilder routine = new StringBuilder();
        if(!isd.isSecuredState()) {
            routine.append(
                    ".ASCIITOHEX I OT.TEST.GT1000.3GKEY16.01\n"
                            + ".DEFINE %MKEY_LABEL I\n"
                            + ".DEFINE %SCP81_KEY_MASTER 1D8A0C5CB0EBBA42E7AA1E83B0F19217\n"
                            + ".DEFINE %PSK_DEK 11 22 33 44 55 66 77 88 99 00 AA BB CC DD EE FF\n"
                            + ".DEFINE %PSK_DEK_KEY_TYPE 80\n"
                            + ".DEFINE %LEN_CARD_MANAGER_AID 08"
                            + ".DEFINE %LEN_CARD_MANAGER_AID 08\n"
                            + ".DEFINE %CST_DERIVATION_ENC 0182\n"
                            + ".DEFINE %CST_DERIVATION_RMAC 0102\n"
                            + ".DEFINE %CST_DERIVATION_CMAC 0101\n"
                            + ".DEFINE %CST_DERIVATION_DEK 0181\n" +

                            "\t;--------------------------------------------------------------------------------------\n" +
                            "\t; PSKID derivation\n" +
                            "\t;--------------------------------------------------------------------------------------\n" +
                            "\t\n" +
                            "\t.ASCIITOHEX J %ICCID\n" +
                            "\t.DEFINE %ICCID_ASCII J\n" +
                            "\t\n" +
                            "\t.SET_DATA %MKEY_LABEL\n" +
                            "\t.SHA I\n" +
                            "\t\n" +
                            "\t.DEFINE %MKEY_LABEL_HASH I\n" +
                            "\t.ASCIITOHEX I %MKEY_LABEL_HASH\n" +
                            "\t\n" +
                            "\t.DEFINE %MKEY_LABEL_HASH_ASCII I\n" +
                            "\t\n" +
                            "\t.DEFINE %SCP81_KEY_ID 4F544132\n" +


                            "\t;--------------------------------------------------------------------------------------\n" +
                            "\t; PSK derivation\n" +
                            "\t;--------------------------------------------------------------------------------------\n" +
                            "\n" +
                            "\t; we take the 8 last bytes of the ICCID_SWAPPED\n" +
                            "\t.SET_BUFFER I %ICCID\n" +
                            "\t.DEFINE %8_LAST_ICCID  I(3;8)\n" +
                            "\t; set KM0 and KM1\n" +
                            "\t.DEFINE %KM0 %SCP81_KEY_MASTER\n" +
                            "\t.SET_BUFFER I %KM0\n" +
                            "\t.DEFINE %KM1 I(9;8) I(1;8)\n" +
                            "\n" +
                            "\t; compute R1\n" +
                            "\t.SET_DATA %8_LAST_ICCID\n" +
                            "\t.SET_KEY %KM0\n" +
                            "\t.DES3 I 00\n" +
                            "\n" +
                            "\t.DEFINE %B1 I\n" +
                            "\n" +
                            "\t; compute R2\n" +
                            "\t.SET_DATA %8_LAST_ICCID\n" +
                            "\t.SET_KEY %KM1\n" +
                            "\t.DES3 I 00\n" +
                            "\n" +
                            "\t.DEFINE %B2 I\n" +
                            "\n" +
                            "\t; psk\n" +
                            "\t.DEFINE %SCP81_KEY %B1 %B2\n" +
                            "\t\n" +
                            "\t.UNDEFINE %8_LAST_ICCID\n" +
                            "\t.UNDEFINE %KM0\n" +
                            "\t.UNDEFINE %KM1\n" +
                            "\t.UNDEFINE %B1\n" +
                            "\t.UNDEFINE %B2\n" +
                            "\t.UNDEFINE %SCP81_KEY_MASTER\n" +
                            "\t\n" +
                            "\t.POWER_ON\n" +

                            "\t;--------------------------------------------------------------------------------------------------------------------\n" +
                            "\t; Create a default SCP81 keyset for the ISD\n" +
                            "\t;--------------------------------------------------------------------------------------------------------------------\n" +
                            "00 A4 04 00 <?> " + root.getRunSettings().getCardParameters().getCardManagerAid() + "\n" +
                            "\t.SET_BUFFER I %ENC_SECRET_KEY\n" +
                            "\t.SET_BUFFER J %MAC_SECRET_KEY\n" +
                            "\t.SET_BUFFER K %KEY_ENCRYPT_KEY\n" +
                            "\t.SET_BUFFER M 00 00 00 ;Open in clear mode\n" +
                            "\t\n" +
                            "\t.IFNDEF T1_PROTOCOL\n" +
                            "\t\t.AUTOMATIC_PROTOCOL_OFF\n" +
                            "\t.ENDIF" +
                            "\t.IFNDEF FIRST_OPEN_CHANNEL;\n" +
                            "\t\t; first initialization of variables\n" +
                            "\t\t.APPEND_IFDEF FIRST_OPEN_CHANNEL;\n" +
                            "\t.ELSE\n" +
                            "\t\t; these variables are presistent, and may be used by compute_command.cmd script\n" +
                            "\t\t.UNDEFINE %SECURITY_LEVEL \n" +
                            "\t\t.UNDEFINE %SESSION_KIC\n" +
                            "\t\t.UNDEFINE %SESSION_CMAC\n" +
                            "\t\t.UNDEFINE %SESSION_RMAC\n" +
                            "\t\t.UNDEFINE %SESSION_KIK\n" +
                            "\t\t.UNDEFINE %LAST_COMPUTED_MAC\n" +
                            "\t\t.UNDEFINE %SEQUENCE_COUNTER\n" +
                            "\t\t\n" +
                            "\t.ENDIF\n" +
                            "\t\n" +
                            "\t; set input variables\n" +
                            "\t.DEFINE %KIC_ I\n" +
                            "\t.DEFINE %KID_ J\n" +
                            "\t.DEFINE %KIK_ K\n" +
                            "\t.DEFINE %KEY_VERSION M(2;1)\n" +
                            "\t.DEFINE %SECURITY_LEVEL M(1;1)\n" +
                            "\t.DEFINE %CHANNEL_NB M(3;1)" +

                            "\t*----------------------------------------------\n" +
                            "\t*2 step 1 : prepare INITIALIZE_UPDATE  command\n" +
                            "\t*----------------------------------------------  \n" +
                            "\t.ASCIITOHEX J HostRand     * host challenge\n" +
                            "\t.DEFINE %HOST_CHALLENGE J\n" +
                            "\t\n" +
                            "\t.DEFINE %SAV_I_ I\n" +
                            "\t.DEFINE %SAV_J_ J\n" +
                            "\t\n" +
                            "\t; compute channel number\n" +
                            "\t.SET_BUFFER I %CHANNEL_NB\n" +
                            "\t.INCREASE_BUFFER I 80\n" +
                            "\t\n" +
                            "\t;* prepare Initialize Update command\n" +
                            "\t.DEFINE %INIT_UPDATE_CMD I \\; channel number\n" +
                            "\t\t\t\t\t\t\t50 \\; INS\n" +
                            "\t\t\t\t\t\t\t%KEY_VERSION \\; key version \n" +
                            "\t\t\t\t\t\t\t00 08 \\; P2 = 00 LE = 08 (mandatory)\n" +
                            "\t\t\t\t\t\t\t%HOST_CHALLENGE ; host challenge\n" +
                            "\t\t\t\n" +
                            "\t.SET_BUFFER I %SAV_I_\n" +
                            "\t.SET_BUFFER J %SAV_J_\t\n" +
                            "\t\t\t\n" +
                            "\t.UNDEFINE %SAV_I_\n" +
                            "\t.UNDEFINE %SAV_J_\n" +
                            "\t\t\n" +
                            "\t\n" +
                            "\t%INIT_UPDATE_CMD 1C (%OK)\n" +
                            "\t\n" +
                            "\t.DEFINE %SEQUENCE_COUNTER R(13;2)     \t;Sequence Counter ;\n" +
                            "\t; don't check card cryptogram\n" +
                            "\t.DEFINE %CARD_CRYPTOGRAM  R(21;8)   \t\t; card cryptogramme\n" +
                            "\t.DEFINE %CARD_CHALLENGE   R(15;6)\t\t\t; card CHALLENGE\n" +

                            "\t*----------------------------------------------\n" +
                            "\t*2 step 2 : precompute sessions keys\n" +
                            "\t*----------------------------------------------  \n" +
                            "\t.DEFINE %SAV_K__ K\n" +
                            "\t.DEFINE %SAV_I__ I\n" +
                            "\t" +
                            "*Buffer K contains a authentication/encryption  derivation data\n" +
                            "\t\t.SET_BUFFER K %CST_DERIVATION_ENC %SEQUENCE_COUNTER 00000000 00000000 00000000\n" +
                            "\t\t.SET_DATA K\n" +
                            "\t\t.SET_VECT_INI 00000000 00000000\n" +
                            "\t\t;* static Authentication/Encryption Key\n" +
                            "\t\t.SET_KEY %KIC_\n" +
                            "\t\t;* session Authentication/Encryption Key\n" +
                            "\t\t.DES3CBC I 00\n" +
                            "\t\t.DEFINE %SESSION_KIC I\n" +
                            "\t\t.DISPLAY %SESSION_KIC\n" +
                            "\t\n" +
                            "\t;* compute C_Mac session key\n" +
                            "\t*Buffer K contains a CMAC  derivation data\n" +
                            "\t\t.SET_BUFFER  K %CST_DERIVATION_CMAC %SEQUENCE_COUNTER 00000000 00000000 00000000\n" +
                            "\t\t.SET_DATA K\n" +
                            "\t\t.SET_VECT_INI 00000000 00000000\n" +
                            "\t\t;* static CMac Key\n" +
                            "\t\t.SET_KEY %KID_\n" +
                            "\t\t;* C_Mac session\n" +
                            "\t\t.DES3CBC I 00\n" +
                            "\t\t.DEFINE %SESSION_CMAC I\n" +
                            "\t\t.DISPLAY %SESSION_CMAC\n" +
                            "\t\n" +
                            "\t;* compute R_Mac session key\n" +
                            "\t*Buffer K contains a RMAC  derivation data\n" +
                            "\t\t.SET_BUFFER  K %CST_DERIVATION_RMAC %SEQUENCE_COUNTER 00000000 00000000 00000000\n" +
                            "\t\t.SET_DATA K\n" +
                            "\t\t.SET_VECT_INI 00000000 00000000\n" +
                            "\t\t;* static static Key Encryption Key\n" +
                            "\t\n" +
                            "\t\t.SET_KEY %KID_ ; SAVE_STATIC_MAC_KEY\n" +
                            "\t\t;* session RMac session Key\n" +
                            "\t\t.DES3CBC I 00\n" +
                            "\t\t.DEFINE %SESSION_RMAC I\n" +
                            "\t\t.DISPLAY %SESSION_RMAC\n" +
                            "\t\n" +
                            "\t;* compute DEK session key\n" +
                            "\t*Buffer K contains a DEK  derivation data\n" +
                            "\t\t.SET_BUFFER  K %CST_DERIVATION_DEK %SEQUENCE_COUNTER 00000000 00000000 00000000\n" +
                            "\t\t.SET_DATA K\n" +
                            "\t\t.SET_VECT_INI 00000000 00000000\n" +
                            "\t\t;* static DEK Key\n" +
                            "\t\t.SET_KEY %KIK_\n" +
                            "\t\t;* session DEK Key\n" +
                            "\t\t.DES3CBC I 00\n" +
                            "\t\t.DEFINE %SESSION_KIK I\n" +
                            "\t\t.DISPLAY %SESSION_KIK\n" +
                            "\t\t\t\n" +
                            "\t\t\t\n" +
                            "\t.SET_BUFFER K %SAV_K__\n" +
                            "\t.SET_BUFFER I %SAV_I__\n" +
                            "\t\n" +
                            "\t.UNDEFINE %SAV_K__\n" +
                            "\t.UNDEFINE %SAV_I__\n" +
                            "\t\n" +


                            "\t*----------------------------------------------\n" +
                            "\t*2 step 3 : precompute host cryptogram\n" +
                            "\t*----------------------------------------------  \n" +
                            "\t.SET_BUFFER G %SEQUENCE_COUNTER %CARD_CHALLENGE %HOST_CHALLENGE\n" +
                            "\t\t\t.SET_DATA G\n" +
                            "\t\t\t.SET_VECT_INI 00000000 00000000\n" +
                            "\t\t\t.SET_KEY %SESSION_KIC\n" +
                            "\t\t\t.MAC33 N 80 /P\n" +
                            "\t\t\t.DEFINE %HOST_CRYPTOGRAM N\n" +
                            "\t\t\t.DISPLAY N\n" +
                            "\t\t\t\n" +
                            "\t\n" +


                            "\t*----------------------------------------------\n" +
                            "\t*2 step 4 : compute command mac\n" +
                            "\t*---------------------------------------------- \n" +
                            "\t;* compute command MAC\n" +
                            "\t.SET_BUFFER G 84 82 %SECURITY_LEVEL 00 10 %HOST_CRYPTOGRAM\n" +
                            "\t.SET_DATA G\n" +
                            "\t.SET_VECT_INI 00000000 00000000 ; ICV set to 00\n" +
                            "\t\n" +
                            "\t* session CMac Key\n" +
                            "\t.SET_KEY %SESSION_CMAC\n" +
                            "\t.MAC3 I 80 /P\n" +
                            "\t.DEFINE %LAST_COMPUTED_MAC I\n" +
                            "\t.DISPLAY I\n" +
                            "\t\n" +
                            "\t;* External Authenticate\n" +
                            "\t.DEFINE %EXTERNAL_AUTH_CMD 84 82 %SECURITY_LEVEL 00 10 %HOST_CRYPTOGRAM %LAST_COMPUTED_MAC (%OK)\n" +
                            "\t\n" +
                            "\t\n" +
                            "\t%EXTERNAL_AUTH_CMD (%OK)\n" +
                            "\t\n" +
                            "\t;set output variables and buffers\n" +
                            "\t.SET_BUFFER I %SESSION_KIC\n" +
                            "\t.SET_BUFFER J %SESSION_CMAC\n" +
                            "\t.SET_BUFFER K %SESSION_KIK\n" +
                            "\t.SET_BUFFER M %LAST_COMPUTED_MAC\n" +
                            "\t\n" +
                            "\t.UNDEFINE %KIC_ \n" +
                            "\t.UNDEFINE %KID_ \n" +
                            "\t.UNDEFINE %KIK_\n" +
                            "\t.UNDEFINE %KEY_VERSION \n" +
                            "\t\n" +
                            "\t.UNDEFINE %CHANNEL_NB \n" +
                            "\t\n" +
                            "\t.UNDEFINE %INIT_UPDATE_CMD\n" +
                            "\t.UNDEFINE %EXTERNAL_AUTH_CMD\n" +
                            "\t\n" +
                            "\t.UNDEFINE %HOST_CHALLENGE\n" +
                            "\t\n" +
                            "\t.UNDEFINE %CARD_CRYPTOGRAM\n" +
                            "\t.UNDEFINE %CARD_CHALLENGE\n" +
                            "\t.UNDEFINE %HOST_CRYPTOGRAM\n" +
                            "\t\n" +
                            "\t.IFNDEF T1_PROTOCOL\n" +
                            "\t\t.AUTOMATIC_PROTOCOL_ON\n" +
                            "\t.ENDIF\n" +
                            "\n" +


                            "\t;--------------------------------------------------------------------------------------------------------\n" +
                            "\t*3 SCP81 KeySet modification. Please put your specific configuration here for KVN change\n" +
                            "\t;--------------------------------------------------------------------------------------------------------\n" +
                            "\t*3 Compute the PUT KEY to create the PSK TLS SCP81 keyset.\n" +
                            "\t.IFDEF DEK_TEST\n" +
                            "\t\t.DEFINE %PSK_TLS_SCP81_KEY  %PSK_TLS_NEW_KEY  ;%SCP81_KEY\n" +
                            "\t\t.DEFINE %DEDICATED_SCP_KEY_VERSION 40 ;Key version number\n" +
                            "\t\t.DEFINE %NUMBER_OF_KEY_VERSION_CREATION_WANTED 01 ;Only one key version created\n" +
                            "\t\t.CALL cmd_tools\\amendment_B_cmd_tools_create_SCP81_key_set_update.cmd  /LIST_OFF\n" +
                            "\t.ENDIF\n" +
                            "\n" +


                            "\t;-----------------------------------------------\n" +
                            "\t; RESET PREDEFINED VARIABLE\n" +
                            "\t;-----------------------------------------------\n" +
                            "\t\n" +
                            "\t.UNDEFINE %MKEY_LABEL \n" +
                            "\t\n" +
                            "\t.UNDEFINE %SCP81_KEY \n" +
                            "\t\n" +
                            "\t.UNDEFINE %PSK_DEK\t\n" +
                            "\t\n" +
                            "\t.UNDEFINE %PSK_DEK_KEY_TYPE\t\t\t\n" +
                            "\t\n" +
                            "\t.UNDEFINE %LEN_CARD_MANAGER_AID         \n" +
                            "\t\n" +
                            "\t.UNDEFINE %AID_CARD_MANAGER\t\t\t\t\n" +
                            "\t\n" +
                            "\t.UNDEFINE %AID_CARD_MANAGER_WITH_LENGTH \n" +
                            "\t\t\t\t\t\t\t\t\t\t\n" +
                            "\t.UNDEFINE %SELECT_AID_CARD_MANAGER\t    \n" +
                            "\t                  \n" +
                            "\t.UNDEFINE %CST_DERIVATION_ENC           \n" +
                            "\t.UNDEFINE %CST_DERIVATION_RMAC          \n" +
                            "\t.UNDEFINE %CST_DERIVATION_CMAC          \n" +
                            "\t.UNDEFINE %CST_DERIVATION_DEK           \n" +
                            "\n" +
                            "\t;.UNDEFINE %ISD_ENC_SECRET_KEY\t \n" +
                            "\t;.UNDEFINE %ISD_MAC_SECRET_KEY\t \n" +
                            "\t;.UNDEFINE %ISD_KEY_ENCRYPT_KEY \t\n" +
                            "\t\n" +
                            "\t.UNDEFINE %ENC_SECRET_KEY\t\t\t\n" +
                            "\t\n" +
                            "\t.UNDEFINE %MAC_SECRET_KEY\t\t\t\n" +
                            "\t\n" +
                            "\t.UNDEFINE %KEY_ENCRYPT_KEY\t\t\n" +
                            "\t\n" +
                            "\t.UNDEFINE %OK \n" +
                            "\t\n" +
                            "\t.UNDEFINE %ICCID_ASCII \n" +
                            "\t\n" +
                            "\t.UNDEFINE %MKEY_LABEL_HASH \n" +
                            "\t\n" +
                            "\t.UNDEFINE %MKEY_LABEL_HASH_ASCII \n" +
                            "\t\n" +
                            "\t.UNDEFINE %SCP81_KEY_ID  \n" +
                            "\t\n" +
                            "\t.UNDEFINE %PUT_KEY  \n" +
                            "\t.UNDEFINE %GET_RESPONSE_61 \n" +
                            "\t.UNDEFINE %GET_response \n" +
                            "\t\n" +
                            "\t.UNDEFINE %SCP81_KEY_MASTER\n" +
                            "\n" +
                            "\t.BREAK\n" +

                            "; Verify Code ADM1\n" +
                            "\tA0 20 0000 08 %ADM1 (9000)\n" +
                            "\t\n" +
                            "\t; Select Card Manager AID\n" +
                            "\t00 A4 0400 <?> %CARD_MANAGER_AID  (61 XX)\n" +
                            "\t\n" +
                            "\t; Get Response\n" +
                            "\t00 C0 00 00   W(2;1) [] (9000)\n" +
                            "\t\n" +
                            "\t.BREAK"
            );
        }
        return routine.toString();
    }

    private String checkLoadedPackage(Isd isd) {
        StringBuilder routine = new StringBuilder();
        if(isd.getMethodForGpCommand().equals("with Card Manager Keyset")) {
            routine.append(
                    openChannel(isd)

            );
        }
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

    private String createFakeCipherKey(SCP80Keyset keyset) {
        if (keyset.getKicKeyLength() == 8)
            return "0102030405060708";
        if (keyset.getKicKeyLength() == 16)
            return "0102030405060708090A0B0C0D0E0F10";
        if (keyset.getKicKeyLength() == 24)
            return "0102030405060708090A0B0C0D0E0F101112131415161718";
        if (keyset.getKicKeyLength() == 32)
            return "0102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F20";
        return null; // intentionally raise syntax error in pcom
    }

    private String createFakeAuthKey(SCP80Keyset keyset) {
        if (keyset.getKidKeyLength() == 8)
            return "0102030405060708";
        if (keyset.getKidKeyLength() == 16)
            return "0102030405060708090A0B0C0D0E0F10";
        if (keyset.getKidKeyLength() == 24)
            return "0102030405060708090A0B0C0D0E0F101112131415161718";
        if (keyset.getKidKeyLength() == 32)
            return "0102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F20";
        return null; // intentionally raise syntax error in pcom
    }



    private String select2gWithAbsolutePath(String fid) {
        StringBuilder routine = new StringBuilder();
        int step = fid.length() / 4;
        int index = 0;
        for (int i = 0; i < step; i++) {
            routine.append("A0A40000 02 " + fid.substring(index, index + 4) + " (9FXX)\n");
            index += 4;
        }
        return routine.toString();
    }

    private String appendScriptSelect3g(String fid) {
        StringBuilder routine = new StringBuilder();
        int step = fid.length() / 4;
        int index = 0;
        for (int i = 0; i < step; i++) {
            routine.append(
                    ".SET_BUFFER J 00 A4 00 00 02 " + fid.substring(index, index + 4) + "\n"
                            + ".APPEND_SCRIPT J\n"
            );
            index += 4;
        }
        return routine.toString();
    }

}
