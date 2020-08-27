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
public class RfmUsimController {

    @FXML private CheckBox chkIncludeRfmUsim;
    @FXML private CheckBox chkIncludeRfmUsimUpdateRecord;
    @FXML private CheckBox chkIncludeRfmUsimExpandedMode;
    @FXML private TextField txtRfmUsimMslByte;
    @FXML private ComboBox<String> cmbRfmUsimCipherAlgo;
    @FXML private CheckBox chkRfmUsimUseCipher;
    @FXML private ComboBox<String> cmbRfmUsimAuthVerif;
    @FXML private ComboBox<String> cmbRfmUsimSigningAlgo;
    @FXML private ComboBox<String> cmbRfmUsimPorRequirement;
    @FXML private ComboBox<String> cmbRfmUsimPorSecurity;
    @FXML private CheckBox chkRfmUsimCipherPor;
    @FXML private ComboBox<String> cmbRfmUsimCounterCheck;
    @FXML private TextField txtRfmUsimTar;
    @FXML private TextField txtRfmUsimTargetEf;
    @FXML private TextField txtRfmUsimTargetEfBadCase;
    @FXML private CheckBox chkRfmUsimFullAccess;
    @FXML private Label lblRfmUsimCustomTarget;
    @FXML private ComboBox<String> cmbRfmUsimCustomTargetAcc;
    @FXML private TextField txtRfmUsimCustomTargetEf;
    @FXML private Label lblRfmUsimCustomTargetBadCase;
    @FXML private ComboBox<String> cmbRfmUsimCustomTargetAccBadCase;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCase;
    @FXML private CheckBox chkUseSpecificKeyset;
    @FXML private Label lblRfmUsimCipheringKeyset;
    @FXML private ComboBox<String> cmbRfmUsimCipheringKeyset;
    @FXML private Label lblRfmUsimKic;
    @FXML private CheckBox chkRfmUsimCustomKic;
    @FXML private TextField txtRfmUsimCustomKic;
    @FXML private Label lblRfmUsimAuthKeyset;
    @FXML private ComboBox<String> cmbRfmUsimAuthKeyset;
    @FXML private Label lblRfmUsimKid;
    @FXML private CheckBox chkRfmUsimCustomKid;
    @FXML private TextField txtRfmUsimCustomKid;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public RfmUsimController() {}

    @FXML public void initialize() {
        chkIncludeRfmUsim.setSelected(root.getRunSettings().getRfmUsim().isIncludeRfmUsim());
        handleIncludeRfmUsimCheck();

        chkIncludeRfmUsimUpdateRecord.setSelected(root.getRunSettings().getRfmUsim().isIncludeRfmUsimUpdateRecord());
        handleIncludeRfmUsimUpdateRecordCheck();

        chkIncludeRfmUsimExpandedMode.setSelected(root.getRunSettings().getRfmUsim().isIncludeRfmUsimExpandedMode());
        handleIncludeRfmUsimExpandedModeCheck();

        // RFM USIM MSL

        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());

        chkRfmUsimUseCipher.setSelected(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().isUseCipher());

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
        cmbRfmUsimCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRfmUsimCipherAlgo.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        cmbRfmUsimAuthVerif.getItems().addAll(authVerifs);
        cmbRfmUsimAuthVerif.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getAuthVerification());

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
        cmbRfmUsimSigningAlgo.getItems().addAll(signingAlgos);
        cmbRfmUsimSigningAlgo.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        cmbRfmUsimCounterCheck.getItems().addAll(counterCheckings);
        cmbRfmUsimCounterCheck.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        cmbRfmUsimPorRequirement.getItems().addAll(porRequirements);
        cmbRfmUsimPorRequirement.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getPorRequirement());

        chkRfmUsimCipherPor.setSelected(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        cmbRfmUsimPorSecurity.getItems().addAll(porSecurities);
        cmbRfmUsimPorSecurity.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getPorSecurity());

        // RFM USIM parameters

        txtRfmUsimTar.setText(root.getRunSettings().getRfmUsim().getTar());
        txtRfmUsimTargetEf.setText(root.getRunSettings().getRfmUsim().getTargetEf());
        txtRfmUsimTargetEfBadCase.setText(root.getRunSettings().getRfmUsim().getTargetEfBadCase());

        if (root.getRunSettings().getRfmUsim().getCustomTargetAcc() != null)
            cmbRfmUsimCustomTargetAcc.getSelectionModel().select(root.getRunSettings().getRfmUsim().getCustomTargetAcc());
        if (root.getRunSettings().getRfmUsim().getCustomTargetEf() != null)
            txtRfmUsimCustomTargetEf.setText(root.getRunSettings().getRfmUsim().getCustomTargetEf());

        if (root.getRunSettings().getRfmUsim().getCustomTargetAccBadCase() != null)
            cmbRfmUsimCustomTargetAccBadCase.getSelectionModel().select(root.getRunSettings().getRfmUsim().getCustomTargetAccBadCase());
        if (root.getRunSettings().getRfmUsim().getCustomTargetEfBadCase() != null)
            txtRfmUsimCustomTargetEfBadCase.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCase());

        chkRfmUsimFullAccess.setSelected(root.getRunSettings().getRfmUsim().isFullAccess());
        handleRfmUsimFullAccessCheck();

        // initialize list of available keysets for RFM USIM

        if (root.getRunSettings().getRfmUsim().getCipheringKeyset() != null) {
            cmbRfmUsimCipheringKeyset.setValue(root.getRunSettings().getRfmUsim().getCipheringKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmUsim().getCipheringKeyset().getKeysetName())) {
                    lblRfmUsimKic.setText("Kic (hex): " + keyset.getComputedKic());
                    break;
                }
            }
            chkRfmUsimCustomKic.setSelected(root.getRunSettings().getRfmUsim().getCipheringKeyset().isCustomKic());
            handleRfmUsimCustomKicCheck();
            if (chkRfmUsimCustomKic.isSelected())
                txtRfmUsimCustomKic.setText(root.getRunSettings().getRfmUsim().getCipheringKeyset().getComputedKic());
            else
                txtRfmUsimCustomKic.setText("");
        }

        if (root.getRunSettings().getRfmUsim().getAuthKeyset() != null) {
            cmbRfmUsimAuthKeyset.setValue(root.getRunSettings().getRfmUsim().getAuthKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmUsim().getAuthKeyset().getKeysetName())) {
                    lblRfmUsimKid.setText("Kid (hex): " + keyset.getComputedKid());
                    break;
                }
            }
            chkRfmUsimCustomKid.setSelected(root.getRunSettings().getRfmUsim().getAuthKeyset().isCustomKid());
            handleRfmUsimCustomKidCheck();
            if (chkRfmUsimCustomKid.isSelected())
                txtRfmUsimCustomKid.setText(root.getRunSettings().getRfmUsim().getAuthKeyset().getComputedKid());
            else
                txtRfmUsimCustomKid.setText("");
        }

        chkUseSpecificKeyset.setSelected(root.getRunSettings().getRfmUsim().isUseSpecificKeyset());
        handleUseSpecificKeysetCheck();
    }

    @FXML private void handleCustomTargetAccContextMenu() { cmbRfmUsimCustomTargetAcc.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleCustomTargetAccBadCaseContextMenu() { cmbRfmUsimCustomTargetAccBadCase.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleCipherKeysetContextMenu() { cmbRfmUsimCipheringKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleAuthKeysetContextMenu() { cmbRfmUsimAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

    @FXML private void handleIncludeRfmUsimCheck() { root.getMenuRfmUsim().setDisable(!chkIncludeRfmUsim.isSelected()); }
    @FXML private void handleIncludeRfmUsimUpdateRecordCheck() { root.getMenuRfmUsimUpdateRecord().setDisable(!chkIncludeRfmUsimUpdateRecord.isSelected()); }
    @FXML private void handleIncludeRfmUsimExpandedModeCheck() { root.getMenuRfmUsimExpandedMode().setDisable(!chkIncludeRfmUsimExpandedMode.isSelected()); }

    @FXML private void handleRfmUsimFullAccessCheck() {
        if (chkRfmUsimFullAccess.isSelected()) {
            lblRfmUsimCustomTarget.setDisable(true);
            cmbRfmUsimCustomTargetAcc.setDisable(true);
            txtRfmUsimCustomTargetEf.setDisable(true);
            lblRfmUsimCustomTargetBadCase.setDisable(true);
            cmbRfmUsimCustomTargetAccBadCase.setDisable(true);
            txtRfmUsimCustomTargetEfBadCase.setDisable(true);
        } else {
            lblRfmUsimCustomTarget.setDisable(false);
            cmbRfmUsimCustomTargetAcc.setDisable(false);
            txtRfmUsimCustomTargetEf.setDisable(false);
            lblRfmUsimCustomTargetBadCase.setDisable(false);
            cmbRfmUsimCustomTargetAccBadCase.setDisable(false);
            txtRfmUsimCustomTargetEfBadCase.setDisable(false);
        }
    }

    @FXML private void handleUseSpecificKeysetCheck() {
        if (chkUseSpecificKeyset.isSelected()) {
            lblRfmUsimCipheringKeyset.setDisable(false);
            cmbRfmUsimCipheringKeyset.setDisable(false);
            lblRfmUsimKic.setDisable(false);
            chkRfmUsimCustomKic.setDisable(false);
            txtRfmUsimCustomKic.setDisable(false);
            lblRfmUsimAuthKeyset.setDisable(false);
            cmbRfmUsimAuthKeyset.setDisable(false);
            lblRfmUsimKid.setDisable(false);
            chkRfmUsimCustomKid.setDisable(false);
            txtRfmUsimCustomKid.setDisable(false);
        }
        else {
            lblRfmUsimCipheringKeyset.setDisable(true);
            cmbRfmUsimCipheringKeyset.setDisable(true);
            lblRfmUsimKic.setDisable(true);
            chkRfmUsimCustomKic.setDisable(true);
            txtRfmUsimCustomKic.setDisable(true);
            lblRfmUsimAuthKeyset.setDisable(true);
            cmbRfmUsimAuthKeyset.setDisable(true);
            lblRfmUsimKid.setDisable(true);
            chkRfmUsimCustomKid.setDisable(true);
            txtRfmUsimCustomKid.setDisable(true);
        }
    }

    @FXML private void handleRfmUsimCipheringKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmUsimCipheringKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmUsimKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML private void handleRfmUsimAuthKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmUsimAuthKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmUsimKid.setText("Kid (hex): " + keyset.getComputedKid());
                break;
            }
        }
    }

    @FXML private void handleRfmUsimCustomKicCheck() { txtRfmUsimCustomKic.setDisable(!chkRfmUsimCustomKic.isSelected()); }
    @FXML private void handleRfmUsimCustomKidCheck() { txtRfmUsimCustomKid.setDisable(!chkRfmUsimCustomKid.isSelected()); }

    @FXML private void handleButtonSetRfmUsimMsl() {
        String mslHexStr = txtRfmUsimMslByte.getText();
        int mslInteger = Integer.parseInt(mslHexStr, 16);
        if (mslInteger > 31) {
            // MSL integer shoould not be higher than 31 (0x1F)
            Alert mslAlert = new Alert(Alert.AlertType.ERROR);
            mslAlert.initModality(Modality.APPLICATION_MODAL);
            mslAlert.initOwner(txtRfmUsimMslByte.getScene().getWindow());
            mslAlert.setTitle("Minimum Security Level");
            mslAlert.setHeaderText("Invalid MSL");
            mslAlert.setContentText("MSL value should not exceed '1F'");
            mslAlert.showAndWait();
        } else {
            // set components accordingly
            if (mslInteger == 0) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtRfmUsimMslByte.setText(mslHexStr);
        }
    }

    @FXML private void handleRfmUsimUseCipherCheck() {
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setUseCipher(chkRfmUsimUseCipher.isSelected());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().computeMsl();
        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmUsimAuthVerifSelection() {
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setAuthVerification(cmbRfmUsimAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().computeMsl();
        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmUsimCounterCheckingSelection() {
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCounterChecking(cmbRfmUsimCounterCheck.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().computeMsl();
        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());
    }

    public void saveControlState() {
        root.getRunSettings().getRfmUsim().setIncludeRfmUsim(chkIncludeRfmUsim.isSelected());
        root.getRunSettings().getRfmUsim().setIncludeRfmUsimUpdateRecord(chkIncludeRfmUsimUpdateRecord.isSelected());
        root.getRunSettings().getRfmUsim().setIncludeRfmUsimExpandedMode(chkIncludeRfmUsimExpandedMode.isSelected());

        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setComputedMsl(txtRfmUsimMslByte.getText());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setUseCipher(chkRfmUsimUseCipher.isSelected());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCipherAlgo(cmbRfmUsimCipherAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setAuthVerification(cmbRfmUsimAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setSigningAlgo(cmbRfmUsimSigningAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCounterChecking(cmbRfmUsimCounterCheck.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setPorRequirement(cmbRfmUsimPorRequirement.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCipherPor(chkRfmUsimCipherPor.isSelected());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setPorSecurity(cmbRfmUsimPorSecurity.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmUsim().setTar(txtRfmUsimTar.getText());
        root.getRunSettings().getRfmUsim().setTargetEf(txtRfmUsimTargetEf.getText());
        root.getRunSettings().getRfmUsim().setTargetEfBadCase(txtRfmUsimTargetEfBadCase.getText());
        root.getRunSettings().getRfmUsim().setFullAccess(chkRfmUsimFullAccess.isSelected());
        if (!root.getRunSettings().getRfmUsim().isFullAccess()) {
            root.getRunSettings().getRfmUsim().setCustomTargetAcc(cmbRfmUsimCustomTargetAcc.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmUsim().setCustomTargetEf(txtRfmUsimCustomTargetEf.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetAccBadCase(cmbRfmUsimCustomTargetAccBadCase.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCase(txtRfmUsimCustomTargetEfBadCase.getText());
        }

        root.getRunSettings().getRfmUsim().setUseSpecificKeyset(chkUseSpecificKeyset.isSelected());
        SCP80Keyset rfmUsimCipheringKeyset = new SCP80Keyset();
        rfmUsimCipheringKeyset.setKeysetName(cmbRfmUsimCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmUsimCipheringKeyset.getKeysetName())) {
                rfmUsimCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmUsimCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmUsimCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmUsimCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmUsimCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmUsimCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmUsimCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmUsimCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmUsimCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmUsimCipheringKeyset.setCustomKic(chkRfmUsimCustomKic.isSelected());
                if (rfmUsimCipheringKeyset.isCustomKic())
                    rfmUsimCipheringKeyset.setComputedKic(txtRfmUsimCustomKic.getText());
                else
                    rfmUsimCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmUsimCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmUsim().setCipheringKeyset(rfmUsimCipheringKeyset);

        SCP80Keyset rfmUsimAuthKeyset = new SCP80Keyset();
        rfmUsimAuthKeyset.setKeysetName(cmbRfmUsimAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmUsimAuthKeyset.getKeysetName())) {
                rfmUsimAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmUsimAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmUsimAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmUsimAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmUsimAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmUsimAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmUsimAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmUsimAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmUsimAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmUsimAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmUsimAuthKeyset.setCustomKid(chkRfmUsimCustomKid.isSelected());
                if (rfmUsimAuthKeyset.isCustomKid())
                    rfmUsimAuthKeyset.setComputedKid(txtRfmUsimCustomKid.getText());
                else
                    rfmUsimAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmUsim().setAuthKeyset(rfmUsimAuthKeyset);
    }

}
