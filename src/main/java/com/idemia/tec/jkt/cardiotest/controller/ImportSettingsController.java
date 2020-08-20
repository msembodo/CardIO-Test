package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ImportSettingsController {

    static Logger logger = Logger.getLogger(ImportSettingsController.class);

    private CardiotestApplication application;

    @FXML private TextField txtProjectDir;
    @FXML private TextField txtAdvSave;
    @FXML private Button btnProjectDir;
    @FXML private Button btnAdvSave;

    public ImportSettingsController() {}

    public void setMainApp(CardiotestApplication application) { this.application = application; }

    @FXML private void initialize() {
        btnProjectDir.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN_ALT));
        btnProjectDir.setTooltip(new Tooltip("Select project directory"));
        btnAdvSave.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT));
        btnAdvSave.setTooltip(new Tooltip("Select adv save variables"));
        // TODO
    }

    @FXML public void handleButtonSelectProjectDir() {
        // TODO
    }

    @FXML public void handleButtonSelectAdvSave() {
        // TODO
    }

    @FXML public void handleButtonCancel() { application.getImportDialogStage().close(); }

    @FXML public void handleButtonOk() {
        // TODO
    }

}
