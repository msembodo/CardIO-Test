package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.customapdu.*;
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

    private void setFields(CustomApdu customApdu) {
        txtVerify2gChv1P1.setText(customApdu.getVerify2g().getVerify2gChv1().getP1());
        txtVerify2gChv1P2.setText(customApdu.getVerify2g().getVerify2gChv1().getP2());
        txtVerify2gChv1P3.setText(customApdu.getVerify2g().getVerify2gChv1().getP3());

        txtVerify2gChv2P1.setText(customApdu.getVerify2g().getVerify2gChv2().getP1());
        txtVerify2gChv2P2.setText(customApdu.getVerify2g().getVerify2gChv2().getP2());
        txtVerify2gChv2P3.setText(customApdu.getVerify2g().getVerify2gChv2().getP3());

        txtVerify2gAdm1P1.setText(customApdu.getVerify2g().getVerify2gAdm1().getP1());
        txtVerify2gAdm1P2.setText(customApdu.getVerify2g().getVerify2gAdm1().getP2());
        txtVerify2gAdm1P3.setText(customApdu.getVerify2g().getVerify2gAdm1().getP3());

        txtVerify2gAdm2P1.setText(customApdu.getVerify2g().getVerify2gAdm2().getP1());
        txtVerify2gAdm2P2.setText(customApdu.getVerify2g().getVerify2gAdm2().getP2());
        txtVerify2gAdm2P3.setText(customApdu.getVerify2g().getVerify2gAdm2().getP3());

        txtVerify2gAdm3P1.setText(customApdu.getVerify2g().getVerify2gAdm3().getP1());
        txtVerify2gAdm3P2.setText(customApdu.getVerify2g().getVerify2gAdm3().getP2());
        txtVerify2gAdm3P3.setText(customApdu.getVerify2g().getVerify2gAdm3().getP3());

        txtVerify2gAdm4P1.setText(customApdu.getVerify2g().getVerify2gAdm4().getP1());
        txtVerify2gAdm4P2.setText(customApdu.getVerify2g().getVerify2gAdm4().getP2());
        txtVerify2gAdm4P3.setText(customApdu.getVerify2g().getVerify2gAdm4().getP3());

        txtVerify3gGpin1P1.setText(customApdu.getVerify3g().getVerify3gGlobalPin1().getP1());
        txtVerify3gGpin1P2.setText(customApdu.getVerify3g().getVerify3gGlobalPin1().getP2());
        txtVerify3gGpin1P3.setText(customApdu.getVerify3g().getVerify3gGlobalPin1().getP3());

        txtVerify3gLpin1P1.setText(customApdu.getVerify3g().getVerify3gLocalPin1().getP1());
        txtVerify3gLpin1P2.setText(customApdu.getVerify3g().getVerify3gLocalPin1().getP2());
        txtVerify3gLpin1P3.setText(customApdu.getVerify3g().getVerify3gLocalPin1().getP3());

        txtVerify3gAdm1P1.setText(customApdu.getVerify3g().getVerify3gAdm1().getP1());
        txtVerify3gAdm1P2.setText(customApdu.getVerify3g().getVerify3gAdm1().getP2());
        txtVerify3gAdm1P3.setText(customApdu.getVerify3g().getVerify3gAdm1().getP3());

        txtVerify3gAdm2P1.setText(customApdu.getVerify3g().getVerify3gAdm2().getP1());
        txtVerify3gAdm2P2.setText(customApdu.getVerify3g().getVerify3gAdm2().getP2());
        txtVerify3gAdm2P3.setText(customApdu.getVerify3g().getVerify3gAdm2().getP3());

        txtVerify3gAdm3P1.setText(customApdu.getVerify3g().getVerify3gAdm3().getP1());
        txtVerify3gAdm3P2.setText(customApdu.getVerify3g().getVerify3gAdm3().getP2());
        txtVerify3gAdm3P3.setText(customApdu.getVerify3g().getVerify3gAdm3().getP3());

        txtVerify3gAdm4P1.setText(customApdu.getVerify3g().getVerify3gAdm4().getP1());
        txtVerify3gAdm4P2.setText(customApdu.getVerify3g().getVerify3gAdm4().getP2());
        txtVerify3gAdm4P3.setText(customApdu.getVerify3g().getVerify3gAdm4().getP3());
    }

    @FXML private void initialize() {
        setFields(root.getRunSettings().getCustomApdu());
    }

    @FXML private void handleButtonSetDefault() {
        setFields(getDefaultApduParams());
    }

    @FXML private void handleButtonCancel() {
        application.getCustomApduStage().close();
    }

    @FXML private void handleButtonOk() {
        // save values in text fields
        Verify2gAdm1 verify2gAdm1 = new Verify2gAdm1(txtVerify2gAdm1P1.getText(), txtVerify2gAdm1P2.getText(), txtVerify2gAdm1P3.getText());
        Verify2gAdm2 verify2gAdm2 = new Verify2gAdm2(txtVerify2gAdm2P1.getText(), txtVerify2gAdm2P2.getText(), txtVerify2gAdm2P3.getText());
        Verify2gAdm3 verify2gAdm3 = new Verify2gAdm3(txtVerify2gAdm3P1.getText(), txtVerify2gAdm3P2.getText(), txtVerify2gAdm3P3.getText());
        Verify2gAdm4 verify2gAdm4 = new Verify2gAdm4(txtVerify2gAdm4P1.getText(), txtVerify2gAdm4P2.getText(), txtVerify2gAdm4P3.getText());
        Verify2gChv1 verify2gChv1 = new Verify2gChv1(txtVerify2gChv1P1.getText(), txtVerify2gChv1P2.getText(), txtVerify2gChv1P3.getText());
        Verify2gChv2 verify2gChv2 = new Verify2gChv2(txtVerify2gChv2P1.getText(), txtVerify2gChv2P2.getText(), txtVerify2gChv2P3.getText());
        Verify3gAdm1 verify3gAdm1 = new Verify3gAdm1(txtVerify3gAdm1P1.getText(), txtVerify3gAdm1P2.getText(), txtVerify3gAdm1P3.getText());
        Verify3gAdm2 verify3gAdm2 = new Verify3gAdm2(txtVerify3gAdm2P1.getText(), txtVerify3gAdm2P2.getText(), txtVerify3gAdm2P3.getText());
        Verify3gAdm3 verify3gAdm3 = new Verify3gAdm3(txtVerify3gAdm3P1.getText(), txtVerify3gAdm3P2.getText(), txtVerify3gAdm3P3.getText());
        Verify3gAdm4 verify3gAdm4 = new Verify3gAdm4(txtVerify3gAdm4P1.getText(), txtVerify3gAdm4P2.getText(), txtVerify3gAdm4P3.getText());
        Verify3gGlobalPin1 verify3gGlobalPin1 = new Verify3gGlobalPin1(txtVerify3gGpin1P1.getText(), txtVerify3gGpin1P2.getText(), txtVerify3gGpin1P3.getText());
        Verify3gLocalPin1 verify3gLocalPin1 = new Verify3gLocalPin1(txtVerify3gLpin1P1.getText(), txtVerify3gLpin1P2.getText(), txtVerify3gLpin1P3.getText());
        Verify2g verify2g = new Verify2g(verify2gChv1, verify2gChv2, verify2gAdm1, verify2gAdm2, verify2gAdm3, verify2gAdm4);
        Verify3g verify3g = new Verify3g(verify3gGlobalPin1, verify3gLocalPin1, verify3gAdm1, verify3gAdm2, verify3gAdm3, verify3gAdm4);
        CustomApdu userCustomApdu = new CustomApdu(verify2g, verify3g);
        root.getRunSettings().setCustomApdu(userCustomApdu);

        application.getCustomApduStage().close();
    }

    public CustomApdu getDefaultApduParams() {
        Verify2gAdm1 verify2gAdm1 = new Verify2gAdm1("00", "00", "08");
        Verify2gAdm2 verify2gAdm2 = new Verify2gAdm2("00", "05", "08");
        Verify2gAdm3 verify2gAdm3 = new Verify2gAdm3("00", "06", "08");
        Verify2gAdm4 verify2gAdm4 = new Verify2gAdm4("00", "07", "08");
        Verify2gChv1 verify2gChv1 = new Verify2gChv1("00", "01", "08");
        Verify2gChv2 verify2gChv2 = new Verify2gChv2("00", "02", "08");
        Verify3gAdm1 verify3gAdm1 = new Verify3gAdm1("00", "0A", "08");
        Verify3gAdm2 verify3gAdm2 = new Verify3gAdm2("00", "0B", "08");
        Verify3gAdm3 verify3gAdm3 = new Verify3gAdm3("00", "0C", "08");
        Verify3gAdm4 verify3gAdm4 = new Verify3gAdm4("00", "0D", "08");
        Verify3gGlobalPin1 verify3gGlobalPin1 = new Verify3gGlobalPin1("00", "01", "08");
        Verify3gLocalPin1 verify3gLocalPin1 = new Verify3gLocalPin1("00", "81", "08");
        Verify2g verify2g = new Verify2g(verify2gChv1, verify2gChv2, verify2gAdm1, verify2gAdm2, verify2gAdm3, verify2gAdm4);
        Verify3g verify3g = new Verify3g(verify3gGlobalPin1, verify3gLocalPin1, verify3gAdm1, verify3gAdm2, verify3gAdm3, verify3gAdm4);

        return new CustomApdu(verify2g, verify3g);
    }

}
