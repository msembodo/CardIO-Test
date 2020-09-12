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

    private String headerScriptRfmIsim(RfmIsim rfmIsim){
        StringBuilder headerScript = new StringBuilder();
        // call mappings and load DLLs

        headerScript.append(
            ";==================================================\n" +
            ";================!!! CAUTION !!!===================\n" +
            ";==================================================\n\n" +
            "*Please check the last byte for the POR\n" +
            "*'00' PoR OK.\n" +
            "*'01' RC/CC/DS failed.\n" +
            "*'02' CNTR low.\n" +
            "*'03' CNTR high.\n" +
            "*'04' CNTR Blocked\n" +
            "*'05' Ciphering error.\n" +
            "*'06' Unidentified security error. This code is for the case where the Receiving Entity cannot correctly\n" +
            "*interpret the Command Header and the Response Packet is sent unciphered with no RC/CC/DS.\n" +
            "*'07' Insufficient memory to process incoming message.\n" +
            "*'08' This status code \"more time\" should be used if the Receiving Entity/Application needs more time\n" +
            "*to process the Command Packet due to timing constraints. In this case a later Response Packet\n" +
            "*should be returned to the Sending Entity once processing has been completed.\n" +
            "*'09' TAR Unknown\n" +
            "*'0A' Insufficient security level\n\n" +
            ";=======================\n\n"
        );

        headerScript.append("; ---------------------------------------- RFM ISIM ------------------------------------------------------\n\n");

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

    public StringBuilder generateRfmIsim(RfmIsim rfmIsim) {
        StringBuilder rfmIsimBuffer = new StringBuilder();

        // Generate Header Script
        rfmIsimBuffer.append(this.headerScriptRfmIsim(rfmIsim));

        // case 1
        rfmIsimBuffer.append("\n*********\n; CASE 1: RFM ISIM with correct security settings\n*********\n");

            // Target Files and Check Initial Content EFs isFullAccess
            if(rfmIsim.isFullAccess()){
                rfmIsimBuffer.append(this.rfmIsimDefineTargetFiles(rfmIsim)); // define Target Files
                rfmIsimBuffer.append(this.rfmIsimCheckInitialContentEf()); // check Initial Content of EF
            }
            else{
                rfmIsimBuffer.append(this.useRfmIsimDefineTargetFilesAccessDomain(rfmIsim));  // define Target Files Access Domain
                rfmIsimBuffer.append(this.useRfmIsimCheckInitialContentEfAccessDomain(rfmIsim)); // check Initial Content of EF Access Domain
            }

            // some TAR may be configured with specific keyset or use all available keysets
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase1(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase1(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), false));
                }
            }

            // perform negative test if not full access
            rfmIsimBuffer.append(this.usePerformNegativeTest(rfmIsim));

        //end of case 1

        // case 2
        rfmIsimBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in ISIM TAR\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase2(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase2(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 2

        // case 3
        rfmIsimBuffer.append("\n*********\n; CASE 3: (Bad Case) send 2G command to ISIM TAR\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase3(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase3(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 3

        // case 4
        rfmIsimBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase4(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase4(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 4

        // case 5
        rfmIsimBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
            if (Integer.parseInt(rfmIsim.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
                rfmIsimBuffer.append("\n; MSL: " + rfmIsim.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
            else {
                if (rfmIsim.isUseSpecificKeyset())
                    rfmIsimBuffer.append(rfmIsimCase5(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), false));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmIsimBuffer.append(rfmIsimCase5(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), false));
                    }
                }
            }
        //end of case 5

        // case 6
        rfmIsimBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimBuffer.append(rfmIsimCase6(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimBuffer.append(rfmIsimCase6(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 6

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
                    rfmIsimBuffer.append(rfmIsimCase7(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), lowMsl, false));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmIsimBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmIsimBuffer.append(rfmIsimCase7(keyset, keyset, lowMsl, false));
                    }
                }
            }
        //end of case 7

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
        //end of case 8

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
        rfmIsimUpdateRecordBuffer.append(this.headerScriptRfmIsim(rfmIsim));

        // case 1
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 1: RFM ISIM Update Record with correct security settings\n*********\n");

            // Target Files and Check Initial Content EFs isFullAccess
            if(rfmIsim.isFullAccess()){
                rfmIsimUpdateRecordBuffer.append(this.rfmIsimDefineTargetFiles(rfmIsim)); // define Target Files
                rfmIsimUpdateRecordBuffer.append(this.rfmIsimCheckInitialContentEf()); // check Initial Content of EF
            }
            else{
                rfmIsimUpdateRecordBuffer.append(this.useRfmIsimDefineTargetFilesAccessDomain(rfmIsim));  // define Target Files Access Domain
                rfmIsimUpdateRecordBuffer.append(this.useRfmIsimCheckInitialContentEfAccessDomain(rfmIsim)); // check Initial Content of EF Access Domain
            }

            // some TAR may be configured with specific keyset or use all available keysets
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimUpdateRecordBuffer.append(rfmIsimCase1(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimUpdateRecordBuffer.append(rfmIsimCase1(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), true));
                }
            }

        //end of case 1

        // case 2
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in ISIM TAR\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimUpdateRecordBuffer.append(rfmIsimCase2(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimUpdateRecordBuffer.append(rfmIsimCase2(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 2

        // case 3
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 3: (Bad Case) send 2G command to ISIM TAR\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimUpdateRecordBuffer.append(rfmIsimCase3(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimUpdateRecordBuffer.append(rfmIsimCase3(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 3

        // case 4
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimUpdateRecordBuffer.append(rfmIsimCase4(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimUpdateRecordBuffer.append(rfmIsimCase4(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 4

        // case 5
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
            if (Integer.parseInt(rfmIsim.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
                rfmIsimUpdateRecordBuffer.append("\n; MSL: " + rfmIsim.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
            else {
                if (rfmIsim.isUseSpecificKeyset())
                    rfmIsimUpdateRecordBuffer.append(rfmIsimCase5(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), true));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmIsimUpdateRecordBuffer.append(rfmIsimCase5(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), true));
                    }
                }
            }
        //end of case 5

        // case 6
        rfmIsimUpdateRecordBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
            if (rfmIsim.isUseSpecificKeyset())
                rfmIsimUpdateRecordBuffer.append(rfmIsimCase6(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmIsimUpdateRecordBuffer.append(rfmIsimCase6(keyset, keyset, rfmIsim.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 6

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
                    rfmIsimUpdateRecordBuffer.append(rfmIsimCase7(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), lowMsl, true));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmIsimUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmIsimUpdateRecordBuffer.append(rfmIsimCase7(keyset, keyset, lowMsl, true));
                    }
                }
            }
        //end of case 7

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

    private String rfmIsimCase1(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmIsim());
        }
        else {
            routine.append(this.init_SMS_0348RfmIsim());
        }

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

            // Send OTA Command isFullAccess
            if(root.getRunSettings().getRfmIsim().isFullAccess()){
                routine.append(this.rfmIsimCommandViaOta());  // command(s) sent via OTA
            }
            else{
                routine.append(this.useRfmIsimCommandViaOtaAccessDomain(root.getRunSettings().getRfmIsim())); // command(s) sent via OTA Access Domain
            }

            routine.append(
                ".END_MESSAGE G J\n"
                + "; show OTA message details\n"
                + ".DISPLAY_MESSAGE J\n"
            );

            // check isUpdateRecord
            if(!isUpdateRecord){
                routine.append(this.sendEnvelopeRfmIsim("rfmIsimCase1"));
            }
            else {
                routine.append(this.updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase1"));
            }

        // Check Update EF and Restore EF isFullAccess
        if (root.getRunSettings().getRfmIsim().isFullAccess()){
            routine.append(this.rfmIsimCheckUpdateEfDone()); //check update has been done on EF
            routine.append(this.rfmIsimRestoreRfmIsimInitialContentEf()); //restore initial content of EF
        }
        else {
            routine.append(this.useRfmIsimCheckUpdateEfDoneAccessDomain(root.getRunSettings().getRfmIsim())); //check update has been done on EF Access Domain
            routine.append(this.useRfmIsimRestoreRfmIsimInitialContentEfAccessDomain(root.getRunSettings().getRfmIsim())); //restore initial content of EF Access Domain
        }

        routine.append(
            "\n; increment counter by one\n"
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
            + "A0 B0 00 00 01 [%EF_CONTENT_ERR] (9000)\n"
            + "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );
        return routine.toString();
    }

    private String rfmIsimCase2(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O " + createFakeCipherKey(cipherKeyset) + " ; bad key\n"
            + ".SET_BUFFER Q " + createFakeAuthKey(authKeyset) + " ; bad key\n"
            + ".SET_BUFFER M 99 ; bad keyset\n"
            + ".SET_BUFFER N 99 ; bad keyset\n"
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmIsim());
        }
        else {
            routine.append(this.init_SMS_0348RfmIsim());
        }

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
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmIsim("rfmIsimCase2"));
        }
        else {
            routine.append(this.updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase2"));
        }

        routine.append(
             "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmIsimCase3(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmIsim());
        }
        else {
            routine.append(this.init_SMS_0348RfmIsim());
        }

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
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmIsim("rfmIsimCase3"));
        }
        else {
            routine.append(this.updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase3"));
        }

        routine.append(
             "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmIsimCase4(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmIsim());
        }
        else {
            routine.append(this.init_SMS_0348RfmIsim());
        }

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
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmIsim("rfmIsimCase4"));
        }
        else {
            routine.append(
                 "\n; update EF SMS record\n" // Case 4 (Bad Case) use unknown TAR, code manually
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000)\n"
                + "A0 A4 00 00 02 7F10 (9F22) ;select DF Telecom\n"
                + "A0 A4 00 00 02 6F3C (9F0F) ;select EF SMS\n"
                + "A0 DC 01 04 G J (9000) ;update EF SMS\n"
                + ".CLEAR_SCRIPT\n"
                + "\n;Check SMS Content\n"
                + "A0 B2 01 04 B0\n"
            );
        }

        routine.append(
             "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmIsimCase5(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmIsim());
        }
        else {
            routine.append(this.init_SMS_0348RfmIsim());
        }

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
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmIsim("rfmIsimCase5"));
        }
        else {
            routine.append(this.updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase5"));
        }

        routine.append(
             "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );


        return routine.toString();
    }

    private String rfmIsimCase6(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q " + createFakeAuthKey(authKeyset) + " ; bad authentication key\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmIsim());
        }
        else {
            routine.append(this.init_SMS_0348RfmIsim());
        }

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
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmIsim("rfmIsimCase6"));
        }
        else {
            routine.append(this.updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase6"));
        }

        routine.append(
             "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmIsimCase7(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "\n.POWER_ON\n"
            + proactiveInitialization()
            + "\n; SPI settings\n"
            + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
            + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
            + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
            + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmIsim());
        }
        else {
            routine.append(this.init_SMS_0348RfmIsim());
        }

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
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmIsim("rfmIsimCase7"));
        }
        else {
            routine.append(this.updateSMSRecordRfmIsim("rfmIsimUpdateRecordCase7"));
        }

        routine.append(
             "\n; increment counter by one\n"
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

    private String init_SMS_0348RfmIsim(){

        StringBuilder routine = new StringBuilder();

        routine.append(
                ".INIT_SMS_0348\n"
                        + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                        + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                        + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                        + ".CHANGE_POR_FORMAT " + this.porFormatRfmIsim(root.getRunSettings().getSmsUpdate().getPorFormat()) + "\n"
        );

        return routine.toString();
    }

    private String init_ENV_0348RfmIsim(){

        StringBuilder routine = new StringBuilder();

        routine.append(
                ".INIT_ENV_0348\n"
                        + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
        );

        return routine.toString();
    }

    private String sendEnvelopeRfmIsim(String rfmIsimCases){

        StringBuilder routine = new StringBuilder();

        routine.append(
            "; send envelope\n"
            + "A0 C2 00 00 G J (9FXX)\n"
            + ".CLEAR_SCRIPT\n"
            + "; check PoR\n"
            + "A0 C0 00 00 W(2;1) [" + this.otaPorSettingRfmIsim(rfmIsimCases)+ "] (9000) ; PoR OK\n"
        );

        return routine.toString();
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
                + "A0 12 00 00 W(2;1) [" + this.otaPorSettingRfmIsimUpdateRecord(rfmIsimCases) + " ] (9000)\n"
                + "A0 14 00 00 0C 8103011300 82028183 830100 (9000)\n"
        );

        return updateSMSRecordRfmIsim.toString();
    }

    private String otaPorSettingRfmIsim(String rfmIsimCases){
        String  POR_OK, POR_NOT_OK, BAD_CASE_WRONG_KEYSET,
                BAD_CASE_WRONG_CLASS_3G, BAD_CASE_WRONG_CLASS_2G,
                 BAD_CASE_COUNTER_LOW,
                BAD_CASE_WRONG_KEY_VALUE, BAD_CASE_INSUFFICIENT_MSL, BAD_CASE_WRONG_TAR;

        String result_set = "";

        POR_OK                      = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 90 00";
        POR_NOT_OK                  = "";
        BAD_CASE_WRONG_KEYSET       = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 06";
        BAD_CASE_WRONG_CLASS_3G     = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 6E 00";
        BAD_CASE_WRONG_CLASS_2G     = "";
        BAD_CASE_COUNTER_LOW        = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 02";
        BAD_CASE_WRONG_KEY_VALUE    = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 01";
        BAD_CASE_INSUFFICIENT_MSL   = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 0A";
        BAD_CASE_WRONG_TAR          = "XX XX XX XX XX XX B0 FF FF XX XX XX XX XX XX 09";

        switch (rfmIsimCases){
            case "rfmIsimCase1" :
                result_set = POR_OK;
                break;
            case "rfmIsimCase2" :
                result_set = BAD_CASE_WRONG_KEYSET;
                break;
            case "rfmIsimCase3" :
                result_set =  BAD_CASE_WRONG_CLASS_3G;
                break;
            case "rfmIsimCase4" :
                result_set =  BAD_CASE_WRONG_TAR;
                break;
            case "rfmIsimCase5" :
                result_set = BAD_CASE_COUNTER_LOW;
                break;
            case "rfmIsimCase6" :
                result_set = BAD_CASE_WRONG_KEY_VALUE;
                break;
            case "rfmIsimCase7" :
                result_set = BAD_CASE_INSUFFICIENT_MSL;
                break;
        }

        return result_set;

    }

    private String otaPorSettingRfmIsimUpdateRecord(String rfmIsimCases){
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

    private String porFormatRfmIsim(String porformat){
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

        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc1()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM1 " + rfmIsim.getCustomTargetEfIsc1() + "; EF protected by ADM1\n");
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc2()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM2 " + rfmIsim.getCustomTargetEfIsc2() + "; EF protected by ADM2\n");
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc3()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM3 " + rfmIsim.getCustomTargetEfIsc3() + "; EF protected by ADM3\n");
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc4()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_ADM4 " + rfmIsim.getCustomTargetEfIsc4() + "; EF protected by ADM4\n");
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseGPin1()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_PIN1 " + rfmIsim.getCustomTargetEfGPin1() + "; EF protected by PIN1\n");
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseLPin1()){
            accessDomain.append(".DEFINE %EF_ID_ISIM_PIN2 " + rfmIsim.getCustomTargetEfLPin1() + "; EF protected by PIN2\n");
        }

        return accessDomain.toString();
    }

    private String rfmIsimDefineTargetFiles(RfmIsim rfmIsim){

        StringBuilder targetFiles = new StringBuilder();

        targetFiles.append(
            "\n; TAR is configured for full access\n"
            + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
            + ".DEFINE %EF_ID " + rfmIsim.getTargetEf() + "\n"
            + ".DEFINE %EF_ID_ERR " + rfmIsim.getTargetEfBadCase() + "\n"
        );

        return targetFiles.toString();
    }

    private String useRfmIsimDefineTargetFilesAccessDomain(RfmIsim rfmIsim){

        StringBuilder targetFiles = new StringBuilder();

        targetFiles.append(
            "\n; TAR is configured with access domain\n"
            + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfIsim() + "\n"
            + (this.useRfmIsimEfAccessDomain(rfmIsim))
            +".DEFINE %EF_ID_ERR " + rfmIsim.getCustomTargetEfBadCase() + "\n"
        );

        return targetFiles.toString();
    }

    private String rfmIsimCheckInitialContentEf(){

        StringBuilder checkInitialContent = new StringBuilder();

        checkInitialContent.append(
            "\n.POWER_ON\n"
            + "; check initial content\n"
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
                +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT R\n"
        );

        return checkInitialContent.toString();

    }

    private String useRfmIsimCheckInitialContentEfAccessDomain(RfmIsim rfmIsim){

        StringBuilder checkInitialContent = new StringBuilder();

        checkInitialContent.append(
            "\n.POWER_ON\n"
            + "; check initial content\n"
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
        );

        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc1()){
            checkInitialContent.append(
                "; check content EF-" + rfmIsim.getCustomTargetEfIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM1 (9F0F)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT_ADM1 R\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc2()){
            checkInitialContent.append(
                "; check content EF-" + rfmIsim.getCustomTargetEfIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM2 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ADM2 R\n"

            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc3()){
            checkInitialContent.append(
                "; check content EF-" + rfmIsim.getCustomTargetEfIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM3 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ADM3 R\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc4()){
            checkInitialContent.append(
                "; check content EF-" + rfmIsim.getCustomTargetEfIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM4 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ADM4 R\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseGPin1()){
            checkInitialContent.append(
                "; check content EF-" + rfmIsim.getCustomTargetEfGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_PIN1 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_PIN1 R\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseLPin1()){
            checkInitialContent.append(
                "; check content EF-" + rfmIsim.getCustomTargetEfLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_PIN2 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_PIN2 R\n"

            );
        }

        return checkInitialContent.toString();
    }

    private String rfmIsimCommandViaOta(){

        StringBuilder commandOta = new StringBuilder();

        commandOta.append("\n; command(s) sent via OTA\n");

        commandOta.append(
            ".SET_BUFFER J 00 A4 00 00 02 %EF_ID ; select EF\n"
            + ".APPEND_SCRIPT J\n"
            + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
            + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }

    private String useRfmIsimCommandViaOtaAccessDomain(RfmIsim rfmIsim){

        StringBuilder commandOta = new StringBuilder();

        commandOta.append("\n; command(s) sent via OTA\n");

        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc1()){
            commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_ADM1 ; select EF on ADM1\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J 00 D6 00 00 <?> A1 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc2()){
            commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_ADM2 ; select EF on ADM2\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J 00 D6 00 00 <?> A2 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc3()){
            commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_ADM3 ; select EF on ADM3\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J 00 D6 00 00 <?> A3 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc4()){
            commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_ADM4 ; select EF on ADM4\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J 00 D6 00 00 <?> A4 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseGPin1()){
            commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_PIN1 ; select EF on PIN1\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J 00 D6 00 00 <?> A5 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseLPin1()){
            commandOta.append(
                ".SET_BUFFER J 00 A4 00 00 02 %EF_ID_ISIM_PIN2 ; select EF on PIN2\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J 00 D6 00 00 <?> A6 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }

        return commandOta.toString();
    }

    private String rfmIsimCheckUpdateEfDone(){

        StringBuilder checkUpdateHasBeenDone = new StringBuilder();

        checkUpdateHasBeenDone.append(
            "\n; check update has been done on EF\n"
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
            +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
            + "A0 B0 00 00 01 [AA] (9000)\n"
        );

        return checkUpdateHasBeenDone.toString();

    }

    private String useRfmIsimCheckUpdateEfDoneAccessDomain(RfmIsim rfmIsim){

        StringBuilder checkUpdateHasBeenDone = new StringBuilder();

        checkUpdateHasBeenDone.append(
            "\n; check update has been done on EF\n"
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
        );


        if(rfmIsim.getRfmIsimAccessDomain().isUseIsc1()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmIsim.getCustomTargetEfIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM1 (9F0F)\n"
                + "A0 B0 00 00 01 [A1] (9000)\n"
            );
        }
        if(rfmIsim.getRfmIsimAccessDomain().isUseIsc2()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmIsim.getCustomTargetEfIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM2 (9F0F)\n"
                + "A0 B0 00 00 01 [A2] (9000)\n"
            );
        }
        if(rfmIsim.getRfmIsimAccessDomain().isUseIsc3()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmIsim.getCustomTargetEfIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM3 (9F0F)\n"
                + "A0 B0 00 00 01 [A3] (9000)\n"
            );
        }
        if(rfmIsim.getRfmIsimAccessDomain().isUseIsc4()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmIsim.getCustomTargetEfIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM4 (9F0F)\n"
                + "A0 B0 00 00 01 [A4] (9000)\n"
            );
        }
        if(rfmIsim.getRfmIsimAccessDomain().isUseGPin1()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmIsim.getCustomTargetEfGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_PIN1 (9F0F)\n"
                + "A0 B0 00 00 01 [A5] (9000)\n"
            );
        }
        if(rfmIsim.getRfmIsimAccessDomain().isUseLPin1()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmIsim.getCustomTargetEfLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_PIN2 (9F0F)\n"
                + "A0 B0 00 00 01 [A6] (9000)\n"
            );
        }

        return checkUpdateHasBeenDone.toString();
    }

    private String rfmIsimRestoreRfmIsimInitialContentEf(){

        StringBuilder restoreInitialContent = new StringBuilder();

        restoreInitialContent.append(
                "\n; restore initial content of EF\n"
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
                +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT (9000)\n"
        );

        return restoreInitialContent.toString();

    }

    private String useRfmIsimRestoreRfmIsimInitialContentEfAccessDomain(RfmIsim rfmIsim){

        StringBuilder restoreInitialContent = new StringBuilder();

        restoreInitialContent.append(
            "\n; restore initial content of EF\n"
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
        );


        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc1()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmIsim.getCustomTargetEfIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM1 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM1 (9000)\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc2()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmIsim.getCustomTargetEfIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM2 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM2 (9000)\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc3()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmIsim.getCustomTargetEfIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM3 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM3 (9000)\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseIsc4()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmIsim.getCustomTargetEfIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_ADM4 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM4 (9000)\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseGPin1()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmIsim.getCustomTargetEfGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_PIN1 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_PIN1 (9000)\n"
            );
        }
        if (rfmIsim.getRfmIsimAccessDomain().isUseLPin1()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmIsim.getCustomTargetEfLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_ISIM_PIN2 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_PIN2 (9000)\n"
            );
        }

        return restoreInitialContent.toString();
    }

    private String usePerformNegativeTest(RfmIsim rfmIsim){

        StringBuilder performNegativeTest = new StringBuilder();

        if (!rfmIsim.isFullAccess()) {
            performNegativeTest.append(
                "\n; perform negative test: updating " + rfmIsim.getCustomTargetEfBadCase() + " Out of Access Domain\n"
                + "\n.POWER_ON\n"
                + "; check initial content of EF\n"
                + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
            );

            if (root.getRunSettings().getSecretCodes().isUseIsc2())
                performNegativeTest.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
            if (root.getRunSettings().getSecretCodes().isUseIsc3())
                performNegativeTest.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
            if (root.getRunSettings().getSecretCodes().isUseIsc4())
                performNegativeTest.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");

            performNegativeTest.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                + "A0 A4 00 00 02 %DF_ID (9F22)\n"
                + "A0 A4 00 00 02 %EF_ID_ERR (9FXX)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT_ERR R\n"
            );

            if (rfmIsim.isUseSpecificKeyset())
                performNegativeTest.append(rfmIsimCase1NegativeTest(rfmIsim.getCipheringKeyset(), rfmIsim.getAuthKeyset(), rfmIsim.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    performNegativeTest.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    performNegativeTest.append(rfmIsimCase1NegativeTest(keyset, keyset, rfmIsim.getMinimumSecurityLevel()));
                }
            }

            performNegativeTest.append("\n.UNDEFINE %EF_CONTENT_ERR \n");
        }

        return performNegativeTest.toString();
    }

//end of script
}

