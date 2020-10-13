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
    //@FXML private TextField txtRfmUsimTargetEfBadCase;
    @FXML private CheckBox chkRfmUsimFullAccess;
    @FXML private Label lblRfmUsimCustomTarget;
    @FXML private TextField txtRfmUsimCustomTargetEfAlw;
    @FXML private TextField txtRfmUsimCustomTargetEfIsc1;
    @FXML private TextField txtRfmUsimCustomTargetEfIsc2;
    @FXML private TextField txtRfmUsimCustomTargetEfIsc3;
    @FXML private TextField txtRfmUsimCustomTargetEfIsc4;
    @FXML private TextField txtRfmUsimCustomTargetEfGPin1;
    @FXML private TextField txtRfmUsimCustomTargetEfLPin1;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCaseAlw;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCaseIsc1;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCaseIsc2;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCaseIsc3;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCaseIsc4;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCaseGPin1;
    @FXML private TextField txtRfmUsimCustomTargetEfBadCaseLPin1;
    @FXML private Label lblRfmUsimCustomTargetBadCase;
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
    @FXML private CheckBox chkRfmUsimUseAlw;
    @FXML private CheckBox chkRfmUsimUseIsc1;
    @FXML private CheckBox chkRfmUsimUseIsc2;
    @FXML private CheckBox chkRfmUsimUseIsc3;
    @FXML private CheckBox chkRfmUsimUseIsc4;
    @FXML private CheckBox chkRfmUsimUseGPin1;
    @FXML private CheckBox chkRfmUsimUseLPin1;
    @FXML private CheckBox chkRfmUsimUseBadCaseAlw;
    @FXML private CheckBox chkRfmUsimUseBadCaseIsc1;
    @FXML private CheckBox chkRfmUsimUseBadCaseIsc2;
    @FXML private CheckBox chkRfmUsimUseBadCaseIsc3;
    @FXML private CheckBox chkRfmUsimUseBadCaseIsc4;
    @FXML private CheckBox chkRfmUsimUseBadCaseGPin1;
    @FXML private CheckBox chkRfmUsimUseBadCaseLPin1;

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

        // RFM ISIM MSL

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

        // RFM ISIM parameters

        txtRfmUsimTar.setText(root.getRunSettings().getRfmUsim().getTar());
        txtRfmUsimTargetEf.setText(root.getRunSettings().getRfmUsim().getTargetEf());
        //txtRfmUsimTargetEfBadCase.setText(root.getRunSettings().getRfmUsim().getTargetEfBadCase());

        // set Checkbox Access Domain Tooltip
        chkRfmUsimUseAlw.setTooltip(new Tooltip("Read EF Access Domain ALW"));
        chkRfmUsimUseIsc1.setTooltip(new Tooltip("Update EF Access Domain ISC1"));
        chkRfmUsimUseIsc2.setTooltip(new Tooltip("Update EF Access Domain ISC2"));
        chkRfmUsimUseIsc3.setTooltip(new Tooltip("Update EF Access Domain ISC3"));
        chkRfmUsimUseIsc4.setTooltip(new Tooltip("Update EF Access Domain ISC4"));
        chkRfmUsimUseGPin1.setTooltip(new Tooltip("Update EF Access Domain PIN1"));
        chkRfmUsimUseLPin1.setTooltip(new Tooltip("Update EF Access Domain PIN2"));
        chkRfmUsimUseBadCaseAlw.setTooltip(new Tooltip("Out of Access Domain ALW"));
        chkRfmUsimUseBadCaseIsc1.setTooltip(new Tooltip("Out of Access Domain ISC1"));
        chkRfmUsimUseBadCaseIsc2.setTooltip(new Tooltip("Out of Access Domain ISC2"));
        chkRfmUsimUseBadCaseIsc3.setTooltip(new Tooltip("Out of Access Domain ISC3"));
        chkRfmUsimUseBadCaseIsc4.setTooltip(new Tooltip("Out of Access Domain ISC4"));
        chkRfmUsimUseBadCaseGPin1.setTooltip(new Tooltip("Out of Access Domain PIN1"));
        chkRfmUsimUseBadCaseLPin1.setTooltip(new Tooltip("Out of Access Domain PIN2"));

        // Initialize Access Domain Positive Case
        chkRfmUsimUseAlw.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimAccessDomain().isUseAlways());
        handleIncludeAlwCheck();
        if (chkRfmUsimUseAlw.isSelected()){
            txtRfmUsimCustomTargetEfAlw.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfAlw());
        }
        else {
            txtRfmUsimCustomTargetEfAlw.setText("");
        }

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

        // Initialize Access Domain Negative Case
        chkRfmUsimUseBadCaseAlw.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimBadCaseAccessDomain().isUseBadCaseAlways());
        handleIncludeAlwCheck();
        if (chkRfmUsimUseBadCaseAlw.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseAlw.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCaseAlw());
        }
        else {
            txtRfmUsimCustomTargetEfBadCaseAlw.setText("");
        }

        chkRfmUsimUseBadCaseIsc1.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimBadCaseAccessDomain().isUseBadCaseIsc1());
        handleIncludeIsc1Check();
        if (chkRfmUsimUseBadCaseIsc1.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc1.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCaseIsc1());
        }
        else {
            txtRfmUsimCustomTargetEfBadCaseIsc1.setText("");
        }

        chkRfmUsimUseBadCaseIsc2.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimBadCaseAccessDomain().isUseBadCaseIsc2());
        handleIncludeIsc2Check();
        if (chkRfmUsimUseBadCaseIsc2.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc2.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCaseIsc2());
        }
        else {
            txtRfmUsimCustomTargetEfBadCaseIsc2.setText("");
        }

        chkRfmUsimUseBadCaseIsc3.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimBadCaseAccessDomain().isUseBadCaseIsc3());
        handleIncludeIsc3Check();
        if (chkRfmUsimUseBadCaseIsc3.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc3.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCaseIsc3());
        }
        else {
            txtRfmUsimCustomTargetEfBadCaseIsc3.setText("");
        }

        chkRfmUsimUseBadCaseIsc4.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimBadCaseAccessDomain().isUseBadCaseIsc4());
        handleIncludeIsc4Check();
        if (chkRfmUsimUseBadCaseIsc4.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc4.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCaseIsc4());
        }
        else {
            txtRfmUsimCustomTargetEfBadCaseIsc4.setText("");
        }

        chkRfmUsimUseBadCaseGPin1.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimBadCaseAccessDomain().isUseBadCaseGPin1());
        handleIncludeGPin1Check();
        if (chkRfmUsimUseBadCaseGPin1.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseGPin1.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCaseGPin1());
        }
        else {
            txtRfmUsimCustomTargetEfBadCaseGPin1.setText("");
        }

        chkRfmUsimUseBadCaseLPin1.setSelected(root.getRunSettings().getRfmUsim().getRfmUsimBadCaseAccessDomain().isUseBadCaseLPin1());
        handleIncludeLPin1Check();
        if (chkRfmUsimUseBadCaseLPin1.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseLPin1.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCaseLPin1());
        }
        else {
            txtRfmUsimCustomTargetEfBadCaseLPin1.setText("");
        }

        chkRfmUsimFullAccess.setSelected(root.getRunSettings().getRfmUsim().isFullAccess());
        handleRfmUsimFullAccessCheck();

        // initialize list of available keysets for RFM ISIM

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
            txtRfmUsimCustomTargetEfAlw.setDisable(true);
            txtRfmUsimCustomTargetEfIsc1.setDisable(true);
            txtRfmUsimCustomTargetEfIsc2.setDisable(true);
            txtRfmUsimCustomTargetEfIsc3.setDisable(true);
            txtRfmUsimCustomTargetEfIsc4.setDisable(true);
            txtRfmUsimCustomTargetEfGPin1.setDisable(true);
            txtRfmUsimCustomTargetEfLPin1.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseAlw.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc1.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc2.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc3.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc4.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseGPin1.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmUsimUseAlw.setDisable(true);
            chkRfmUsimUseIsc1.setDisable(true);
            chkRfmUsimUseIsc2.setDisable(true);
            chkRfmUsimUseIsc3.setDisable(true);
            chkRfmUsimUseIsc4.setDisable(true);
            chkRfmUsimUseGPin1.setDisable(true);
            chkRfmUsimUseLPin1.setDisable(true);
            chkRfmUsimUseBadCaseAlw.setDisable(true);
            chkRfmUsimUseBadCaseIsc1.setDisable(true);
            chkRfmUsimUseBadCaseIsc2.setDisable(true);
            chkRfmUsimUseBadCaseIsc3.setDisable(true);
            chkRfmUsimUseBadCaseIsc4.setDisable(true);
            chkRfmUsimUseBadCaseGPin1.setDisable(true);
            chkRfmUsimUseBadCaseLPin1.setDisable(true);
            lblRfmUsimCustomTargetBadCase.setDisable(true);
            txtRfmUsimTargetEf.setDisable(false);
            //txtRfmUsimTargetEfBadCase.setDisable(false);
        }
        else {
            txtRfmUsimCustomTargetEfAlw.setDisable(false);
            txtRfmUsimCustomTargetEfIsc1.setDisable(false);
            txtRfmUsimCustomTargetEfIsc2.setDisable(false);
            txtRfmUsimCustomTargetEfIsc3.setDisable(false);
            txtRfmUsimCustomTargetEfIsc4.setDisable(false);
            txtRfmUsimCustomTargetEfGPin1.setDisable(false);
            txtRfmUsimCustomTargetEfLPin1.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseAlw.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc1.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc2.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc3.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc4.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseGPin1.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseLPin1.setDisable(false);
            chkRfmUsimUseAlw.setDisable(false);
            chkRfmUsimUseIsc1.setDisable(false);
            chkRfmUsimUseIsc2.setDisable(false);
            chkRfmUsimUseIsc3.setDisable(false);
            chkRfmUsimUseIsc4.setDisable(false);
            chkRfmUsimUseGPin1.setDisable(false);
            chkRfmUsimUseLPin1.setDisable(false);
            chkRfmUsimUseBadCaseAlw.setDisable(false);
            chkRfmUsimUseBadCaseIsc1.setDisable(false);
            chkRfmUsimUseBadCaseIsc2.setDisable(false);
            chkRfmUsimUseBadCaseIsc3.setDisable(false);
            chkRfmUsimUseBadCaseIsc4.setDisable(false);
            chkRfmUsimUseBadCaseGPin1.setDisable(false);
            chkRfmUsimUseBadCaseLPin1.setDisable(false);
            lblRfmUsimCustomTargetBadCase.setDisable(false);
            lblRfmUsimCustomTarget.setDisable(false);
            lblRfmUsimCustomTargetBadCase.setDisable(false);
            txtRfmUsimTargetEf.setDisable(true);
            //txtRfmUsimTargetEfBadCase.setDisable(true);
        }
    }

    // handle Access Domain Positive Case

    @FXML private void handleIncludeAlwCheck() {
        if(chkRfmUsimUseAlw.isSelected()){
            txtRfmUsimCustomTargetEfAlw.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseAlw.setDisable(true);
            chkRfmUsimUseBadCaseAlw.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfAlw.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseAlw.setDisable(true);
            chkRfmUsimUseBadCaseAlw.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc1Check() {
        if(chkRfmUsimUseIsc1.isSelected()){
            txtRfmUsimCustomTargetEfIsc1.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc1.setDisable(true);
            chkRfmUsimUseBadCaseIsc1.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfIsc1.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc1.setDisable(true);
            chkRfmUsimUseBadCaseIsc1.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc2Check() {
        if(chkRfmUsimUseIsc2.isSelected()){
            txtRfmUsimCustomTargetEfIsc2.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc2.setDisable(true);
            chkRfmUsimUseBadCaseIsc2.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfIsc2.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc2.setDisable(true);
            chkRfmUsimUseBadCaseIsc2.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc3Check() {
        if(chkRfmUsimUseIsc3.isSelected()){
            txtRfmUsimCustomTargetEfIsc3.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc3.setDisable(true);
            chkRfmUsimUseBadCaseIsc3.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfIsc3.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc3.setDisable(true);
            chkRfmUsimUseBadCaseIsc3.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc4Check() {
        if(chkRfmUsimUseIsc4.isSelected()){
            txtRfmUsimCustomTargetEfIsc4.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseIsc4.setDisable(true);
            chkRfmUsimUseBadCaseIsc4.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfIsc4.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseIsc4.setDisable(true);
            chkRfmUsimUseBadCaseIsc4.setDisable(false);
        }
    }

    @FXML private void handleIncludeGPin1Check() {
        if(chkRfmUsimUseGPin1.isSelected()){
            txtRfmUsimCustomTargetEfGPin1.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseGPin1.setDisable(true);
            chkRfmUsimUseBadCaseGPin1.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfGPin1.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseGPin1.setDisable(true);
            chkRfmUsimUseBadCaseGPin1.setDisable(false);
        }
    }

    @FXML private void handleIncludeLPin1Check() {
        if(chkRfmUsimUseLPin1.isSelected()){
            txtRfmUsimCustomTargetEfLPin1.setDisable(false);
            txtRfmUsimCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmUsimUseBadCaseLPin1.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfLPin1.setDisable(true);
            txtRfmUsimCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmUsimUseBadCaseLPin1.setDisable(false);
        }
    }

    // handle Access Domain Negative Case

    @FXML private void handleIncludeBadCaseAlwCheck() {
        if(chkRfmUsimUseBadCaseAlw.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseAlw.setDisable(false);
            txtRfmUsimCustomTargetEfAlw.setDisable(true);
            chkRfmUsimUseAlw.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfBadCaseAlw.setDisable(true);
            txtRfmUsimCustomTargetEfAlw.setDisable(true);
            chkRfmUsimUseAlw.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc1Check() {
        if(chkRfmUsimUseBadCaseIsc1.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc1.setDisable(false);
            txtRfmUsimCustomTargetEfIsc1.setDisable(true);
            chkRfmUsimUseIsc1.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfBadCaseIsc1.setDisable(true);
            txtRfmUsimCustomTargetEfIsc1.setDisable(true);
            chkRfmUsimUseIsc1.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc2Check() {
        if(chkRfmUsimUseBadCaseIsc2.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc2.setDisable(false);
            txtRfmUsimCustomTargetEfIsc2.setDisable(true);
            chkRfmUsimUseIsc2.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfBadCaseIsc2.setDisable(true);
            txtRfmUsimCustomTargetEfIsc2.setDisable(true);
            chkRfmUsimUseIsc2.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc3Check() {
        if(chkRfmUsimUseBadCaseIsc3.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc3.setDisable(false);
            txtRfmUsimCustomTargetEfIsc3.setDisable(true);
            chkRfmUsimUseIsc3.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfBadCaseIsc3.setDisable(true);
            txtRfmUsimCustomTargetEfIsc3.setDisable(true);
            chkRfmUsimUseIsc3.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc4Check() {
        if(chkRfmUsimUseBadCaseIsc4.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseIsc4.setDisable(false);
            txtRfmUsimCustomTargetEfIsc4.setDisable(true);
            chkRfmUsimUseIsc4.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfBadCaseIsc4.setDisable(true);
            txtRfmUsimCustomTargetEfIsc4.setDisable(true);
            chkRfmUsimUseIsc4.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseGPin1Check() {
        if(chkRfmUsimUseBadCaseGPin1.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseGPin1.setDisable(false);
            txtRfmUsimCustomTargetEfGPin1.setDisable(true);
            chkRfmUsimUseGPin1.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfBadCaseGPin1.setDisable(true);
            txtRfmUsimCustomTargetEfGPin1.setDisable(true);
            chkRfmUsimUseGPin1.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseLPin1Check() {
        if(chkRfmUsimUseBadCaseLPin1.isSelected()){
            txtRfmUsimCustomTargetEfBadCaseLPin1.setDisable(false);
            txtRfmUsimCustomTargetEfLPin1.setDisable(true);
            chkRfmUsimUseLPin1.setDisable(true);
        }
        else{
            txtRfmUsimCustomTargetEfBadCaseLPin1.setDisable(true);
            txtRfmUsimCustomTargetEfLPin1.setDisable(true);
            chkRfmUsimUseLPin1.setDisable(false);
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
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimCipherAlgo.setValue("no cipher");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtRfmUsimMslByte.setText(mslHexStr);
        }
    }

    @FXML private void handleRfmUsimUseCipherCheck() {
        if (!chkRfmUsimUseCipher.isSelected()) cmbRfmUsimCipherAlgo.setValue("no cipher");
        else cmbRfmUsimCipherAlgo.setValue("as defined in keyset");
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setUseCipher(chkRfmUsimUseCipher.isSelected());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().computeMsl();
        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRfmUsimAuthVerifSelection() {
        if (cmbRfmUsimAuthVerif.getSelectionModel().getSelectedItem().equals("No verification"))
            cmbRfmUsimSigningAlgo.setValue("no algorithm");
        else
            cmbRfmUsimSigningAlgo.setValue("as defined in keyset");
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
        //root.getRunSettings().getRfmUsim().setTargetEfBadCase(txtRfmUsimTargetEfBadCase.getText());
        root.getRunSettings().getRfmUsim().setFullAccess(chkRfmUsimFullAccess.isSelected());
        if (!root.getRunSettings().getRfmUsim().isFullAccess()) {
            root.getRunSettings().getRfmUsim().setCustomTargetEfAlw(txtRfmUsimCustomTargetEfAlw.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc1(txtRfmUsimCustomTargetEfIsc1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc2(txtRfmUsimCustomTargetEfIsc2.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc3(txtRfmUsimCustomTargetEfIsc3.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfIsc4(txtRfmUsimCustomTargetEfIsc4.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfGPin1(txtRfmUsimCustomTargetEfGPin1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfLPin1(txtRfmUsimCustomTargetEfLPin1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCaseAlw(txtRfmUsimCustomTargetEfBadCaseAlw.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCaseIsc1(txtRfmUsimCustomTargetEfBadCaseIsc1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCaseIsc2(txtRfmUsimCustomTargetEfBadCaseIsc2.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCaseIsc3(txtRfmUsimCustomTargetEfBadCaseIsc3.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCaseIsc4(txtRfmUsimCustomTargetEfBadCaseIsc4.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCaseGPin1(txtRfmUsimCustomTargetEfBadCaseGPin1.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCaseLPin1(txtRfmUsimCustomTargetEfBadCaseLPin1.getText());
            //save control Positive Case Access Domain RFM ISIM
            AccessDomain rfmUsimAccessDomain = new AccessDomain();
            rfmUsimAccessDomain.setUseAlways(chkRfmUsimUseAlw.isSelected());
            rfmUsimAccessDomain.setUseIsc1(chkRfmUsimUseIsc1.isSelected());
            rfmUsimAccessDomain.setUseIsc2(chkRfmUsimUseIsc2.isSelected());
            rfmUsimAccessDomain.setUseIsc3(chkRfmUsimUseIsc3.isSelected());
            rfmUsimAccessDomain.setUseIsc4(chkRfmUsimUseIsc4.isSelected());
            rfmUsimAccessDomain.setUseGPin1(chkRfmUsimUseGPin1.isSelected());
            rfmUsimAccessDomain.setUseLPin1(chkRfmUsimUseLPin1.isSelected());
            root.getRunSettings().getRfmUsim().setRfmUsimAccessDomain(rfmUsimAccessDomain);
            //save control Negative Case Access Domain RFM ISIM
            AccessDomain rfmUsimBadCaseAccessDomain = new AccessDomain();
            rfmUsimBadCaseAccessDomain.setUseBadCaseAlways(chkRfmUsimUseBadCaseAlw.isSelected());
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc1(chkRfmUsimUseBadCaseIsc1.isSelected());
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc2(chkRfmUsimUseBadCaseIsc2.isSelected());
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc3(chkRfmUsimUseBadCaseIsc3.isSelected());
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc4(chkRfmUsimUseBadCaseIsc4.isSelected());
            rfmUsimBadCaseAccessDomain.setUseBadCaseGPin1(chkRfmUsimUseBadCaseGPin1.isSelected());
            rfmUsimBadCaseAccessDomain.setUseBadCaseLPin1(chkRfmUsimUseBadCaseLPin1.isSelected());
            root.getRunSettings().getRfmUsim().setRfmUsimBadCaseAccessDomain(rfmUsimBadCaseAccessDomain);
        }

        root.getRunSettings().getRfmUsim().setUseSpecificKeyset(chkUseSpecificKeyset.isSelected());
        SCP80Keyset RfmUsimCipheringKeyset = new SCP80Keyset();
        RfmUsimCipheringKeyset.setKeysetName(cmbRfmUsimCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RfmUsimCipheringKeyset.getKeysetName())) {
                RfmUsimCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RfmUsimCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RfmUsimCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RfmUsimCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RfmUsimCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                RfmUsimCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RfmUsimCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RfmUsimCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                RfmUsimCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RfmUsimCipheringKeyset.setCustomKic(chkRfmUsimCustomKic.isSelected());
                if (RfmUsimCipheringKeyset.isCustomKic())
                    RfmUsimCipheringKeyset.setComputedKic(txtRfmUsimCustomKic.getText());
                else
                    RfmUsimCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RfmUsimCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmUsim().setCipheringKeyset(RfmUsimCipheringKeyset);

        SCP80Keyset RfmUsimAuthKeyset = new SCP80Keyset();
        RfmUsimAuthKeyset.setKeysetName(cmbRfmUsimAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RfmUsimAuthKeyset.getKeysetName())) {
                RfmUsimAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RfmUsimAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RfmUsimAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RfmUsimAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RfmUsimAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                RfmUsimAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RfmUsimAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RfmUsimAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                RfmUsimAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RfmUsimAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RfmUsimAuthKeyset.setCustomKid(chkRfmUsimCustomKid.isSelected());
                if (RfmUsimAuthKeyset.isCustomKid())
                    RfmUsimAuthKeyset.setComputedKid(txtRfmUsimCustomKid.getText());
                else
                    RfmUsimAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmUsim().setAuthKeyset(RfmUsimAuthKeyset);
    }

}
