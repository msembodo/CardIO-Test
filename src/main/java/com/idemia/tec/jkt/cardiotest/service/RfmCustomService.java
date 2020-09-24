package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.MinimumSecurityLevel;
import com.idemia.tec.jkt.cardiotest.model.RfmCustom;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RfmCustomService {

    @Autowired private RootLayoutController root;

    private String headerScriptRfmCustom(RfmCustom rfmCustom){
        StringBuilder headerScript = new StringBuilder();

        headerScript.append(
                ";==================================================\n" +
                        ";================!!! CAUTION !!!===================\n" +
                        ";==================================================\n\n" +
                        "* Please check the last byte for the POR\n" +
                        "* '00' PoR OK.\n" +
                        "* '01' RC/CC/DS failed.\n" +
                        "* '02' CNTR low.\n" +
                        "* '03' CNTR high.\n" +
                        "* '04' CNTR Blocked\n" +
                        "* '05' Ciphering error.\n" +
                        "* '06' Unidentified security error. This code is for the case where the Receiving Entity cannot correctly\n" +
                        "* interpret the Command Header and the Response Packet is sent unciphered with no RC/CC/DS.\n" +
                        "* '07' Insufficient memory to process incoming message.\n" +
                        "* '08' This status code \"more time\" should be used if the Receiving Entity/Application needs more time\n" +
                        "* to process the Command Packet due to timing constraints. In this case a later Response Packet\n" +
                        "* should be returned to the Sending Entity once processing has been completed.\n" +
                        "* '09' TAR Unknown\n" +
                        "* '0A' Insufficient security level\n\n" +
                        ";=======================\n\n"
        );

        headerScript.append("; ---------------------------------------- RFM CUSTOM ------------------------------------------------------\n\n");

        headerScript.append(
                // call mappings and load DLLs
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
                        + ".DEFINE %TAR " + rfmCustom.getTar() + "\n"
        );

        // enable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            headerScript.append("\nA0 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n");

        return headerScript.toString();
    }

    public StringBuilder generateRfmCustom(RfmCustom rfmCustom) {
        StringBuilder rfmCustomBuffer = new StringBuilder();

        // Generate Header Script
        rfmCustomBuffer.append(this.headerScriptRfmCustom(rfmCustom));

        // case 1
        rfmCustomBuffer.append("\n*********\n; CASE 1: RFM CUSTOM with correct security settings\n*********\n");

        // Target Files and Check Initial Content EFs isFullAccess
        if(rfmCustom.isFullAccess()){
            rfmCustomBuffer.append(this.rfmCustomDefineTargetFiles(rfmCustom)); // define Target Files
            rfmCustomBuffer.append(this.rfmCustomCheckInitialContentEf()); // check Initial Content of EF
        }
        else{
            rfmCustomBuffer.append(this.useRfmCustomDefineTargetFilesAccessDomain(rfmCustom));  // define Target Files Access Domain
            rfmCustomBuffer.append(this.useRfmCustomCheckInitialContentEfAccessDomain(rfmCustom)); // check Initial Content of EF Access Domain
        }

        // some TAR may be configured with specific keyset or use all available keysets
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomBuffer.append(rfmCustomCase1(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), false));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomBuffer.append(rfmCustomCase1(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), false));
            }
        }

        // perform negative test if not full access
        if (!rfmCustom.isFullAccess()) {
            rfmCustomBuffer.append("\n\n ; ================ Perform Negative Test Access Domain ================\n");
            rfmCustomBuffer.append(this.useRfmCustomCheckInitialContentEfBadCaseAccessDomain(rfmCustom));

            if (rfmCustom.isUseSpecificKeyset())
                rfmCustomBuffer.append(rfmCustomCase1NegativeTest(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmCustomBuffer.append(rfmCustomCase1NegativeTest(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), false));
                }
            }
        }

        //end of case 1

        // case 2
        rfmCustomBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in CUSTOM TAR\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomBuffer.append(rfmCustomCase2(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), false));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomBuffer.append(rfmCustomCase2(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), false));
            }
        }
        //end of case 2

        // case 3
        rfmCustomBuffer.append("\n*********\n; CASE 3: (Bad Case) send 2G command to CUSTOM TAR\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomBuffer.append(rfmCustomCase3(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), false));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomBuffer.append(rfmCustomCase3(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), false));
            }
        }
        //end of case 3

        // case 4
        rfmCustomBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomBuffer.append(rfmCustomCase4(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), false));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomBuffer.append(rfmCustomCase4(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), false));
            }
        }
        //end of case 4

        // case 5
        rfmCustomBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
        if (Integer.parseInt(rfmCustom.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
            rfmCustomBuffer.append("\n; MSL: " + rfmCustom.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
        else {
            if (rfmCustom.isUseSpecificKeyset())
                rfmCustomBuffer.append(rfmCustomCase5(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmCustomBuffer.append(rfmCustomCase5(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), false));
                }
            }
        }
        //end of case 5

        // case 6
        rfmCustomBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomBuffer.append(rfmCustomCase6(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), false));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomBuffer.append(rfmCustomCase6(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), false));
            }
        }
        //end of case 6

        // case 7
        rfmCustomBuffer.append("\n*********\n; CASE 7: (Bad Case) insufficient MSL\n*********\n");
        if (rfmCustom.getMinimumSecurityLevel().getComputedMsl().equals("00"))
            rfmCustomBuffer.append("\n; MSL: " + rfmCustom.getMinimumSecurityLevel().getComputedMsl() + " -- case 7 is not executed\n");
        else {
            MinimumSecurityLevel lowMsl = new MinimumSecurityLevel(false, "No verification", "No counter available");
            lowMsl.setSigningAlgo("no algorithm");
            lowMsl.setCipherAlgo("no cipher");
            lowMsl.setPorRequirement("PoR required");
            lowMsl.setPorSecurity("response with no security");
            lowMsl.setCipherPor(false);
            if (rfmCustom.isUseSpecificKeyset())
                rfmCustomBuffer.append(rfmCustomCase7(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), lowMsl, false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmCustomBuffer.append(rfmCustomCase7(keyset, keyset, lowMsl, false));
                }
            }
        }
        //end of case 7

        // case 8
        rfmCustomBuffer.append("\n*********\n; CASE 8: Bad TP-OA value\n*********\n");
        if (!root.getRunSettings().getSmsUpdate().isUseWhiteList())
            rfmCustomBuffer.append("\n; profile does not have white list configuration -- case 8 is not executed\n");
        else {
            if (rfmCustom.isUseSpecificKeyset())
                rfmCustomBuffer.append(rfmCustomCase8(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmCustomBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmCustomBuffer.append(rfmCustomCase8(keyset, keyset, rfmCustom.getMinimumSecurityLevel()));
                }
            }
        }
        //end of case 8

        // save counter
        rfmCustomBuffer.append(
                "\n; save counter state\n"
                        + ".EXPORT_BUFFER L COUNTER.bin\n"
        );

        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            rfmCustomBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");

        // unload DLLs
        rfmCustomBuffer.append(
                "\n.UNLOAD Calcul.dll\n"
                        + ".UNLOAD OTA2.dll\n"
                        + ".UNLOAD Var_Reader.dll\n"
                        + "\n.POWER_OFF\n"
        );

        return rfmCustomBuffer;
    }

    public StringBuilder generateRfmCustomUpdateRecord(RfmCustom rfmCustom){
        StringBuilder rfmCustomUpdateRecordBuffer = new StringBuilder();

        // Generate Header Script
        rfmCustomUpdateRecordBuffer.append(this.headerScriptRfmCustom(rfmCustom));

        // case 1
        rfmCustomUpdateRecordBuffer.append("\n*********\n; CASE 1: RFM CUSTOM Update Record with correct security settings\n*********\n");

        // Target Files and Check Initial Content EFs isFullAccess
        if(rfmCustom.isFullAccess()){
            rfmCustomUpdateRecordBuffer.append(this.rfmCustomDefineTargetFiles(rfmCustom)); // define Target Files
            rfmCustomUpdateRecordBuffer.append(this.rfmCustomCheckInitialContentEf()); // check Initial Content of EF
        }
        else{
            rfmCustomUpdateRecordBuffer.append(this.useRfmCustomDefineTargetFilesAccessDomain(rfmCustom));  // define Target Files Access Domain
            rfmCustomUpdateRecordBuffer.append(this.useRfmCustomCheckInitialContentEfAccessDomain(rfmCustom)); // check Initial Content of EF Access Domain
        }

        // some TAR may be configured with specific keyset or use all available keysets
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomUpdateRecordBuffer.append(rfmCustomCase1(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), true));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase1(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), true));
            }
        }

        if (!rfmCustom.isFullAccess()) {
            rfmCustomUpdateRecordBuffer.append("\n\n ; ================ Perform Negative Test Access Domain ================\n");
            rfmCustomUpdateRecordBuffer.append(this.useRfmCustomCheckInitialContentEfBadCaseAccessDomain(rfmCustom));

            if (rfmCustom.isUseSpecificKeyset())
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase1NegativeTest(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmCustomUpdateRecordBuffer.append(rfmCustomCase1NegativeTest(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), true));
                }
            }
        }

        //end of case 1

        // case 2
        rfmCustomUpdateRecordBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in CUSTOM TAR\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomUpdateRecordBuffer.append(rfmCustomCase2(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), true));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase2(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), true));
            }
        }
        //end of case 2

        // case 3
        rfmCustomUpdateRecordBuffer.append("\n*********\n; CASE 3: (Bad Case) send 2G command to CUSTOM TAR\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomUpdateRecordBuffer.append(rfmCustomCase3(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), true));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase3(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), true));
            }
        }
        //end of case 3

        // case 4
        rfmCustomUpdateRecordBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomUpdateRecordBuffer.append(rfmCustomCase4(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), true));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase4(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), true));
            }
        }
        //end of case 4

        // case 5
        rfmCustomUpdateRecordBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
        if (Integer.parseInt(rfmCustom.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
            rfmCustomUpdateRecordBuffer.append("\n; MSL: " + rfmCustom.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
        else {
            if (rfmCustom.isUseSpecificKeyset())
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase5(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmCustomUpdateRecordBuffer.append(rfmCustomCase5(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), true));
                }
            }
        }
        //end of case 5

        // case 6
        rfmCustomUpdateRecordBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
        if (rfmCustom.isUseSpecificKeyset())
            rfmCustomUpdateRecordBuffer.append(rfmCustomCase6(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), rfmCustom.getMinimumSecurityLevel(), true));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase6(keyset, keyset, rfmCustom.getMinimumSecurityLevel(), true));
            }
        }
        //end of case 6

        // case 7
        rfmCustomUpdateRecordBuffer.append("\n*********\n; CASE 7: (Bad Case) insufficient MSL\n*********\n");
        if (rfmCustom.getMinimumSecurityLevel().getComputedMsl().equals("00"))
            rfmCustomUpdateRecordBuffer.append("\n; MSL: " + rfmCustom.getMinimumSecurityLevel().getComputedMsl() + " -- case 7 is not executed\n");
        else {
            MinimumSecurityLevel lowMsl = new MinimumSecurityLevel(false, "No verification", "No counter available");
            lowMsl.setSigningAlgo("no algorithm");
            lowMsl.setCipherAlgo("no cipher");
            lowMsl.setPorRequirement("PoR required");
            lowMsl.setPorSecurity("response with no security");
            lowMsl.setCipherPor(false);
            if (rfmCustom.isUseSpecificKeyset())
                rfmCustomUpdateRecordBuffer.append(rfmCustomCase7(rfmCustom.getCipheringKeyset(), rfmCustom.getAuthKeyset(), lowMsl, true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmCustomUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmCustomUpdateRecordBuffer.append(rfmCustomCase7(keyset, keyset, lowMsl, true));
                }
            }
        }
        //end of case 7

        // save counter
        rfmCustomUpdateRecordBuffer.append(
                "\n; save counter state\n"
                        + ".EXPORT_BUFFER L COUNTER.bin\n"
        );

        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            rfmCustomUpdateRecordBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");

        // unload DLLs
        rfmCustomUpdateRecordBuffer.append(
                "\n.UNLOAD Calcul.dll\n"
                        + ".UNLOAD OTA2.dll\n"
                        + ".UNLOAD Var_Reader.dll\n"
                        + "\n.POWER_OFF\n"
        );

        return rfmCustomUpdateRecordBuffer;
    }

    public StringBuilder generateRfmCustomExpandedMode(RfmCustom rfmCustom) {
        StringBuilder rfmCustomExpandedModeBuffer = new StringBuilder();
        // TODO
        return rfmCustomExpandedModeBuffer;
    }

    private String rfmCustomCase1(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
        if(root.getRunSettings().getRfmCustom().isFullAccess()){
            routine.append(this.rfmCustomCommandViaOta());  // command(s) sent via OTA
        }
        else{
            routine.append(this.useRfmCustomCommandViaOtaAccessDomain(root.getRunSettings().getRfmCustom())); // command(s) sent via OTA Access Domain
        }

        routine.append(
                ".END_MESSAGE G J\n"
                        + "; show OTA message details\n"
                        + ".DISPLAY_MESSAGE J\n"
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomCase1"));
        }
        else {
            routine.append(this.updateSMSRecordRfmCustom("rfmCustomUpdateRecordCase1"));
        }

        // Check Update EF and Restore EF isFullAccess
        if (root.getRunSettings().getRfmCustom().isFullAccess()){
            routine.append(this.rfmCustomCheckUpdateEfDone()); //check update has been done on EF
            routine.append(this.rfmCustomRestoreRfmCustomInitialContentEf()); //restore initial content of EF
        }
        else {
            routine.append(this.useRfmCustomCheckUpdateEfDoneAccessDomain(root.getRunSettings().getRfmCustom())); //check update has been done on EF Access Domain
            routine.append(this.useRfmCustomRestoreRfmCustomInitialContentEfAccessDomain(root.getRunSettings().getRfmCustom())); //restore initial content of EF Access Domain
        }

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmCustomCase1NegativeTest(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
        StringBuilder routine = new StringBuilder();
        routine.append(
                "\n.POWER_ON\n"
                        + proactiveInitialization()
                        + "\n; SPI settings\n"
                        + ".SET_BUFFER O %" + cipherKeyset.getKicValuation() + "\n"
                        + ".SET_BUFFER Q %" + authKeyset.getKidValuation() + "\n"
                        + ".SET_BUFFER M " + cipherKeyset.getComputedKic() + "\n"
                        + ".SET_BUFFER N " + authKeyset.getComputedKid() + "\n"
                //TODO if need for update record failed (?)
        );

        // check 0348 isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
//        if(root.getRunSettings().getRfmCustom().isFullAccess()){
//            routine.append(this.rfmCustomCommandViaOta());  // command(s) sent via OTA
//        }
//        else{
//            routine.append(this.useRfmCustomCommandViaOtaBadCaseAccessDomain(root.getRunSettings().getRfmCustom())); // command(s) sent via OTA Bad Case Access Domain
//        }

        routine.append(this.useRfmCustomCommandViaOtaBadCaseAccessDomain(root.getRunSettings().getRfmCustom())); // command(s) sent via OTA Bad Case Access Domain

        routine.append(
                ".END_MESSAGE G J\n"
                        + "; show OTA message details\n"
                        + ".DISPLAY_MESSAGE J\n"
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomNegativeCase"));
        }
        else {
            routine.append(this.updateSMSRecordRfmCustom("rfmCustomUpdateRecordNegativeCase"));
        }

        routine.append(this.useRfmCustomCheckUpdateEfFailedAccessDomain(root.getRunSettings().getRfmCustom()));

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmCustomCase2(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomCase2"));
        }
        else {
            routine.append(this.updateSMSRecordRfmCustom("rfmCustomUpdateRecordCase2"));
        }

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmCustomCase3(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
                        + ".SET_BUFFER J A0 A4 00 00 02 3F00 ; this command isn't supported by CUSTOM\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".END_MESSAGE G J\n"
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomCase3"));
        }
        else {
            routine.append(this.updateSMSRecordRfmCustom("rfmCustomUpdateRecordCase3"));
        }

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmCustomCase4(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomCase4"));
        }
        else {
            routine.append(
                    "\n; update EF SMS record\n" // Case 4 (Bad Case) use unknown TAR, code manually
                            + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000)\n"
                            + "A0 A4 00 00 02 7F10 (9FXX) ;select DF Telecom\n"
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

    private String rfmCustomCase5(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomCase5"));
        }
        else {
            routine.append(this.updateSMSRecordRfmCustom("rfmCustomUpdateRecordCase5"));
        }

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );


        return routine.toString();
    }

    private String rfmCustomCase6(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomCase6"));
        }
        else {
            routine.append(this.updateSMSRecordRfmCustom("rfmCustomUpdateRecordCase6"));
        }

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmCustomCase7(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmCustom());
        }
        else {
            routine.append(this.init_SMS_0348RfmCustom());
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
            routine.append(this.sendEnvelopeRfmCustom("rfmCustomCase7"));
        }
        else {
            routine.append(this.updateSMSRecordRfmCustom("rfmCustomUpdateRecordCase7"));
        }

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmCustomCase8(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String init_SMS_0348RfmCustom(){

        StringBuilder routine = new StringBuilder();

        routine.append(
                ".INIT_SMS_0348\n"
                        + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                        + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                        + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                        + ".CHANGE_POR_FORMAT " + this.porFormatRfmCustom(root.getRunSettings().getSmsUpdate().getPorFormat()) + "\n"
        );

        return routine.toString();
    }

    private String init_ENV_0348RfmCustom(){

        StringBuilder routine = new StringBuilder();

        routine.append(
                ".INIT_ENV_0348\n"
                        + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
        );

        return routine.toString();
    }

    private String sendEnvelopeRfmCustom(String rfmCustomCases){

        StringBuilder routine = new StringBuilder();

        routine.append(
                "; send envelope\n"
                        + "A0 C2 00 00 G J (9XXX)\n"
                        + ".CLEAR_SCRIPT\n"
                        + "; check PoR\n"
                        + "A0 C0 00 00 W(2;1) [" + this.otaPorSettingRfmCustom(rfmCustomCases)+ "] (9000) ; PoR OK\n"
        );

        return routine.toString();
    }

    private String updateSMSRecordRfmCustom(String rfmCustomCases) {
        StringBuilder updateSMSRecordRfmCustom = new StringBuilder();

        updateSMSRecordRfmCustom.append(
                "\n; update EF SMS record\n"
                        + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000)\n"
                        + "A0 A4 00 00 02 7F10 (9FXX) ;select DF Telecom\n"
                        + "A0 A4 00 00 02 6F3C (9F0F) ;select EF SMS\n"
                        + "A0 DC 01 04 G J (91XX) ;update EF SMS\n"
                        + ".CLEAR_SCRIPT\n"
                        + "\n;Check SMS Content\n" // CHECK_SMS_CONTENT
                        + "A0 B2 01 04 B0\n" // READ_SMS
                        + "; check PoR\n"
                        + "A0 12 00 00 W(2;1) [" + this.otaPorSettingRfmCustomUpdateRecord(rfmCustomCases) + " ] (9000)\n"
                        + "A0 14 00 00 0C 8103011300 82028183 830100 (9000)\n"
        );

        return updateSMSRecordRfmCustom.toString();
    }

    private String otaPorSettingRfmCustom(String rfmCustomCases){
        String  POR_OK, POR_NOT_OK, BAD_CASE_WRONG_KEYSET,
                BAD_CASE_WRONG_CLASS_3G, BAD_CASE_WRONG_CLASS_2G,
                BAD_CASE_COUNTER_LOW,
                BAD_CASE_WRONG_KEY_VALUE, BAD_CASE_INSUFFICIENT_MSL, BAD_CASE_WRONG_TAR, NEGATIVE_CASE;

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
        NEGATIVE_CASE               = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 69 82";

        switch (rfmCustomCases){
            case "rfmCustomCase1" :
                result_set = POR_OK;
                break;
            case "rfmCustomCase2" :
                result_set = BAD_CASE_WRONG_KEYSET;
                break;
            case "rfmCustomCase3" :
                result_set =  BAD_CASE_WRONG_CLASS_3G;
                break;
            case "rfmCustomCase4" :
                result_set =  BAD_CASE_WRONG_TAR;
                break;
            case "rfmCustomCase5" :
                result_set = BAD_CASE_COUNTER_LOW;
                break;
            case "rfmCustomCase6" :
                result_set = BAD_CASE_WRONG_KEY_VALUE;
                break;
            case "rfmCustomCase7" :
                result_set = BAD_CASE_INSUFFICIENT_MSL;
                break;
            case "rfmCustomNegativeCase" :
                result_set = NEGATIVE_CASE;
                break;
        }

        return result_set;

    }

    private String otaPorSettingRfmCustomUpdateRecord(String rfmCustomCases){
        String  POR_OK, POR_NOT_OK, BAD_CASE_WRONG_KEYSET,
                BAD_CASE_WRONG_CLASS_3G, BAD_CASE_WRONG_CLASS_2G,
                BAD_CASE_COUNTER_LOW,
                BAD_CASE_WRONG_KEY_VALUE, BAD_CASE_INSUFFICIENT_MSL, NEGATIVE_CASE; //BAD_CASE_WRONG_TAR;

        String result_set = "";

        String scAddress            = root.getRunSettings().getSmsUpdate().getScAddress();
        String tag33UpdateRecord    = "D0 33 X1 XX XX 13 XX X2 02 X1 X3 X5 XX X6 " + scAddress + " XB XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX %TAR XX XX XX XX XX XX";
        String tag30UpdateRecord    = "D0 30 X1 XX XX 13 XX X2 02 X1 X3 X5 XX X6 " + scAddress + " XB XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX %TAR XX XX XX XX XX XX";

        POR_OK                      = tag33UpdateRecord+" XX XX 90 00";
        POR_NOT_OK                  = tag33UpdateRecord+" XX XX 98 04";
        BAD_CASE_WRONG_KEYSET       = tag30UpdateRecord+" 06";
        BAD_CASE_WRONG_CLASS_3G     = tag33UpdateRecord+" XX XX 6E 00";
        BAD_CASE_WRONG_CLASS_2G     = tag33UpdateRecord+" XX XX 6D 00";
        BAD_CASE_COUNTER_LOW        = tag30UpdateRecord+" 02";
        BAD_CASE_WRONG_KEY_VALUE    = tag30UpdateRecord+" 01";
        BAD_CASE_INSUFFICIENT_MSL   = tag30UpdateRecord+" 0A";
        NEGATIVE_CASE               = tag33UpdateRecord+" XX XX 69 82";
        //BAD_CASE_WRONG_TAR          = "D0 30 81 XX XX 13 XX 82 02 81 83 85 XX 86 "+scAddress+" 8B XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX B0 FF FF XX XX XX XX XX XX 09";

        switch (rfmCustomCases){
            case "rfmCustomUpdateRecordCase1" :
                result_set = POR_OK;
                break;
            case "rfmCustomUpdateRecordCase2" :
                result_set = BAD_CASE_WRONG_KEYSET;
                break;
            case "rfmCustomUpdateRecordCase3" :
                result_set =  BAD_CASE_WRONG_CLASS_3G;
                break;
            case "rfmCustomUpdateRecordCase5" :
                result_set = BAD_CASE_COUNTER_LOW;
                break;
            case "rfmCustomUpdateRecordCase6" :
                result_set = BAD_CASE_WRONG_KEY_VALUE;
                break;
            case "rfmCustomUpdateRecordCase7" :
                result_set = BAD_CASE_INSUFFICIENT_MSL;
                break;
            case "rfmCustomUpdateRecordNegativeCase" :
                result_set = NEGATIVE_CASE;
                break;
        }

        return result_set;

    }

    private String porFormatRfmCustom(String porformat){
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

    private String useRfmCustomEfAccessDomain(RfmCustom rfmCustom){

        StringBuilder accessDomain = new StringBuilder();

        if (rfmCustom.getRfmCustomAccessDomain().isUseAlways()){
            accessDomain.append(".DEFINE %EF_ID_CUSTOM_ALW " + rfmCustom.getCustomTargetEfAlw() + "; EF protected by Always\n");
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc1()){
            accessDomain.append(".DEFINE %EF_ID_CUSTOM_ADM1 " + rfmCustom.getCustomTargetEfIsc1() + "; EF protected by ADM1\n");
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc2()){
            accessDomain.append(".DEFINE %EF_ID_CUSTOM_ADM2 " + rfmCustom.getCustomTargetEfIsc2() + "; EF protected by ADM2\n");
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc3()){
            accessDomain.append(".DEFINE %EF_ID_CUSTOM_ADM3 " + rfmCustom.getCustomTargetEfIsc3() + "; EF protected by ADM3\n");
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc4()){
            accessDomain.append(".DEFINE %EF_ID_CUSTOM_ADM4 " + rfmCustom.getCustomTargetEfIsc4() + "; EF protected by ADM4\n");
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseGPin1()){
            accessDomain.append(".DEFINE %EF_ID_CUSTOM_PIN1 " + rfmCustom.getCustomTargetEfGPin1() + "; EF protected by PIN1\n");
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseLPin1()){
            accessDomain.append(".DEFINE %EF_ID_CUSTOM_PIN2 " + rfmCustom.getCustomTargetEfLPin1() + "; EF protected by PIN2\n");
        }

        return accessDomain.toString();
    }

    private String useRfmCustomEfBadCaseAccessDomain(RfmCustom rfmCustom){

        StringBuilder badCaseAccessDomain = new StringBuilder();

        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseAlways()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_CUSTOM_ERR_ALW " + rfmCustom.getCustomTargetEfBadCaseAlw() + "; EF is not protected by Always\n");
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc1()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_CUSTOM_ERR_ADM1 " + rfmCustom.getCustomTargetEfBadCaseIsc1() + "; EF is not protected by ADM1\n");
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc2()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_CUSTOM_ERR_ADM2 " + rfmCustom.getCustomTargetEfBadCaseIsc2() + "; EF is not protected by ADM2\n");
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc3()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_CUSTOM_ERR_ADM3 " + rfmCustom.getCustomTargetEfBadCaseIsc3() + "; EF is not protected by ADM3\n");
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc4()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_CUSTOM_ERR_ADM4 " + rfmCustom.getCustomTargetEfBadCaseIsc4() + "; EF is not protected by ADM4\n");
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseGPin1()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_CUSTOM_ERR_PIN1 " + rfmCustom.getCustomTargetEfBadCaseGPin1() + "; EF is not protected by PIN1\n");
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseLPin1()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_CUSTOM_ERR_PIN2 " + rfmCustom.getCustomTargetEfBadCaseLPin1() + "; EF is not protected by PIN2\n");
        }

        return badCaseAccessDomain.toString();
    }

    private String rfmCustomDefineTargetFiles(RfmCustom rfmCustom){

        StringBuilder targetFiles = new StringBuilder();

        targetFiles.append(
                "\n; TAR is configured for full access\n"
                        + ".DEFINE %DF_ID " + root.getRunSettings().getRfmCustom().getTargetDf() + "\n"
                        + ".DEFINE %EF_ID " + rfmCustom.getTargetEf() + "\n"
                        //+ ".DEFINE %EF_ID_ERR " + rfmCustom.getTargetEfBadCase() + "\n"
        );

        return targetFiles.toString();
    }

    private String useRfmCustomDefineTargetFilesAccessDomain(RfmCustom rfmCustom){

        StringBuilder targetFiles = new StringBuilder();

        targetFiles.append(
                "\n; TAR is configured with access domain\n"
                        + ".DEFINE %DF_ID " + root.getRunSettings().getRfmCustom().getTargetDf()  + "\n"
                        + (this.useRfmCustomEfAccessDomain(rfmCustom))
                        + (this.useRfmCustomEfBadCaseAccessDomain(rfmCustom))

        );

        return targetFiles.toString();
    }

    private String rfmCustomCheckInitialContentEf(){

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
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
                        +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
                        +"A0 B0 00 00 01 (9000)\n"
                        +".DEFINE %EF_CONTENT R\n"
        );

        return checkInitialContent.toString();

    }

    private String useRfmCustomCheckInitialContentEfAccessDomain(RfmCustom rfmCustom){

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
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );

        if (rfmCustom.getRfmCustomAccessDomain().isUseAlways()){
            checkInitialContent.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfAlw() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ALW (9F0F)\n"
                            + "A0 B0 00 00 01 (9000)\n"
                            + ".DEFINE %EF_CONTENT_ALW R\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc1()){
            checkInitialContent.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfIsc1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM1 (9F0F)\n"
                            + "A0 B0 00 00 01 (9000)\n"
                            + ".DEFINE %EF_CONTENT_ADM1 R\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc2()){
            checkInitialContent.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfIsc2() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM2 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ADM2 R\n"

            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc3()){
            checkInitialContent.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfIsc3() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM3 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ADM3 R\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc4()){
            checkInitialContent.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfIsc4() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM4 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ADM4 R\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseGPin1()){
            checkInitialContent.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfGPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_PIN1 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_PIN1 R\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseLPin1()){
            checkInitialContent.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfLPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_PIN2 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_PIN2 R\n"

            );
        }

        return checkInitialContent.toString();
    }

    private String useRfmCustomCheckInitialContentEfBadCaseAccessDomain(RfmCustom rfmCustom){

        StringBuilder checkInitialContentBadCase = new StringBuilder();

        checkInitialContentBadCase.append(
                "\n.POWER_ON\n"
                        + "; check initial content\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContentBadCase.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContentBadCase.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContentBadCase.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContentBadCase.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );

        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseAlways()){
            checkInitialContentBadCase.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfBadCaseAlw() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ALW (9F0F)\n"
                            + "A0 B0 00 00 01 (9000)\n"
                            + ".DEFINE %EF_CONTENT_ERR_ALW R\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc1()){
            checkInitialContentBadCase.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfBadCaseIsc1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM1 (9F0F)\n"
                            + "A0 B0 00 00 01 (9000)\n"
                            + ".DEFINE %EF_CONTENT_ERR_ADM1 R\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc2()){
            checkInitialContentBadCase.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfBadCaseIsc2() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM2 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ERR_ADM2 R\n"

            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc3()){
            checkInitialContentBadCase.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfBadCaseIsc3() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM3 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ERR_ADM3 R\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc4()){
            checkInitialContentBadCase.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfBadCaseIsc4() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM4 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ERR_ADM4 R\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseGPin1()){
            checkInitialContentBadCase.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfBadCaseGPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_PIN1 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ERR_PIN1 R\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseLPin1()){
            checkInitialContentBadCase.append(
                    "; check content EF-" + rfmCustom.getCustomTargetEfBadCaseLPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_PIN2 (9F0F)\n"
                            +"A0 B0 00 00 01 (9000)\n"
                            +".DEFINE %EF_CONTENT_ERR_PIN2 R\n"
            );
        }

        return checkInitialContentBadCase.toString();
    }

    private String rfmCustomCommandViaOta(){

        StringBuilder commandOta = new StringBuilder();

        commandOta.append("\n; command(s) sent via OTA\n");

        commandOta.append(
                //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                        + ".APPEND_SCRIPT J\n"
                +".SET_BUFFER J 00 A4 00 00 02 %EF_ID ; select EF\n"
                        + ".APPEND_SCRIPT J\n"
                        + ".SET_BUFFER J 00 D6 00 00 <?> AA ; update binary\n"
                        + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }

    private String useRfmCustomCommandViaOtaAccessDomain(RfmCustom rfmCustom){

        StringBuilder commandOta = new StringBuilder();

        commandOta.append("\n; command(s) sent via OTA\n");

        if (rfmCustom.getRfmCustomAccessDomain().isUseAlways()){
            commandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ALW ; select EF on Always\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 B0 00 00 02 ; read binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc1()){
            commandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ADM1 ; select EF on ADM1\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A1 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc2()){
            commandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ADM2 ; select EF on ADM2\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A2 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc3()){
            commandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ADM3 ; select EF on ADM3\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A3 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc4()){
            commandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ADM4 ; select EF on ADM4\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A4 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseGPin1()){
            commandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                            ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_PIN1 ; select EF on PIN1\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A5 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseLPin1()){
            commandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_PIN2 ; select EF on PIN2\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A6 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }

        return commandOta.toString();
    }

    private String useRfmCustomCommandViaOtaBadCaseAccessDomain(RfmCustom rfmCustom){

        StringBuilder badCasecommandOta = new StringBuilder();

        badCasecommandOta.append("\n; command(s) sent via OTA Bad Case\n");

        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseAlways()){
            badCasecommandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ERR_ALW ; select EF on Bad Case Always\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 B0 00 00 02 ; read binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc1()){
            badCasecommandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM1 ; select EF on Bad Case ADM1\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A1 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc2()){
            badCasecommandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM2 ; select EF on Bad Case ADM2\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A2 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc3()){
            badCasecommandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM3 ; select EF on Bad Case ADM3\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A3 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc4()){
            badCasecommandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM4 ; select EF on Bad Case ADM4\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A4 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseGPin1()){
            badCasecommandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                            ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ERR_PIN1 ; select EF on Bad Case PIN1\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A5 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseLPin1()){
            badCasecommandOta.append(
                    //".SET_BUFFER J 00 A4 00 00 02 3F00 ; select MF\n"
                    ".SET_BUFFER J 00 A4 00 00 02 %DF_ID ; select DF\n"
                            + ".APPEND_SCRIPT J\n"
                            +".SET_BUFFER J 00 A4 00 00 02 %EF_ID_CUSTOM_ERR_PIN2 ; select EF on Bad Case PIN2\n"
                            + ".APPEND_SCRIPT J\n"
                            + ".SET_BUFFER J 00 D6 00 00 <?> A6 ; update binary\n"
                            + ".APPEND_SCRIPT J\n"
            );
        }

        return badCasecommandOta.toString();
    }

    private String rfmCustomCheckUpdateEfDone(){

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
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
                        +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
                        + "A0 B0 00 00 01 [AA] (9000)\n"
        );

        return checkUpdateHasBeenDone.toString();

    }

    private String useRfmCustomCheckUpdateEfDoneAccessDomain(RfmCustom rfmCustom){

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
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );


        if(rfmCustom.getRfmCustomAccessDomain().isUseAlways()){
            checkUpdateHasBeenDone.append(
                    "; check Read Binary on EF-" + rfmCustom.getCustomTargetEfAlw() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ALW (9F0F)\n"
                            + "A0 B0 00 00 02 [%EF_CONTENT_ALW] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomAccessDomain().isUseIsc1()){
            checkUpdateHasBeenDone.append(
                    "; check update on EF-" + rfmCustom.getCustomTargetEfIsc1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM1 (9F0F)\n"
                            + "A0 B0 00 00 01 [A1] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomAccessDomain().isUseIsc2()){
            checkUpdateHasBeenDone.append(
                    "; check update on EF-" + rfmCustom.getCustomTargetEfIsc2() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM2 (9F0F)\n"
                            + "A0 B0 00 00 01 [A2] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomAccessDomain().isUseIsc3()){
            checkUpdateHasBeenDone.append(
                    "; check update on EF-" + rfmCustom.getCustomTargetEfIsc3() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM3 (9F0F)\n"
                            + "A0 B0 00 00 01 [A3] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomAccessDomain().isUseIsc4()){
            checkUpdateHasBeenDone.append(
                    "; check update on EF-" + rfmCustom.getCustomTargetEfIsc4() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM4 (9F0F)\n"
                            + "A0 B0 00 00 01 [A4] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomAccessDomain().isUseGPin1()){
            checkUpdateHasBeenDone.append(
                    "; check update on EF-" + rfmCustom.getCustomTargetEfGPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_PIN1 (9F0F)\n"
                            + "A0 B0 00 00 01 [A5] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomAccessDomain().isUseLPin1()){
            checkUpdateHasBeenDone.append(
                    "; check update on EF-" + rfmCustom.getCustomTargetEfLPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_PIN2 (9F0F)\n"
                            + "A0 B0 00 00 01 [A6] (9000)\n"
            );
        }

        return checkUpdateHasBeenDone.toString();
    }

    private String useRfmCustomCheckUpdateEfFailedAccessDomain(RfmCustom rfmCustom){

        StringBuilder checkUpdateHasFailed = new StringBuilder();

        checkUpdateHasFailed.append(
                "\n; check update has failed on EF\n"
                        + ".POWER_ON\n"
                        + "A0 20 00 00 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkUpdateHasFailed.append("A0 20 00 05 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkUpdateHasFailed.append("A0 20 00 06 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkUpdateHasFailed.append("A0 20 00 07 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkUpdateHasFailed.append(
                "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + "A0 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );


        if(rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseAlways()){
            checkUpdateHasFailed.append(
                    "; check Read Binary on EF-" + rfmCustom.getCustomTargetEfBadCaseAlw() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ALW (9F0F)\n"
                            + "A0 B0 00 00 02 [%EF_CONTENT_ERR_ALW] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc1()){
            checkUpdateHasFailed.append(
                    "; check update failed on EF-" + rfmCustom.getCustomTargetEfBadCaseIsc1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM1 (9F0F)\n"
                            + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM1] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc2()){
            checkUpdateHasFailed.append(
                    "; check update failed on EF-" + rfmCustom.getCustomTargetEfBadCaseIsc2() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM2 (9F0F)\n"
                            + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM2] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc3()){
            checkUpdateHasFailed.append(
                    "; check update failed on EF-" + rfmCustom.getCustomTargetEfBadCaseIsc3() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM3 (9F0F)\n"
                            + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM3] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc4()){
            checkUpdateHasFailed.append(
                    "; check update failed on EF-" + rfmCustom.getCustomTargetEfBadCaseIsc4() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_ADM4 (9F0F)\n"
                            + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM4] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseGPin1()){
            checkUpdateHasFailed.append(
                    "; check update failed on EF-" + rfmCustom.getCustomTargetEfBadCaseGPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_PIN1 (9F0F)\n"
                            + "A0 B0 00 00 01 [%EF_CONTENT_ERR_PIN1] (9000)\n"
            );
        }
        if(rfmCustom.getRfmCustomBadCaseAccessDomain().isUseBadCaseLPin1()){
            checkUpdateHasFailed.append(
                    "; check update failed on EF-" + rfmCustom.getCustomTargetEfBadCaseLPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ERR_PIN2 (9F0F)\n"
                            + "A0 B0 00 00 01 [%EF_CONTENT_ERR_PIN2] (9000)\n"
            );
        }

        return checkUpdateHasFailed.toString();
    }

    private String rfmCustomRestoreRfmCustomInitialContentEf(){

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
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
                        +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
                        + "A0 D6 00 00 01 %EF_CONTENT (9000)\n"
        );

        return restoreInitialContent.toString();

    }

    private String useRfmCustomRestoreRfmCustomInitialContentEfAccessDomain(RfmCustom rfmCustom){

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
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );


        if(rfmCustom.getRfmCustomAccessDomain().isUseAlways()){
            restoreInitialContent.append(
                    "; check Read Binary on EF-" + rfmCustom.getCustomTargetEfAlw() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ALW (9F0F)\n"
                            + "A0 B0 00 00 02 [%EF_CONTENT_ALW] (9000) ; Read Binary\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc1()){
            restoreInitialContent.append(
                    "; restore content EF-" + rfmCustom.getCustomTargetEfIsc1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM1 (9F0F)\n"
                            + "A0 D6 00 00 01 %EF_CONTENT_ADM1 (9000)\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc2()){
            restoreInitialContent.append(
                    "; restore content EF-" + rfmCustom.getCustomTargetEfIsc2() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM2 (9F0F)\n"
                            + "A0 D6 00 00 01 %EF_CONTENT_ADM2 (9000)\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc3()){
            restoreInitialContent.append(
                    "; restore content EF-" + rfmCustom.getCustomTargetEfIsc3() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM3 (9F0F)\n"
                            + "A0 D6 00 00 01 %EF_CONTENT_ADM3 (9000)\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseIsc4()){
            restoreInitialContent.append(
                    "; restore content EF-" + rfmCustom.getCustomTargetEfIsc4() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_ADM4 (9F0F)\n"
                            + "A0 D6 00 00 01 %EF_CONTENT_ADM4 (9000)\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseGPin1()){
            restoreInitialContent.append(
                    "; restore content EF-" + rfmCustom.getCustomTargetEfGPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_PIN1 (9F0F)\n"
                            + "A0 D6 00 00 01 %EF_CONTENT_PIN1 (9000)\n"
            );
        }
        if (rfmCustom.getRfmCustomAccessDomain().isUseLPin1()){
            restoreInitialContent.append(
                    "; restore content EF-" + rfmCustom.getCustomTargetEfLPin1() +  "\n"
                            +"A0 A4 00 00 02 %EF_ID_CUSTOM_PIN2 (9F0F)\n"
                            + "A0 D6 00 00 01 %EF_CONTENT_PIN2 (9000)\n"
            );
        }

        return restoreInitialContent.toString();
    }

//end of script
}

