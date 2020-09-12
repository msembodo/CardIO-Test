package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.AccessDomain;
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
    @FXML private TextField txtRfmUsimCustomTargetEfIsc1;
    @FXML private TextField txtRfmUsimCustomTargetEfIsc2;
    @FXML private TextField txtRfmUsimCustomTargetEfIsc3;
    @FXML private TextField txtRfmUsimCustomTargetEfIsc4;
    @FXML private TextField txtRfmUsimCustomTargetEfGPin1;
    @FXML private TextField txtRfmUsimCustomTargetEfLPin1;
    @FXML private Label lblRfmUsimCustomTargetBadCase;
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
    @FXML private CheckBox chkRfmUsimUseIsc1;
    @FXML private CheckBox chkRfmUsimUseIsc2;
    @FXML private CheckBox chkRfmUsimUseIsc3;
    @FXML private CheckBox chkRfmUsimUseIsc4;
    @FXML private CheckBox chkRfmUsimUseGPin1;
    @FXML private CheckBox chkRfmUsimUseLPin1;

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
        if (!(cmbRfmUsimCipherAlgo.getItems().size() > 0)) cmbRfmUsimCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRfmUsimCipherAlgo.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        if (!(cmbRfmUsimAuthVerif.getItems().size() > 0)) cmbRfmUsimAuthVerif.getItems().addAll(authVerifs);
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
        if (!(cmbRfmUsimSigningAlgo.getItems().size() > 0)) cmbRfmUsimSigningAlgo.getItems().addAll(signingAlgos);
        cmbRfmUsimSigningAlgo.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        if (!(cmbRfmUsimCounterCheck.getItems().size() > 0)) cmbRfmUsimCounterCheck.getItems().addAll(counterCheckings);
        cmbRfmUsimCounterCheck.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        if (!(cmbRfmUsimPorRequirement.getItems().size() > 0)) cmbRfmUsimPorRequirement.getItems().addAll(porRequirements);
        cmbRfmUsimPorRequirement.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getPorRequirement());

        chkRfmUsimCipherPor.setSelected(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        if (!(cmbRfmUsimPorSecurity.getItems().size() > 0)) cmbRfmUsimPorSecurity.getItems().addAll(porSecurities);
        cmbRfmUsimPorSecurity.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getPorSecurity());

        // RFM USIM parameters

        txtRfmUsimTar.setText(root.getRunSettings().getRfmUsim().getTar());
        txtRfmUsimTargetEf.setText(root.getRunSettings().getRfmUsim().getTargetEf());
        txtRfmUsimTargetEfBadCase.setText(root.getRunSettings().getRfmUsim().getTargetEfBadCase());

        // Initialize Access Domain
        chkRfmUsimUseIsc1.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimAccessDomain().isUseIsc1());
        handleIncludeIsc1Check();
        if (chkRfmUsimUseIsc1.isSelected()){
            txtRfmUsimCustomTargetEfIsc1.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfIsc1());
        }
        else {
            txtRfmUsimCustomTargetEfIsc1.setText("");
        }

        chkRfmUsimUseIsc2.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimAccessDomain().isUseIsc2());
        handleIncludeIsc2Check();
        if (chkRfmUsimUseIsc2.isSelected()){
            txtRfmUsimCustomTargetEfIsc2.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfIsc2());
        }
        else {
            txtRfmUsimCustomTargetEfIsc2.setText("");
        }

        chkRfmUsimUseIsc3.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimAccessDomain().isUseIsc3());
        handleIncludeIsc3Check();
        if (chkRfmUsimUseIsc3.isSelected()){
            txtRfmUsimCustomTargetEfIsc3.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfIsc3());
        }
        else {
            txtRfmUsimCustomTargetEfIsc3.setText("");
        }

        chkRfmUsimUseIsc4.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimAccessDomain().isUseIsc4());
        handleIncludeIsc4Check();
        if (chkRfmUsimUseIsc4.isSelected()){
            txtRfmUsimCustomTargetEfIsc4.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfIsc4());
        }
        else {
            txtRfmUsimCustomTargetEfIsc4.setText("");
        }

        chkRfmUsimUseGPin1.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimAccessDomain().isUseGPin1());
        handleIncludeGPin1Check();
        if (chkRfmUsimUseGPin1.isSelected()){
            txtRfmUsimCustomTargetEfGPin1.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfGPin1());
        }
        else {
            txtRfmUsimCustomTargetEfGPin1.setText("");
        }

        chkRfmUsimUseLPin1.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimAccessDomain().isUseLPin1());
        handleIncludeLPin1Check();
        if (chkRfmUsimUseLPin1.isSelected()){
            txtRfmUsimCustomTargetEfLPin1.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfLPin1());
        }
        else {
            txtRfmUsimCustomTargetEfLPin1.setText("");
        }

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

    @FXML private void handleCipherKeysetContextMenu() { cmbRfmUsimCipheringKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleAuthKeysetContextMenu() { cmbRfmUsimAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

    @FXML private void handleIncludeRfmUsimCheck() { root.getMenuRfmUsim().setDisable(!chkIncludeRfmUsim.isSelected()); }
    @FXML private void handleIncludeRfmUsimUpdateRecordCheck() { root.getMenuRfmUsimUpdateRecord().setDisable(!chkIncludeRfmUsimUpdateRecord.isSelected()); }
    @FXML private void handleIncludeRfmUsimExpandedModeCheck() { root.getMenuRfmUsimExpandedMode().setDisable(!chkIncludeRfmUsimExpandedMode.isSelected()); }

    @FXML private void handleRfmUsimFullAccessCheck() {
        if (chkRfmUsimFullAccess.isSelected()) {
            lblRfmUsimCustomTarget.setDisable(true);
            txtRfmUsimCustomTargetEfIsc1.setDisable(true);
            txtRfmUsimCustomTargetEfIsc2.setDisable(true);
            txtRfmUsimCustomTargetEfIsc3.setDisable(true);
            txtRfmUsimCustomTargetEfIsc4.setDisable(true);
            txtRfmUsimCustomTargetEfGPin1.setDisable(true);
            txtRfmUsimCustomTargetEfLPin1.setDisable(true);
            chkRfmUsimUseIsc1.setDisable(true);
            chkRfmUsimUseIsc2.setDisable(true);
            chkRfmUsimUseIsc3.setDisable(true);
            chkRfmUsimUseIsc4.setDisable(true);
            chkRfmUsimUseGPin1.setDisable(true);
            chkRfmUsimUseLPin1.setDisable(true);
            lblRfmUsimCustomTargetBadCase.setDisable(true);
            txtRfmUsimCustomTargetEfBadCase.setDisable(true);
            txtRfmUsimTargetEf.setDisable(false);
            txtRfmUsimTargetEfBadCase.setDisable(false);
        }
        else {
            lblRfmUsimCustomTarget.setDisable(false);
            chkRfmUsimUseIsc1.setDisable(false);
            chkRfmUsimUseIsc2.setDisable(false);
            chkRfmUsimUseIsc3.setDisable(false);
            chkRfmUsimUseIsc4.setDisable(false);
            chkRfmUsimUseGPin1.setDisable(false);
            chkRfmUsimUseLPin1.setDisable(false);
            lblRfmUsimCustomTargetBadCase.setDisable(false);
            txtRfmUsimCustomTargetEfBadCase.setDisable(false);
            txtRfmUsimTargetEf.setDisable(true);
            txtRfmUsimTargetEfBadCase.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc1Check() {
        if(chkRfmUsimUseIsc1.isSelected()){
            txtRfmUsimCustomTargetEfIsc1.setDisable(false);
        }
        else{
            txtRfmUsimCustomTargetEfIsc1.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc2Check() {
        if(chkRfmUsimUseIsc2.isSelected()){
            txtRfmUsimCustomTargetEfIsc2.setDisable(false);
        }
        else{
            txtRfmUsimCustomTargetEfIsc2.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc3Check() {
        if(chkRfmUsimUseIsc3.isSelected()){
            txtRfmUsimCustomTargetEfIsc3.setDisable(false);
        }
        else{
            txtRfmUsimCustomTargetEfIsc3.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc4Check() {
        if(chkRfmUsimUseIsc4.isSelected()){
            txtRfmUsimCustomTargetEfIsc4.setDisable(false);
        }
        else{
            txtRfmUsimCustomTargetEfIsc4.setDisable(true);
        }
    }

    @FXML private void handleIncludeGPin1Check() {
        if(chkRfmUsimUseGPin1.isSelected()){
            txtRfmUsimCustomTargetEfGPin1.setDisable(false);
        }
        else{
            txtRfmUsimCustomTargetEfGPin1.setDisable(true);
        }
    }

    @FXML private void handleIncludeLPin1Check() {
        if(chkRfmUsimUseLPin1.isSelected()){
            txtRfmUsimCustomTargetEfLPin1.setDisable(false);
        }
        else{
            txtRfmUsimCustomTargetEfLPin1.setDisable(true);
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
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc1(txtRfmUsimCustomTargetEfIsc1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc2(txtRfmUsimCustomTargetEfIsc2.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc3(txtRfmUsimCustomTargetEfIsc3.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc4(txtRfmUsimCustomTargetEfIsc4.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfGPin1(txtRfmUsimCustomTargetEfGPin1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfLPin1(txtRfmUsimCustomTargetEfLPin1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCase(txtRfmUsimCustomTargetEfBadCase.getText());
            //save control Access Domain RFM USIM
            AccessDomain rfmUsimAccessDomain = new AccessDomain();
            rfmUsimAccessDomain.setUseIsc1(chkRfmUsimUseIsc1.isSelected());
            rfmUsimAccessDomain.setUseIsc2(chkRfmUsimUseIsc2.isSelected());
            rfmUsimAccessDomain.setUseIsc3(chkRfmUsimUseIsc3.isSelected());
            rfmUsimAccessDomain.setUseIsc4(chkRfmUsimUseIsc4.isSelected());
            rfmUsimAccessDomain.setUseGPin1(chkRfmUsimUseGPin1.isSelected());
            rfmUsimAccessDomain.setUseLPin1(chkRfmUsimUseLPin1.isSelected());
            root.getRunSettings().getRfmUsim().setRfmUsimAccessDomain(rfmUsimAccessDomain);
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
