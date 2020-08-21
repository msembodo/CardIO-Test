package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ImportSettingsController {

    static Logger logger = Logger.getLogger(ImportSettingsController.class);

    private CardiotestApplication application;
    @Autowired private RootLayoutController root;

    @FXML private TextField txtProjectDir;
    @FXML private TextField txtAdvSave;
    @FXML private Button btnProjectDir;
    @FXML private Button btnAdvSave;

    private File projectDir;
    private File selectVarFile;

    public ImportSettingsController() {}

    public void setMainApp(CardiotestApplication application) { this.application = application; }

    @FXML private void initialize() {
        btnProjectDir.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN_ALT));
        btnProjectDir.setTooltip(new Tooltip("Select project directory"));
        btnAdvSave.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT));
        btnAdvSave.setTooltip(new Tooltip("Select adv save variables"));
    }

    @FXML public void handleButtonSelectProjectDir() {
        DirectoryChooser projectChooser = new DirectoryChooser();
        projectChooser.setTitle("Select Project Directory");
        projectDir = projectChooser.showDialog(application.getPrimaryStage());
        if (projectDir != null) { txtProjectDir.setText(projectDir.getAbsolutePath()); }
    }

    @FXML public void handleButtonSelectAdvSave() {
        FileChooser varFileChooser = new FileChooser();
        varFileChooser.setTitle("Select MCC exported variable file");
        varFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Variables data", "*.txt")
        );
        selectVarFile = varFileChooser.showOpenDialog(application.getPrimaryStage());
        if (selectVarFile != null) { txtAdvSave.setText(selectVarFile.getAbsolutePath()); }
    }

    @FXML public void handleButtonOk() {
        if (projectDir != null && selectVarFile != null) {
            root.setImportProjectDir(projectDir);
            root.setImportVarFile(selectVarFile);
            application.getImportDialogStage().close();
        }
        else {
            Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
            emptyFieldAlert.initModality(Modality.APPLICATION_MODAL);
            emptyFieldAlert.initOwner(application.getPrimaryStage());
            emptyFieldAlert.setTitle("Empty field error");
            emptyFieldAlert.setHeaderText("One or more required field is empty.");
            emptyFieldAlert.setContentText("Please select both project directory and adv save variables.");
            emptyFieldAlert.showAndWait();
        }
    }

    @FXML public void handleButtonCancel() { application.getImportDialogStage().close(); }

}
