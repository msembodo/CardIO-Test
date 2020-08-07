package com.idemia.tec.jkt.cardiotest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecretCodesController {

    @FXML private CheckBox chkPin1Disabled;
    @FXML private CheckBox chkPin2Disabled;
    @FXML private CheckBox chkInclude3gScript;
    @FXML private CheckBox chkInclude2gScript;
    @FXML private ComboBox<String> cmbGpin;
    @FXML private ComboBox<String> cmbLpin;
    @FXML private ComboBox<String> cmbGpuk;
    @FXML private ComboBox<String> cmbLpuk;
    @FXML private TextField txtGpinRetries;
    @FXML private TextField txtLpinRetries;
    @FXML private TextField txtGpukRetries;
    @FXML private TextField txtLpukRetries;
    @FXML private CheckBox chkBlockGpuk;
    @FXML private CheckBox chkBlockLpuk;
    @FXML private ComboBox<String> cmbChv1;
    @FXML private ComboBox<String> cmbChv2;
    @FXML private ComboBox<String> cmbPuk1;
    @FXML private ComboBox<String> cmbPuk2;
    @FXML private TextField txtChv1Retries;
    @FXML private TextField txtChv2Retries;
    @FXML private TextField txtPuk1Retries;
    @FXML private TextField txtPuk2Retries;
    @FXML private CheckBox chkBlockPuk1;
    @FXML private CheckBox chkBlockPuk2;
    @FXML private Label lblIsc2;
    @FXML private Label lblIsc3;
    @FXML private Label lblIsc4;
    @FXML private ComboBox<String> cmbIsc1;
    @FXML private ComboBox<String> cmbIsc2;
    @FXML private ComboBox<String> cmbIsc3;
    @FXML private ComboBox<String> cmbIsc4;
    @FXML private Label lblIsc2Retries;
    @FXML private Label lblIsc3Retries;
    @FXML private Label lblIsc4Retries;
    @FXML private TextField txtIsc1Retries;
    @FXML private TextField txtIsc2Retries;
    @FXML private TextField txtIsc3Retries;
    @FXML private TextField txtIsc4Retries;
    @FXML private CheckBox chkUseIsc2;
    @FXML private CheckBox chkUseIsc3;
    @FXML private CheckBox chkUseIsc4;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public SecretCodesController() {}

    @FXML
    private void initialize() {
        chkPin1Disabled.setSelected(root.getRunSettings().getSecretCodes().isPin1disabled());
        chkPin2Disabled.setSelected(root.getRunSettings().getSecretCodes().isPin2disabled());

        chkInclude3gScript.setSelected(root.getRunSettings().getSecretCodes().isInclude3gScript());
        handleInclude3gScriptCheck();

        chkInclude2gScript.setSelected(root.getRunSettings().getSecretCodes().isInclude2gScript());
        handleInclude2gScriptCheck();

        cmbGpin.setItems(cardiotest.getMappedVariables());
        cardiotest.registerForComboUpdate(cmbGpin);
        if (root.getRunSettings().getSecretCodes().getGpin() != null)
            cmbGpin.getSelectionModel().select(root.getRunSettings().getSecretCodes().getGpin());
        if (root.getRunSettings().getSecretCodes().getGpinRetries() != 0)
            txtGpinRetries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getGpinRetries()));
    }

    @FXML
    private void handleInclude3gScriptCheck() {
        if (chkInclude3gScript.isSelected())
            root.getMenuCodes3g().setDisable(false);
        else
            root.getMenuCodes3g().setDisable(true);
    }

    @FXML
    private void handleInclude2gScriptCheck() {
        if (chkInclude2gScript.isSelected())
            root.getMenuCodes2g().setDisable(false);
        else
            root.getMenuCodes2g().setDisable(true);
    }

    @FXML
    private void handleUseIsc2Check() {
        if (chkUseIsc2.isSelected()) {
            lblIsc2.setDisable(false);
            cmbIsc2.setDisable(false);
            lblIsc2Retries.setDisable(false);
            txtIsc2Retries.setDisable(false);
        } else {
            lblIsc2.setDisable(true);
            cmbIsc2.setDisable(true);
            lblIsc2Retries.setDisable(true);
            txtIsc2Retries.setDisable(true);
        }
    }

    @FXML
    private void handleUseIsc3Check() {
        if (chkUseIsc3.isSelected()) {
            lblIsc3.setDisable(false);
            cmbIsc3.setDisable(false);
            lblIsc3Retries.setDisable(false);
            txtIsc3Retries.setDisable(false);
        } else {
            lblIsc3.setDisable(true);
            cmbIsc3.setDisable(true);
            lblIsc3Retries.setDisable(true);
            txtIsc3Retries.setDisable(true);
        }
    }

    @FXML
    private void handleUseIsc4Check() {
        if (chkUseIsc4.isSelected()) {
            lblIsc4.setDisable(false);
            cmbIsc4.setDisable(false);
            lblIsc4Retries.setDisable(false);
            txtIsc4Retries.setDisable(false);
        } else {
            lblIsc4.setDisable(true);
            cmbIsc4.setDisable(true);
            lblIsc4Retries.setDisable(true);
            txtIsc4Retries.setDisable(true);
        }
    }

    public void saveControlState() {
        root.getRunSettings().getSecretCodes().setInclude3gScript(chkInclude3gScript.isSelected());
        root.getRunSettings().getSecretCodes().setInclude2gScript(chkInclude2gScript.isSelected());

        root.getRunSettings().getSecretCodes().setPin1disabled(chkPin1Disabled.isSelected());
        root.getRunSettings().getSecretCodes().setPin2disabled(chkPin2Disabled.isSelected());

        root.getRunSettings().getSecretCodes().setGpin(cmbGpin.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setGpinRetries(Integer.parseInt(txtGpinRetries.getText()));
        root.getRunSettings().getSecretCodes().setLpin(cmbLpin.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setLpinRetries(Integer.parseInt(txtLpinRetries.getText()));
        root.getRunSettings().getSecretCodes().setGpuk(cmbGpuk.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setGpukRetries(Integer.parseInt(txtGpukRetries.getText()));
        root.getRunSettings().getSecretCodes().setLpuk(cmbLpuk.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setLpukRetries(Integer.parseInt(txtLpukRetries.getText()));
        root.getRunSettings().getSecretCodes().setChv1(cmbChv1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setChv1Retries(Integer.parseInt(txtChv1Retries.getText()));
        root.getRunSettings().getSecretCodes().setChv2(cmbChv2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setChv2Retries(Integer.parseInt(txtChv2Retries.getText()));
        root.getRunSettings().getSecretCodes().setPuk1(cmbPuk1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setPuk1Retries(Integer.parseInt(txtPuk1Retries.getText()));
        root.getRunSettings().getSecretCodes().setPuk2(cmbPuk2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setPuk2Retries(Integer.parseInt(txtPuk2Retries.getText()));

        root.getRunSettings().getSecretCodes().setIsc1(cmbIsc1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc1Retries(Integer.parseInt(txtIsc1Retries.getText()));
        root.getRunSettings().getSecretCodes().setIsc2(cmbIsc2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc2Retries(Integer.parseInt(txtIsc2Retries.getText()));
        root.getRunSettings().getSecretCodes().setUseIsc2(chkUseIsc2.isSelected());
        root.getRunSettings().getSecretCodes().setIsc3(cmbIsc3.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc3Retries(Integer.parseInt(txtIsc3Retries.getText()));
        root.getRunSettings().getSecretCodes().setUseIsc3(chkUseIsc3.isSelected());
        root.getRunSettings().getSecretCodes().setIsc4(cmbIsc4.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc4Retries(Integer.parseInt(txtIsc4Retries.getText()));
        root.getRunSettings().getSecretCodes().setUseIsc4(chkUseIsc4.isSelected());
        root.getRunSettings().getSecretCodes().setBlockGpuk(chkBlockGpuk.isSelected());
        root.getRunSettings().getSecretCodes().setBlockLpuk(chkBlockLpuk.isSelected());
        root.getRunSettings().getSecretCodes().setBlockPuk1(chkBlockPuk1.isSelected());
        root.getRunSettings().getSecretCodes().setBlockPuk2(chkBlockPuk2.isSelected());
    }

}
