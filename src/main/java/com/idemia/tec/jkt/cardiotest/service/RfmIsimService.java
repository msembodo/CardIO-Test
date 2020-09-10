package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.MinimumSecurityLevel;
import com.idemia.tec.jkt.cardiotest.model.RfmIsim;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RfmIsimService {

    @Autowired private RootLayoutController root;

    public StringBuilder generateRfmIsim(RfmIsim rfmIsim) {
        StringBuilder rfmIsimBuffer = new StringBuilder();

        // Generate Header Script
        rfmIsimBuffer.append(this.headerScript(rfmIsim));

        // case 1
        rfmIsimBuffer.append("\n*********\n; CASE 1: RFM ISIM with correct security settings\n*********\n");
        // define target files
        if (rfmIsim.isFullAccess()) {
            rfmIsimBuffer.append(
                "\n; TAR is configured for full access\n"
                + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
                + ".DEFINE %EF_ID " + rfmIsim.getTargetEf() + "\n"
                + ".DEFINE %EF_ID_ERR " + rfmIsim.getTargetEfBadCase() + "\n"
            );
        }
        else {
            rfmIsimBuffer.append(
                "\n; TAR is configured with access domain\n"
                + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
                + useRfmIsimEfAccessDomain(rfmIsim)
                +".DEFINE %EF_ID_ERR " + rfmIsim.getCustomTargetEfBadCase() + "\n"
            );
        }

        // check Initial Content of EF
        rfmIsimBuffer.append(useRfmIsimCheckInitialContent(rfmIsim));

        // some TAR may be configured with specific keyset or use all available keysets
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimBuffer.append(rfmIsimCase1(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimBuffer.append(rfmIsimCase1(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }
        rfmIsimBuffer.append("\n.UNDEFINE %EF_CONTENT_ADM2\n");
        // perform negative test if not full access
        if (!rfmIsim.isFullAccess()) {
            rfmIsimBuffer.append(
                "\n; perform negative test: updating " + rfmIsim.getCustomTargetEfBadCase() + " Out of Access Domain\n"
                + "\n.POWER_ON\n"
                + "; check initial content of EF\n"
                + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
            );
            if (root.getRunSettings().getSecretCodes().isUseIsc2())
                rfmIsimBuffer.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
            if (root.getRunSettings().getSecretCodes().isUseIsc3())
                rfmIsimBuffer.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
            if (root.getRunSettings().getSecretCodes().isUseIsc4())
                rfmIsimBuffer.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
            rfmIsimBuffer.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                + "A0 A4 00 00 02 %EF_ID_ERR (9FXX)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT R\n"
            );
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase1NegativeTest(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase1NegativeTest(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
                }
            }
            rfmIsimBuffer.append("\n.UNDEFINE %EF_CONTENT\n");
        }
        // case 2
        rfmIsimBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in ISIM TAR\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimBuffer.append(rfmIsimCase2(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimBuffer.append(rfmIsimCase2(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }
        // case 3
        rfmIsimBuffer.append("\n*********\n; CASE 3: (Bad Case) send 2G command to ISIM TAR\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimBuffer.append(rfmIsimCase3(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimBuffer.append(rfmIsimCase3(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }
        // case 4
        rfmIsimBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimBuffer.append(rfmIsimCase4(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimBuffer.append(rfmIsimCase4(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }
        // case 5
        rfmIsimBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
        if (Integer.parseInt(rfmIsim.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
            rfmIsimBuffer.append("\n; MSL: " + rfmIsim.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
        else {
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase5(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase5(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
                }
            }
        }
        // case 6
        rfmIsimBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimBuffer.append(rfmIsimCase6(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimBuffer.append(rfmIsimCase6(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }
        // case 7
        rfmIsimBuffer.append("\n*********\n; CASE 7: (Bad Case) insufficient MSL\n*********\n");
        if (rfmIsim.getMinimumSecurityLevel().getComputedMsl().equals("00"))
            rfmIsimBuffer.append("\n; MSL: " + rfmIsim.getMinimumSecurityLevel().getComputedMsl() + " -- case 7 is not executed\n");
        else {
            MinimumSecurityLevel lowMsl = new MinimumSecurityLevel(false, "No verification", "No counter available");
            lowMsl.setSigningAlgo("no algorithm");
            lowMsl.setCipherAlgo("no cipher");
            lowMsl.setPorRequirement("PoR required");
            lowMsl.setPorSecurity("response with no security");
            lowMsl.setCipherPor(false);
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase7(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), lowMsl));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase7(keyset, keyset, lowMsl));
                }
            }
        }
        // case 8
        rfmIsimBuffer.append("\n*********\n; CASE 8: Bad TP-OA value\n*********\n");
        if (!root.getRunSettings().getSmsUpdate().isUseWhiteList())
            rfmIsimBuffer.append("\n; profile does not have white list configuration -- case 8 is not executed\n");
        else {
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase8(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase8(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
                }
            }
        }
        // save counter
        rfmIsimBuffer.append(
            "\n; save counter state\n"
            + ".EXPORT_BUFFER L COUNTER.bin\n"
        );
        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            rfmIsimBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");
        // unload DLLs
        rfmIsimBuffer.append(
            "\n.UNLOAD Calcul.dll\n"
            + ".UNLOAD OTA2.dll\n"
            + ".UNLOAD Var_Reader.dll\n"
            + "\n.POWER_OFF\n"
        );
        return rfmIsimBuffer;
    }

    public StringBuilder generateRfmIsimUpdateRecord(RfmIsim rfmIsim){
        StringBuilder rfmIsimUpdateRecordBuffer = new StringBuilder();

        // Generate Header Script
        rfmIsimUpdateRecordBuffer.append(this.headerScript(rfmIsim));

        // case 1
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 1: RFM ISIM Update Record with correct security settings\n*********\n");
        // define target files
        if (rfmIsim.isFullAccess()) {
            rfmIsimUpdateRecordBuffer.append(
                "\n; TAR is configured for full access\n"
                + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
                + ".DEFINE %EF_ID " + rfmIsim.getTargetEf() + "\n"
                + ".DEFINE %EF_ID_ERR " + rfmIsim.getTargetEfBadCase() + "\n"
            );
        } else {
            rfmIsimUpdateRecordBuffer.append(
                "\n; TAR is configured with access domain\n"
                + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
                + ".DEFINE %EF_ID " + rfmIsim.getCustomTargetEf() + "; EF protected by " + rfmIsim.getCustomTargetAcc() +  "\n"
                + ".DEFINE %EF_ID_ERR " + rfmIsim.getCustomTargetEfBadCase() + "; (negative test) EF protected by " + rfmIsim.getCustomTargetAccBadCase() +  "\n"
            );
        }
        rfmIsimUpdateRecordBuffer.append(
                "\n.POWER_ON\n"
                + "; check initial content of EF\n"
                + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            rfmIsimUpdateRecordBuffer.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            rfmIsimUpdateRecordBuffer.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            rfmIsimUpdateRecordBuffer.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        rfmIsimUpdateRecordBuffer.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT R\n"
        );
        // some TAR may be configured with specific keyset or use all available keysets
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase1(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase1(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }
        rfmIsimUpdateRecordBuffer.append("\n.UNDEFINE %EF_CONTENT\n");

        // case 2
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in ISIM TAR\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase2(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase2(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }

        // case 3
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 3: (Bad Case) send 2G command to ISIM TAR\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase3(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase3(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }

        // case 4
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase4(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase4(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }

        // case 5
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
        if (Integer.parseInt(rfmIsim.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
            rfmIsimUpdateRecordBuffer.append("\n; MSL: " + rfmIsim.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
        else {
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase5(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase5(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
                }
            }
        }

        // case 6
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
        if (rfmIsim.isUseSpecificKeyset())
            rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase6(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase6(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
            }
        }

        // case 7
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 7: (Bad Case) insufficient MSL\n*********\n");
        if (rfmIsim.getMinimumSecurityLevel().getComputedMsl().equals("00"))
            rfmIsimUpdateRecordBuffer.append("\n; MSL: " + rfmIsim.getMinimumSecurityLevel().getComputedMsl() + " -- case 7 is not executed\n");
        else {
            MinimumSecurityLevel lowMsl = new MinimumSecurityLevel(false, "No verification", "No counter available");
            lowMsl.setSigningAlgo("no algorithm");
            lowMsl.setCipherAlgo("no cipher");
            lowMsl.setPorRequirement("PoR required");
            lowMsl.setPorSecurity("response with no security");
            lowMsl.setCipherPor(false);
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase7(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), lowMsl));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimUpdateRecordBuffer.append(rfmIsimUpdateRecordCase7(keyset, keyset, lowMsl));
                }
            }
        }

        // save counter
        rfmIsimUpdateRecordBuffer.append(
                "\n; save counter state\n"
                + ".EXPORT_BUFFER L COUNTER.bin\n"
        );

        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            rfmIsimUpdateRecordBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");
        // unload DLLs
        rfmIsimUpdateRecordBuffer.append(
                "\n.UNLOAD Calcul.dll\n"
                + ".UNLOAD OTA2.dll\n"
                + ".UNLOAD Var_Reader.dll\n"
                + "\n.POWER_OFF\n"
        );

        return rfmIsimUpdateRecordBuffer;
    }

    public StringBuilder generateRfmIsimExpandedMode(RfmIsim rfmIsim) {
        StringBuilder rfmIsimExpandedModeBuffer = new StringBuilder();
        // TODO
        return rfmIsimExpandedModeBuffer;
    }

    private String rfmIsimCase1(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
            + useRfmIsimCommandViaOtaAccessDomain(root.getRunSettings().getRfmIsim())
            + ".END_MESSAGE G J\n"
            + "; show OTA message details\n"
            + ".DISPLAY_MESSAGE J\n"
            + "; send envelope\n"
            + "A0 C2 00 00 G J (9FXX)\n"
            + ".CLEAR_SCRIPT\n"
            + "; check PoR\n"
            + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 90 00] (9000) ; PoR OK\n"
        );

        //check update has been done on EF
        routine.append(this.useRfmIsimCheckUpdateEfDone());

        //restore initial content of EF
        routine.append(this.useRfmIsimRestoreRfmIsimInitialContent());

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );


        return routine.toString();
    }

    private String rfmIsimUpdateRecordCase1(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                + proactiveInitialization()
                + "\n; SPI settings\n"
                + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
                + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                + ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormat(root.getRunSettings().getSmsUpdate().getPorFormat()) + "\n"
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
                + ".SET_BUFFER J 00 A4 00 00 02 %EF_ID ; select EF\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
                + ".APPEND_SCRIPT J\n"
                + ".END_MESSAGE G J\n"
                + "; show OTA message details\n"
                + ".DISPLAY_MESSAGE J\n"
                + updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase1") // call updateSMSRecordRfmIsim per-cases
                + "\n; check update has been done on EF\n"
                + ".POWER_ON\n"
                + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            routine.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            routine.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            routine.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        routine.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
                + "A0 B0 00 00 01 [AA] (9000)\n"
                + "\n; restore initial content of EF\n"
                + ".POWER_ON\n"
                + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            routine.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            routine.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            routine.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        routine.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT (9000)\n"
                + "\n; increment counter by one\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase1NegativeTest(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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
            + ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ERR\n"
            + ".APPEND_SCRIPT J\n"
            + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
            + ".APPEND_SCRIPT J\n"
            + ".END_MESSAGE G J\n"
            + "; show OTA message details\n"
            + ".DISPLAY_MESSAGE J\n"
            + "; send envelope\n"
            + "A0 C2 00 00 G J (9FXX)\n"
            + ".CLEAR_SCRIPT\n"
            + "; check PoR\n"
            + "A0 C0 00 00 W(2;1) [XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 69 82] (9000) ; PoR OK, but failed to update\n"
            + "\n; check update has failed\n"
            + ".POWER_ON\n"
            + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            routine.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            routine.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            routine.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        routine.append(
            "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9F22)\n"
            + "A0 A4 00 00 02 %EF_ID_ERR (9F0F)\n"
            + "A0 B0 00 00 01 [%EF_CONTENT] (9000)\n"
            + "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase2(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String rfmIsimUpdateRecordCase2(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                + proactiveInitialization()
                + "\n; SPI settings\n"
                + ".SET_BUFFER O " + createFakeCipherKey(cipherKeyset) + " ; bad key\n"
                + ".SET_BUFFER Q " + createFakeAuthKey(authKeyset) + " ; bad key\n"
                + ".SET_BUFFER M 99 ; bad keyset\n"
                + ".SET_BUFFER N 99 ; bad keyset\n"
                + ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormat(root.getRunSettings().getSmsUpdate().getPorFormat()) + "\n"
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
                + updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase2")
                + "\n; increment counter by one\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase3(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String rfmIsimUpdateRecordCase3(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                + proactiveInitialization()
                + "\n; SPI settings\n"
                + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
                + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                + ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormat(root.getRunSettings().getSmsUpdate().getPorFormat()) +"\n"
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
                + updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase3")
                + "\n; increment counter by one\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase4(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String rfmIsimUpdateRecordCase4(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                + proactiveInitialization()
                + "\n; SPI settings\n"
                + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
                + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                + ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormat(root.getRunSettings().getSmsUpdate().getPorFormat()) +"\n"
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
                + "\n; update EF SMS record\n" // Case 4 (Bad Case) use unknown TAR, code manually
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000)\n"
                + "A0 A4 00 00 02 7F10 (9F22) ;select DF Telecom\n"
                + "A0 A4 00 00 02 6F3C (9F0F) ;select EF SMS\n"
                + "A0 DC 01 04 G J (9000) ;update EF SMS\n"
                + ".CLEAR_SCRIPT\n"
                + "\n;Check SMS Content\n"
                + "A0 B2 01 04 B0\n"
                + "\n; increment counter by one\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase5(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String rfmIsimUpdateRecordCase5(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                + proactiveInitialization()
                + "\n; SPI settings\n"
                + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
                + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                + ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormat(root.getRunSettings().getSmsUpdate().getPorFormat()) +"\n"
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
                + updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase5")
                + "\n; increment counter by one\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase6(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String rfmIsimUpdateRecordCase6(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                + proactiveInitialization()
                + "\n; SPI settings\n"
                + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                + ".SET_BUFFER Q " + createFakeAuthKey(authKeyset) + " ; bad authentication key\n"
                + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                + ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormat(root.getRunSettings().getSmsUpdate().getPorFormat()) +"\n"
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
                + updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase6")
                + "\n; increment counter by one\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase7(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String rfmIsimUpdateRecordCase7(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                + proactiveInitialization()
                + "\n; SPI settings\n"
                + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
                + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                + ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormat(root.getRunSettings().getSmsUpdate().getPorFormat()) +"\n"
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
                + updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase7")
                + "\n; increment counter by one\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase8(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String headerScript(RfmIsim rfmIsim){
        StringBuilder headerScript = new StringBuilder();
        // call mappings and load DLLs
        headerScript.append(
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
            headerScript.append(
                "\n; initialize counter\n"
                + ".SET_BUFFER L 00 00 00 00 00\n"
                + ".EXPORT_BUFFER L COUNTER.bin\n"
            );
        }
        // load anti-replay counter
        headerScript.append(
                "\n; buffer L contains the anti-replay counter for OTA message\n"
                + ".SET_BUFFER L\n"
                + ".IMPORT_BUFFER L COUNTER.bin\n"
                + ".INCREASE_BUFFER L(04:05) 0001\n"
                + ".DISPLAY L\n"
                + "\n; setup TAR\n"
                + ".DEFINE %TAR " + rfmIsim.getTar() + "\n"
        );
        // enable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            headerScript.append("\nA0 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n");

        return headerScript.toString();
    }

    private String updateSMSRecordRfmIsim(String rfmIsimCases) {
        StringBuilder updateSMSRecordRfmIsim = new StringBuilder();

        updateSMSRecordRfmIsim.append(
                "\n; update EF SMS record\n"
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000)\n"
                + "A0 A4 00 00 02 7F10 (9F22) ;select DF Telecom\n"
                + "A0 A4 00 00 02 6F3C (9F0F) ;select EF SMS\n"
                + "A0 DC 01 04 G J (91XX) ;update EF SMS\n"
                + ".CLEAR_SCRIPT\n"
                + "\n;Check SMS Content\n" // CHECK_SMS_CONTENT
                + "A0 B2 01 04 B0\n" // READ_SMS
                + "; check PoR\n"
                + "A0 12 00 00 W(2;1) [" + this.otaPorSetting(rfmIsimCases) + " ] (9000)\n"
                + "A0 14 00 00 0C 8103011300 82028183 830100 (9000)\n"
        );

        return updateSMSRecordRfmIsim.toString();
    }

    private String otaPorSetting(String rfmIsimCases){
        String  POR_OK, POR_NOT_OK, BAD_CASE_WRONG_KEYSET,
                BAD_CASE_WRONG_CLASS_3G, BAD_CASE_WRONG_CLASS_2G,
                 BAD_CASE_COUNTER_LOW,
                BAD_CASE_WRONG_KEY_VALUE, BAD_CASE_INSUFFICIENT_MSL; //BAD_CASE_WRONG_TAR;

        String result_set = "";

        String scAddress            = root.getRunSettings().getSmsUpdate().getScAddress();
        String tag33UpdateRecord    = "D0 33 81 XX XX 13 XX 82 02 81 83 85 XX 86 " + scAddress + " 8B XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX %TAR XX XX XX XX XX XX";
        String tag30UpdateRecord    = "D0 30 81 XX XX 13 XX 82 02 81 83 85 XX 86 " + scAddress + " 8B XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX %TAR XX XX XX XX XX XX";

        POR_OK                      = tag33UpdateRecord+" XX XX 90 00";
        POR_NOT_OK                  = tag33UpdateRecord+" XX XX 98 04";
        BAD_CASE_WRONG_KEYSET       = tag30UpdateRecord+" 06";
        BAD_CASE_WRONG_CLASS_3G     = tag33UpdateRecord+" XX XX 6E 00";
        BAD_CASE_WRONG_CLASS_2G     = tag33UpdateRecord+" XX XX 6D 00";
        BAD_CASE_COUNTER_LOW        = tag30UpdateRecord+" 02";
        BAD_CASE_WRONG_KEY_VALUE    = tag30UpdateRecord+" 01";
        BAD_CASE_INSUFFICIENT_MSL   = tag30UpdateRecord+" 0A";
        //BAD_CASE_WRONG_TAR          = "D0 30 81 XX XX 13 XX 82 02 81 83 85 XX 86 "+scAddress+" 8B XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX B0 FF FF XX XX XX XX XX XX 09";

        switch (rfmIsimCases){
            case "rfmIsimUpdateRecordCase1" :
                result_set = POR_OK;
                break;
            case "rfmIsimUpdateRecordCase2" :
                result_set = BAD_CASE_WRONG_KEYSET;
                break;
            case "rfmIsimUpdateRecordCase3" :
                result_set =  BAD_CASE_WRONG_CLASS_3G;
                break;
            case "rfmIsimUpdateRecordCase5" :
                result_set = BAD_CASE_COUNTER_LOW;
                break;
            case "rfmIsimUpdateRecordCase6" :
                result_set = BAD_CASE_WRONG_KEY_VALUE;
                break;
            case "rfmIsimUpdateRecordCase7" :
                result_set = BAD_CASE_INSUFFICIENT_MSL;
                break;
        }

        return result_set;

    }

    private String porFormat(String porformat){
        String result_set = "";

        switch (porformat) {
            case "PoR as SMS-SUBMIT":
                result_set = "01";
                break;
            case "PoR as SMS-DELIVER-REPORT":
                result_set = "00";
                break;
        }

        return result_set;
    }

    // Separate some looped on method()
    private String useRfmIsimEfAccessDomain(RfmIsim rfmIsim){

        StringBuilder accessDomain = new StringBuilder();

        if (rfmIsim.getAccessDomain().isUseIsc1()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM1 " + rfmIsim.getCustomTargetEfIsc1() + "; EF protected by " + root.getRunSettings().getSecretCodes().getIsc1() +  "\n");
        }
        if (rfmIsim.getAccessDomain().isUseIsc2()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM2 " + rfmIsim.getCustomTargetEfIsc2() + "; EF protected by ADM2" + "\n");
        }
        if (rfmIsim.getAccessDomain().isUseIsc3()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM3 " + rfmIsim.getCustomTargetEfIsc3() + "; EF protected by ADM3\n");
        }
        if (rfmIsim.getAccessDomain().isUseIsc4()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM4 " + rfmIsim.getCustomTargetEfIsc4() + "; EF protected by ADM4\n");
        }
        if (rfmIsim.getAccessDomain().isUseGPin1()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_PIN1 " + rfmIsim.getCustomTargetEfGPin1() + "; EF protected by " + root.getRunSettings().getSecretCodes().getGpin() +  "\n");
        }
        if (rfmIsim.getAccessDomain().isUseLPin1()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_PIN2 " + rfmIsim.getCustomTargetEfLPin1() + "; EF protected by " + root.getRunSettings().getSecretCodes().getLpin() +  "\n");
        }

        return accessDomain.toString();
    }

    private String useRfmIsimCheckInitialContent(RfmIsim rfmIsim){

        StringBuilder specificCheckInitialContent = new StringBuilder();

        if(!rfmIsim.isFullAccess()){
            if (rfmIsim.getAccessDomain().isUseIsc1()){
                specificCheckInitialContent.append(this.checkInitialContentOnIsc1());
            }
            if (rfmIsim.getAccessDomain().isUseIsc2()){
                specificCheckInitialContent.append(this.checkInitialContentOnIsc2());
            }
            if (rfmIsim.getAccessDomain().isUseIsc3()){
                specificCheckInitialContent.append(this.checkInitialContentOnIsc3());
            }
            if (rfmIsim.getAccessDomain().isUseIsc4()){
                specificCheckInitialContent.append(this.checkInitialContentOnIsc4());
            }
            if (rfmIsim.getAccessDomain().isUseGPin1()){
                specificCheckInitialContent.append(this.checkInitialContentOnGPin1());
            }
            if (rfmIsim.getAccessDomain().isUseLPin1()){
                specificCheckInitialContent.append(this.checkInitialContentOnLPin1());
            }
        }
        else{
            specificCheckInitialContent.append(this.checkInitialContent());
        }

        return specificCheckInitialContent.toString();
    }

    private String useRfmIsimCommandViaOtaAccessDomain(RfmIsim rfmIsim){

        StringBuilder commandOta = new StringBuilder();

        if (!rfmIsim.isFullAccess()){
            if (rfmIsim.getAccessDomain().isUseIsc1()){
                commandOta.append(this.commandOtaIsc1());
            }
            if (rfmIsim.getAccessDomain().isUseIsc2()){
                commandOta.append(this.commandOtaIsc2());
            }
            if (rfmIsim.getAccessDomain().isUseIsc3()){
                commandOta.append(this.commandOtaIsc3());
            }
            if (rfmIsim.getAccessDomain().isUseIsc4()){
                commandOta.append(this.commandOtaIsc4());
            }
            if (rfmIsim.getAccessDomain().isUseGPin1()){
                commandOta.append(this.commandOtaGPin1());
            }
            if (rfmIsim.getAccessDomain().isUseLPin1()){
                commandOta.append(this.commandOtaLpin1());
            }
        }
        else{
            commandOta.append(this.commandOta());
        }

        return commandOta.toString();
    }

    private String useRfmIsimCheckUpdateEfDone(){

        StringBuilder checkUpdateHasBeenDone = new StringBuilder();

        checkUpdateHasBeenDone.append(
                "\n; check update has been done on EF on ADM2\n"
                        + ".POWER_ON\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkUpdateHasBeenDone.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkUpdateHasBeenDone.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkUpdateHasBeenDone.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkUpdateHasBeenDone.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_ADM2 (9F0F)\n"
                        + "A0 B0 00 00 01 [AA] (9000)\n"
        );

        return checkUpdateHasBeenDone.toString();
    }

    private String useRfmIsimRestoreRfmIsimInitialContent(){

        StringBuilder restoreInitialContent = new StringBuilder();

        restoreInitialContent.append(
                "\n; restore initial content of EF on ADM2\n"
                        + ".POWER_ON\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            restoreInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            restoreInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            restoreInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        restoreInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_ADM2 (9F0F)\n"
                        + "A0 D6 00 00 01 %EF_CONTENT_ADM2 (9000)\n"
        );

        return restoreInitialContent.toString();
    }

    private String checkInitialContent(){
        StringBuilder checkInitialContent = new StringBuilder();
        checkInitialContent.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT R\n"
        );

        return checkInitialContent.toString();
    }
    private String checkInitialContentOnIsc1(){
        StringBuilder checkInitialContent = new StringBuilder();
        checkInitialContent.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF on ADM1\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_ADM1 (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT_ADM1 R\n"
        );

        return checkInitialContent.toString();
    }
    private String checkInitialContentOnIsc2(){
        StringBuilder checkInitialContent = new StringBuilder();
        checkInitialContent.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF on ADM2\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_ADM2 (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT_ADM2 R\n"
        );

        return checkInitialContent.toString();
    }
    private String checkInitialContentOnIsc3(){
        StringBuilder checkInitialContent = new StringBuilder();
        checkInitialContent.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF on ADM3\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_ADM3 (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT_ADM3 R\n"
        );

        return checkInitialContent.toString();
    }
    private String checkInitialContentOnIsc4(){
        StringBuilder checkInitialContent = new StringBuilder();
        checkInitialContent.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF on ADM4\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_ADM4 (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT_ADM4 R\n"
        );

        return checkInitialContent.toString();
    }
    private String checkInitialContentOnGPin1(){
        StringBuilder checkInitialContent = new StringBuilder();
        checkInitialContent.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF on PIN1\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_PIN1 (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT_PIN1 R\n"
        );

        return checkInitialContent.toString();
    }
    private String checkInitialContentOnLPin1(){
        StringBuilder checkInitialContent = new StringBuilder();
        checkInitialContent.append(
                "\n.POWER_ON\n"
                        + "; check initial content of EF on PIN2\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                        + "A0 A4 00 00 02 %EF_ID_ISIM_PIN2 (9F0F)\n"
                        + "A0 B0 00 00 01 (9000)\n"
                        + ".DEFINE %EF_CONTENT_PIN2 R\n"
        );

        return checkInitialContent.toString();
    }

    private String commandOta(){
        StringBuilder commandOta = new StringBuilder();
        commandOta.append(
            ".SET_BUFFER J 00 A4 00 00 02 %EF_ID ; select EF\n"
            + ".APPEND_SCRIPT J\n"
            + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
            + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }
    private String commandOtaIsc1(){
        StringBuilder commandOta = new StringBuilder();
        commandOta.append(
            ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ADM1 ; select EF on ADM1\n"
            + ".APPEND_SCRIPT J\n"
            + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
            + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }
    private String commandOtaIsc2(){
        StringBuilder commandOta = new StringBuilder();
        commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_ADM2 ; select EF on ADM2\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
                        + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }
    private String commandOtaIsc3(){
        StringBuilder commandOta = new StringBuilder();
        commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ADM3 ; select EF on ADM3\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
                        + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }
    private String commandOtaIsc4(){
        StringBuilder commandOta = new StringBuilder();
        commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ADM4 ; select EF on ADM4\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
                        + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }
    private String commandOtaGPin1(){
        StringBuilder commandOta = new StringBuilder();
        commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_PIN1 ; select EF on PIN1\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
                        + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }
    private String commandOtaLpin1(){
        StringBuilder commandOta = new StringBuilder();
        commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_PIN2 ; select EF on PIN2\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
                        + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }

//end of script
}

