package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.AdvSaveVariable;
import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import com.idemia.tec.jkt.cardiotest.model.TestCase;
import com.idemia.tec.jkt.cardiotest.response.TestSuiteResponse;
import com.idemia.tec.jkt.cardiotest.service.CardioConfigService;
import com.idemia.tec.jkt.cardiotest.service.RunService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
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
    private TerminalFactory terminalFactory;
    private TestSuiteResponse tsResponse;

    @Autowired
    private CardiotestController cardiotest;
    @Autowired
    private CardioConfigService cardioConfigService;
    @Autowired
    private RunService runService;

    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private MenuBar menuBar;

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
        lblTerminalInfo = new Label();
        appStatusBar.getRightItems().add(new Separator(Orientation.VERTICAL));
        appStatusBar.getRightItems().add(lblTerminalInfo);
        try {
            // list available readers
            List<CardTerminal> terminals = terminalFactory.terminals().list();
            if (terminals.isEmpty())
                lblTerminalInfo.setText("(no terminal/reader detected)");
            else
                if (runSettings.getReaderNumber() != -1)
                    lblTerminalInfo.setText(terminals.get(runSettings.getReaderNumber()).getName());
        } catch (CardException e) {
            logger.error("Failed to list PCSC terminals");
            lblTerminalInfo.setText("(no terminal/reader detected)");
            lblTerminalInfo.setTextFill(Color.RED);
//            e.printStackTrace();
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
        File selectedVarFile = variableFileChooser.showOpenDialog(application.getPrimaryStage());
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

    @FXML
    private void handleMenuToolOptions() {
        application.showToolOptions();
    }

    @FXML
    private void handleMenuRunAll() {
        handleMenuSaveSettings();

        // make user wait as verification executes
        cardiotest.getMaskerPane().setText("Executing RunAll. Please wait..");
        // display masker pane
        cardiotest.getMaskerPane().setVisible(true);
        menuBar.setDisable(true);
        appStatusBar.setDisable(true);

        cardiotest.getTxtInterpretedLog().getChildren().clear();
        appendTextFlow("Executing RunAll..\n\n");

        // use threads to avoid application freeze
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                tsResponse = runService.runAll();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // dismiss masker pane
                cardiotest.getMaskerPane().setVisible(false);
                menuBar.setDisable(false);
                appStatusBar.setDisable(false);
                // update status bar
                if (tsResponse.isSuccess()) {
                    appStatusBar.setText(tsResponse.getMessage());
                    // show notification
                    Notifications.create().title("CardIO").text(tsResponse.getMessage()).showInformation();

                    // display log
                    int testModules = Integer.parseInt(tsResponse.getTestSuite().getTests());
                    List<TestCase> testCases = tsResponse.getTestSuite().getTestCases();
                    int runErrors = Integer.parseInt(tsResponse.getTestSuite().getErrors());
                    int runFailures = 0;
                    if (tsResponse.getTestSuite().getFailures() != null)
                        runFailures = Integer.parseInt(tsResponse.getTestSuite().getFailures());
                    if (runErrors != 0) {
                        appendTextFlow(">> NOT OK\n", 1);
                        appendTextFlow("System error: " + tsResponse.getTestSuite().getSystemErr() + "\n\n");
                    }
                    if (runFailures != 0) {
                        appendTextFlow(">> NOT OK\n", 1);
                        appendTextFlow("Failures: " + runFailures + "\n\n");
                    }
                    if (runErrors == 0 & runFailures == 0)
                        appendTextFlow(">> OK\n\n", 0);
                    appendTextFlow("Test modules: " + testModules + "\n\n");
                    if (testCases != null) {
                        for (TestCase module : testCases) {
                            appendTextFlow("Name: " + module.getName() + "\n");
                            appendTextFlow("Execution time: " + Float.parseFloat(module.getTime()) + " s\n");
                            if (module.getError() != null)
                                appendTextFlow("Error: \n" + module.getError() + "\n", 1);
                            if (module.getFailure() != null)
                                appendTextFlow("Failure: \n" + module.getFailure() + "\n", 1);
                            appendTextFlow("\n");
                        }
                    }
                }
                else {
                    appStatusBar.setText(tsResponse.getMessage());
                    Notifications.create().title("CardIO").text(tsResponse.getMessage()).showError();
                    logger.error(tsResponse.getMessage());
                    appendTextFlow(">> ABORTED");
                    Alert pcomAlert = new Alert(Alert.AlertType.ERROR);
                    pcomAlert.initModality(Modality.APPLICATION_MODAL);
                    pcomAlert.initOwner(application.getPrimaryStage());
                    pcomAlert.setTitle("Execution error");
                    pcomAlert.setHeaderText("Failed to execute");
                    pcomAlert.setContentText(tsResponse.getMessage());
                    pcomAlert.showAndWait();
                }
            }
        };

        Thread runAllThread = new Thread(task);
        runAllThread.start(); // run in background

    }

    private void appendTextFlow(String text) {
        Text message = new Text(text);
        cardiotest.getTxtInterpretedLog().getChildren().add(message);
    }

    private void appendTextFlow(String text, int style) {
        Text message = new Text(text);
        if (style == 0)
            message.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
        if (style == 1)
            message.setStyle("-fx-fill: RED;-fx-font-weight:normal;");
        cardiotest.getTxtInterpretedLog().getChildren().add(message);
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
