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
public class RfmIsimController {

    @FXML private CheckBox chkIncludeRfmIsim;
    @FXML private CheckBox chkIncludeRfmIsimUpdateRecord;
    @FXML private CheckBox chkIncludeRfmIsimExpandedMode;
    @FXML private TextField txtRfmIsimMslByte;
    @FXML private ComboBox<String> cmbRfmIsimCipherAlgo;
    @FXML private CheckBox chkRfmIsimUseCipher;
    @FXML private ComboBox<String> cmbRfmIsimAuthVerif;
    @FXML private ComboBox<String> cmbRfmIsimSigningAlgo;
    @FXML private ComboBox<String> cmbRfmIsimPorRequirement;
    @FXML private ComboBox<String> cmbRfmIsimPorSecurity;
    @FXML private CheckBox chkRfmIsimCipherPor;
    @FXML private ComboBox<String> cmbRfmIsimCounterCheck;
    @FXML private TextField txtRfmIsimTar;
    @FXML private TextField txtRfmIsimTargetEf;
    @FXML private TextField txtRfmIsimTargetEfBadCase;
    @FXML private CheckBox chkRfmIsimFullAccess;
    @FXML private Label lblRfmIsimCustomTarget;
    @FXML private TextField txtRfmIsimCustomTargetEfIsc1;
    @FXML private TextField txtRfmIsimCustomTargetEfIsc2;
    @FXML private TextField txtRfmIsimCustomTargetEfIsc3;
    @FXML private TextField txtRfmIsimCustomTargetEfIsc4;
    @FXML private TextField txtRfmIsimCustomTargetEfGPin1;
    @FXML private TextField txtRfmIsimCustomTargetEfLPin1;
    @FXML private Label lblRfmIsimCustomTargetBadCase;
    @FXML private TextField txtRfmIsimCustomTargetEfBadCase;
    @FXML private CheckBox chkUseSpecificKeyset;
    @FXML private Label lblRfmIsimCipheringKeyset;
    @FXML private ComboBox<String> cmbRfmIsimCipheringKeyset;
    @FXML private Label lblRfmIsimKic;
    @FXML private CheckBox chkRfmIsimCustomKic;
    @FXML private TextField txtRfmIsimCustomKic;
    @FXML private Label lblRfmIsimAuthKeyset;
    @FXML private ComboBox<String> cmbRfmIsimAuthKeyset;
    @FXML private Label lblRfmIsimKid;
    @FXML private CheckBox chkRfmIsimCustomKid;
    @FXML private TextField txtRfmIsimCustomKid;
    @FXML private CheckBox chkRfmIsimUseIsc1;
    @FXML private CheckBox chkRfmIsimUseIsc2;
    @FXML private CheckBox chkRfmIsimUseIsc3;
    @FXML private CheckBox chkRfmIsimUseIsc4;
    @FXML private CheckBox chkRfmIsimUseGPin1;
    @FXML private CheckBox chkRfmIsimUseLPin1;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public RfmIsimController() {}

    @FXML public void initialize() {
        chkIncludeRfmIsim.setSelected(root.getRunSettings().getRfmIsim().isIncludeRfmIsim());
        handleIncludeRfmIsimCheck();

        chkIncludeRfmIsimUpdateRecord.setSelected(root.getRunSettings().getRfmIsim().isIncludeRfmIsimUpdateRecord());
        handleIncludeRfmIsimUpdateRecordCheck();

        chkIncludeRfmIsimExpandedMode.setSelected(root.getRunSettings().getRfmIsim().isIncludeRfmIsimExpandedMode());
        handleIncludeRfmIsimExpandedModeCheck();

        // RFM ISIM MSL

        txtRfmIsimMslByte.setText(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getComputedMsl());

        chkRfmIsimUseCipher.setSelected(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().isUseCipher());

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
        if (!(cmbRfmIsimCipherAlgo.getItems().size() > 0)) cmbRfmIsimCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRfmIsimCipherAlgo.setValue(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        if (!(cmbRfmIsimAuthVerif.getItems().size() > 0)) cmbRfmIsimAuthVerif.getItems().addAll(authVerifs);
        cmbRfmIsimAuthVerif.setValue(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getAuthVerification());

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
        if (!(cmbRfmIsimSigningAlgo.getItems().size() > 0)) cmbRfmIsimSigningAlgo.getItems().addAll(signingAlgos);
        cmbRfmIsimSigningAlgo.setValue(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        if (!(cmbRfmIsimCounterCheck.getItems().size() > 0)) cmbRfmIsimCounterCheck.getItems().addAll(counterCheckings);
        cmbRfmIsimCounterCheck.setValue(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        if (!(cmbRfmIsimPorRequirement.getItems().size() > 0)) cmbRfmIsimPorRequirement.getItems().addAll(porRequirements);
        cmbRfmIsimPorRequirement.setValue(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getPorRequirement());

        chkRfmIsimCipherPor.setSelected(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        if (!(cmbRfmIsimPorSecurity.getItems().size() > 0)) cmbRfmIsimPorSecurity.getItems().addAll(porSecurities);
        cmbRfmIsimPorSecurity.setValue(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getPorSecurity());

        // RFM ISIM parameters

        txtRfmIsimTar.setText(root.getRunSettings().getRfmIsim().getTar());
        txtRfmIsimTargetEf.setText(root.getRunSettings().getRfmIsim().getTargetEf());
        txtRfmIsimTargetEfBadCase.setText(root.getRunSettings().getRfmIsim().getTargetEfBadCase());

        // Initialize Access Domain
        chkRfmIsimUseIsc1.setSelected(root.getRunSettings().getRfmIsim().getAccessDomain().isUseIsc1());
        handleIncludeIsc1Check();
        if (chkRfmIsimUseIsc1.isSelected()){
            txtRfmIsimCustomTargetEfIsc1.setText(root.getRunSettings().getRfmIsim().getCustomTargetEfIsc1());
        }
        else {
            txtRfmIsimCustomTargetEfIsc1.setText("");
        }

        chkRfmIsimUseIsc2.setSelected(root.getRunSettings().getRfmIsim().getAccessDomain().isUseIsc2());
        handleIncludeIsc2Check();
        if (chkRfmIsimUseIsc2.isSelected()){
            txtRfmIsimCustomTargetEfIsc2.setText(root.getRunSettings().getRfmIsim().getCustomTargetEfIsc2());
        }
        else {
            txtRfmIsimCustomTargetEfIsc2.setText("");
        }

        chkRfmIsimUseIsc3.setSelected(root.getRunSettings().getRfmIsim().getAccessDomain().isUseIsc3());
        handleIncludeIsc3Check();
        if (chkRfmIsimUseIsc3.isSelected()){
            txtRfmIsimCustomTargetEfIsc3.setText(root.getRunSettings().getRfmIsim().getCustomTargetEfIsc3());
        }
        else {
            txtRfmIsimCustomTargetEfIsc3.setText("");
        }

        chkRfmIsimUseIsc4.setSelected(root.getRunSettings().getRfmIsim().getAccessDomain().isUseIsc4());
        handleIncludeIsc4Check();
        if (chkRfmIsimUseIsc4.isSelected()){
            txtRfmIsimCustomTargetEfIsc4.setText(root.getRunSettings().getRfmIsim().getCustomTargetEfIsc4());
        }
        else {
            txtRfmIsimCustomTargetEfIsc4.setText("");
        }

        chkRfmIsimUseGPin1.setSelected(root.getRunSettings().getRfmIsim().getAccessDomain().isUseGPin1());
        handleIncludeGPin1Check();
        if (chkRfmIsimUseGPin1.isSelected()){
            txtRfmIsimCustomTargetEfGPin1.setText(root.getRunSettings().getRfmIsim().getCustomTargetEfGPin1());
        }
        else {
            txtRfmIsimCustomTargetEfGPin1.setText("");
        }

        chkRfmIsimUseLPin1.setSelected(root.getRunSettings().getRfmIsim().getAccessDomain().isUseLPin1());
        handleIncludeLPin1Check();
        if (chkRfmIsimUseLPin1.isSelected()){
            txtRfmIsimCustomTargetEfLPin1.setText(root.getRunSettings().getRfmIsim().getCustomTargetEfLPin1());
        }
        else {
            txtRfmIsimCustomTargetEfLPin1.setText("");
        }

        if (root.getRunSettings().getRfmIsim().getCustomTargetEfBadCase() != null)
            txtRfmIsimCustomTargetEfBadCase.setText(root.getRunSettings().getRfmIsim().getCustomTargetEfBadCase());

        chkRfmIsimFullAccess.setSelected(root.getRunSettings().getRfmIsim().isFullAccess());
        handleRfmIsimFullAccessCheck();

        // initialize list of available keysets for RFM ISIM

        if (root.getRunSettings().getRfmIsim().getCipheringKeyset() != null) {
            cmbRfmIsimCipheringKeyset.setValue(root.getRunSettings().getRfmIsim().getCipheringKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmIsim().getCipheringKeyset().getKeysetName())) {
                    lblRfmIsimKic.setText("Kic (hex): " + keyset.getComputedKic());
                    break;
                }
            }
            chkRfmIsimCustomKic.setSelected(root.getRunSettings().getRfmIsim().getCipheringKeyset().isCustomKic());
            handleRfmIsimCustomKicCheck();
            if (chkRfmIsimCustomKic.isSelected())
                txtRfmIsimCustomKic.setText(root.getRunSettings().getRfmIsim().getCipheringKeyset().getComputedKic());
            else
                txtRfmIsimCustomKic.setText("");
        }

        if (root.getRunSettings().getRfmIsim().getAuthKeyset() != null) {
            cmbRfmIsimAuthKeyset.setValue(root.getRunSettings().getRfmIsim().getAuthKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmIsim().getAuthKeyset().getKeysetName())) {
                    lblRfmIsimKid.setText("Kid (hex): " + keyset.getComputedKid());
                    break;
                }
            }
            chkRfmIsimCustomKid.setSelected(root.getRunSettings().getRfmIsim().getAuthKeyset().isCustomKid());
            handleRfmIsimCustomKidCheck();
            if (chkRfmIsimCustomKid.isSelected())
                txtRfmIsimCustomKid.setText(root.getRunSettings().getRfmIsim().getAuthKeyset().getComputedKid());
            else
                txtRfmIsimCustomKid.setText("");
        }

        chkUseSpecificKeyset.setSelected(root.getRunSettings().getRfmIsim().isUseSpecificKeyset());
        handleUseSpecificKeysetCheck();
    }

    @FXML private void handleCipherKeysetContextMenu() { cmbRfmIsimCipheringKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleAuthKeysetContextMenu() { cmbRfmIsimAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

    @FXML private void handleIncludeRfmIsimCheck() { root.getMenuRfmIsim().setDisable(!chkIncludeRfmIsim.isSelected()); }
    @FXML private void handleIncludeRfmIsimUpdateRecordCheck() { root.getMenuRfmIsimUpdateRecord().setDisable(!chkIncludeRfmIsimUpdateRecord.isSelected()); }
    @FXML private void handleIncludeRfmIsimExpandedModeCheck() { root.getMenuRfmIsimExpandedMode().setDisable(!chkIncludeRfmIsimExpandedMode.isSelected()); }

    @FXML private void handleRfmIsimFullAccessCheck() {
        if (chkRfmIsimFullAccess.isSelected()) {
            lblRfmIsimCustomTarget.setDisable(true);
            txtRfmIsimCustomTargetEfIsc1.setDisable(true);
            txtRfmIsimCustomTargetEfIsc2.setDisable(true);
            txtRfmIsimCustomTargetEfIsc3.setDisable(true);
            txtRfmIsimCustomTargetEfIsc4.setDisable(true);
            txtRfmIsimCustomTargetEfGPin1.setDisable(true);
            txtRfmIsimCustomTargetEfLPin1.setDisable(true);
            chkRfmIsimUseIsc1.setDisable(true);
            chkRfmIsimUseIsc2.setDisable(true);
            chkRfmIsimUseIsc3.setDisable(true);
            chkRfmIsimUseIsc4.setDisable(true);
            chkRfmIsimUseGPin1.setDisable(true);
            chkRfmIsimUseLPin1.setDisable(true);
            lblRfmIsimCustomTargetBadCase.setDisable(true);
            txtRfmIsimCustomTargetEfBadCase.setDisable(true);
            txtRfmIsimTargetEf.setDisable(false);
            txtRfmIsimTargetEfBadCase.setDisable(false);

        } else {
            lblRfmIsimCustomTarget.setDisable(false);
            chkRfmIsimUseIsc1.setDisable(false);
            chkRfmIsimUseIsc2.setDisable(false);
            chkRfmIsimUseIsc3.setDisable(false);
            chkRfmIsimUseIsc4.setDisable(false);
            chkRfmIsimUseGPin1.setDisable(false);
            chkRfmIsimUseLPin1.setDisable(false);
            lblRfmIsimCustomTargetBadCase.setDisable(false);
            txtRfmIsimCustomTargetEfBadCase.setDisable(false);
            txtRfmIsimTargetEf.setDisable(true);
            txtRfmIsimTargetEfBadCase.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc1Check() {
        if(chkRfmIsimUseIsc1.isSelected()){
            txtRfmIsimCustomTargetEfIsc1.setDisable(false);
        }
        else{
            txtRfmIsimCustomTargetEfIsc1.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc2Check() {
        if(chkRfmIsimUseIsc2.isSelected()){
            txtRfmIsimCustomTargetEfIsc2.setDisable(false);
        }
        else{
            txtRfmIsimCustomTargetEfIsc2.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc3Check() {
        if(chkRfmIsimUseIsc3.isSelected()){
            txtRfmIsimCustomTargetEfIsc3.setDisable(false);
        }
        else{
            txtRfmIsimCustomTargetEfIsc3.setDisable(true);
        }
    }

    @FXML private void handleIncludeIsc4Check() {
        if(chkRfmIsimUseIsc4.isSelected()){
            txtRfmIsimCustomTargetEfIsc4.setDisable(false);
        }
        else{
            txtRfmIsimCustomTargetEfIsc4.setDisable(true);
        }
    }

    @FXML private void handleIncludeGPin1Check() {
        if(chkRfmIsimUseGPin1.isSelected()){
            txtRfmIsimCustomTargetEfGPin1.setDisable(false);
        }
        else{
            txtRfmIsimCustomTargetEfGPin1.setDisable(true);
        }
    }

    @FXML private void handleIncludeLPin1Check() {
        if(chkRfmIsimUseLPin1.isSelected()){
            txtRfmIsimCustomTargetEfLPin1.setDisable(false);
        }
        else{
            txtRfmIsimCustomTargetEfLPin1.setDisable(true);
        }
    }

    @FXML private void handleUseSpecificKeysetCheck() {
        if (chkUseSpecificKeyset.isSelected()) {
            lblRfmIsimCipheringKeyset.setDisable(false);
            cmbRfmIsimCipheringKeyset.setDisable(false);
            lblRfmIsimKic.setDisable(false);
            chkRfmIsimCustomKic.setDisable(false);
            txtRfmIsimCustomKic.setDisable(false);
            lblRfmIsimAuthKeyset.setDisable(false);
            cmbRfmIsimAuthKeyset.setDisable(false);
            lblRfmIsimKid.setDisable(false);
            chkRfmIsimCustomKid.setDisable(false);
            txtRfmIsimCustomKid.setDisable(false);
        }
        else {
            lblRfmIsimCipheringKeyset.setDisable(true);
            cmbRfmIsimCipheringKeyset.setDisable(true);
            lblRfmIsimKic.setDisable(true);
            chkRfmIsimCustomKic.setDisable(true);
            txtRfmIsimCustomKic.setDisable(true);
            lblRfmIsimAuthKeyset.setDisable(true);
            cmbRfmIsimAuthKeyset.setDisable(true);
            lblRfmIsimKid.setDisable(true);
            chkRfmIsimCustomKid.setDisable(true);
            txtRfmIsimCustomKid.setDisable(true);
        }
    }

    @FXML private void handleRfmIsimCipheringKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmIsimCipheringKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmIsimKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML private void handleRfmIsimAuthKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmIsimAuthKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmIsimKid.setText("Kid (hex): " + keyset.getComputedKid());
                break;
            }
        }
    }

    @FXML private void handleRfmIsimCustomKicCheck() { txtRfmIsimCustomKic.setDisable(!chkRfmIsimCustomKic.isSelected()); }
    @FXML private void handleRfmIsimCustomKidCheck() { txtRfmIsimCustomKid.setDisable(!chkRfmIsimCustomKid.isSelected()); }

    @FXML private void handleButtonSetRfmIsimMsl() {
        String mslHexStr = txtRfmIsimMslByte.getText();
        int mslInteger = Integer.parseInt(mslHexStr, 16);
        if (mslInteger > 31) {
            // MSL integer shoould not be higher than 31 (0x1F)
            Alert mslAlert = new Alert(Alert.AlertType.ERROR);
            mslAlert.initModality(Modality.APPLICATION_MODAL);
            mslAlert.initOwner(txtRfmIsimMslByte.getScene().getWindow());
            mslAlert.setTitle("Minimum Security Level");
            mslAlert.setHeaderText("Invalid MSL");
            mslAlert.setContentText("MSL value should not exceed '1F'");
            mslAlert.showAndWait();
        } else {
            // set components accordingly
            if (mslInteger == 0) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkRfmIsimUseCipher.setSelected(false);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkRfmIsimUseCipher.setSelected(true);
                cmbRfmIsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmIsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtRfmIsimMslByte.setText(mslHexStr);
        }
    }

    @FXML private void handleRfmIsimUseCipherCheck() {
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setUseCipher(chkRfmIsimUseCipher.isSelected());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().computeMsl();
        txtRfmIsimMslByte.setText(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmIsimAuthVerifSelection() {
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setAuthVerification(cmbRfmIsimAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().computeMsl();
        txtRfmIsimMslByte.setText(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmIsimCounterCheckingSelection() {
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setCounterChecking(cmbRfmIsimCounterCheck.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().computeMsl();
        txtRfmIsimMslByte.setText(root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().getComputedMsl());
    }

    public void saveControlState() {
        root.getRunSettings().getRfmIsim().setIncludeRfmIsim(chkIncludeRfmIsim.isSelected());
        root.getRunSettings().getRfmIsim().setIncludeRfmIsimUpdateRecord(chkIncludeRfmIsimUpdateRecord.isSelected());
        root.getRunSettings().getRfmIsim().setIncludeRfmIsimExpandedMode(chkIncludeRfmIsimExpandedMode.isSelected());

        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setComputedMsl(txtRfmIsimMslByte.getText());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setUseCipher(chkRfmIsimUseCipher.isSelected());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setCipherAlgo(cmbRfmIsimCipherAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setAuthVerification(cmbRfmIsimAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setSigningAlgo(cmbRfmIsimSigningAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setCounterChecking(cmbRfmIsimCounterCheck.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setPorRequirement(cmbRfmIsimPorRequirement.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setCipherPor(chkRfmIsimCipherPor.isSelected());
        root.getRunSettings().getRfmIsim().getMinimumSecurityLevel().setPorSecurity(cmbRfmIsimPorSecurity.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmIsim().setTar(txtRfmIsimTar.getText());
        root.getRunSettings().getRfmIsim().setTargetEf(txtRfmIsimTargetEf.getText());
        root.getRunSettings().getRfmIsim().setTargetEfBadCase(txtRfmIsimTargetEfBadCase.getText());
        root.getRunSettings().getRfmIsim().setFullAccess(chkRfmIsimFullAccess.isSelected());
        if (!root.getRunSettings().getRfmIsim().isFullAccess()) {
            root.getRunSettings().getRfmIsim().setCustomTargetEfIsc1(txtRfmIsimCustomTargetEfIsc1.getText());
            root.getRunSettings().getRfmIsim().setCustomTargetEfIsc2(txtRfmIsimCustomTargetEfIsc2.getText());
            root.getRunSettings().getRfmIsim().setCustomTargetEfIsc3(txtRfmIsimCustomTargetEfIsc3.getText());
            root.getRunSettings().getRfmIsim().setCustomTargetEfIsc4(txtRfmIsimCustomTargetEfIsc4.getText());
            root.getRunSettings().getRfmIsim().setCustomTargetEfGPin1(txtRfmIsimCustomTargetEfGPin1.getText());
            root.getRunSettings().getRfmIsim().setCustomTargetEfLPin1(txtRfmIsimCustomTargetEfLPin1.getText());
            root.getRunSettings().getRfmIsim().setCustomTargetEfBadCase(txtRfmIsimCustomTargetEfBadCase.getText());
            //save control Access Domain RFM ISIM
            AccessDomain rfmIsimAccessDomain = new AccessDomain();
            rfmIsimAccessDomain.setUseIsc1(chkRfmIsimUseIsc1.isSelected());
            rfmIsimAccessDomain.setUseIsc2(chkRfmIsimUseIsc2.isSelected());
            rfmIsimAccessDomain.setUseIsc3(chkRfmIsimUseIsc3.isSelected());
            rfmIsimAccessDomain.setUseIsc4(chkRfmIsimUseIsc4.isSelected());
            rfmIsimAccessDomain.setUseGPin1(chkRfmIsimUseGPin1.isSelected());
            rfmIsimAccessDomain.setUseLPin1(chkRfmIsimUseLPin1.isSelected());
            root.getRunSettings().getRfmIsim().setAccessDomain(rfmIsimAccessDomain);
        }

        root.getRunSettings().getRfmIsim().setUseSpecificKeyset(chkUseSpecificKeyset.isSelected());
        SCP80Keyset RfmIsimCipheringKeyset = new SCP80Keyset();
        RfmIsimCipheringKeyset.setKeysetName(cmbRfmIsimCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RfmIsimCipheringKeyset.getKeysetName())) {
                RfmIsimCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RfmIsimCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RfmIsimCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RfmIsimCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RfmIsimCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                RfmIsimCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RfmIsimCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RfmIsimCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                RfmIsimCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RfmIsimCipheringKeyset.setCustomKic(chkRfmIsimCustomKic.isSelected());
                if (RfmIsimCipheringKeyset.isCustomKic())
                    RfmIsimCipheringKeyset.setComputedKic(txtRfmIsimCustomKic.getText());
                else
                    RfmIsimCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RfmIsimCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmIsim().setCipheringKeyset(RfmIsimCipheringKeyset);

        SCP80Keyset RfmIsimAuthKeyset = new SCP80Keyset();
        RfmIsimAuthKeyset.setKeysetName(cmbRfmIsimAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RfmIsimAuthKeyset.getKeysetName())) {
                RfmIsimAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RfmIsimAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RfmIsimAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RfmIsimAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RfmIsimAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                RfmIsimAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RfmIsimAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RfmIsimAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                RfmIsimAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RfmIsimAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RfmIsimAuthKeyset.setCustomKid(chkRfmIsimCustomKid.isSelected());
                if (RfmIsimAuthKeyset.isCustomKid())
                    RfmIsimAuthKeyset.setComputedKid(txtRfmIsimCustomKid.getText());
                else
                    RfmIsimAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmIsim().setAuthKeyset(RfmIsimAuthKeyset);
    }

}
