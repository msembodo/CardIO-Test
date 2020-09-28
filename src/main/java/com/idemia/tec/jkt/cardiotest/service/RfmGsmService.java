package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.MinimumSecurityLevel;
import com.idemia.tec.jkt.cardiotest.model.RfmGsm;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RfmGsmService {

    @Autowired private RootLayoutController root;
    @Autowired private ApduService apduService;

    private String headerScriptRfmGsm(RfmGsm rfmGsm){
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
                + ".DEFINE %TAR " + rfmGsm.getTar() + "\n"
        );
        // enable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            headerScript.append("\nA0 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n");

        return headerScript.toString();

    }

    public StringBuilder generateRfmGsm(RfmGsm rfmGsm) {

        StringBuilder rfmGsmBuffer = new StringBuilder();

        // Generate Header Script
        rfmGsmBuffer.append(this.headerScriptRfmGsm(rfmGsm));

        // case 1
        rfmGsmBuffer.append("\n*********\n; CASE 1: RFM GSM with correct security settings\n*********\n");
        // Target Files and Check Initial Content EFs isFullAccess
        if(rfmGsm.isFullAccess()){
            rfmGsmBuffer.append(this.rfmGsmDefineTargetFiles(rfmGsm)); // define Target Files
            rfmGsmBuffer.append(this.rfmGsmCheckInitialContentEf()); // check Initial Content of EF
        }
        else{
            rfmGsmBuffer.append(this.useRfmGsmDefineTargetFilesAccessDomain(rfmGsm));  // define Target Files Access Domain
            rfmGsmBuffer.append(this.useRfmGsmCheckInitialContentEfAccessDomain(rfmGsm)); // check Initial Content of EF Access Domain
        }

        // some TAR may be configured with specific keyset or use all available keysets
        if (rfmGsm.isUseSpecificKeyset())
            rfmGsmBuffer.append(rfmGsmCase1(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), false));
        else {
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                rfmGsmBuffer.append(rfmGsmCase1(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), false));
            }
        }

        // perform negative test if not full access
        if (!rfmGsm.isFullAccess()) {
            rfmGsmBuffer.append("\n\n ; ================ Perform Negative Test Access Domain ================\n");
            rfmGsmBuffer.append(this.useRfmGsmCheckInitialContentEfBadCaseAccessDomain(rfmGsm));

            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmBuffer.append(rfmGsmCase1NegativeTest(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmBuffer.append(rfmGsmCase1NegativeTest(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), false));
                }
            }
        }
        //end of case 1

        // case 2
        rfmGsmBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in Gsm TAR\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmBuffer.append(rfmGsmCase2(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmBuffer.append(rfmGsmCase2(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 2

        // case 3
            rfmGsmBuffer.append("\n*********\n; CASE 3: (Bad Case) send 3G command to GSM TAR\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmBuffer.append(rfmGsmCase3(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmBuffer.append(rfmGsmCase3(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 3

        // case 4
        rfmGsmBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmBuffer.append(rfmGsmCase4(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmBuffer.append(rfmGsmCase4(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 4

        // case 5
        rfmGsmBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
            if (Integer.parseInt(rfmGsm.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
                rfmGsmBuffer.append("\n; MSL: " + rfmGsm.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
            else {
                if (rfmGsm.isUseSpecificKeyset())
                    rfmGsmBuffer.append(rfmGsmCase5(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), false));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmGsmBuffer.append(rfmGsmCase5(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), false));
                    }
                }
            }
        //end of case 5

        // case 6
        rfmGsmBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmBuffer.append(rfmGsmCase6(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), false));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmBuffer.append(rfmGsmCase6(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), false));
                }
            }
        //end of case 6

        // case 7
        rfmGsmBuffer.append("\n*********\n; CASE 7: (Bad Case) insufficient MSL\n*********\n");
            if (rfmGsm.getMinimumSecurityLevel().getComputedMsl().equals("00"))
                rfmGsmBuffer.append("\n; MSL: " + rfmGsm.getMinimumSecurityLevel().getComputedMsl() + " -- case 7 is not executed\n");
            else {
                MinimumSecurityLevel lowMsl = new MinimumSecurityLevel(false, "No verification", "No counter available");
                lowMsl.setSigningAlgo("no algorithm");
                lowMsl.setCipherAlgo("no cipher");
                lowMsl.setPorRequirement("PoR required");
                lowMsl.setPorSecurity("response with no security");
                lowMsl.setCipherPor(false);
                if (rfmGsm.isUseSpecificKeyset())
                    rfmGsmBuffer.append(rfmGsmCase7(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), lowMsl, false));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmGsmBuffer.append(rfmGsmCase7(keyset, keyset, lowMsl, false));
                    }
                }
            }
        //end of case 7

        // case 8
        rfmGsmBuffer.append("\n*********\n; CASE 8: Bad TP-OA value\n*********\n");
        if (!root.getRunSettings().getSmsUpdate().isUseWhiteList())
            rfmGsmBuffer.append("\n; profile does not have white list configuration -- case 8 is not executed\n");
        else {
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmBuffer.append(rfmGsmCase8(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel()));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmBuffer.append(rfmGsmCase8(keyset, keyset, rfmGsm.getMinimumSecurityLevel()));
                }
            }
        }
        //end of case 8

        // save counter
        rfmGsmBuffer.append(
            "\n; save counter state\n"
            + ".EXPORT_BUFFER L COUNTER.bin\n"
        );

        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            rfmGsmBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");

        // unload DLLs
        rfmGsmBuffer.append(
            "\n.UNLOAD Calcul.dll\n"
            + ".UNLOAD OTA2.dll\n"
            + ".UNLOAD Var_Reader.dll\n"
            + "\n.POWER_OFF\n"
        );

        return rfmGsmBuffer;
    }

    public StringBuilder generateRfmGsmUpdateRecord(RfmGsm rfmGsm) {
        StringBuilder rfmGsmUpdateRecordBuffer = new StringBuilder();

        // Generate Header Script RFM GSM
        rfmGsmUpdateRecordBuffer.append(this.headerScriptRfmGsm(rfmGsm));

        // case 1
        rfmGsmUpdateRecordBuffer.append("\n*********\n; CASE 1: RFM GSM Update Record with correct security settings\n*********\n");

            // Target Files and Check Initial Content EFs isFullAccess
            if (rfmGsm.isFullAccess()) {
                rfmGsmUpdateRecordBuffer.append(this.rfmGsmDefineTargetFiles(rfmGsm)); // define Target Files
                rfmGsmUpdateRecordBuffer.append(this.rfmGsmCheckInitialContentEf()); // check Initial Content of EF
            }
            else{
                rfmGsmUpdateRecordBuffer.append(this.useRfmGsmDefineTargetFilesAccessDomain(rfmGsm));  // define Target Files Access Domain
                rfmGsmUpdateRecordBuffer.append(this.useRfmGsmCheckInitialContentEfAccessDomain(rfmGsm)); // check Initial Content of EF Access Domain
            }

            // some TAR may be configured with specific keyset or use all available keysets
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmUpdateRecordBuffer.append(rfmGsmCase1(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase1(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), true));
                }
            }

            // perform negative test if not full access
            if (!rfmGsm.isFullAccess()) {
                rfmGsmUpdateRecordBuffer.append("\n\n ; ================ Perform Negative Test Access Domain ================\n");
                rfmGsmUpdateRecordBuffer.append(this.useRfmGsmCheckInitialContentEfBadCaseAccessDomain(rfmGsm));

                if (rfmGsm.isUseSpecificKeyset())
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase1NegativeTest(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), true));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmGsmUpdateRecordBuffer.append(rfmGsmCase1NegativeTest(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), true));
                    }
                }
            }
            //end of case 1

        // case 2
            rfmGsmUpdateRecordBuffer.append("\n*********\n; CASE 2: (Bad Case) RFM with keyset which is not allowed in Gsm TAR\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmUpdateRecordBuffer.append(rfmGsmCase2(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase2(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 2

        // case 3
            rfmGsmUpdateRecordBuffer.append("\n*********\n; CASE 3: (Bad Case) send 3G command to GSM TAR\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmUpdateRecordBuffer.append(rfmGsmCase3(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase3(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 3

        // case 4
            rfmGsmUpdateRecordBuffer.append("\n*********\n; CASE 4: (Bad Case) use unknown TAR\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmUpdateRecordBuffer.append(rfmGsmCase4(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase4(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 4

        // case 5
            rfmGsmUpdateRecordBuffer.append("\n*********\n; CASE 5: (Bad Case) counter is low\n*********\n");
            if (Integer.parseInt(rfmGsm.getMinimumSecurityLevel().getComputedMsl(), 16) < 16)
                rfmGsmUpdateRecordBuffer.append("\n; MSL: " + rfmGsm.getMinimumSecurityLevel().getComputedMsl() + " -- no need to check counter\n");
            else {
                if (rfmGsm.isUseSpecificKeyset())
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase5(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), true));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmGsmUpdateRecordBuffer.append(rfmGsmCase5(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), true));
                    }
                }
            }
        //end of case 5

        // case 6
            rfmGsmUpdateRecordBuffer.append("\n*********\n; CASE 6: (Bad Case) use bad key for authentication\n*********\n");
            if (rfmGsm.isUseSpecificKeyset())
                rfmGsmUpdateRecordBuffer.append(rfmGsmCase6(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), rfmGsm.getMinimumSecurityLevel(), true));
            else {
                for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                    rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase6(keyset, keyset, rfmGsm.getMinimumSecurityLevel(), true));
                }
            }
        //end of case 6

        // case 7
            rfmGsmUpdateRecordBuffer.append("\n*********\n; CASE 7: (Bad Case) insufficient MSL\n*********\n");
            if (rfmGsm.getMinimumSecurityLevel().getComputedMsl().equals("00"))
                rfmGsmUpdateRecordBuffer.append("\n; MSL: " + rfmGsm.getMinimumSecurityLevel().getComputedMsl() + " -- case 7 is not executed\n");
            else {
                MinimumSecurityLevel lowMsl = new MinimumSecurityLevel(false, "No verification", "No counter available");
                lowMsl.setSigningAlgo("no algorithm");
                lowMsl.setCipherAlgo("no cipher");
                lowMsl.setPorRequirement("PoR required");
                lowMsl.setPorSecurity("response with no security");
                lowMsl.setCipherPor(false);
                if (rfmGsm.isUseSpecificKeyset())
                    rfmGsmUpdateRecordBuffer.append(rfmGsmCase7(rfmGsm.getCipheringKeyset(), rfmGsm.getAuthKeyset(), lowMsl, true));
                else {
                    for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                        rfmGsmUpdateRecordBuffer.append("\n; using keyset: " + keyset.getKeysetName() + "\n");
                        rfmGsmUpdateRecordBuffer.append(rfmGsmCase7(keyset, keyset, lowMsl, true));
                    }
                }
            }
        //end of case 7

        // save counter
        rfmGsmUpdateRecordBuffer.append(
                "\n; save counter state\n"
                        + ".EXPORT_BUFFER L COUNTER.bin\n"
        );

        // disable pin if required
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            rfmGsmUpdateRecordBuffer.append("\nA0 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n");

        // unload DLLs
        rfmGsmUpdateRecordBuffer.append(
                "\n.UNLOAD Calcul.dll\n"
                        + ".UNLOAD OTA2.dll\n"
                        + ".UNLOAD Var_Reader.dll\n"
                        + "\n.POWER_OFF\n"
        );

        return rfmGsmUpdateRecordBuffer;
    }

    public StringBuilder generateRfmGsmExpandedMode(RfmGsm rfmGsm) {
        StringBuilder rfmGsmExpandedModeBuffer = new StringBuilder();
        // TODO
        return rfmGsmExpandedModeBuffer;
    }

    private String rfmGsmCase1(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
            if(root.getRunSettings().getRfmGsm().isFullAccess()){
                routine.append(this.rfmGsmCommandViaOta());  // command(s) sent via OTA
            }
            else{
                routine.append(this.useRfmGsmCommandViaOtaAccessDomain(root.getRunSettings().getRfmGsm())); // command(s) sent via OTA Access Domain
            }

            routine.append(
                ".END_MESSAGE G J\n"
                + "; show OTA message details\n"
                + ".DISPLAY_MESSAGE J\n"
            );

            // check isUpdateRecord
            if(!isUpdateRecord){
                routine.append(this.sendEnvelopeRfmGsm("rfmGsmCase1"));
            }
            else {
                routine.append(this.updateSMSRecordRfmGsm("rfmGsmUpdateRecordCase1"));
            }


        // Check Update EF and Restore EF isFullAccess
        if (root.getRunSettings().getRfmGsm().isFullAccess()){
            routine.append(this.rfmGsmCheckUpdateEfDone()); //check update has been done on EF
            routine.append(this.rfmGsmRestoreRfmGsmInitialContentEf()); //restore initial content of EF
        }
        else {
            routine.append(this.useRfmGsmCheckUpdateEfDoneAccessDomain(root.getRunSettings().getRfmGsm())); //check update has been done on EF Access Domain
            routine.append(this.useRfmGsmRestoreRfmGsmInitialContentEfAccessDomain(root.getRunSettings().getRfmGsm())); //restore initial content of EF Access Domain
        }

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmGsmCase1NegativeTest(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
        routine.append(this.useRfmGsmCommandViaOtaBadCaseAccessDomain(root.getRunSettings().getRfmGsm())); // command(s) sent via OTA Bad Case Access Domain

        routine.append(
            ".END_MESSAGE G J\n"
            + "; show OTA message details\n"
            + ".DISPLAY_MESSAGE J\n"
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmGsm("rfmGsmNegativeCase"));
        }
        else {
            routine.append(this.updateSMSRecordRfmGsm("rfmGsmUpdateRecordNegativeCase"));
        }

        routine.append(this.useRfmGsmCheckUpdateEfFailedAccessDomain(root.getRunSettings().getRfmGsm()));

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmGsmCase2(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
            + ".SET_BUFFER J A0 A4 00 00 02 3F00\n"
            + ".APPEND_SCRIPT J\n"
            + ".END_MESSAGE G J\n"
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmGsm("rfmGsmCase2"));
        }
        else {
            routine.append(this.updateSMSRecordRfmGsm("rfmGsmUpdateRecordCase2"));
        }

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmGsmCase3(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
            + ".SET_BUFFER J 00 A4 00 00 02 3F00 ; this command isn't supported by GSM\n"
            + ".APPEND_SCRIPT J\n"
            + ".END_MESSAGE G J\n"
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmGsm("rfmGsmCase3"));
        }
        else {
            routine.append(this.updateSMSRecordRfmGsm("rfmGsmUpdateRecordCase3"));
        }

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );


        return routine.toString();
    }

    private String rfmGsmCase4(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
            + ".SET_BUFFER J A0 A4 00 00 02 3F00\n"
            + ".APPEND_SCRIPT J\n"
            + ".END_MESSAGE G J\n"

        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmGsm("rfmGsmCase4"));
        }
        else {
            routine.append(
                "\n; update EF SMS record\n" // Case 4 (Bad Case) use unknown TAR, code manually
                + apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getGpin() + " (9000)\n"
                + "A0 A4 00 00 02 7F10 (9FXX) ;select DF Telecom\n"
                + "A0 A4 00 00 02 6F3C (9F0F) ;select EF SMS\n"
                + "A0 DC 01 04 G J (9000) ;update EF SMS\n"
                + ".CLEAR_SCRIPT\n"
                + "A0 B2 01 04 B0 (9000, 91XX) ;FOR SIMbiOS CTD will have SW 91 XX\n\n"
                + ".SWITCH W(2:2) \n"
                + " .CASE 00\n"
                + "     ;can not check PoR\n"
                + " .BREAK\n"
                + " .DEFAULT\n"
                + "     ;check PoR\n"
                + "     A0 12 00 00 W(2:2) [XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXX XXXXXX 09] (9000)\n"
                + "     A0 14 00 00 0C 8103011300 82028183 830100 (9000)\n"
                + ".ENDSWITCH\n"
            );
        }

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmGsmCase5(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
            + ".SET_BUFFER J A0 A4 00 00 02 3F00\n"
            + ".APPEND_SCRIPT J\n"
            + ".END_MESSAGE G J\n"
        );

        // check isUpdateRecord
        if(!isUpdateRecord){
            routine.append(this.sendEnvelopeRfmGsm("rfmGsmCase5"));
        }
        else {
            routine.append(this.updateSMSRecordRfmGsm("rfmGsmUpdateRecordCase5"));
        }

        routine.append(
                "\n; increment counter by one\n"
                        + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmGsmCase6(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
            routine.append(this.sendEnvelopeRfmGsm("rfmGsmCase6"));
        }
        else {
            routine.append(this.updateSMSRecordRfmGsm("rfmGsmUpdateRecordCase6"));
        }

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );

        return routine.toString();
    }

    private String rfmGsmCase7(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl, Boolean isUpdateRecord) {
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
            routine.append(this.init_ENV_0348RfmGsm());
        }
        else {
            routine.append(this.init_SMS_0348RfmGsm());
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
            routine.append(this.sendEnvelopeRfmGsm("rfmGsmCase7"));
        }
        else {
            routine.append(this.updateSMSRecordRfmGsm("rfmGsmUpdateRecordCase7"));
        }

        routine.append(
            "\n; increment counter by one\n"
            + ".INCREASE_BUFFER L(04:05) 0001\n"
        );


        return routine.toString();
    }

    private String rfmGsmCase8(SCP80Keyset cipherKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel msl) {
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

    private String sendEnvelopeRfmGsm(String rfmGsmCases){

        StringBuilder routine = new StringBuilder();

        routine.append(
            "; send envelope\n"
            + "A0 C2 00 00 G J (9XXX)\n"
            + ".CLEAR_SCRIPT\n"
            + "; check PoR\n"
            + "A0 C0 00 00 W(2;1) [" + this.otaPorSettingRfmGsm(rfmGsmCases)+ "] (9000) ; PoR OK\n"
        );

        return routine.toString();
    }

    private String updateSMSRecordRfmGsm(String rfmGsmCases) {
        StringBuilder updateSMSRecord = new StringBuilder();

        updateSMSRecord.append(
            "\n; update EF SMS record\n"
            + apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getGpin() + " (9000)\n"
            + "A0 A4 00 00 02 7F10 (9FXX) ;select DF Telecom\n"
            + "A0 A4 00 00 02 6F3C (9F0F) ;select EF SMS\n"
            + "A0 DC 01 04 G J (91XX) ;update EF SMS\n"
            + ".CLEAR_SCRIPT\n"
            + "\n;Check SMS Content\n" // CHECK_SMS_CONTENT
            + "A0 B2 01 04 B0\n" // READ_SMS
            + "; check PoR\n"
            + "A0 12 00 00 W(2;1) [" + this.otaPorSettingRfmGsmUpdateRecord(rfmGsmCases) + " ] (9000)\n"
            + "A0 14 00 00 0C 8103011300 82028183 830100 (9000)\n"
        );

        return updateSMSRecord.toString();
    }

    private String otaPorSettingRfmGsm(String rfmGsmCases){
        String  POR_OK, POR_NOT_OK, BAD_CASE_WRONG_KEYSET,
                BAD_CASE_WRONG_CLASS_3G, BAD_CASE_WRONG_CLASS_2G,
                BAD_CASE_COUNTER_LOW,
                BAD_CASE_WRONG_KEY_VALUE, BAD_CASE_INSUFFICIENT_MSL, BAD_CASE_WRONG_TAR, NEGATIVE_CASE;

        String result_set = "";

        POR_OK                      = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 90 00";
        POR_NOT_OK                  = "";
        BAD_CASE_WRONG_KEYSET       = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 06";
        BAD_CASE_WRONG_CLASS_3G     = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 6E 00";
        BAD_CASE_WRONG_CLASS_2G     = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 6D 00";
        BAD_CASE_COUNTER_LOW        = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 02";
        BAD_CASE_WRONG_KEY_VALUE    = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 01";
        BAD_CASE_INSUFFICIENT_MSL   = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 0A";
        BAD_CASE_WRONG_TAR          = "XX XX XX XX XX XX B0 FF FF XX XX XX XX XX XX 09";
        NEGATIVE_CASE               = "XX XX XX XX XX XX %TAR XX XX XX XX XX XX 00 XX 98 04";
        //BAD_CASE_WRONG_TAR          = "D0 30 81 XX XX 13 XX 82 02 81 83 85 XX 86 "+scAddress+" 8B XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX B0 FF FF XX XX XX XX XX XX 09";

        switch (rfmGsmCases){
            case "rfmGsmCase1" :
                result_set = POR_OK;
                break;
            case "rfmGsmCase2" :
                result_set = BAD_CASE_WRONG_KEYSET;
                break;
            case "rfmGsmCase3" :
                result_set =  BAD_CASE_WRONG_CLASS_2G;
                break;
            case "rfmGsmCase5" :
                result_set = BAD_CASE_COUNTER_LOW;
                break;
            case "rfmGsmCase6" :
                result_set = BAD_CASE_WRONG_KEY_VALUE;
                break;
            case "rfmGsmCase7" :
                result_set = BAD_CASE_INSUFFICIENT_MSL;
                break;
            case "rfmGsmNegativeCase" :
                result_set = NEGATIVE_CASE;
                break;
        }

        return result_set;

    }

    private String otaPorSettingRfmGsmUpdateRecord(String rfmGsmCases){
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
        NEGATIVE_CASE               = tag33UpdateRecord+" XX XX 98 04";
        //BAD_CASE_WRONG_TAR          = "D0 30 81 XX XX 13 XX 82 02 81 83 85 XX 86 "+scAddress+" 8B XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX B0 FF FF XX XX XX XX XX XX 09";

        switch (rfmGsmCases){
            case "rfmGsmUpdateRecordCase1" :
                result_set = POR_OK;
                break;
            case "rfmGsmUpdateRecordCase2" :
                result_set = BAD_CASE_WRONG_KEYSET;
                break;
            case "rfmGsmUpdateRecordCase3" :
                result_set =  BAD_CASE_WRONG_CLASS_2G;
                break;
            case "rfmGsmUpdateRecordCase5" :
                result_set = BAD_CASE_COUNTER_LOW;
                break;
            case "rfmGsmUpdateRecordCase6" :
                result_set = BAD_CASE_WRONG_KEY_VALUE;
                break;
            case "rfmGsmUpdateRecordCase7" :
                result_set = BAD_CASE_INSUFFICIENT_MSL;
                break;
            case "rfmGsmUpdateRecordNegativeCase" :
                result_set = NEGATIVE_CASE;
                break;
        }

        return result_set;

    }

    private String porFormatRfmGsm(String porformat){
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

    private String init_SMS_0348RfmGsm(){
        StringBuilder routine = new StringBuilder();

        routine.append(
                ".INIT_SMS_0348\n"
                + ".CHANGE_FIRST_BYTE " + root.getRunSettings().getSmsUpdate().getUdhiFirstByte() + "\n"
                + ".CHANGE_SC_ADDRESS " + root.getRunSettings().getSmsUpdate().getScAddress() + "\n"
                + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
                + ".CHANGE_POR_FORMAT " + this.porFormatRfmGsm(root.getRunSettings().getSmsUpdate().getPorFormat()) + "\n"
        );

        return routine.toString();

    }

    private String init_ENV_0348RfmGsm(){

        StringBuilder routine = new StringBuilder();

        routine.append(
            ".INIT_ENV_0348\n"
            + ".CHANGE_TP_PID " + root.getRunSettings().getSmsUpdate().getTpPid() + "\n"
        );

        return routine.toString();
    }

    // Separate some looped on method()

    private String useRfmGsmEfAccessDomain(RfmGsm rfmGsm){

        StringBuilder accessDomain = new StringBuilder();

        if (rfmGsm.getRfmGsmAccessDomain().isUseAlways()){
            accessDomain.append(".DEFINE %EF_ID_GSM_ALW " + rfmGsm.getCustomTargetEfAlw() + "; EF protected by Always\n");
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc1()){
            accessDomain.append(".DEFINE %EF_ID_GSM_ADM1 " + rfmGsm.getCustomTargetEfIsc1() + "; EF protected by ADM1\n");
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc2()){
            accessDomain.append(".DEFINE %EF_ID_GSM_ADM2 " + rfmGsm.getCustomTargetEfIsc2() + "; EF protected by ADM2\n");
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc3()){
            accessDomain.append(".DEFINE %EF_ID_GSM_ADM3 " + rfmGsm.getCustomTargetEfIsc3() + "; EF protected by ADM3\n");
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc4()){
            accessDomain.append(".DEFINE %EF_ID_GSM_ADM4 " + rfmGsm.getCustomTargetEfIsc4() + "; EF protected by ADM4\n");
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseGPin1()){
            accessDomain.append(".DEFINE %EF_ID_GSM_PIN1 " + rfmGsm.getCustomTargetEfGPin1() + "; EF protected by PIN1\n");
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseLPin1()){
            accessDomain.append(".DEFINE %EF_ID_GSM_PIN2 " + rfmGsm.getCustomTargetEfLPin1() + "; EF protected by PIN2\n");
        }

        return accessDomain.toString();
    }

    private String useRfmGsmEfBadCaseAccessDomain(RfmGsm rfmGsm){

        StringBuilder badCaseAccessDomain = new StringBuilder();

        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseAlways()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_GSM_ERR_ALW " + rfmGsm.getCustomTargetEfBadCaseIsc1() + "; EF is not protected by Always\n");
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc1()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_GSM_ERR_ADM1 " + rfmGsm.getCustomTargetEfBadCaseIsc1() + "; EF is not protected by ADM1\n");
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc2()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_GSM_ERR_ADM2 " + rfmGsm.getCustomTargetEfBadCaseIsc2() + "; EF is not protected by ADM2\n");
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc3()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_GSM_ERR_ADM3 " + rfmGsm.getCustomTargetEfBadCaseIsc3() + "; EF is not protected by ADM3\n");
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc4()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_GSM_ERR_ADM4 " + rfmGsm.getCustomTargetEfBadCaseIsc4() + "; EF is not protected by ADM4\n");
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseGPin1()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_GSM_ERR_PIN1 " + rfmGsm.getCustomTargetEfBadCaseGPin1() + "; EF is not protected by PIN1\n");
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseLPin1()){
            badCaseAccessDomain.append(".DEFINE %EF_ID_GSM_ERR_PIN2 " + rfmGsm.getCustomTargetEfBadCaseLPin1() + "; EF is not protected by PIN2\n");
        }

        return badCaseAccessDomain.toString();
    }

    private String rfmGsmDefineTargetFiles(RfmGsm rfmGsm){

        StringBuilder targetFiles = new StringBuilder();

        targetFiles.append(
            "\n; TAR is configured for full access\n"
            + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfGsm() + "\n"
            + ".DEFINE %EF_ID " + rfmGsm.getTargetEf() + "\n"
//            + ".DEFINE %EF_ID_ERR " + rfmGsm.getTargetEfBadCase() + "\n"
        );

        return targetFiles.toString();
    }

    private String useRfmGsmDefineTargetFilesAccessDomain(RfmGsm rfmGsm){

        StringBuilder targetFiles = new StringBuilder();

        targetFiles.append(
            "\n; TAR is configured with access domain\n"
            + ".DEFINE %DF_ID " + root.getRunSettings().getCardParameters().getDfGsm() + "\n"
            + (this.useRfmGsmEfAccessDomain(rfmGsm))
            + (this.useRfmGsmEfBadCaseAccessDomain(rfmGsm))
        );

        return targetFiles.toString();
    }

    private String rfmGsmCheckInitialContentEf(){

        StringBuilder checkInitialContent = new StringBuilder();

        checkInitialContent.append(
            "\n.POWER_ON\n"
            + "; check initial content\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
            apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
            +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
            +"A0 B0 00 00 01 (9000)\n"
            +".DEFINE %EF_CONTENT R\n"
        );

        return checkInitialContent.toString();

    }

    private String useRfmGsmCheckInitialContentEfAccessDomain(RfmGsm rfmGsm){

        StringBuilder checkInitialContent = new StringBuilder();

        checkInitialContent.append(
            "\n.POWER_ON\n"
            + "; check initial content\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContent.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContent.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContent.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContent.append(
            apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );

        if (rfmGsm.getRfmGsmAccessDomain().isUseAlways()){
            checkInitialContent.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfAlw() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ALW (9F0F)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT_ALW R\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc1()){
            checkInitialContent.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM1 (9F0F)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT_ADM1 R\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc2()){
            checkInitialContent.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM2 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ADM2 R\n"

            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc3()){
            checkInitialContent.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM3 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ADM3 R\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc4()){
            checkInitialContent.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM4 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ADM4 R\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseGPin1()){
            checkInitialContent.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_PIN1 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_PIN1 R\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseLPin1()){
            checkInitialContent.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_PIN2 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_PIN2 R\n"
            );
        }

        return checkInitialContent.toString();
    }

    private String useRfmGsmCheckInitialContentEfBadCaseAccessDomain(RfmGsm rfmGsm){

        StringBuilder checkInitialContentBadCase = new StringBuilder();

        checkInitialContentBadCase.append(
            "\n.POWER_ON\n"
            + "; check initial content\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkInitialContentBadCase.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkInitialContentBadCase.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkInitialContentBadCase.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkInitialContentBadCase.append(
                apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
                        + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
                        + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );

        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseAlways()){
            checkInitialContentBadCase.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfBadCaseAlw() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ALW (9F0F)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT_ERR_ALW R\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc1()){
            checkInitialContentBadCase.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfBadCaseIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM1 (9F0F)\n"
                + "A0 B0 00 00 01 (9000)\n"
                + ".DEFINE %EF_CONTENT_ERR_ADM1 R\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc2()){
            checkInitialContentBadCase.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfBadCaseIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM2 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ERR_ADM2 R\n"

            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc3()){
            checkInitialContentBadCase.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfBadCaseIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM3 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ERR_ADM3 R\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc4()){
            checkInitialContentBadCase.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfBadCaseIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM4 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ERR_ADM4 R\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseGPin1()){
            checkInitialContentBadCase.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfBadCaseGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_PIN1 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ERR_PIN1 R\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseLPin1()){
            checkInitialContentBadCase.append(
                "; check content EF-" + rfmGsm.getCustomTargetEfBadCaseLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_PIN2 (9F0F)\n"
                +"A0 B0 00 00 01 (9000)\n"
                +".DEFINE %EF_CONTENT_ERR_PIN2 R\n"
            );
        }

        return checkInitialContentBadCase.toString();
    }

    private String rfmGsmCommandViaOta(){

        StringBuilder commandOta = new StringBuilder();

        commandOta.append("\n; command(s) sent via OTA\n");

        commandOta.append(
            ".SET_BUFFER J A0 A4 00 00 02 %EF_ID ; select EF\n"
            + ".APPEND_SCRIPT J\n"
            + ".SET_BUFFER J A0 D6 00 00 <?> AA ; update binary\n"
            + ".APPEND_SCRIPT J\n"
        );

        return commandOta.toString();
    }

    private String useRfmGsmCommandViaOtaAccessDomain(RfmGsm rfmGsm){

        StringBuilder commandOta = new StringBuilder();

        commandOta.append("\n; command(s) sent via OTA\n");


        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc1()){
            commandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ADM1 ; select EF on ADM1\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A1 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc2()){
            commandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ADM2 ; select EF on ADM2\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A2 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc3()){
            commandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ADM3 ; select EF on ADM3\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A3 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc4()){
            commandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ADM4 ; select EF on ADM4\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A4 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseGPin1()){
            commandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_PIN1 ; select EF on PIN1\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A5 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseLPin1()){
            commandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_PIN2 ; select EF on PIN2\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A6 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseAlways()){
            commandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ALW ; select EF on Always\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 B0 00 00 02 ; read binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }

        return commandOta.toString();
    }

    private String useRfmGsmCommandViaOtaBadCaseAccessDomain(RfmGsm rfmGsm){

        StringBuilder badCasecommandOta = new StringBuilder();

        badCasecommandOta.append("\n; command(s) sent via OTA\n");

        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc1()){
            badCasecommandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM1 ; select EF on ADM1\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A1 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc2()){
            badCasecommandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM2 ; select EF on ADM2\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A2 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc3()){
            badCasecommandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM3 ; select EF on ADM3\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A3 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc4()){
            badCasecommandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM4 ; select EF on ADM4\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A4 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseGPin1()){
            badCasecommandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ERR_PIN1 ; select EF on PIN1\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A5 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseLPin1()){
            badCasecommandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ERR_PIN2 ; select EF on PIN2\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 D6 00 00 <?> A6 ; update binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }
        if (rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseAlways()){
            badCasecommandOta.append(
                ".SET_BUFFER J A0 A4 00 00 02 %EF_ID_GSM_ERR_ALW ; select EF on Always\n"
                + ".APPEND_SCRIPT J\n"
                + ".SET_BUFFER J A0 B0 00 00 02 ; read binary\n"
                + ".APPEND_SCRIPT J\n"
            );
        }

        return badCasecommandOta.toString();
    }

    private String rfmGsmCheckUpdateEfDone(){

        StringBuilder checkUpdateHasBeenDone = new StringBuilder();

        checkUpdateHasBeenDone.append(
            "\n; check update has been done on EF\n"
            + ".POWER_ON\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkUpdateHasBeenDone.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkUpdateHasBeenDone.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkUpdateHasBeenDone.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkUpdateHasBeenDone.append(
            apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
            +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
            + "A0 B0 00 00 01 [AA] (9000)\n"
        );

        return checkUpdateHasBeenDone.toString();

    }

    private String useRfmGsmCheckUpdateEfDoneAccessDomain(RfmGsm rfmGsm){

        StringBuilder checkUpdateHasBeenDone = new StringBuilder();

        checkUpdateHasBeenDone.append(
            "\n; check update has been done on EF\n"
            + ".POWER_ON\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkUpdateHasBeenDone.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkUpdateHasBeenDone.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkUpdateHasBeenDone.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkUpdateHasBeenDone.append(
            apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );

        if(rfmGsm.getRfmGsmAccessDomain().isUseAlways()){
            checkUpdateHasBeenDone.append(
                "; check Read Binary on EF-" + rfmGsm.getCustomTargetEfAlw() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ALW (9F0F)\n"
                + "A0 B0 00 00 02 [%EF_CONTENT_ALW] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmAccessDomain().isUseIsc1()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmGsm.getCustomTargetEfIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM1 (9F0F)\n"
                + "A0 B0 00 00 01 [A1] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmAccessDomain().isUseIsc2()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmGsm.getCustomTargetEfIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM2 (9F0F)\n"
                + "A0 B0 00 00 01 [A2] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmAccessDomain().isUseIsc3()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmGsm.getCustomTargetEfIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM3 (9F0F)\n"
                + "A0 B0 00 00 01 [A3] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmAccessDomain().isUseIsc4()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmGsm.getCustomTargetEfIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM4 (9F0F)\n"
                + "A0 B0 00 00 01 [A4] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmAccessDomain().isUseGPin1()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmGsm.getCustomTargetEfGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_PIN1 (9F0F)\n"
                + "A0 B0 00 00 01 [A5] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmAccessDomain().isUseLPin1()){
            checkUpdateHasBeenDone.append(
                "; check update on EF-" + rfmGsm.getCustomTargetEfLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_PIN2 (9F0F)\n"
                + "A0 B0 00 00 01 [A6] (9000)\n"
            );
        }

        return checkUpdateHasBeenDone.toString();
    }

    private String useRfmGsmCheckUpdateEfFailedAccessDomain(RfmGsm rfmGsm){

        StringBuilder checkUpdateHasFailed = new StringBuilder();

        checkUpdateHasFailed.append(
            "\n; check update has failed on EF\n"
            + ".POWER_ON\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            checkUpdateHasFailed.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            checkUpdateHasFailed.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            checkUpdateHasFailed.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        checkUpdateHasFailed.append(
            apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );

        if(rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseAlways()){
            checkUpdateHasFailed.append(
                "; check Read Binary on EF-" + rfmGsm.getCustomTargetEfBadCaseAlw() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ERR_ALW (9F0F)\n"
                + "A0 B0 00 00 02 [%EF_CONTENT_ERR_ALW] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc1()){
            checkUpdateHasFailed.append(
                "; check update failed on EF-" + rfmGsm.getCustomTargetEfBadCaseIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM1 (9F0F)\n"
                + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM1] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc2()){
            checkUpdateHasFailed.append(
                "; check update failed on EF-" + rfmGsm.getCustomTargetEfBadCaseIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM2 (9F0F)\n"
                + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM2] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc3()){
            checkUpdateHasFailed.append(
                "; check update failed on EF-" + rfmGsm.getCustomTargetEfBadCaseIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM3 (9F0F)\n"
                + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM3] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc4()){
            checkUpdateHasFailed.append(
                "; check update failed on EF-" + rfmGsm.getCustomTargetEfBadCaseIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_ADM4 (9F0F)\n"
                + "A0 B0 00 00 01 [%EF_CONTENT_ERR_ADM4] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseGPin1()){
            checkUpdateHasFailed.append(
                "; check update failed on EF-" + rfmGsm.getCustomTargetEfBadCaseGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_PIN1 (9F0F)\n"
                + "A0 B0 00 00 01 [%EF_CONTENT_ERR_PIN1] (9000)\n"
            );
        }
        if(rfmGsm.getRfmGsmBadCaseAccessDomain().isUseBadCaseLPin1()){
            checkUpdateHasFailed.append(
                "; check update failed on EF-" + rfmGsm.getCustomTargetEfBadCaseLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ERR_PIN2 (9F0F)\n"
                + "A0 B0 00 00 01 [%EF_CONTENT_ERR_PIN1] (9000)\n"
            );
        }

        return checkUpdateHasFailed.toString();
    }

    private String rfmGsmRestoreRfmGsmInitialContentEf(){

        StringBuilder restoreInitialContent = new StringBuilder();

        restoreInitialContent.append(
            "\n; restore initial content of EF\n"
            + ".POWER_ON\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            restoreInitialContent.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            restoreInitialContent.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            restoreInitialContent.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        restoreInitialContent.append(
            apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
            +"A0 A4 00 00 02 %EF_ID (9F0F)\n"
            + "A0 D6 00 00 01 %EF_CONTENT (9000)\n"
        );

        return restoreInitialContent.toString();

    }

    private String useRfmGsmRestoreRfmGsmInitialContentEfAccessDomain(RfmGsm rfmGsm){

        StringBuilder restoreInitialContent = new StringBuilder();

        restoreInitialContent.append(
            "\n; restore initial content of EF\n"
            + ".POWER_ON\n"
            + apduService.verify2gAdm1() + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
        );

        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            restoreInitialContent.append(apduService.verify2gAdm2() + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            restoreInitialContent.append(apduService.verify2gAdm3() + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            restoreInitialContent.append(apduService.verify2gAdm4() + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");
        restoreInitialContent.append(
            apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n"
            + apduService.verifyPin2() + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n"
            + "A0 A4 00 00 02 %DF_ID (9FXX)\n"
        );


        if(rfmGsm.getRfmGsmAccessDomain().isUseAlways()){
            restoreInitialContent.append(
                "; check Read Binary on EF-" + rfmGsm.getCustomTargetEfIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ALW (9F0F)\n"
                + "A0 B0 00 00 02 [%EF_CONTENT_ALW] (9000) ; Read Binary\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc1()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmGsm.getCustomTargetEfIsc1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM1 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM1 (9000)\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc2()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmGsm.getCustomTargetEfIsc2() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM2 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM2 (9000)\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc3()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmGsm.getCustomTargetEfIsc3() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM3 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM3 (9000)\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseIsc4()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmGsm.getCustomTargetEfIsc4() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_ADM4 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_ADM4 (9000)\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseGPin1()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmGsm.getCustomTargetEfGPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_PIN1 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_PIN1 (9000)\n"
            );
        }
        if (rfmGsm.getRfmGsmAccessDomain().isUseLPin1()){
            restoreInitialContent.append(
                "; restore content EF-" + rfmGsm.getCustomTargetEfLPin1() +  "\n"
                +"A0 A4 00 00 02 %EF_ID_GSM_PIN2 (9F0F)\n"
                + "A0 D6 00 00 01 %EF_CONTENT_PIN2 (9000)\n"
            );
        }

        return restoreInitialContent.toString();
    }

}
