package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.AdvSaveVariable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.controlsfx.control.StatusBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class RootLayoutController {

    static Logger logger = Logger.getLogger(RootLayoutController.class);

    private CardiotestApplication application;
    private File selectedVarFile;

    @Autowired
    private CardiotestController cardiotest;

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private Menu menuRun;

    private StatusBar appStatusBar;

    public RootLayoutController() {}

    public void setMainApp(CardiotestApplication application) {
        this.application = application;
    }

    @FXML
    private void initialize() {
        appStatusBar = new StatusBar();
        rootBorderPane.setBottom(appStatusBar);
    }

    @FXML
    private void handleMenuQuit() {
        // quit application
        Platform.exit();
    }

    @FXML
    private void handleMenuLoadVariables() {
        // user select variable file
        FileChooser variableFileChooser = new FileChooser();
        variableFileChooser.setTitle("Select MCC exported variable file");
        variableFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Variables data", "*.txt"));
        selectedVarFile = variableFileChooser.showOpenDialog(application.getPrimaryStage());
        if (selectedVarFile != null) {
            try {
                Scanner scanner = new Scanner(selectedVarFile);
                List<String> definedVariables = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith(".DEFINE"))
                        definedVariables.add(line);
                }
                logger.info(String.format("Variable file selected: %s", selectedVarFile.getAbsolutePath()));
                appStatusBar.setText("Variables loaded.");
                for (String line : definedVariables) {
                    String[] components = line.split("\\s+");
                    application.getAdvSaveVariables().add(new AdvSaveVariable(components[1].substring(1), components[2]));
                    cardiotest.getCmbMccVar().getItems().add(components[1].substring(1));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleMenuNewSession() {
        Tab tabAtr = new Tab();
        tabAtr.setText("ATR");
        cardiotest.getModulesPane().getTabs().add(tabAtr);

        menuRun.setDisable(false);
    }

}
