package com.idemia.tec.jkt.cardiotest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @FXML private ComboBox<String> cmbRfmIsimCustomTargetAcc;
    @FXML private TextField txtRfmIsimCustomTargetEf;
    @FXML private Label lblRfmIsimCustomTargetBadCase;
    @FXML private ComboBox<String> cmbRfmIsimCustomTargetAccBadCase;
    @FXML private TextField txtRfmIsimCustomTargetEfBadCase;
    @FXML private CheckBox chkRfmIsimUseSpecificKeyset;
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

    @Autowired
    private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public RfmIsimController() {}

    @FXML
    private void initialize() {
        // TODO
    }

    public void saveControlState() {
        // TODO
    }

}
