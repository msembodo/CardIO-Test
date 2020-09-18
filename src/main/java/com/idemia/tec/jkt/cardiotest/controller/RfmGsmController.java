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
    @FXML private TextField txtRfmGsmCustomTargetEfAlw;
    @FXML private TextField txtRfmGsmCustomTargetEfIsc1;
    @FXML private TextField txtRfmGsmCustomTargetEfIsc2;
    @FXML private TextField txtRfmGsmCustomTargetEfIsc3;
    @FXML private TextField txtRfmGsmCustomTargetEfIsc4;
    @FXML private TextField txtRfmGsmCustomTargetEfGPin1;
    @FXML private TextField txtRfmGsmCustomTargetEfLPin1;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCaseAlw;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCaseIsc1;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCaseIsc2;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCaseIsc3;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCaseIsc4;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCaseGPin1;
    @FXML private TextField txtRfmGsmCustomTargetEfBadCaseLPin1;
    @FXML private Label lblRfmGsmCustomTargetBadCase;
    @FXML private CheckBox chkUseSpecificKeyset;
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
    @FXML private CheckBox chkRfmGsmUseAlw;
    @FXML private CheckBox chkRfmGsmUseIsc1;
    @FXML private CheckBox chkRfmGsmUseIsc2;
    @FXML private CheckBox chkRfmGsmUseIsc3;
    @FXML private CheckBox chkRfmGsmUseIsc4;
    @FXML private CheckBox chkRfmGsmUseGPin1;
    @FXML private CheckBox chkRfmGsmUseLPin1;
    @FXML private CheckBox chkRfmGsmUseBadCaseAlw;
    @FXML private CheckBox chkRfmGsmUseBadCaseIsc1;
    @FXML private CheckBox chkRfmGsmUseBadCaseIsc2;
    @FXML private CheckBox chkRfmGsmUseBadCaseIsc3;
    @FXML private CheckBox chkRfmGsmUseBadCaseIsc4;
    @FXML private CheckBox chkRfmGsmUseBadCaseGPin1;
    @FXML private CheckBox chkRfmGsmUseBadCaseLPin1;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public RfmGsmController() {}

    @FXML public void initialize() {
        chkIncludeRfmGsm.setSelected(root.getRunSettings().getRfmGsm().isIncludeRfmGsm());
        handleIncludeRfmGsmCheck();

        chkIncludeRfmGsmUpdateRecord.setSelected(root.getRunSettings().getRfmGsm().isIncludeRfmGsmUpdateRecord());
        handleIncludeRfmGsmUpdateRecordCheck();

        chkIncludeRfmGsmExpandedMode.setSelected(root.getRunSettings().getRfmGsm().isIncludeRfmGsmExpandedMode());
        handleIncludeRfmGsmExpandedModeCheck();

        // RFM GSM MSL

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
        if (!(cmbRfmGsmCipherAlgo.getItems().size() > 0)) cmbRfmGsmCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRfmGsmCipherAlgo.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        if (!(cmbRfmGsmAuthVerif.getItems().size() > 0)) cmbRfmGsmAuthVerif.getItems().addAll(authVerifs);
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
        if (!(cmbRfmGsmSigningAlgo.getItems().size() > 0)) cmbRfmGsmSigningAlgo.getItems().addAll(signingAlgos);
        cmbRfmGsmSigningAlgo.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        if (!(cmbRfmGsmCounterCheck.getItems().size() > 0)) cmbRfmGsmCounterCheck.getItems().addAll(counterCheckings);
        cmbRfmGsmCounterCheck.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        if (!(cmbRfmGsmPorRequirement.getItems().size() > 0)) cmbRfmGsmPorRequirement.getItems().addAll(porRequirements);
        cmbRfmGsmPorRequirement.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getPorRequirement());

        chkRfmGsmCipherPor.setSelected(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        if (!(cmbRfmGsmPorSecurity.getItems().size() > 0)) cmbRfmGsmPorSecurity.getItems().addAll(porSecurities);
        cmbRfmGsmPorSecurity.setValue(root.getRunSettings().getRfmGsm().getMinimumSecurityLevel().getPorSecurity());

        // RFM GSM parameters

        txtRfmGsmTar.setText(root.getRunSettings().getRfmGsm().getTar());
        txtRfmGsmTargetEf.setText(root.getRunSettings().getRfmGsm().getTargetEf());
        txtRfmGsmTargetEfBadCase.setText(root.getRunSettings().getRfmGsm().getTargetEfBadCase());

        // Initialize Access Domain Positive Case
        chkRfmGsmUseAlw.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmAccessDomain().isUseAlways());
        handleIncludeAlwCheck();
        if (chkRfmGsmUseAlw.isSelected()){
            txtRfmGsmCustomTargetEfAlw.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfAlw());
        }
        else {
            txtRfmGsmCustomTargetEfAlw.setText("");
        }

        chkRfmGsmUseIsc1.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmAccessDomain().isUseIsc1());
        handleIncludeIsc1Check();
        if (chkRfmGsmUseIsc1.isSelected()){
            txtRfmGsmCustomTargetEfIsc1.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfIsc1());
        }
        else {
            txtRfmGsmCustomTargetEfIsc1.setText("");
        }

        chkRfmGsmUseIsc2.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmAccessDomain().isUseIsc2());
        handleIncludeIsc2Check();
        if (chkRfmGsmUseIsc2.isSelected()){
            txtRfmGsmCustomTargetEfIsc2.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfIsc2());
        }
        else {
            txtRfmGsmCustomTargetEfIsc2.setText("");
        }

        chkRfmGsmUseIsc3.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmAccessDomain().isUseIsc3());
        handleIncludeIsc3Check();
        if (chkRfmGsmUseIsc3.isSelected()){
            txtRfmGsmCustomTargetEfIsc3.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfIsc3());
        }
        else {
            txtRfmGsmCustomTargetEfIsc3.setText("");
        }

        chkRfmGsmUseIsc4.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmAccessDomain().isUseIsc4());
        handleIncludeIsc4Check();
        if (chkRfmGsmUseIsc4.isSelected()){
            txtRfmGsmCustomTargetEfIsc4.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfIsc4());
        }
        else {
            txtRfmGsmCustomTargetEfIsc4.setText("");
        }

        chkRfmGsmUseGPin1.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmAccessDomain().isUseGPin1());
        handleIncludeGPin1Check();
        if (chkRfmGsmUseGPin1.isSelected()){
            txtRfmGsmCustomTargetEfGPin1.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfGPin1());
        }
        else {
            txtRfmGsmCustomTargetEfGPin1.setText("");
        }

        chkRfmGsmUseLPin1.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmAccessDomain().isUseLPin1());
        handleIncludeLPin1Check();
        if (chkRfmGsmUseLPin1.isSelected()){
            txtRfmGsmCustomTargetEfLPin1.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfLPin1());
        }
        else {
            txtRfmGsmCustomTargetEfLPin1.setText("");
        }

        // Initialize Access Domain Negative Case
        chkRfmGsmUseBadCaseAlw.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmBadCaseAccessDomain().isUseBadCaseAlways());
        handleIncludeAlwCheck();
        if (chkRfmGsmUseBadCaseAlw.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseAlw.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCaseAlw());
        }
        else {
            txtRfmGsmCustomTargetEfBadCaseAlw.setText("");
        }

        chkRfmGsmUseBadCaseIsc1.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc1());
        handleIncludeIsc1Check();
        if (chkRfmGsmUseBadCaseIsc1.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc1.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCaseIsc1());
        }
        else {
            txtRfmGsmCustomTargetEfBadCaseIsc1.setText("");
        }

        chkRfmGsmUseBadCaseIsc2.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc2());
        handleIncludeIsc2Check();
        if (chkRfmGsmUseBadCaseIsc2.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc2.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCaseIsc2());
        }
        else {
            txtRfmGsmCustomTargetEfBadCaseIsc2.setText("");
        }

        chkRfmGsmUseBadCaseIsc3.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc3());
        handleIncludeIsc3Check();
        if (chkRfmGsmUseBadCaseIsc3.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc3.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCaseIsc3());
        }
        else {
            txtRfmGsmCustomTargetEfBadCaseIsc3.setText("");
        }

        chkRfmGsmUseBadCaseIsc4.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmBadCaseAccessDomain().isUseBadCaseIsc4());
        handleIncludeIsc4Check();
        if (chkRfmGsmUseBadCaseIsc4.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc4.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCaseIsc4());
        }
        else {
            txtRfmGsmCustomTargetEfBadCaseIsc4.setText("");
        }

        chkRfmGsmUseBadCaseGPin1.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmBadCaseAccessDomain().isUseBadCaseGPin1());
        handleIncludeGPin1Check();
        if (chkRfmGsmUseBadCaseGPin1.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseGPin1.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCaseGPin1());
        }
        else {
            txtRfmGsmCustomTargetEfBadCaseGPin1.setText("");
        }

        chkRfmGsmUseBadCaseLPin1.setSelected(root.getRunSettings().getRfmGsm().getRfmGsmBadCaseAccessDomain().isUseBadCaseLPin1());
        handleIncludeLPin1Check();
        if (chkRfmGsmUseBadCaseLPin1.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseLPin1.setText(root.getRunSettings().getRfmGsm().getCustomTargetEfBadCaseLPin1());
        }
        else {
            txtRfmGsmCustomTargetEfBadCaseLPin1.setText("");
        }

        chkRfmGsmFullAccess.setSelected(root.getRunSettings().getRfmGsm().isFullAccess());
        handleRfmGsmFullAccessCheck();

        // initialize list of available keysets for RFM GSM

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

        chkUseSpecificKeyset.setSelected(root.getRunSettings().getRfmGsm().isUseSpecificKeyset());
        handleUseSpecificKeysetCheck();
    }

    @FXML private void handleCipherKeysetContextMenu() { cmbRfmGsmCipheringKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleAuthKeysetContextMenu() { cmbRfmGsmAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

    @FXML private void handleIncludeRfmGsmCheck() { root.getMenuRfmGsm().setDisable(!chkIncludeRfmGsm.isSelected()); }
    @FXML private void handleIncludeRfmGsmUpdateRecordCheck() { root.getMenuRfmGsmUpdateRecord().setDisable(!chkIncludeRfmGsmUpdateRecord.isSelected()); }
    @FXML private void handleIncludeRfmGsmExpandedModeCheck() { root.getMenuRfmGsmExpandedMode().setDisable(!chkIncludeRfmGsmExpandedMode.isSelected()); }

    @FXML private void handleRfmGsmFullAccessCheck() {
        if (chkRfmGsmFullAccess.isSelected()) {
            lblRfmGsmCustomTarget.setDisable(true);
            txtRfmGsmCustomTargetEfAlw.setDisable(true);
            txtRfmGsmCustomTargetEfIsc1.setDisable(true);
            txtRfmGsmCustomTargetEfIsc2.setDisable(true);
            txtRfmGsmCustomTargetEfIsc3.setDisable(true);
            txtRfmGsmCustomTargetEfIsc4.setDisable(true);
            txtRfmGsmCustomTargetEfGPin1.setDisable(true);
            txtRfmGsmCustomTargetEfLPin1.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseAlw.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc1.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc2.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc3.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc4.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseGPin1.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmGsmUseAlw.setDisable(true);
            chkRfmGsmUseIsc1.setDisable(true);
            chkRfmGsmUseIsc2.setDisable(true);
            chkRfmGsmUseIsc3.setDisable(true);
            chkRfmGsmUseIsc4.setDisable(true);
            chkRfmGsmUseGPin1.setDisable(true);
            chkRfmGsmUseLPin1.setDisable(true);
            chkRfmGsmUseBadCaseAlw.setDisable(true);
            chkRfmGsmUseBadCaseIsc1.setDisable(true);
            chkRfmGsmUseBadCaseIsc2.setDisable(true);
            chkRfmGsmUseBadCaseIsc3.setDisable(true);
            chkRfmGsmUseBadCaseIsc4.setDisable(true);
            chkRfmGsmUseBadCaseGPin1.setDisable(true);
            chkRfmGsmUseBadCaseLPin1.setDisable(true);
            lblRfmGsmCustomTargetBadCase.setDisable(true);
            txtRfmGsmTargetEf.setDisable(false);
            txtRfmGsmTargetEfBadCase.setDisable(false);
        }
        else {
            txtRfmGsmCustomTargetEfAlw.setDisable(false);
            txtRfmGsmCustomTargetEfIsc1.setDisable(false);
            txtRfmGsmCustomTargetEfIsc2.setDisable(false);
            txtRfmGsmCustomTargetEfIsc3.setDisable(false);
            txtRfmGsmCustomTargetEfIsc4.setDisable(false);
            txtRfmGsmCustomTargetEfGPin1.setDisable(false);
            txtRfmGsmCustomTargetEfLPin1.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseAlw.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc1.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc2.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc3.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc4.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseGPin1.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseLPin1.setDisable(false);
            chkRfmGsmUseAlw.setDisable(false);
            chkRfmGsmUseIsc1.setDisable(false);
            chkRfmGsmUseIsc2.setDisable(false);
            chkRfmGsmUseIsc3.setDisable(false);
            chkRfmGsmUseIsc4.setDisable(false);
            chkRfmGsmUseGPin1.setDisable(false);
            chkRfmGsmUseLPin1.setDisable(false);
            chkRfmGsmUseBadCaseAlw.setDisable(false);
            chkRfmGsmUseBadCaseIsc1.setDisable(false);
            chkRfmGsmUseBadCaseIsc2.setDisable(false);
            chkRfmGsmUseBadCaseIsc3.setDisable(false);
            chkRfmGsmUseBadCaseIsc4.setDisable(false);
            chkRfmGsmUseBadCaseGPin1.setDisable(false);
            chkRfmGsmUseBadCaseLPin1.setDisable(false);
            lblRfmGsmCustomTargetBadCase.setDisable(false);
            lblRfmGsmCustomTarget.setDisable(false);
            lblRfmGsmCustomTargetBadCase.setDisable(false);
            txtRfmGsmTargetEf.setDisable(true);
            txtRfmGsmTargetEfBadCase.setDisable(true);
        }
    }

    // handle Access Domain Positive Case

    @FXML private void handleIncludeAlwCheck() {
        if(chkRfmGsmUseAlw.isSelected()){
            txtRfmGsmCustomTargetEfAlw.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseAlw.setDisable(true);
            chkRfmGsmUseBadCaseAlw.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfAlw.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseAlw.setDisable(true);
            chkRfmGsmUseBadCaseAlw.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc1Check() {
        if(chkRfmGsmUseIsc1.isSelected()){
            txtRfmGsmCustomTargetEfIsc1.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc1.setDisable(true);
            chkRfmGsmUseBadCaseIsc1.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfIsc1.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc1.setDisable(true);
            chkRfmGsmUseBadCaseIsc1.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc2Check() {
        if(chkRfmGsmUseIsc2.isSelected()){
            txtRfmGsmCustomTargetEfIsc2.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc2.setDisable(true);
            chkRfmGsmUseBadCaseIsc2.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfIsc2.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc2.setDisable(true);
            chkRfmGsmUseBadCaseIsc2.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc3Check() {
        if(chkRfmGsmUseIsc3.isSelected()){
            txtRfmGsmCustomTargetEfIsc3.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc3.setDisable(true);
            chkRfmGsmUseBadCaseIsc3.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfIsc3.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc3.setDisable(true);
            chkRfmGsmUseBadCaseIsc3.setDisable(false);
        }
    }

    @FXML private void handleIncludeIsc4Check() {
        if(chkRfmGsmUseIsc4.isSelected()){
            txtRfmGsmCustomTargetEfIsc4.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseIsc4.setDisable(true);
            chkRfmGsmUseBadCaseIsc4.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfIsc4.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseIsc4.setDisable(true);
            chkRfmGsmUseBadCaseIsc4.setDisable(false);
        }
    }

    @FXML private void handleIncludeGPin1Check() {
        if(chkRfmGsmUseGPin1.isSelected()){
            txtRfmGsmCustomTargetEfGPin1.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseGPin1.setDisable(true);
            chkRfmGsmUseBadCaseGPin1.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfGPin1.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseGPin1.setDisable(true);
            chkRfmGsmUseBadCaseGPin1.setDisable(false);
        }
    }

    @FXML private void handleIncludeLPin1Check() {
        if(chkRfmGsmUseLPin1.isSelected()){
            txtRfmGsmCustomTargetEfLPin1.setDisable(false);
            txtRfmGsmCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmGsmUseBadCaseLPin1.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfLPin1.setDisable(true);
            txtRfmGsmCustomTargetEfBadCaseLPin1.setDisable(true);
            chkRfmGsmUseBadCaseLPin1.setDisable(false);
        }
    }

    // handle Access Domain Negative Case

    @FXML private void handleIncludeBadCaseAlwCheck() {
        if(chkRfmGsmUseBadCaseAlw.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseAlw.setDisable(false);
            txtRfmGsmCustomTargetEfAlw.setDisable(true);
            chkRfmGsmUseAlw.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfBadCaseAlw.setDisable(true);
            txtRfmGsmCustomTargetEfAlw.setDisable(true);
            chkRfmGsmUseAlw.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc1Check() {
        if(chkRfmGsmUseBadCaseIsc1.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc1.setDisable(false);
            txtRfmGsmCustomTargetEfIsc1.setDisable(true);
            chkRfmGsmUseIsc1.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfBadCaseIsc1.setDisable(true);
            txtRfmGsmCustomTargetEfIsc1.setDisable(true);
            chkRfmGsmUseIsc1.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc2Check() {
        if(chkRfmGsmUseBadCaseIsc2.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc2.setDisable(false);
            txtRfmGsmCustomTargetEfIsc2.setDisable(true);
            chkRfmGsmUseIsc2.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfBadCaseIsc2.setDisable(true);
            txtRfmGsmCustomTargetEfIsc2.setDisable(true);
            chkRfmGsmUseIsc2.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc3Check() {
        if(chkRfmGsmUseBadCaseIsc3.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc3.setDisable(false);
            txtRfmGsmCustomTargetEfIsc3.setDisable(true);
            chkRfmGsmUseIsc3.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfBadCaseIsc3.setDisable(true);
            txtRfmGsmCustomTargetEfIsc3.setDisable(true);
            chkRfmGsmUseIsc3.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseIsc4Check() {
        if(chkRfmGsmUseBadCaseIsc4.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseIsc4.setDisable(false);
            txtRfmGsmCustomTargetEfIsc4.setDisable(true);
            chkRfmGsmUseIsc4.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfBadCaseIsc4.setDisable(true);
            txtRfmGsmCustomTargetEfIsc4.setDisable(true);
            chkRfmGsmUseIsc4.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseGPin1Check() {
        if(chkRfmGsmUseBadCaseGPin1.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseGPin1.setDisable(false);
            txtRfmGsmCustomTargetEfGPin1.setDisable(true);
            chkRfmGsmUseGPin1.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfBadCaseGPin1.setDisable(true);
            txtRfmGsmCustomTargetEfGPin1.setDisable(true);
            chkRfmGsmUseGPin1.setDisable(false);
        }
    }

    @FXML private void handleIncludeBadCaseLPin1Check() {
        if(chkRfmGsmUseBadCaseLPin1.isSelected()){
            txtRfmGsmCustomTargetEfBadCaseLPin1.setDisable(false);
            txtRfmGsmCustomTargetEfLPin1.setDisable(true);
            chkRfmGsmUseLPin1.setDisable(true);
        }
        else{
            txtRfmGsmCustomTargetEfBadCaseLPin1.setDisable(true);
            txtRfmGsmCustomTargetEfLPin1.setDisable(true);
            chkRfmGsmUseLPin1.setDisable(false);
        }
    }


    @FXML private void handleUseSpecificKeysetCheck() {
        if (chkUseSpecificKeyset.isSelected()) {
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
            root.getRunSettings().getRfmGsm().setCustomTargetEfAlw(txtRfmGsmCustomTargetEfAlw.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfIsc1(txtRfmGsmCustomTargetEfIsc1.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfIsc2(txtRfmGsmCustomTargetEfIsc2.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfIsc3(txtRfmGsmCustomTargetEfIsc3.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfIsc4(txtRfmGsmCustomTargetEfIsc4.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfGPin1(txtRfmGsmCustomTargetEfGPin1.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfLPin1(txtRfmGsmCustomTargetEfLPin1.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCaseAlw(txtRfmGsmCustomTargetEfBadCaseAlw.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCaseIsc1(txtRfmGsmCustomTargetEfBadCaseIsc1.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCaseIsc2(txtRfmGsmCustomTargetEfBadCaseIsc2.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCaseIsc3(txtRfmGsmCustomTargetEfBadCaseIsc3.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCaseIsc4(txtRfmGsmCustomTargetEfBadCaseIsc4.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCaseGPin1(txtRfmGsmCustomTargetEfBadCaseGPin1.getText());
            root.getRunSettings().getRfmGsm().setCustomTargetEfBadCaseLPin1(txtRfmGsmCustomTargetEfBadCaseLPin1.getText());
            //save control Positive Case Access Domain RFM GSM
            AccessDomain rfmGsmAccessDomain = new AccessDomain();
            rfmGsmAccessDomain.setUseAlways(chkRfmGsmUseAlw.isSelected());
            rfmGsmAccessDomain.setUseIsc1(chkRfmGsmUseIsc1.isSelected());
            rfmGsmAccessDomain.setUseIsc2(chkRfmGsmUseIsc2.isSelected());
            rfmGsmAccessDomain.setUseIsc3(chkRfmGsmUseIsc3.isSelected());
            rfmGsmAccessDomain.setUseIsc4(chkRfmGsmUseIsc4.isSelected());
            rfmGsmAccessDomain.setUseGPin1(chkRfmGsmUseGPin1.isSelected());
            rfmGsmAccessDomain.setUseLPin1(chkRfmGsmUseLPin1.isSelected());
            root.getRunSettings().getRfmGsm().setRfmGsmAccessDomain(rfmGsmAccessDomain);
            //save control Negative Case Access Domain RFM GSM
            AccessDomain rfmGsmBadCaseAccessDomain = new AccessDomain();
            rfmGsmBadCaseAccessDomain.setUseBadCaseAlways(chkRfmGsmUseBadCaseAlw.isSelected());
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc1(chkRfmGsmUseBadCaseIsc1.isSelected());
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc2(chkRfmGsmUseBadCaseIsc2.isSelected());
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc3(chkRfmGsmUseBadCaseIsc3.isSelected());
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc4(chkRfmGsmUseBadCaseIsc4.isSelected());
            rfmGsmBadCaseAccessDomain.setUseBadCaseGPin1(chkRfmGsmUseBadCaseGPin1.isSelected());
            rfmGsmBadCaseAccessDomain.setUseBadCaseLPin1(chkRfmGsmUseBadCaseLPin1.isSelected());
            root.getRunSettings().getRfmGsm().setRfmGsmBadCaseAccessDomain(rfmGsmBadCaseAccessDomain);
        }

        root.getRunSettings().getRfmGsm().setUseSpecificKeyset(chkUseSpecificKeyset.isSelected());
        SCP80Keyset RfmGsmCipheringKeyset = new SCP80Keyset();
        RfmGsmCipheringKeyset.setKeysetName(cmbRfmGsmCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RfmGsmCipheringKeyset.getKeysetName())) {
                RfmGsmCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RfmGsmCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RfmGsmCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RfmGsmCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RfmGsmCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                RfmGsmCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RfmGsmCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RfmGsmCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                RfmGsmCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RfmGsmCipheringKeyset.setCustomKic(chkRfmGsmCustomKic.isSelected());
                if (RfmGsmCipheringKeyset.isCustomKic())
                    RfmGsmCipheringKeyset.setComputedKic(txtRfmGsmCustomKic.getText());
                else
                    RfmGsmCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RfmGsmCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmGsm().setCipheringKeyset(RfmGsmCipheringKeyset);

        SCP80Keyset RfmGsmAuthKeyset = new SCP80Keyset();
        RfmGsmAuthKeyset.setKeysetName(cmbRfmGsmAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RfmGsmAuthKeyset.getKeysetName())) {
                RfmGsmAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RfmGsmAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RfmGsmAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RfmGsmAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RfmGsmAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                RfmGsmAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RfmGsmAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RfmGsmAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                RfmGsmAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RfmGsmAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RfmGsmAuthKeyset.setCustomKid(chkRfmGsmCustomKid.isSelected());
                if (RfmGsmAuthKeyset.isCustomKid())
                    RfmGsmAuthKeyset.setComputedKid(txtRfmGsmCustomKid.getText());
                else
                    RfmGsmAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmGsm().setAuthKeyset(RfmGsmAuthKeyset);
    }

}
