package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.AdvSaveVariable;
import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import com.idemia.tec.jkt.cardiotest.service.CardioConfigService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.controlsfx.control.StatusBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class RootLayoutController {

    static Logger logger = Logger.getLogger(RootLayoutController.class);

    private RunSettings runSettings;

    private CardiotestApplication application;
    private File selectedVarFile;
    private TerminalFactory terminalFactory;

    @Autowired
    private CardiotestController cardiotest;

    @Autowired
    private CardioConfigService cardioConfigService;

    @FXML
    private BorderPane rootBorderPane;

    private StatusBar appStatusBar;
    private Label lblTerminalInfo;

    public RootLayoutController() {}

    public void setMainApp(CardiotestApplication application) {
        this.application = application;
    }

    public RunSettings getRunSettings() {
        return runSettings;
    }

    @FXML
    private void initialize() {
        appStatusBar = new StatusBar();
        rootBorderPane.setBottom(appStatusBar);

        // get run settings from 'run-settings.json' or by default values
        runSettings = cardioConfigService.initConfig();

        terminalFactory = TerminalFactory.getDefault();
        try {
            // list available readers
            List<CardTerminal> terminals = terminalFactory.terminals().list();
            lblTerminalInfo = new Label();
            appStatusBar.getRightItems().add(new Separator(Orientation.VERTICAL));
            appStatusBar.getRightItems().add(lblTerminalInfo);
            if (terminals.isEmpty())
                lblTerminalInfo.setText("(no terminal/reader detected)");
            else
                if (runSettings.getReaderNumber() != -1)
                    lblTerminalInfo.setText(terminals.get(runSettings.getReaderNumber()).getName());
        } catch (CardException e) {
            e.printStackTrace();
        }
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
        variableFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Variables data", "*.txt")
        );
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
                runSettings.setAdvSaveVariablesPath(selectedVarFile.getAbsolutePath());
                logger.info(String.format("Variable file selected: %s", selectedVarFile.getAbsolutePath()));
                appStatusBar.setText("Variables loaded.");
                for (String line : definedVariables) {
                    String[] components = line.split("\\s+");
                    application.getAdvSaveVariables().add(
                            new AdvSaveVariable(components[1].substring(1), components[2])
                    );
                    cardiotest.getCmbMccVar().getItems().add(components[1].substring(1));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleMenuSaveSettings() {
        cardiotest.saveControlState();
        runSettings.setVariableMappings(application.getMappings());
        cardioConfigService.saveConfig(runSettings);
    }

    @FXML
    private void handleMenuSelectReader() {
        application.showSelectReader();
    }

    public StatusBar getAppStatusBar() {
        return appStatusBar;
    }

    public TerminalFactory getTerminalFactory() {
        return terminalFactory;
    }

    public Label getLblTerminalInfo() {
        return lblTerminalInfo;
    }
}
