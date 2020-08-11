package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.CustomScript;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomTabController {

    static Logger logger = Logger.getLogger(CustomTabController.class);

    @Autowired private RootLayoutController root;

    @FXML private TableView<CustomScript> tblCustomScript1;
    @FXML private TableColumn<CustomScript, String> clmnCs1Name;
    @FXML private TextField txtCs1Description;
    @FXML private TextField txtCs1ScriptName;
    @FXML private Button btnCs1Choose;
    @FXML private Button btnCs1Edit;
    @FXML private Label lblAddCs1ScriptErrMsg;

    public CustomTabController() {}

    @FXML private void initialize() {
        clmnCs1Name.setCellValueFactory(celldata -> celldata.getValue().customScriptNameProperty());
        showCustomScriptSection1(null);
        tblCustomScript1.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCustomScriptSection1(newValue)
        );
        tblCustomScript1.setItems(root.getCustomScriptsSection1());
        for (CustomScript customScript : root.getRunSettings().getCustomScriptsSection1())
            root.getCustomScriptsSection1().add(customScript);
        tblCustomScript1.getSelectionModel().select(0); // select first row initially

        btnCs1Choose.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT));
        btnCs1Choose.setTooltip(new Tooltip("Choose custom script"));
        btnCs1Edit.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        btnCs1Edit.setTooltip(new Tooltip("Edit custom script"));
        // TODO
    }

    @FXML private void handleBtnCs1Choose() {
        // TODO
    }

    @FXML private void handleBtnCs1Edit() {
        // TODO
    }

    @FXML private void handleBtnCs1Add() {
        if (section1ScriptExists(txtCs1ScriptName.getText())) {
            lblAddCs1ScriptErrMsg.setVisible(true);
            lblAddCs1ScriptErrMsg.setText("Custom script with same name already exists.");
        } else {
            String scriptName = txtCs1ScriptName.getText();
            String description = txtCs1Description.getText();
            CustomScript customScript = new CustomScript(scriptName, description);
            root.getCustomScriptsSection1().add(customScript);
            lblAddCs1ScriptErrMsg.setVisible(false);
            logger.info("Added custom script in section 1: " + customScript.toJson());
        }
    }

    @FXML private void handleBtnCs1Delete() {
        if (root.getCustomScriptsSection1().size() > 0) {
            lblAddCs1ScriptErrMsg.setVisible(false);
            logger.info("Delete custom script in section 1: " + tblCustomScript1.getSelectionModel().getSelectedItem().getCustomScriptName());
            int selectedIndex = tblCustomScript1.getSelectionModel().getSelectedIndex();
            tblCustomScript1.getItems().remove(selectedIndex);
            showCustomScriptSection1(null);
            tblCustomScript1.getSelectionModel().clearSelection();
        }
    }

    private void showCustomScriptSection1(CustomScript customScript) {
        if (customScript != null) {
            txtCs1Description.setText(customScript.getDescription());
            txtCs1ScriptName.setText(customScript.getCustomScriptName());
        } else {
            txtCs1Description.setText("");
            txtCs1ScriptName.setText("");
        }
    }

    private boolean section1ScriptExists(String checkScriptName) {
        for (CustomScript customScript : root.getCustomScriptsSection1()) {
            if (customScript.getCustomScriptName().equals(checkScriptName))
                return true;
        }
        return false;
    }

}
