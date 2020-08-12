package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.CustomScript;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomTabController {

    static Logger logger = Logger.getLogger(CustomTabController.class);

    @Autowired private RootLayoutController root;

    // section 1 controls
    @FXML private TableView<CustomScript> tblCustomScript1;
    @FXML private TableColumn<CustomScript, String> clmnCs1Name;
    @FXML private TextField txtCs1Description;
    @FXML private TextField txtCs1ScriptName;
    @FXML private Button btnCs1Choose;
    @FXML private Button btnCs1Edit;
    @FXML private Label lblAddCs1ScriptErrMsg;

    // section 2 controls
    @FXML private TableView<CustomScript> tblCustomScript2;
    @FXML private TableColumn<CustomScript, String> clmnCs2Name;
    @FXML private TextField txtCs2Description;
    @FXML private TextField txtCs2ScriptName;
    @FXML private Button btnCs2Choose;
    @FXML private Button btnCs2Edit;
    @FXML private Label lblAddCs2ScriptErrMsg;

    // section 3 controls
    @FXML private TableView<CustomScript> tblCustomScript3;
    @FXML private TableColumn<CustomScript, String> clmnCs3Name;
    @FXML private TextField txtCs3Description;
    @FXML private TextField txtCs3ScriptName;
    @FXML private Button btnCs3Choose;
    @FXML private Button btnCs3Edit;
    @FXML private Label lblAddCs3ScriptErrMsg;

    private String scriptsDirectory;

    public CustomTabController() {}

    @FXML private void initialize() {
        // init section 1
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

        // init section 2
        clmnCs2Name.setCellValueFactory(celldata -> celldata.getValue().customScriptNameProperty());
        showCustomScriptSection2(null);
        tblCustomScript2.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCustomScriptSection2(newValue)
        );
        tblCustomScript2.setItems(root.getCustomScriptsSection2());
        for (CustomScript customScript : root.getRunSettings().getCustomScriptsSection2())
            root.getCustomScriptsSection2().add(customScript);
        tblCustomScript2.getSelectionModel().select(0); // select first row initially
        btnCs2Choose.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT));
        btnCs2Choose.setTooltip(new Tooltip("Choose custom script"));
        btnCs2Edit.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        btnCs2Edit.setTooltip(new Tooltip("Edit custom script"));

        // init section 3
        clmnCs3Name.setCellValueFactory(celldata -> celldata.getValue().customScriptNameProperty());
        showCustomScriptSection3(null);
        tblCustomScript3.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCustomScriptSection3(newValue)
        );
        tblCustomScript3.setItems(root.getCustomScriptsSection3());
        for (CustomScript customScript : root.getRunSettings().getCustomScriptsSection3())
            root.getCustomScriptsSection3().add(customScript);
        tblCustomScript3.getSelectionModel().select(0); // select first row initially
        btnCs3Choose.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT));
        btnCs3Choose.setTooltip(new Tooltip("Choose custom script"));
        btnCs3Edit.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        btnCs3Edit.setTooltip(new Tooltip("Edit custom script"));

        scriptsDirectory = root.getRunSettings().getProjectPath() + "\\scripts\\";
    }

    // section 1 handlers

    private void showCustomScriptSection1(CustomScript customScript) {
        if (customScript != null) {
            txtCs1Description.setText(customScript.getDescription());
            txtCs1ScriptName.setText(customScript.getCustomScriptName());
        } else {
            txtCs1Description.setText("");
            txtCs1ScriptName.setText("");
        }
    }

    @FXML private void handleBtnCs1Choose() {
        // choose from available script and copy to target script folder
        FileChooser cs1FileChooser = new FileChooser();
        cs1FileChooser.setTitle("Select custom script");
        cs1FileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PCOM script", "*.pcom", "*.txt")
        );
        File cs1ChooseFile = cs1FileChooser.showOpenDialog(btnCs1Choose.getScene().getWindow());
        if (cs1ChooseFile != null) {
            File targetFile = new File(scriptsDirectory + cs1ChooseFile.getName());
            try {
                Files.copy(cs1ChooseFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                txtCs1ScriptName.setText(cs1ChooseFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void handleBtnCs1Edit() {
        // edit the specified script or, if not exists, create one
        String cs1EditFilePath = scriptsDirectory + txtCs1ScriptName.getText();
        File cs1EditFile = new File(cs1EditFilePath);
        if (!cs1EditFile.exists()) {
            try {
                Files.createFile(cs1EditFile.toPath());
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(cs1EditFilePath))) {
                    bw.append(new StringBuilder().append(".CALL Mapping.txt /LIST_OFF\n.CALL Options.txt /LIST_OFF\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                editScript(cs1EditFilePath);
                return null;
            }
        };
        Thread editThread = new Thread(task);
        editThread.start();
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

    private boolean section1ScriptExists(String checkScriptName) {
        for (CustomScript customScript : root.getCustomScriptsSection1()) {
            if (customScript.getCustomScriptName().equals(checkScriptName))
                return true;
        }
        return false;
    }

    @FXML private void handleBtnCs1Delete() {
        if (root.getCustomScriptsSection1().size() > 0) {
            // delete from scripts directory
            String cs1DeleteFilePath = scriptsDirectory + txtCs1ScriptName.getText();
            File cs1DeleteFile = new File(cs1DeleteFilePath);
            try {
                Files.deleteIfExists(cs1DeleteFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // remove from table
            lblAddCs1ScriptErrMsg.setVisible(false);
            logger.info("Delete custom script in section 1: " + tblCustomScript1.getSelectionModel().getSelectedItem().getCustomScriptName());
            int selectedIndex = tblCustomScript1.getSelectionModel().getSelectedIndex();
            tblCustomScript1.getItems().remove(selectedIndex);
            showCustomScriptSection1(null);
            tblCustomScript1.getSelectionModel().clearSelection();
        }
    }

    // section 2 handlers

    private void showCustomScriptSection2(CustomScript customScript) {
        if (customScript != null) {
            txtCs2Description.setText(customScript.getDescription());
            txtCs2ScriptName.setText(customScript.getCustomScriptName());
        } else {
            txtCs2Description.setText("");
            txtCs2ScriptName.setText("");
        }
    }

    @FXML private void handleBtnCs2Choose() {
        // choose from available script and copy to target script folder
        FileChooser cs2FileChooser = new FileChooser();
        cs2FileChooser.setTitle("Select custom script");
        cs2FileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PCOM script", "*.pcom", "*.txt")
        );
        File cs2ChooseFile = cs2FileChooser.showOpenDialog(btnCs2Choose.getScene().getWindow());
        if (cs2ChooseFile != null) {
            File targetFile = new File(scriptsDirectory + cs2ChooseFile.getName());
            try {
                Files.copy(cs2ChooseFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                txtCs2ScriptName.setText(cs2ChooseFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void handleBtnCs2Edit() {
        // edit the specified script or, if not exists, create one
        String cs2EditFilePath = scriptsDirectory + txtCs2ScriptName.getText();
        File cs2EditFile = new File(cs2EditFilePath);
        if (!cs2EditFile.exists()) {
            try {
                Files.createFile(cs2EditFile.toPath());
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(cs2EditFilePath))) {
                    bw.append(new StringBuilder().append(".CALL Mapping.txt /LIST_OFF\n.CALL Options.txt /LIST_OFF\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                editScript(cs2EditFilePath);
                return null;
            }
        };
        Thread editThread = new Thread(task);
        editThread.start();
    }

    @FXML private void handleBtnCs2Add() {
        if (section2ScriptExists(txtCs2ScriptName.getText())) {
            lblAddCs2ScriptErrMsg.setVisible(true);
            lblAddCs2ScriptErrMsg.setText("Custom script with same name already exists.");
        } else {
            String scriptName = txtCs2ScriptName.getText();
            String description = txtCs2Description.getText();
            CustomScript customScript = new CustomScript(scriptName, description);
            root.getCustomScriptsSection2().add(customScript);
            lblAddCs2ScriptErrMsg.setVisible(false);
            logger.info("Added custom script in section 2: " + customScript.toJson());
        }
    }

    private boolean section2ScriptExists(String checkScriptName) {
        for (CustomScript customScript : root.getCustomScriptsSection2()) {
            if (customScript.getCustomScriptName().equals(checkScriptName))
                return true;
        }
        return false;
    }

    @FXML private void handleBtnCs2Delete() {
        if (root.getCustomScriptsSection2().size() > 0) {
            // delete from scripts directory
            String cs2DeleteFilePath = scriptsDirectory + txtCs2ScriptName.getText();
            File cs2DeleteFile = new File(cs2DeleteFilePath);
            try {
                Files.deleteIfExists(cs2DeleteFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // remove from table
            lblAddCs2ScriptErrMsg.setVisible(false);
            logger.info("Delete custom script in section 2: " + tblCustomScript2.getSelectionModel().getSelectedItem().getCustomScriptName());
            int selectedIndex = tblCustomScript2.getSelectionModel().getSelectedIndex();
            tblCustomScript2.getItems().remove(selectedIndex);
            showCustomScriptSection2(null);
            tblCustomScript2.getSelectionModel().clearSelection();
        }
    }

    // section 3 handlers

    private void showCustomScriptSection3(CustomScript customScript) {
        if (customScript != null) {
            txtCs3Description.setText(customScript.getDescription());
            txtCs3ScriptName.setText(customScript.getCustomScriptName());
        } else {
            txtCs3Description.setText("");
            txtCs3ScriptName.setText("");
        }
    }

    @FXML private void handleBtnCs3Choose() {
        // choose from available script and copy to target script folder
        FileChooser cs3FileChooser = new FileChooser();
        cs3FileChooser.setTitle("Select custom script");
        cs3FileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PCOM script", "*.pcom", "*.txt")
        );
        File cs3ChooseFile = cs3FileChooser.showOpenDialog(btnCs3Choose.getScene().getWindow());
        if (cs3ChooseFile != null) {
            File targetFile = new File(scriptsDirectory + cs3ChooseFile.getName());
            try {
                Files.copy(cs3ChooseFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                txtCs3ScriptName.setText(cs3ChooseFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void handleBtnCs3Edit() {
        // edit the specified script or, if not exists, create one
        String cs3EditFilePath = scriptsDirectory + txtCs3ScriptName.getText();
        File cs3EditFile = new File(cs3EditFilePath);
        if (!cs3EditFile.exists()) {
            try {
                Files.createFile(cs3EditFile.toPath());
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(cs3EditFilePath))) {
                    bw.append(new StringBuilder().append(".CALL Mapping.txt /LIST_OFF\n.CALL Options.txt /LIST_OFF\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                editScript(cs3EditFilePath);
                return null;
            }
        };
        Thread editThread = new Thread(task);
        editThread.start();
    }

    @FXML private void handleBtnCs3Add() {
        if (section3ScriptExists(txtCs3ScriptName.getText())) {
            lblAddCs3ScriptErrMsg.setVisible(true);
            lblAddCs3ScriptErrMsg.setText("Custom script with same name already exists.");
        } else {
            String scriptName = txtCs3ScriptName.getText();
            String description = txtCs3Description.getText();
            CustomScript customScript = new CustomScript(scriptName, description);
            root.getCustomScriptsSection3().add(customScript);
            lblAddCs3ScriptErrMsg.setVisible(false);
            logger.info("Added custom script in section 3: " + customScript.toJson());
        }
    }

    private boolean section3ScriptExists(String checkScriptName) {
        for (CustomScript customScript : root.getCustomScriptsSection3()) {
            if (customScript.getCustomScriptName().equals(checkScriptName))
                return true;
        }
        return false;
    }

    @FXML private void handleBtnCs3Delete() {
        if (root.getCustomScriptsSection3().size() > 0) {
            // delete from scripts directory
            String cs3DeleteFilePath = scriptsDirectory + txtCs3ScriptName.getText();
            File cs3DeleteFile = new File(cs3DeleteFilePath);
            try {
                Files.deleteIfExists(cs3DeleteFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // remove from table
            lblAddCs3ScriptErrMsg.setVisible(false);
            logger.info("Delete custom script in section 3: " + tblCustomScript3.getSelectionModel().getSelectedItem().getCustomScriptName());
            int selectedIndex = tblCustomScript3.getSelectionModel().getSelectedIndex();
            tblCustomScript3.getItems().remove(selectedIndex);
            showCustomScriptSection3(null);
            tblCustomScript3.getSelectionModel().clearSelection();
        }
    }

    private void editScript(String scriptName) {
        List<String> cmdArray = new ArrayList<>();
        cmdArray.add("pcom32");
        cmdArray.add(scriptName);
        ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
