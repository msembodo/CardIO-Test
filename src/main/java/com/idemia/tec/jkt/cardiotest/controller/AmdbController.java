package com.idemia.tec.jkt.cardiotest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmdbController {

    @FXML private TextField txtSdTar;
    @FXML private ComboBox<String> cmbCipherKeyset;
    @FXML private ComboBox<String> cmbAuthKeyset;
    @FXML private Label lblKic;
    @FXML private Label lblKid;
    @FXML private TextField txtMslByte;
    @FXML private ComboBox<String> cmbCipherAlgo;
    @FXML private CheckBox chkUseCipher;
    @FXML private ComboBox<String> cmbAuthVerif;
    @FXML private ComboBox<String> cmbSigningAlgo;
    @FXML private ComboBox<String> cmbCounterCheck;
    @FXML private ComboBox<String> cmbPorRequirement;
    @FXML private ComboBox<String> cmbPorSecurity;
    @FXML private CheckBox chkCipherPor;
    @FXML private CheckBox chkDestinationAddress;
    @FXML private CheckBox chkBufferSize;
    @FXML private CheckBox chkNetworkAccessName;
    @FXML private CheckBox chkTransportLevel;
    @FXML private TextField txtDestinationAddress;
    @FXML private TextField txtBufferSize;
    @FXML private TextField txtNetworkAccessName;
    @FXML private ComboBox<String> cmbTransportLevel;
    @FXML private TextField txtPort;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public AmdbController() {}

    @FXML public void initialize() {
        // TODO
    }

    @FXML private void handleCipherKeysetSelection() {
        // TODO
    }

    @FXML private void handleAuthKeysetSelection() {
        // TODO
    }

    @FXML private void handleBtnSetMsl() {
        // TODO
    }

    @FXML private void handleUseCipherCheck() {
        // TODO
    }

    @FXML private void handleAuthVerifSelection() {
        // TODO
    }

    @FXML private void handleCounterCheckSelection() {
        // TODO
    }

    public void saveControlState() {
        // TODO
    }

}
