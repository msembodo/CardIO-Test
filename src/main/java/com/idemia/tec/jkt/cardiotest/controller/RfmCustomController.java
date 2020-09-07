package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RfmCustomController {

    @FXML private CheckBox chkIncludeRfmCustom;
    @FXML private CheckBox chkIncludeRfmCustomUpdateRecord;
    @FXML private CheckBox chkIncludeRfmCustomExpandedMode;
    @FXML private TextField txtRfmCustomMslByte;
    @FXML private ComboBox<String> cmbRfmCustomCipherAlgo;
    @FXML private CheckBox chkRfmCustomUseCipher;
    @FXML private ComboBox<String> cmbRfmCustomAuthVerif;
    @FXML private ComboBox<String> cmbRfmCustomSigningAlgo;
    @FXML private ComboBox<String> cmbRfmCustomPorRequirement;
    @FXML private ComboBox<String> cmbRfmCustomPorSecurity;
    @FXML private CheckBox chkRfmCustomCipherPor;
    @FXML private ComboBox<String> cmbRfmCustomCounterCheck;
    @FXML private TextField txtRfmCustomTar;
    @FXML private TextField txtRfmCustomTargetDf;
    @FXML private TextField txtRfmCustomTargetEf;
    @FXML private TextField txtRfmCustomTargetEfBadCase;
    @FXML private CheckBox chkRfmCustomFullAccess;
    @FXML private Label lblRfmCustomCustomTarget;
    @FXML private ComboBox<String> cmbRfmCustomCustomTargetAcc;
    @FXML private TextField txtRfmCustomCustomTargetEf;
    @FXML private Label lblRfmCustomCustomTargetBadCase;
    @FXML private ComboBox<String> cmbRfmCustomCustomTargetAccBadCase;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCase;
    @FXML private CheckBox chkRfmCustomUseSpecificKeyset;
    @FXML private Label lblRfmCustomCipheringKeyset;
    @FXML private ComboBox<String> cmbRfmCustomCipheringKeyset;
    @FXML private Label lblRfmCustomKic;
    @FXML private CheckBox chkRfmCustomCustomKic;
    @FXML private TextField txtRfmCustomCustomKic;
    @FXML private Label lblRfmCustomAuthKeyset;
    @FXML private ComboBox<String> cmbRfmCustomAuthKeyset;
    @FXML private Label lblRfmCustomKid;
    @FXML private CheckBox chkRfmCustomCustomKid;
    @FXML private TextField txtRfmCustomCustomKid;
    @FXML private TextField txtCustomRfmDesc;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public RfmCustomController() {}

    @FXML public void initialize() {
        chkIncludeRfmCustom.setSelected(root.getRunSettings().getRfmCustom().isIncludeRfmCustom());
        handleIncludeRfmCustomCheck();

        chkIncludeRfmCustomUpdateRecord.setSelected(root.getRunSettings().getRfmCustom().isIncludeRfmCustomUpdateRecord());
        handleIncludeRfmCustomUpdateRecordCheck();

        chkIncludeRfmCustomExpandedMode.setSelected(root.getRunSettings().getRfmCustom().isIncludeRfmCustomExpandedMode());
        handleIncludeRfmCustomExpandedModeCheck();

        // RFM CUSTOM MSL

        txtRfmCustomMslByte.setText(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getComputedMsl());

        chkRfmCustomUseCipher.setSelected(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().isUseCipher());

        // initialize list of cipher algorithm
        List<String> cipherAlgos = new ArrayList<>();
        cipherAlgos.add("as defined in keyset");
        cipherAlgos.add("no cipher");
        cipherAlgos.add("DES - CBC");
        cipherAlgos.add("AES - CBC");
        cipherAlgos.add("XOR");
        cipherAlgos.add("3DES - CBC 2 keys");
        cipherAlgos.add("3DES - CBC 3 keys");
        cipherAlgos.add("DES - ECB");
        if (!(cmbRfmCustomCipherAlgo.getItems().size() > 0)) cmbRfmCustomCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRfmCustomCipherAlgo.setValue(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        if (!(cmbRfmCustomAuthVerif.getItems().size() > 0)) cmbRfmCustomAuthVerif.getItems().addAll(authVerifs);
        cmbRfmCustomAuthVerif.setValue(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getAuthVerification());

        // initialize list of signing algorithm
        List<String> signingAlgos = new ArrayList<>();
        signingAlgos.add("as defined in keyset");
        signingAlgos.add("no algorithm");
        signingAlgos.add("DES - CBC");
        signingAlgos.add("AES - CMAC");
        signingAlgos.add("XOR");
        signingAlgos.add("3DES - CBC 2 keys");
        signingAlgos.add("3DES - CBC 3 keys");
        signingAlgos.add("DES - ECB");
        signingAlgos.add("CRC32 (may be X5h)");
        signingAlgos.add("CRC32 (may be X0h)");
        signingAlgos.add("ISO9797 Algo 3 (auth value 8 byte)");
        signingAlgos.add("ISO9797 Algo 3 (auth value 4 byte)");
        signingAlgos.add("ISO9797 Algo 4 (auth value 4 byte)");
        signingAlgos.add("ISO9797 Algo 4 (auth value 8 byte)");
        signingAlgos.add("CRC16");
        if (!(cmbRfmCustomSigningAlgo.getItems().size() > 0)) cmbRfmCustomSigningAlgo.getItems().addAll(signingAlgos);
        cmbRfmCustomSigningAlgo.setValue(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        if (!(cmbRfmCustomCounterCheck.getItems().size() > 0)) cmbRfmCustomCounterCheck.getItems().addAll(counterCheckings);
        cmbRfmCustomCounterCheck.setValue(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        if (!(cmbRfmCustomPorRequirement.getItems().size() > 0)) cmbRfmCustomPorRequirement.getItems().addAll(porRequirements);
        cmbRfmCustomPorRequirement.setValue(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getPorRequirement());

        chkRfmCustomCipherPor.setSelected(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        if (!(cmbRfmCustomPorSecurity.getItems().size() > 0)) cmbRfmCustomPorSecurity.getItems().addAll(porSecurities);
        cmbRfmCustomPorSecurity.setValue(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getPorSecurity());

        // RFM CUSTOM parameters

        txtRfmCustomTar.setText(root.getRunSettings().getRfmCustom().getTar());

        txtRfmCustomTargetDf.setText(root.getRunSettings().getRfmCustom().getTargetDf());
        txtCustomRfmDesc.setText(root.getRunSettings().getRfmCustom().getCustomRfmDesc());

        txtRfmCustomTargetEf.setText(root.getRunSettings().getRfmCustom().getTargetEf());
        txtRfmCustomTargetEfBadCase.setText(root.getRunSettings().getRfmCustom().getTargetEfBadCase());

        if (root.getRunSettings().getRfmCustom().getCustomTargetAcc() != null)
            cmbRfmCustomCustomTargetAcc.getSelectionModel().select(root.getRunSettings().getRfmCustom().getCustomTargetAcc());
        if (root.getRunSettings().getRfmCustom().getTargetDf() != null)
            txtRfmCustomTargetDf.setText(root.getRunSettings().getRfmCustom().getTargetDf());

        if (root.getRunSettings().getRfmCustom().getCustomRfmDesc() != null)
            txtCustomRfmDesc.setText(root.getRunSettings().getRfmCustom().getCustomRfmDesc());

        if (root.getRunSettings().getRfmCustom().getCustomTargetEf() != null)
            txtRfmCustomCustomTargetEf.setText(root.getRunSettings().getRfmCustom().getCustomTargetEf());

        if (root.getRunSettings().getRfmCustom().getCustomTargetAccBadCase() != null)
            cmbRfmCustomCustomTargetAccBadCase.getSelectionModel().select(root.getRunSettings().getRfmCustom().getCustomTargetAccBadCase());
        if (root.getRunSettings().getRfmCustom().getCustomTargetEfBadCase() != null)
            txtRfmCustomCustomTargetEfBadCase.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCase());

        chkRfmCustomFullAccess.setSelected(root.getRunSettings().getRfmCustom().isFullAccess());
        handleRfmCustomFullAccessCheck();

        // initialize list of available keysets for RFM CUSTOM

        if (root.getRunSettings().getRfmCustom().getCipheringKeyset() != null) {
            cmbRfmCustomCipheringKeyset.setValue(root.getRunSettings().getRfmCustom().getCipheringKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmCustom().getCipheringKeyset().getKeysetName())) {
                    lblRfmCustomKic.setText("Kic (hex): " + keyset.getComputedKic());
                    break;
                }
            }
            chkRfmCustomCustomKic.setSelected(root.getRunSettings().getRfmCustom().getCipheringKeyset().isCustomKic());
            handleRfmCustomCustomKicCheck();
            if (chkRfmCustomCustomKic.isSelected())
                txtRfmCustomCustomKic.setText(root.getRunSettings().getRfmCustom().getCipheringKeyset().getComputedKic());
            else
                txtRfmCustomCustomKic.setText("");
        }

        if (root.getRunSettings().getRfmCustom().getAuthKeyset() != null) {
            cmbRfmCustomAuthKeyset.setValue(root.getRunSettings().getRfmCustom().getAuthKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmCustom().getAuthKeyset().getKeysetName())) {
                    lblRfmCustomKid.setText("Kid (hex): " + keyset.getComputedKid());
                    break;
                }
            }
            chkRfmCustomCustomKid.setSelected(root.getRunSettings().getRfmCustom().getAuthKeyset().isCustomKid());
            handleRfmCustomCustomKidCheck();
            if (chkRfmCustomCustomKid.isSelected())
                txtRfmCustomCustomKid.setText(root.getRunSettings().getRfmCustom().getAuthKeyset().getComputedKid());
            else
                txtRfmCustomCustomKid.setText("");
        }

        chkRfmCustomUseSpecificKeyset.setSelected(root.getRunSettings().getRfmCustom().isUseSpecificKeyset());
        handleRfmCustomUseSpecificKeysetCheck();
    }

    @FXML private void handleCustomTargetAccContextMenu() { cmbRfmCustomCustomTargetAcc.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleCustomTargetAccBadCaseContextMenu() { cmbRfmCustomCustomTargetAccBadCase.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleCipherKeysetContextMenu() { cmbRfmCustomCipheringKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleAuthKeysetContextMenu() { cmbRfmCustomAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

    @FXML private void handleIncludeRfmCustomCheck() {
        if (chkIncludeRfmCustom.isSelected())
            root.getMenuRfmCustom().setDisable(false);
        else
            root.getMenuRfmCustom().setDisable(true);
    }
    @FXML private void handleIncludeRfmCustomUpdateRecordCheck() {
        if (chkIncludeRfmCustomUpdateRecord.isSelected())
            root.getMenuRfmCustomUpdateRecord().setDisable(false);
        else
            root.getMenuRfmCustomUpdateRecord().setDisable(true);
    }
    @FXML private void handleIncludeRfmCustomExpandedModeCheck() {
        if (chkIncludeRfmCustomExpandedMode.isSelected())
            root.getMenuRfmCustomExpandedMode().setDisable(false);
        else
            root.getMenuRfmCustomExpandedMode().setDisable(true);
    }

    @FXML private void handleRfmCustomFullAccessCheck() {
        if (chkRfmCustomFullAccess.isSelected()) {
            //lblRfmCustomCustomTarget.setDisable(true);
            cmbRfmCustomCustomTargetAcc.setDisable(true);
            txtRfmCustomCustomTargetEf.setDisable(true);
            //lblRfmCustomCustomTargetBadCase.setDisable(true);
            cmbRfmCustomCustomTargetAccBadCase.setDisable(true);
            txtRfmCustomCustomTargetEfBadCase.setDisable(true);
            txtRfmCustomTargetEf.setDisable(false);
            txtRfmCustomTargetEfBadCase.setDisable(false);
        } else {
            //lblRfmCustomCustomTarget.setDisable(false);
            cmbRfmCustomCustomTargetAcc.setDisable(false);
            txtRfmCustomCustomTargetEf.setDisable(false);
            //lblRfmCustomCustomTargetBadCase.setDisable(false);
            cmbRfmCustomCustomTargetAccBadCase.setDisable(false);
            txtRfmCustomCustomTargetEfBadCase.setDisable(false);
            txtRfmCustomTargetEf.setDisable(true);
            txtRfmCustomTargetEfBadCase.setDisable(true);
        }
    }

    @FXML private void handleRfmCustomUseSpecificKeysetCheck() {
        if (chkRfmCustomUseSpecificKeyset.isSelected()) {
            lblRfmCustomCipheringKeyset.setDisable(false);
            cmbRfmCustomCipheringKeyset.setDisable(false);
            lblRfmCustomKic.setDisable(false);
            chkRfmCustomCustomKic.setDisable(false);
            txtRfmCustomCustomKic.setDisable(false);
            lblRfmCustomAuthKeyset.setDisable(false);
            cmbRfmCustomAuthKeyset.setDisable(false);
            lblRfmCustomKid.setDisable(false);
            chkRfmCustomCustomKid.setDisable(false);
            txtRfmCustomCustomKid.setDisable(false);
        }
        else {
            lblRfmCustomCipheringKeyset.setDisable(true);
            cmbRfmCustomCipheringKeyset.setDisable(true);
            lblRfmCustomKic.setDisable(true);
            chkRfmCustomCustomKic.setDisable(true);
            txtRfmCustomCustomKic.setDisable(true);
            lblRfmCustomAuthKeyset.setDisable(true);
            cmbRfmCustomAuthKeyset.setDisable(true);
            lblRfmCustomKid.setDisable(true);
            chkRfmCustomCustomKid.setDisable(true);
            txtRfmCustomCustomKid.setDisable(true);
        }
    }

    @FXML private void handleRfmCustomCipheringKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmCustomCipheringKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmCustomKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML private void handleRfmCustomAuthKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmCustomAuthKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmCustomKid.setText("Kid (hex): " + keyset.getComputedKid());
                break;
            }
        }
    }

    @FXML private void handleRfmCustomCustomKicCheck() {
        if (chkRfmCustomCustomKic.isSelected())
            txtRfmCustomCustomKic.setDisable(false);
        else
            txtRfmCustomCustomKic.setDisable(true);
    }

    @FXML private void handleRfmCustomCustomKidCheck() {
        if (chkRfmCustomCustomKid.isSelected())
            txtRfmCustomCustomKid.setDisable(false);
        else
            txtRfmCustomCustomKid.setDisable(true);
    }

    @FXML private void handleButtonSetRfmCustomMsl() {
        String mslHexStr = txtRfmCustomMslByte.getText();
        int mslInteger = Integer.parseInt(mslHexStr, 16);
        if (mslInteger > 31) {
            // MSL integer shoould not be higher than 31 (0x1F)
            Alert mslAlert = new Alert(Alert.AlertType.ERROR);
            mslAlert.initModality(Modality.APPLICATION_MODAL);
//            mslAlert.initOwner(application.getPrimaryStage());
            mslAlert.setTitle("Minimum Security Level");
            mslAlert.setHeaderText("Invalid MSL");
            mslAlert.setContentText("MSL value should not exceed '1F'");
            mslAlert.showAndWait();
        } else {
            // set components accordingly
            if (mslInteger == 0) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkRfmCustomUseCipher.setSelected(false);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("No verification");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkRfmCustomUseCipher.setSelected(true);
                cmbRfmCustomAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmCustomCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtRfmCustomMslByte.setText(mslHexStr);
        }
    }

    @FXML private void handleRfmCustomUseCipherCheck() {
        if (chkRfmCustomUseCipher.isSelected())
            root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setUseCipher(true);
        else
            root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setUseCipher(false);
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().computeMsl();
        txtRfmCustomMslByte.setText(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmCustomAuthVerifSelection() {
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setAuthVerification(cmbRfmCustomAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().computeMsl();
        txtRfmCustomMslByte.setText(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmCustomCounterCheckingSelection() {
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setCounterChecking(cmbRfmCustomCounterCheck.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().computeMsl();
        txtRfmCustomMslByte.setText(root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().getComputedMsl());
    }


    public void saveControlState() {
        root.getRunSettings().getRfmCustom().setIncludeRfmCustom(chkIncludeRfmCustom.isSelected());
        root.getRunSettings().getRfmCustom().setIncludeRfmCustomUpdateRecord(chkIncludeRfmCustomUpdateRecord.isSelected());
        root.getRunSettings().getRfmCustom().setIncludeRfmCustomExpandedMode(chkIncludeRfmCustomExpandedMode.isSelected());

        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setComputedMsl(txtRfmCustomMslByte.getText());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setUseCipher(chkRfmCustomUseCipher.isSelected());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setCipherAlgo(cmbRfmCustomCipherAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setAuthVerification(cmbRfmCustomAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setSigningAlgo(cmbRfmCustomSigningAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setCounterChecking(cmbRfmCustomCounterCheck.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setPorRequirement(cmbRfmCustomPorRequirement.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setCipherPor(chkRfmCustomCipherPor.isSelected());
        root.getRunSettings().getRfmCustom().getMinimumSecurityLevel().setPorSecurity(cmbRfmCustomPorSecurity.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmCustom().setTar(txtRfmCustomTar.getText());
        root.getRunSettings().getRfmCustom().setTargetDf(txtRfmCustomTargetDf.getText());

        root.getRunSettings().getRfmCustom().setCustomRfmDesc(txtCustomRfmDesc.getText());

        root.getRunSettings().getRfmCustom().setTargetEf(txtRfmCustomTargetEf.getText());
        root.getRunSettings().getRfmCustom().setTargetEfBadCase(txtRfmCustomTargetEfBadCase.getText());
        root.getRunSettings().getRfmCustom().setFullAccess(chkRfmCustomFullAccess.isSelected());
        if (!root.getRunSettings().getRfmCustom().isFullAccess()) {
            root.getRunSettings().getRfmCustom().setCustomTargetAcc(cmbRfmCustomCustomTargetAcc.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmCustom().setTargetDf(txtRfmCustomTargetDf.getText());

            root.getRunSettings().getRfmCustom().setCustomRfmDesc(txtCustomRfmDesc.getText());

            root.getRunSettings().getRfmCustom().setCustomTargetEf(txtRfmCustomCustomTargetEf.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetAccBadCase(cmbRfmCustomCustomTargetAccBadCase.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCase(txtRfmCustomCustomTargetEfBadCase.getText());
        }

        root.getRunSettings().getRfmCustom().setUseSpecificKeyset(chkRfmCustomUseSpecificKeyset.isSelected());
        SCP80Keyset rfmCustomCipheringKeyset = new SCP80Keyset();
        rfmCustomCipheringKeyset.setKeysetName(cmbRfmCustomCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmCustomCipheringKeyset.getKeysetName())) {
                rfmCustomCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmCustomCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmCustomCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmCustomCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmCustomCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmCustomCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmCustomCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmCustomCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmCustomCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmCustomCipheringKeyset.setCustomKic(chkRfmCustomCustomKic.isSelected());
                if (rfmCustomCipheringKeyset.isCustomKic())
                    rfmCustomCipheringKeyset.setComputedKic(txtRfmCustomCustomKic.getText());
                else
                    rfmCustomCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmCustomCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmCustom().setCipheringKeyset(rfmCustomCipheringKeyset);

        SCP80Keyset rfmCustomAuthKeyset = new SCP80Keyset();
        rfmCustomAuthKeyset.setKeysetName(cmbRfmCustomAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmCustomAuthKeyset.getKeysetName())) {
                rfmCustomAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmCustomAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmCustomAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmCustomAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmCustomAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmCustomAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmCustomAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmCustomAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmCustomAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmCustomAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmCustomAuthKeyset.setCustomKid(chkRfmCustomCustomKid.isSelected());
                if (rfmCustomAuthKeyset.isCustomKid())
                    rfmCustomAuthKeyset.setComputedKid(txtRfmCustomCustomKid.getText());
                else
                    rfmCustomAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmCustom().setAuthKeyset(rfmCustomAuthKeyset);
    }

}
