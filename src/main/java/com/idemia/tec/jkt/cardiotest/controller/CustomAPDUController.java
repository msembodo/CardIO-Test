package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.customapdu.CustomApdu;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomAPDUController {

    private CardiotestApplication application;
    @Autowired private RootLayoutController root;

    @FXML private TextField txtVerify2gChv1P1;
    @FXML private TextField txtVerify2gChv1P2;
    @FXML private TextField txtVerify2gChv1P3;
    @FXML private TextField txtVerify2gChv2P1;
    @FXML private TextField txtVerify2gChv2P2;
    @FXML private TextField txtVerify2gChv2P3;
    @FXML private TextField txtVerify2gAdm1P1;
    @FXML private TextField txtVerify2gAdm1P2;
    @FXML private TextField txtVerify2gAdm1P3;
    @FXML private TextField txtVerify2gAdm2P1;
    @FXML private TextField txtVerify2gAdm2P2;
    @FXML private TextField txtVerify2gAdm2P3;
    @FXML private TextField txtVerify2gAdm3P1;
    @FXML private TextField txtVerify2gAdm3P2;
    @FXML private TextField txtVerify2gAdm3P3;
    @FXML private TextField txtVerify2gAdm4P1;
    @FXML private TextField txtVerify2gAdm4P2;
    @FXML private TextField txtVerify2gAdm4P3;

    @FXML private TextField txtVerify3gGpin1P1;
    @FXML private TextField txtVerify3gGpin1P2;
    @FXML private TextField txtVerify3gGpin1P3;
    @FXML private TextField txtVerify3gLpin1P1;
    @FXML private TextField txtVerify3gLpin1P2;
    @FXML private TextField txtVerify3gLpin1P3;
    @FXML private TextField txtVerify3gAdm1P1;
    @FXML private TextField txtVerify3gAdm1P2;
    @FXML private TextField txtVerify3gAdm1P3;
    @FXML private TextField txtVerify3gAdm2P1;
    @FXML private TextField txtVerify3gAdm2P2;
    @FXML private TextField txtVerify3gAdm2P3;
    @FXML private TextField txtVerify3gAdm3P1;
    @FXML private TextField txtVerify3gAdm3P2;
    @FXML private TextField txtVerify3gAdm3P3;
    @FXML private TextField txtVerify3gAdm4P1;
    @FXML private TextField txtVerify3gAdm4P2;
    @FXML private TextField txtVerify3gAdm4P3;

    public CustomAPDUController() {}

    public void setMainApp(CardiotestApplication application) { this.application = application; }

    @FXML private void initialize() {
        // todo
    }

    @FXML private void handleButtonSetDefault() {
        // todo
    }

    @FXML private void handleButtonCancel() {
        // todo
    }

    @FXML private void handleButtonOk() {
        // todo
    }

    public CustomApdu getDefaultApduParams() {
        // todo
        return null;
    }

}
