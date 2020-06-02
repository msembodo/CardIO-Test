package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToolOptionsController {

    @FXML
    private CheckBox chkStopOnError;

    private CardiotestApplication application;

    @Autowired
    private RootLayoutController root;

    public ToolOptionsController() {}

    public void setMainApp(CardiotestApplication application) {
        this.application = application;
    }

    @FXML
    private void initialize() {
        chkStopOnError.setSelected(root.getRunSettings().isStopOnError());
    }

    @FXML
    private void handleButtonCancel() {
        application.getToolOptionsDialogStage().close();
    }

    @FXML
    private void handleButtonOk() {
        root.getRunSettings().setStopOnError(chkStopOnError.isSelected());
        application.getToolOptionsDialogStage().close();
    }

}
