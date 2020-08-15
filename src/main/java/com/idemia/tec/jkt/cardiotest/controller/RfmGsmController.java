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
public class RfmGsmController {

    @FXML private CheckBox chkIncludeRfmGsm;
    @FXML private CheckBox chkIncludeRfmGsmUpdateRecord;
    @FXML private CheckBox chkIncludeRfmGsmExpandedMode;
    @FXML private TextField txtRfmGsmMslByte;
    @FXML private ComboBox<String> cmbRfmGsmCipherAlgo;
    @FXML private CheckBox chkRfmGsmUseCipher;
    @FXML private ComboBox<String> cmbRfmGsmAuthVerif;
    @FXML private ComboBox<String> cmbRfmGsmSigningAlgo;
    @FXML private ComboBox<String> cmbRfmGsmPorRequirement;
    @FXML private ComboBox<String> cmbRfmGsmPorSecurity;
    @FXML private CheckBox chkRfmGsmCipherPor;
    @FXML private ComboBox<String> cmbRfmGsmCounterCheck;
    @FXML private TextField txtRfmGsmTar;
    @FXML private TextField txtRfmGsmTargetEf;
    @FXML private TextField txtRfmGsmTargetEfBadCase;
    @FXML private CheckBox chkRfmGsmFullAccess;
    @FXML private Label lblRfmGsmCustomTarget;
    @FXML private ComboBox<String> cmbRfmGsmCustomTargetAcc;
    @FXML private TextField txtRfmGsmCustomTargetEf;
    @FXML private Label lblRfmGsmCustomTargetBadCase;
    @FXML private ComboBox<String> cmbRfmGsmCustomTargetAccBadCase;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCase;
    @FXML private CheckBox chkRfmGsmUseSpecificKeyset;
    @FXML private Label lblRfmGsmCipheringKeyset;
    @FXML private ComboBox<String> cmbRfmGsmCipheringKeyset;
    @FXML private Label lblRfmGsmKic;
    @FXML private CheckBox chkRfmGsmCustomKic;
    @FXML private TextField txtRfmGsmCustomKic;
    @FXML private Label lblRfmGsmAuthKeyset;
    @FXML private ComboBox<String> cmbRfmGsmAuthKeyset;
    @FXML private Label lblRfmGsmKid;
    @FXML private CheckBox chkRfmGsmCustomKid;
    @FXML private TextField txtRfmGsmCustomKid;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public RfmGsmController() {}

    @FXML private void initialize() {
        chkIncludeRfmGsm.setSelected(root.getRunSettings().getRfmGsm().isIncludeRfmGsm());
        handleIncludeRfmGsmCheck();

        chkIncludeRfmGsmUpdateRecord.setSelected(root.getRunSettings().getRfmGsm().isIncludeRfmGsmUpdateRecord());
        handleIncludeRfmGsmUpdateRecordCheck();

        chkIncludeRfmGsmExpandedMode.setSelected(root.getRunSettings().getRfmGsm().isIncludeRfmGsmExpandedMode());
        handleIncludeRfmGsmExpandedModeCheck();

        // RFM Gsm MSL

        txtRfmGsmMslByte.setText(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getComputedMsl());

        chkRfmGsmUseCipher.setSelected(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().isUseCipher());

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
        cmbRfmGsmCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRfmGsmCipherAlgo.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        cmbRfmGsmAuthVerif.getItems().addAll(authVerifs);
        cmbRfmGsmAuthVerif.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getAuthVerification());

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
        cmbRfmGsmSigningAlgo.getItems().addAll(signingAlgos);
        cmbRfmGsmSigningAlgo.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        cmbRfmGsmCounterCheck.getItems().addAll(counterCheckings);
        cmbRfmGsmCounterCheck.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        cmbRfmGsmPorRequirement.getItems().addAll(porRequirements);
        cmbRfmGsmPorRequirement.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getPorRequirement());

        chkRfmGsmCipherPor.setSelected(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        cmbRfmGsmPorSecurity.getItems().addAll(porSecurities);
        cmbRfmGsmPorSecurity.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getPorSecurity());

        // RFM Gsm parameters

        txtRfmGsmTar.setText(root.getRunSettings().getRfmGsm().getTar());
        txtRfmGsmTargetEf.setText(root.getRunSettings().getRfmGsm().getTargetEf());
        txtRfmGsmTargetEfBadCase.setText(root.getRunSettings().getRfmGsm().getTargetEfBadCase());

        if (root.getRunSettings().getRfmGsm().getCustomTargetAcc() != null)
            cmbRfmGsmCustomTargetAcc.getSelectionModel().select(root.getRunSettings().getRfmGsm().getCustomTargetAcc());
        if (root.getRunSettings().getRfmGsm().getCustomTargetEf() != null)
            txtRfmGsmCustomTargetEf.setText(root.getRunSettings().getRfmGsm().getCustomTargetEf());

        if (root.getRunSettings().getRfmGsm().getCustomTargetAccBadCase() != null)
            cmbRfmGsmCustomTargetAccBadCase.getSelectionModel().select(root.getRunSettings().getRfmGsm().getCustomTargetAccBadCase());
        if (root.getRunSettings().getRfmGsm().getCustomTargetEfBadCase() != null)
            txtRfmGsmCustomTargetEfBadCase.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCase());

        chkRfmGsmFullAccess.setSelected(root.getRunSettings().getRfmGsm().isFullAccess());
        handleRfmGsmFullAccessCheck();

        // initialize list of available keysets for RFM Gsm

        if (root.getRunSettings().getRfmGsm().getCipheringKeyset() != null) {
            cmbRfmGsmCipheringKeyset.setValue(root.getRunSettings().getRfmGsm().getCipheringKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmGsm().getCipheringKeyset().getKeysetName())) {
                    lblRfmGsmKic.setText("Kic (hex): " + keyset.getComputedKic());
                    break;
                }
            }
            chkRfmGsmCustomKic.setSelected(root.getRunSettings().getRfmGsm().getCipheringKeyset().isCustomKic());
            handleRfmGsmCustomKicCheck();
            if (chkRfmGsmCustomKic.isSelected())
                txtRfmGsmCustomKic.setText(root.getRunSettings().getRfmGsm().getCipheringKeyset().getComputedKic());
            else
                txtRfmGsmCustomKic.setText("");
        }

        if (root.getRunSettings().getRfmGsm().getAuthKeyset() != null) {
            cmbRfmGsmAuthKeyset.setValue(root.getRunSettings().getRfmGsm().getAuthKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmGsm().getAuthKeyset().getKeysetName())) {
                    lblRfmGsmKid.setText("Kid (hex): " + keyset.getComputedKid());
                    break;
                }
            }
            chkRfmGsmCustomKid.setSelected(root.getRunSettings().getRfmGsm().getAuthKeyset().isCustomKid());
            handleRfmGsmCustomKidCheck();
            if (chkRfmGsmCustomKid.isSelected())
                txtRfmGsmCustomKid.setText(root.getRunSettings().getRfmGsm().getAuthKeyset().getComputedKid());
            else
                txtRfmGsmCustomKid.setText("");
        }

        chkRfmGsmUseSpecificKeyset.setSelected(root.getRunSettings().getRfmGsm().isUseSpecificKeyset());
        handleRfmGsmUseSpecificKeysetCheck();
    }

    @FXML private void handleCustomTargetAccContextMenu() { cmbRfmGsmCustomTargetAcc.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleCustomTargetAccBadCaseContextMenu() { cmbRfmGsmCustomTargetAccBadCase.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleCipherKeysetContextMenu() { cmbRfmGsmCipheringKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleAuthKeysetContextMenu() { cmbRfmGsmAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

    @FXML private void handleIncludeRfmGsmCheck() { root.getMenuRfmGsm().setDisable(!chkIncludeRfmGsm.isSelected()); }
    @FXML private void handleIncludeRfmGsmUpdateRecordCheck() { root.getMenuRfmGsmUpdateRecord().setDisable(!chkIncludeRfmGsmUpdateRecord.isSelected()); }
    @FXML private void handleIncludeRfmGsmExpandedModeCheck() { root.getMenuRfmGsmExpandedMode().setDisable(!chkIncludeRfmGsmExpandedMode.isSelected()); }

    @FXML private void handleRfmGsmFullAccessCheck() {
        if (chkRfmGsmFullAccess.isSelected()) {
            lblRfmGsmCustomTarget.setDisable(true);
            cmbRfmGsmCustomTargetAcc.setDisable(true);
            txtRfmGsmCustomTargetEf.setDisable(true);
            lblRfmGsmCustomTargetBadCase.setDisable(true);
            cmbRfmGsmCustomTargetAccBadCase.setDisable(true);
            txtRfmGsmCustomTargetEfBadCase.setDisable(true);
        } else {
            lblRfmGsmCustomTarget.setDisable(false);
            cmbRfmGsmCustomTargetAcc.setDisable(false);
            txtRfmGsmCustomTargetEf.setDisable(false);
            lblRfmGsmCustomTargetBadCase.setDisable(false);
            cmbRfmGsmCustomTargetAccBadCase.setDisable(false);
            txtRfmGsmCustomTargetEfBadCase.setDisable(false);
        }
    }

    @FXML private void handleRfmGsmUseSpecificKeysetCheck() {
        if (chkRfmGsmUseSpecificKeyset.isSelected()) {
            lblRfmGsmCipheringKeyset.setDisable(false);
            cmbRfmGsmCipheringKeyset.setDisable(false);
            lblRfmGsmKic.setDisable(false);
            chkRfmGsmCustomKic.setDisable(false);
            txtRfmGsmCustomKic.setDisable(false);
            lblRfmGsmAuthKeyset.setDisable(false);
            cmbRfmGsmAuthKeyset.setDisable(false);
            lblRfmGsmKid.setDisable(false);
            chkRfmGsmCustomKid.setDisable(false);
            txtRfmGsmCustomKid.setDisable(false);
        }
        else {
            lblRfmGsmCipheringKeyset.setDisable(true);
            cmbRfmGsmCipheringKeyset.setDisable(true);
            lblRfmGsmKic.setDisable(true);
            chkRfmGsmCustomKic.setDisable(true);
            txtRfmGsmCustomKic.setDisable(true);
            lblRfmGsmAuthKeyset.setDisable(true);
            cmbRfmGsmAuthKeyset.setDisable(true);
            lblRfmGsmKid.setDisable(true);
            chkRfmGsmCustomKid.setDisable(true);
            txtRfmGsmCustomKid.setDisable(true);
        }
    }

    @FXML private void handleRfmGsmCipheringKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmGsmCipheringKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmGsmKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML private void handleRfmGsmAuthKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmGsmAuthKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmGsmKid.setText("Kid (hex): " + keyset.getComputedKid());
                break;
            }
        }
    }

    @FXML private void handleRfmGsmCustomKicCheck() { txtRfmGsmCustomKic.setDisable(!chkRfmGsmCustomKic.isSelected()); }
    @FXML private void handleRfmGsmCustomKidCheck() { txtRfmGsmCustomKid.setDisable(!chkRfmGsmCustomKid.isSelected()); }

    @FXML private void handleButtonSetRfmGsmMsl() {
        String mslHexStr = txtRfmGsmMslByte.getText();
        int mslInteger = Integer.parseInt(mslHexStr, 16);
        if (mslInteger > 31) {
            // MSL integer shoould not be higher than 31 (0x1F)
            Alert mslAlert = new Alert(Alert.AlertType.ERROR);
            mslAlert.initModality(Modality.APPLICATION_MODAL);
            mslAlert.initOwner(txtRfmGsmMslByte.getScene().getWindow());
            mslAlert.setTitle("Minimum Security Level");
            mslAlert.setHeaderText("Invalid MSL");
            mslAlert.setContentText("MSL value should not exceed '1F'");
            mslAlert.showAndWait();
        } else {
            // set components accordingly
            if (mslInteger == 0) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkRfmGsmUseCipher.setSelected(false);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("No verification");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkRfmGsmUseCipher.setSelected(true);
                cmbRfmGsmAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmGsmCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtRfmGsmMslByte.setText(mslHexStr);
        }
    }

    @FXML private void handleRfmGsmUseCipherCheck() {
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setUseCipher(chkRfmGsmUseCipher.isSelected());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().computeMsl();
        txtRfmGsmMslByte.setText(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmGsmAuthVerifSelection() {
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setAuthVerification(cmbRfmGsmAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().computeMsl();
        txtRfmGsmMslByte.setText(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmGsmCounterCheckingSelection() {
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setCounterChecking(cmbRfmGsmCounterCheck.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().computeMsl();
        txtRfmGsmMslByte.setText(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getComputedMsl());
    }

    public void saveControlState() {
        root.getRunSettings().getRfmGsm().setIncludeRfmGsm(chkIncludeRfmGsm.isSelected());
        root.getRunSettings().getRfmGsm().setIncludeRfmGsmUpdateRecord(chkIncludeRfmGsmUpdateRecord.isSelected());
        root.getRunSettings().getRfmGsm().setIncludeRfmGsmExpandedMode(chkIncludeRfmGsmExpandedMode.isSelected());

        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setComputedMsl(txtRfmGsmMslByte.getText());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setUseCipher(chkRfmGsmUseCipher.isSelected());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setCipherAlgo(cmbRfmGsmCipherAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setAuthVerification(cmbRfmGsmAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setSigningAlgo(cmbRfmGsmSigningAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setCounterChecking(cmbRfmGsmCounterCheck.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setPorRequirement(cmbRfmGsmPorRequirement.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setCipherPor(chkRfmGsmCipherPor.isSelected());
        root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().setPorSecurity(cmbRfmGsmPorSecurity.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmGsm().setTar(txtRfmGsmTar.getText());
        root.getRunSettings().getRfmGsm().setTargetEf(txtRfmGsmTargetEf.getText());
        root.getRunSettings().getRfmGsm().setTargetEfBadCase(txtRfmGsmTargetEfBadCase.getText());
        root.getRunSettings().getRfmGsm().setFullAccess(chkRfmGsmFullAccess.isSelected());
        if (!root.getRunSettings().getRfmGsm().isFullAccess()) {
            root.getRunSettings().getRfmGsm().setCustomTargetAcc(cmbRfmGsmCustomTargetAcc.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmGsm().setCustomTargetEf(txtRfmGsmCustomTargetEf.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetAccBadCase(cmbRfmGsmCustomTargetAccBadCase.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCase(txtRfmGsmCustomTargetEfBadCase.getText());
        }

        root.getRunSettings().getRfmGsm().setUseSpecificKeyset(chkRfmGsmUseSpecificKeyset.isSelected());
        SCP80Keyset rfmGsmCipheringKeyset = new SCP80Keyset();
        rfmGsmCipheringKeyset.setKeysetName(cmbRfmGsmCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmGsmCipheringKeyset.getKeysetName())) {
                rfmGsmCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmGsmCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmGsmCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmGsmCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmGsmCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmGsmCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmGsmCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmGsmCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmGsmCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmGsmCipheringKeyset.setCustomKic(chkRfmGsmCustomKic.isSelected());
                if (rfmGsmCipheringKeyset.isCustomKic())
                    rfmGsmCipheringKeyset.setComputedKic(txtRfmGsmCustomKic.getText());
                else
                    rfmGsmCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmGsmCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmGsm().setCipheringKeyset(rfmGsmCipheringKeyset);

        SCP80Keyset rfmGsmAuthKeyset = new SCP80Keyset();
        rfmGsmAuthKeyset.setKeysetName(cmbRfmGsmAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmGsmAuthKeyset.getKeysetName())) {
                rfmGsmAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmGsmAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmGsmAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmGsmAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmGsmAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmGsmAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmGsmAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmGsmAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmGsmAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmGsmAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmGsmAuthKeyset.setCustomKid(chkRfmGsmCustomKid.isSelected());
                if (rfmGsmAuthKeyset.isCustomKid())
                    rfmGsmAuthKeyset.setComputedKid(txtRfmGsmCustomKid.getText());
                else
                    rfmGsmAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmGsm().setAuthKeyset(rfmGsmAuthKeyset);
    }

}
