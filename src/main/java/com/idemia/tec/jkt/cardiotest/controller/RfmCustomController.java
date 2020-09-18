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
    @FXML private TextField txtRfmCustomCustomTargetEfAlw;
    @FXML private TextField txtRfmCustomCustomTargetEfIsc1;
    @FXML private TextField txtRfmCustomCustomTargetEfIsc2;
    @FXML private TextField txtRfmCustomCustomTargetEfIsc3;
    @FXML private TextField txtRfmCustomCustomTargetEfIsc4;
    @FXML private TextField txtRfmCustomCustomTargetEfGPin1;
    @FXML private TextField txtRfmCustomCustomTargetEfLPin1;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCaseAlw;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCaseIsc1;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCaseIsc2;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCaseIsc3;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCaseIsc4;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCaseGPin1;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCaseLPin1;
    @FXML private Label lblRfmCustomCustomTargetBadCase;

    /*
    @FXML private Label lblRfmCustomCustomTarget;
    @FXML private ComboBox<String> cmbRfmCustomCustomTargetAcc;
    @FXML private TextField txtRfmCustomCustomTargetEf;
    @FXML private Label lblRfmCustomCustomTargetBadCase;
    @FXML private ComboBox<String> cmbRfmCustomCustomTargetAccBadCase;
    @FXML private TextField txtRfmCustomCustomTargetEfBadCase;
     */

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

    @FXML private CheckBox chkRfmCustomUseAlw;
    @FXML private CheckBox chkRfmCustomUseIsc1;
    @FXML private CheckBox chkRfmCustomUseIsc2;
    @FXML private CheckBox chkRfmCustomUseIsc3;
    @FXML private CheckBox chkRfmCustomUseIsc4;
    @FXML private CheckBox chkRfmCustomUseGPin1;
    @FXML private CheckBox chkRfmCustomUseLPin1;
    @FXML private CheckBox chkRfmCustomUseBadCaseAlw;
    @FXML private CheckBox chkRfmCustomUseBadCaseIsc1;
    @FXML private CheckBox chkRfmCustomUseBadCaseIsc2;
    @FXML private CheckBox chkRfmCustomUseBadCaseIsc3;
    @FXML private CheckBox chkRfmCustomUseBadCaseIsc4;
    @FXML private CheckBox chkRfmCustomUseBadCaseGPin1;
    @FXML private CheckBox chkRfmCustomUseBadCaseLPin1;

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
        txtRfmCustomTargetEf.setText(root.getRunSettings().getRfmCustom().getTargetEf());
        txtRfmCustomTargetEfBadCase.setText(root.getRunSettings().getRfmCustom().getTargetEfBadCase());
        txtRfmCustomTargetDf.setText(root.getRunSettings().getRfmCustom().getTargetDf());
        txtCustomRfmDesc.setText(root.getRunSettings().getRfmCustom().getCustomRfmDesc());

//        // Initialize Access Domain Positive Case
//        chkRfmCustomUseAlw.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomAccessDomain().isUseAlways());
//        handleIncludeAlwCheck();
//        if (chkRfmCustomUseAlw.isSelected()){
//            txtRfmCustomCustomTargetEfAlw.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfAlw());
//        }
//        else {
//            txtRfmCustomCustomTargetEfAlw.setText("");
//        }
//
//        chkRfmCustomUseIsc1.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomAccessDomain().isUseIsc1());
//        handleIncludeIsc1Check();
//        if (chkRfmCustomUseIsc1.isSelected()){
//            txtRfmCustomCustomTargetEfIsc1.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfIsc1());
//        }
//        else {
//            txtRfmCustomCustomTargetEfIsc1.setText("");
//        }
//
//        chkRfmCustomUseIsc2.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomAccessDomain().isUseIsc2());
//        handleIncludeIsc2Check();
//        if (chkRfmCustomUseIsc2.isSelected()){
//            txtRfmCustomCustomTargetEfIsc2.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfIsc2());
//        }
//        else {
//            txtRfmCustomCustomTargetEfIsc2.setText("");
//        }
//
//        chkRfmCustomUseIsc3.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomAccessDomain().isUseIsc3());
//        handleIncludeIsc3Check();
//        if (chkRfmCustomUseIsc3.isSelected()){
//            txtRfmCustomCustomTargetEfIsc3.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfIsc3());
//        }
//        else {
//            txtRfmCustomCustomTargetEfIsc3.setText("");
//        }
//
//        chkRfmCustomUseIsc4.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomAccessDomain().isUseIsc4());
//        handleIncludeIsc4Check();
//        if (chkRfmCustomUseIsc4.isSelected()){
//            txtRfmCustomCustomTargetEfIsc4.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfIsc4());
//        }
//        else {
//            txtRfmCustomCustomTargetEfIsc4.setText("");
//        }
//
//        chkRfmCustomUseGPin1.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomAccessDomain().isUseGPin1());
//        handleIncludeGPin1Check();
//        if (chkRfmCustomUseGPin1.isSelected()){
//            txtRfmCustomCustomTargetEfGPin1.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfGPin1());
//        }
//        else {
//            txtRfmCustomCustomTargetEfGPin1.setText("");
//        }
//
//        chkRfmCustomUseLPin1.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomAccessDomain().isUseLPin1());
//        handleIncludeLPin1Check();
//        if (chkRfmCustomUseLPin1.isSelected()){
//            txtRfmCustomCustomTargetEfLPin1.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfLPin1());
//        }
//        else {
//            txtRfmCustomCustomTargetEfLPin1.setText("");
//        }
//
//        // Initialize Access Domain Negative Case
//        chkRfmCustomUseBadCaseAlw.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomBadCaseAccessDomain().isUseBadCaseAlways());
//        handleIncludeAlwCheck();
//        if (chkRfmCustomUseBadCaseAlw.isSelected()){
//            txtRfmCustomCustomTargetEfBadCaseAlw.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCaseAlw());
//        }
//        else {
//            txtRfmCustomCustomTargetEfBadCaseAlw.setText("");
//        }
//
//        chkRfmCustomUseBadCaseIsc1.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc1());
//        handleIncludeIsc1Check();
//        if (chkRfmCustomUseBadCaseIsc1.isSelected()){
//            txtRfmCustomCustomTargetEfBadCaseIsc1.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCaseIsc1());
//        }
//        else {
//            txtRfmCustomCustomTargetEfBadCaseIsc1.setText("");
//        }
//
//        chkRfmCustomUseBadCaseIsc2.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc2());
//        handleIncludeIsc2Check();
//        if (chkRfmCustomUseBadCaseIsc2.isSelected()){
//            txtRfmCustomCustomTargetEfBadCaseIsc2.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCaseIsc2());
//        }
//        else {
//            txtRfmCustomCustomTargetEfBadCaseIsc2.setText("");
//        }
//
//        chkRfmCustomUseBadCaseIsc3.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc3());
//        handleIncludeIsc3Check();
//        if (chkRfmCustomUseBadCaseIsc3.isSelected()){
//            txtRfmCustomCustomTargetEfBadCaseIsc3.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCaseIsc3());
//        }
//        else {
//            txtRfmCustomCustomTargetEfBadCaseIsc3.setText("");
//        }
//
//        chkRfmCustomUseBadCaseIsc4.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomBadCaseAccessDomain().isUseBadCaseIsc4());
//        handleIncludeIsc4Check();
//        if (chkRfmCustomUseBadCaseIsc4.isSelected()){
//            txtRfmCustomCustomTargetEfBadCaseIsc4.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCaseIsc4());
//        }
//        else {
//            txtRfmCustomCustomTargetEfBadCaseIsc4.setText("");
//        }
//
//        chkRfmCustomUseBadCaseGPin1.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomBadCaseAccessDomain().isUseBadCaseGPin1());
//        handleIncludeGPin1Check();
//        if (chkRfmCustomUseBadCaseGPin1.isSelected()){
//            txtRfmCustomCustomTargetEfBadCaseGPin1.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCaseGPin1());
//        }
//        else {
//            txtRfmCustomCustomTargetEfBadCaseGPin1.setText("");
//        }
//
//        chkRfmCustomUseBadCaseLPin1.setSelected(root.getRunSettings().getRfmCustom().getRfmCustomBadCaseAccessDomain().isUseBadCaseLPin1());
//        handleIncludeLPin1Check();
//        if (chkRfmCustomUseBadCaseLPin1.isSelected()){
//            txtRfmCustomCustomTargetEfBadCaseLPin1.setText(root.getRunSettings().getRfmCustom().getCustomTargetEfBadCaseLPin1());
//        }
//        else {
//            txtRfmCustomCustomTargetEfBadCaseLPin1.setText("");
//        }

        chkRfmCustomFullAccess.setSelected(root.getRunSettings().getRfmCustom().isFullAccess());
        handleRfmCustomFullAccessCheck();




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
            lblRfmCustomCustomTarget.setDisable(true);
            txtRfmCustomCustomTargetEfAlw.setDisable(true);
            txtRfmCustomCustomTargetEfIsc1.setDisable(true);
            txtRfmCustomCustomTargetEfIsc2.setDisable(true);
            txtRfmCustomCustomTargetEfIsc3.setDisable(true);
            txtRfmCustomCustomTargetEfIsc4.setDisable(true);
            txtRfmCustomCustomTargetEfGPin1.setDisable(true);
            txtRfmCustomCustomTargetEfLPin1.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseAlw.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc1.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc2.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc3.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc4.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseGPin1.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmCustomUseAlw.setDisable(true);
            chkRfmCustomUseIsc1.setDisable(true);
            chkRfmCustomUseIsc2.setDisable(true);
            chkRfmCustomUseIsc3.setDisable(true);
            chkRfmCustomUseIsc4.setDisable(true);
            chkRfmCustomUseGPin1.setDisable(true);
            chkRfmCustomUseLPin1.setDisable(true);
            chkRfmCustomUseBadCaseAlw.setDisable(true);
            chkRfmCustomUseBadCaseIsc1.setDisable(true);
            chkRfmCustomUseBadCaseIsc2.setDisable(true);
            chkRfmCustomUseBadCaseIsc3.setDisable(true);
            chkRfmCustomUseBadCaseIsc4.setDisable(true);
            chkRfmCustomUseBadCaseGPin1.setDisable(true);
            chkRfmCustomUseBadCaseLPin1.setDisable(true);
            lblRfmCustomCustomTargetBadCase.setDisable(true);
            txtRfmCustomTargetEf.setDisable(false);
            txtRfmCustomTargetEfBadCase.setDisable(false);
        } else {
            txtRfmCustomCustomTargetEfAlw.setDisable(false);
            txtRfmCustomCustomTargetEfIsc1.setDisable(false);
            txtRfmCustomCustomTargetEfIsc2.setDisable(false);
            txtRfmCustomCustomTargetEfIsc3.setDisable(false);
            txtRfmCustomCustomTargetEfIsc4.setDisable(false);
            txtRfmCustomCustomTargetEfGPin1.setDisable(false);
            txtRfmCustomCustomTargetEfLPin1.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseAlw.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc1.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc2.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc3.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc4.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseGPin1.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseLPin1.setDisable(false);
            chkRfmCustomUseAlw.setDisable(false);
            chkRfmCustomUseIsc1.setDisable(false);
            chkRfmCustomUseIsc2.setDisable(false);
            chkRfmCustomUseIsc3.setDisable(false);
            chkRfmCustomUseIsc4.setDisable(false);
            chkRfmCustomUseGPin1.setDisable(false);
            chkRfmCustomUseLPin1.setDisable(false);
            chkRfmCustomUseBadCaseAlw.setDisable(false);
            chkRfmCustomUseBadCaseIsc1.setDisable(false);
            chkRfmCustomUseBadCaseIsc2.setDisable(false);
            chkRfmCustomUseBadCaseIsc3.setDisable(false);
            chkRfmCustomUseBadCaseIsc4.setDisable(false);
            chkRfmCustomUseBadCaseGPin1.setDisable(false);
            chkRfmCustomUseBadCaseLPin1.setDisable(false);
            lblRfmCustomCustomTargetBadCase.setDisable(false);
            lblRfmCustomCustomTarget.setDisable(false);
            lblRfmCustomCustomTargetBadCase.setDisable(false);
            txtRfmCustomTargetEf.setDisable(true);
            txtRfmCustomTargetEfBadCase.setDisable(true);
        }
    }

    // handle Access Domain Positive Case

    @FXML private void handleIncludeAlwCheck() {
        if(chkRfmCustomUseAlw.isSelected()){
            txtRfmCustomCustomTargetEfAlw.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseAlw.setDisable(true);
            chkRfmCustomUseBadCaseAlw.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfAlw.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseAlw.setDisable(true);
            chkRfmCustomUseBadCaseAlw.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc1Check() {
        if(chkRfmCustomUseIsc1.isSelected()){
            txtRfmCustomCustomTargetEfIsc1.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc1.setDisable(true);
            chkRfmCustomUseBadCaseIsc1.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfIsc1.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc1.setDisable(true);
            chkRfmCustomUseBadCaseIsc1.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc2Check() {
        if(chkRfmCustomUseIsc2.isSelected()){
            txtRfmCustomCustomTargetEfIsc2.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc2.setDisable(true);
            chkRfmCustomUseBadCaseIsc2.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfIsc2.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc2.setDisable(true);
            chkRfmCustomUseBadCaseIsc2.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc3Check() {
        if(chkRfmCustomUseIsc3.isSelected()){
            txtRfmCustomCustomTargetEfIsc3.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc3.setDisable(true);
            chkRfmCustomUseBadCaseIsc3.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfIsc3.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc3.setDisable(true);
            chkRfmCustomUseBadCaseIsc3.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc4Check() {
        if(chkRfmCustomUseIsc4.isSelected()){
            txtRfmCustomCustomTargetEfIsc4.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseIsc4.setDisable(true);
            chkRfmCustomUseBadCaseIsc4.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfIsc4.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseIsc4.setDisable(true);
            chkRfmCustomUseBadCaseIsc4.setDisable(false);
        }
    }

    @FXML private void handleIncludeGPin1Check() {
        if(chkRfmCustomUseGPin1.isSelected()){
            txtRfmCustomCustomTargetEfGPin1.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseGPin1.setDisable(true);
            chkRfmCustomUseBadCaseGPin1.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfGPin1.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseGPin1.setDisable(true);
            chkRfmCustomUseBadCaseGPin1.setDisable(false);
        }
    }

    @FXML private void handleIncludeLPin1Check() {
        if(chkRfmCustomUseLPin1.isSelected()){
            txtRfmCustomCustomTargetEfLPin1.setDisable(false);
            txtRfmCustomCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmCustomUseBadCaseLPin1.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfLPin1.setDisable(true);
            txtRfmCustomCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmCustomUseBadCaseLPin1.setDisable(false);
        }
    }

    // handle Access Domain Negative Case

    @FXML private void handleIncludeBadCaseAlwCheck() {
        if(chkRfmCustomUseBadCaseAlw.isSelected()){
            txtRfmCustomCustomTargetEfBadCaseAlw.setDisable(false);
            txtRfmCustomCustomTargetEfAlw.setDisable(true);
            chkRfmCustomUseAlw.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfBadCaseAlw.setDisable(true);
            txtRfmCustomCustomTargetEfAlw.setDisable(true);
            chkRfmCustomUseAlw.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc1Check() {
        if(chkRfmCustomUseBadCaseIsc1.isSelected()){
            txtRfmCustomCustomTargetEfBadCaseIsc1.setDisable(false);
            txtRfmCustomCustomTargetEfIsc1.setDisable(true);
            chkRfmCustomUseIsc1.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfBadCaseIsc1.setDisable(true);
            txtRfmCustomCustomTargetEfIsc1.setDisable(true);
            chkRfmCustomUseIsc1.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc2Check() {
        if(chkRfmCustomUseBadCaseIsc2.isSelected()){
            txtRfmCustomCustomTargetEfBadCaseIsc2.setDisable(false);
            txtRfmCustomCustomTargetEfIsc2.setDisable(true);
            chkRfmCustomUseIsc2.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfBadCaseIsc2.setDisable(true);
            txtRfmCustomCustomTargetEfIsc2.setDisable(true);
            chkRfmCustomUseIsc2.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc3Check() {
        if(chkRfmCustomUseBadCaseIsc3.isSelected()){
            txtRfmCustomCustomTargetEfBadCaseIsc3.setDisable(false);
            txtRfmCustomCustomTargetEfIsc3.setDisable(true);
            chkRfmCustomUseIsc3.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfBadCaseIsc3.setDisable(true);
            txtRfmCustomCustomTargetEfIsc3.setDisable(true);
            chkRfmCustomUseIsc3.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc4Check() {
        if(chkRfmCustomUseBadCaseIsc4.isSelected()){
            txtRfmCustomCustomTargetEfBadCaseIsc4.setDisable(false);
            txtRfmCustomCustomTargetEfIsc4.setDisable(true);
            chkRfmCustomUseIsc4.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfBadCaseIsc4.setDisable(true);
            txtRfmCustomCustomTargetEfIsc4.setDisable(true);
            chkRfmCustomUseIsc4.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseGPin1Check() {
        if(chkRfmCustomUseBadCaseGPin1.isSelected()){
            txtRfmCustomCustomTargetEfBadCaseGPin1.setDisable(false);
            txtRfmCustomCustomTargetEfGPin1.setDisable(true);
            chkRfmCustomUseGPin1.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfBadCaseGPin1.setDisable(true);
            txtRfmCustomCustomTargetEfGPin1.setDisable(true);
            chkRfmCustomUseGPin1.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseLPin1Check() {
        if(chkRfmCustomUseBadCaseLPin1.isSelected()){
            txtRfmCustomCustomTargetEfBadCaseLPin1.setDisable(false);
            txtRfmCustomCustomTargetEfLPin1.setDisable(true);
            chkRfmCustomUseLPin1.setDisable(true);
        }
        else{
            txtRfmCustomCustomTargetEfBadCaseLPin1.setDisable(true);
            txtRfmCustomCustomTargetEfLPin1.setDisable(true);
            chkRfmCustomUseLPin1.setDisable(false);
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
            root.getRunSettings().getRfmCustom().setCustomTargetEfAlw(txtRfmCustomCustomTargetEfAlw.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfIsc1(txtRfmCustomCustomTargetEfIsc1.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfIsc2(txtRfmCustomCustomTargetEfIsc2.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfIsc3(txtRfmCustomCustomTargetEfIsc3.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfIsc4(txtRfmCustomCustomTargetEfIsc4.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfGPin1(txtRfmCustomCustomTargetEfGPin1.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfLPin1(txtRfmCustomCustomTargetEfLPin1.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCaseAlw(txtRfmCustomCustomTargetEfBadCaseAlw.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCaseIsc1(txtRfmCustomCustomTargetEfBadCaseIsc1.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCaseIsc2(txtRfmCustomCustomTargetEfBadCaseIsc2.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCaseIsc3(txtRfmCustomCustomTargetEfBadCaseIsc3.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCaseIsc4(txtRfmCustomCustomTargetEfBadCaseIsc4.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCaseGPin1(txtRfmCustomCustomTargetEfBadCaseGPin1.getText());
            root.getRunSettings().getRfmCustom().setCustomTargetEfBadCaseLPin1(txtRfmCustomCustomTargetEfBadCaseLPin1.getText());
            //save control Positive Case Access Domain RFM ISIM
            AccessDomain rfmCustomAccessDomain = new AccessDomain();
            rfmCustomAccessDomain.setUseAlways(chkRfmCustomUseAlw.isSelected());
            rfmCustomAccessDomain.setUseIsc1(chkRfmCustomUseIsc1.isSelected());
            rfmCustomAccessDomain.setUseIsc2(chkRfmCustomUseIsc2.isSelected());
            rfmCustomAccessDomain.setUseIsc3(chkRfmCustomUseIsc3.isSelected());
            rfmCustomAccessDomain.setUseIsc4(chkRfmCustomUseIsc4.isSelected());
            rfmCustomAccessDomain.setUseGPin1(chkRfmCustomUseGPin1.isSelected());
            rfmCustomAccessDomain.setUseLPin1(chkRfmCustomUseLPin1.isSelected());
            root.getRunSettings().getRfmCustom().setRfmCustomAccessDomain(rfmCustomAccessDomain);
            //save control Negative Case Access Domain RFM ISIM
            AccessDomain rfmCustomBadCaseAccessDomain = new AccessDomain();
            rfmCustomBadCaseAccessDomain.setUseBadCaseAlways(chkRfmCustomUseBadCaseAlw.isSelected());
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc1(chkRfmCustomUseBadCaseIsc1.isSelected());
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc2(chkRfmCustomUseBadCaseIsc2.isSelected());
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc3(chkRfmCustomUseBadCaseIsc3.isSelected());
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc4(chkRfmCustomUseBadCaseIsc4.isSelected());
            rfmCustomBadCaseAccessDomain.setUseBadCaseGPin1(chkRfmCustomUseBadCaseGPin1.isSelected());
            rfmCustomBadCaseAccessDomain.setUseBadCaseLPin1(chkRfmCustomUseBadCaseLPin1.isSelected());
            root.getRunSettings().getRfmCustom().setRfmCustomBadCaseAccessDomain(rfmCustomBadCaseAccessDomain);
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
