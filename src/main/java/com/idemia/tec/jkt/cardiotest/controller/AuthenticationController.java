package com.idemia.tec.jkt.cardiotest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationController {

    @FXML private CheckBox chkIncludeDeltaTest;
    @FXML private CheckBox chkIncludeSqnMax;
    @FXML private TextField txtResLength;
    @FXML private ComboBox<String> cmbAkaC1;
    @FXML private ComboBox<String> cmbAkaC2;
    @FXML private ComboBox<String> cmbAkaC3;
    @FXML private ComboBox<String> cmbAkaC4;
    @FXML private ComboBox<String> cmbAkaC5;
    @FXML private ComboBox<String> cmbAkaRi;
    @FXML private TextField txtRand;
    @FXML private TextField txtSqn;
    @FXML private TextField txtSqnMax;
    @FXML private TextField txtDelta;
    @FXML private TextField txtAmf;
    @FXML private ComboBox<String> cmbKi;
    @FXML private ComboBox<String> cmbOpc;
    @FXML private CheckBox chkComp1282;
    @FXML private CheckBox chkComp1283;
    @FXML private CheckBox chkMilenage;
    @FXML private CheckBox chkIsimAuth;
    @FXML private CheckBox chkGsmAlgo;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public AuthenticationController() {}

    @FXML private void initialize() {
        chkIncludeDeltaTest.setSelected(root.getRunSettings().getAuthentication().isIncludeDeltaTest());
        handleIncludeDeltaTestCheck();

        chkIncludeSqnMax.setSelected(root.getRunSettings().getAuthentication().isIncludeSqnMax());
        handleIncludeSqnMaxCheck();

        txtResLength.setText(root.getRunSettings().getAuthentication().getResLength());

        cmbAkaC1.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC1());
        cmbAkaC2.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC2());
        cmbAkaC3.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC3());
        cmbAkaC4.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC4());
        cmbAkaC5.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC5());
        cmbAkaRi.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaRi());

        txtRand.setText(root.getRunSettings().getAuthentication().getRand());
        txtSqn.setText(root.getRunSettings().getAuthentication().getSqn());
        txtSqnMax.setText(root.getRunSettings().getAuthentication().getSqnMax());
        txtDelta.setText(root.getRunSettings().getAuthentication().getDelta());
        txtAmf.setText(root.getRunSettings().getAuthentication().getAmf());

        cmbKi.getSelectionModel().select(root.getRunSettings().getAuthentication().getKi());
        cmbOpc.getSelectionModel().select(root.getRunSettings().getAuthentication().getOpc());

        chkComp1282.setSelected(root.getRunSettings().getAuthentication().isComp1282());
        chkComp1283.setSelected(root.getRunSettings().getAuthentication().isComp1283());
        chkMilenage.setSelected(root.getRunSettings().getAuthentication().isMilenage());
        chkIsimAuth.setSelected(root.getRunSettings().getAuthentication().isIsimAuth());
        chkGsmAlgo.setSelected(root.getRunSettings().getAuthentication().isGsmAlgo());
    }

    @FXML private void handleC1ContextMenu() { cmbAkaC1.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleC2ContextMenu() { cmbAkaC2.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleC3ContextMenu() { cmbAkaC3.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleC4ContextMenu() { cmbAkaC4.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleC5ContextMenu() { cmbAkaC5.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleRiContextMenu() { cmbAkaRi.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleKiContextMenu() { cmbKi.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleOpcContextMenu() { cmbOpc.setItems(cardiotest.getMappedVariables()); }

    @FXML
    private void handleIncludeDeltaTestCheck() {
        if (chkIncludeDeltaTest.isSelected())
            root.getMenuDeltaTest().setDisable(false);
        else
            root.getMenuDeltaTest().setDisable(true);
    }

    @FXML
    private void handleIncludeSqnMaxCheck() {
        if (chkIncludeSqnMax.isSelected())
            root.getMenuSqnMax().setDisable(false);
        else
            root.getMenuSqnMax().setDisable(true);
    }

    public void saveControlState() {
        root.getRunSettings().getAuthentication().setIncludeDeltaTest(chkIncludeDeltaTest.isSelected());
        root.getRunSettings().getAuthentication().setIncludeSqnMax(chkIncludeSqnMax.isSelected());

        root.getRunSettings().getAuthentication().setResLength(txtResLength.getText());
        root.getRunSettings().getAuthentication().setAkaC1(cmbAkaC1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC2(cmbAkaC2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC3(cmbAkaC3.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC4(cmbAkaC4.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC5(cmbAkaC5.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaRi(cmbAkaRi.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setRand(txtRand.getText());
        root.getRunSettings().getAuthentication().setDelta(txtDelta.getText());
        root.getRunSettings().getAuthentication().setSqn(txtSqn.getText());
        root.getRunSettings().getAuthentication().setSqnMax(txtSqnMax.getText());
        root.getRunSettings().getAuthentication().setAmf(txtAmf.getText());
        root.getRunSettings().getAuthentication().setKi(cmbKi.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setOpc(cmbOpc.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setComp1282(chkComp1282.isSelected());
        root.getRunSettings().getAuthentication().setComp1283(chkComp1283.isSelected());
        root.getRunSettings().getAuthentication().setMilenage(chkMilenage.isSelected());
        root.getRunSettings().getAuthentication().setIsimAuth(chkIsimAuth.isSelected());
        root.getRunSettings().getAuthentication().setGsmAlgo(chkGsmAlgo.isSelected());
    }

}
