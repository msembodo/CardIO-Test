package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.AdvSaveVariable;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class CardiotestController {

    @FXML
    private TableView<AdvSaveVariable> tblAdvSave;
    @FXML
    private TableColumn<AdvSaveVariable, String> clmnDefined;
    @FXML
    private TableColumn<AdvSaveVariable, String> clmnValue;
    @FXML
    private TableView<VariableMapping> tblMapping;
    @FXML
    private TableColumn<VariableMapping, String> clmnMappedTo;
    @FXML
    private TableColumn<VariableMapping, String> clmnMccVar;
    @FXML
    private TextField txtMappedTo;
    @FXML
    private ComboBox<String> cmbMccVar;
    @FXML
    private CheckBox chkFixedVal;
    @FXML
    private TextField txtMccVar;

    // project details tab
    @FXML
    private TextField txtProjectFolder;
    @FXML
    private TextField txtRequestId;
    @FXML
    private TextField txtRequestName;
    @FXML
    private TextField txtProfileName;
    @FXML
    private TextField txtProfileVersion;
    @FXML
    private TextField txtCardImageItemId;
    @FXML
    private TextField txtCustomer;
    @FXML
    private TextField txtDeveloperName;
    @FXML
    private TextField txtTesterName;

    static Logger logger = Logger.getLogger(CardiotestController.class);

    private CardiotestApplication application;

    @Autowired
    private RootLayoutController root;

    public CardiotestController() {}

    public void setMainApp(CardiotestApplication application) {
        this.application = application;
    }

    public void setObservableList() {
        // add observable list data to table
        tblAdvSave.setItems(application.getAdvSaveVariables());
        tblMapping.setItems(application.getMappings());

        // load variables from saved settings
        File loadedVarFile = new File(root.getRunSettings().getAdvSaveVariablesPath());
        if (loadedVarFile != null) {
            try {
                Scanner scanner = new Scanner(loadedVarFile);
                List<String> definedVariables = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith(".DEFINE"))
                        definedVariables.add(line);
                }
                root.getRunSettings().setAdvSaveVariablesPath(loadedVarFile.getAbsolutePath());
                logger.info(String.format("Variable file selected: %s", loadedVarFile.getAbsolutePath()));
                root.getAppStatusBar().setText("Variables loaded.");
                for (String line : definedVariables) {
                    String[] components = line.split("\\s+");
                    application.getAdvSaveVariables().add(
                            new AdvSaveVariable(components[1].substring(1), components[2])
                    );
                    cmbMccVar.getItems().add(components[1].substring(1));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // load mappings from saved settings
        for (VariableMapping mapping : root.getRunSettings().getVariableMappings())
            application.getMappings().add(mapping);

    }

    @FXML
    private void initialize() {
        // initialize variable table
        clmnDefined.setCellValueFactory(celldata -> celldata.getValue().definedVariable());
        clmnValue.setCellValueFactory(celldata -> celldata.getValue().value());

        // initialize mapping table
        clmnMappedTo.setCellValueFactory(celldata -> celldata.getValue().mappedVariable());
        clmnMccVar.setCellValueFactory(celldata -> celldata.getValue().mccVariable());

        // clear mapping display
        showMappings(null);

        // listen for selection changes and show mapping detail when changed
        tblMapping.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> showMappings(newValue))
        );
        chkFixedVal.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (chkFixedVal.isSelected()) {
                cmbMccVar.setDisable(true);
                txtMccVar.setDisable(false);
            } else {
                cmbMccVar.setDisable(false);
                txtMccVar.setDisable(true);
            }
        });

        // restore values of controls

        // project details
        txtProjectFolder.setText(root.getRunSettings().getProjectPath());
        txtRequestId.setText(root.getRunSettings().getRequestId());
        txtRequestName.setText(root.getRunSettings().getRequestName());
        txtProfileName.setText(root.getRunSettings().getProfileName());
        txtProfileVersion.setText(Integer.toString(root.getRunSettings().getProfileVersion()));
        txtCardImageItemId.setText(root.getRunSettings().getCardImageItemId());
        txtCustomer.setText(root.getRunSettings().getCustomer());
        txtDeveloperName.setText(root.getRunSettings().getDeveloperName());
        txtTesterName.setText(root.getRunSettings().getTesterName());

    }

    private void showMappings(VariableMapping mapping) {
        if (mapping != null) {
            // fill mappings with info from mapping object
            txtMappedTo.setText(mapping.getMappedVariable());
            if (mapping.isFixed()) {
                chkFixedVal.setSelected(true);
                txtMccVar.setText(mapping.getValue());
            } else {
                chkFixedVal.setSelected(false);
                cmbMccVar.setValue(mapping.getMccVariable());
            }
        } else {
            txtMappedTo.setText("");
            txtMccVar.setText("");
            cmbMccVar.setValue(null);
        }
    }

    public ComboBox<String> getCmbMccVar() {
        return cmbMccVar;
    }

    @FXML
    private void handleBtnUpdateMapping() {
        if (mappedVariableExist(txtMappedTo.getText())) {
            VariableMapping selectedMapping = tblMapping.getSelectionModel().getSelectedItem();
            if (chkFixedVal.isSelected()) {
                selectedMapping.setFixed(true);
                selectedMapping.setMccVariable(null);
                selectedMapping.setValue(txtMccVar.getText());
                logger.info(String.format("Update mapping: %s -> %s", selectedMapping.getMappedVariable(),
                        selectedMapping.getValue()));
            } else {
                selectedMapping.setFixed(false);
                selectedMapping.setMccVariable(cmbMccVar.getValue());
                selectedMapping.setValue(null);
                logger.info(String.format("Update mapping: %s -> %s", selectedMapping.getMappedVariable(),
                        selectedMapping.getMccVariable()));
            }
        } else {
            // add new mapping
            String mappedVariable = txtMappedTo.getText();
            boolean fixed = false;
            String mccVariable = cmbMccVar.getValue();
            String value = null;
            if (chkFixedVal.isSelected()) {
                fixed = true;
                mccVariable = null;
                value = txtMccVar.getText();
            }
            VariableMapping mapping = new VariableMapping(mappedVariable, mccVariable, value, fixed);
            application.getMappings().add(mapping);
            if (mapping.isFixed())
                logger.info(String.format("Add mapping: %s -> %s", mapping.getMappedVariable(),
                        mapping.getValue()));
            else
                logger.info(String.format("Add mapping: %s -> %s", mapping.getMappedVariable(),
                        mapping.getMccVariable()));
        }
    }

    @FXML
    private void handleBtnDeleteMapping() {
        if (application.getMappings().size() > 0) {
            logger.info(String.format("Delete mapping: %s",
                    tblMapping.getSelectionModel().getSelectedItem().getMappedVariable()));
            int selectedIndex = tblMapping.getSelectionModel().getSelectedIndex();
            tblMapping.getItems().remove(selectedIndex);
            showMappings(null);
            tblMapping.getSelectionModel().clearSelection();

        }
    }

    private boolean mappedVariableExist(String testMappedVariable) {
        for (VariableMapping mapping : application.getMappings()) {
            if (mapping.getMappedVariable().equals(testMappedVariable))
                return true;
        }
        return false;
    }

    public void saveControlState() {
        root.getRunSettings().setProjectPath(txtProjectFolder.getText());
        root.getRunSettings().setReaderNumber(0); // TODO: will be selected by user
        root.getRunSettings().setStopOnError(false); // TODO: will be selected by user as option
        root.getRunSettings().setRequestId(txtRequestId.getText());
        root.getRunSettings().setRequestName(txtRequestName.getText());
        root.getRunSettings().setProfileName(txtProfileName.getText());
        root.getRunSettings().setProfileVersion(Integer.parseInt(txtProfileVersion.getText()));
        root.getRunSettings().setCardImageItemId(txtCardImageItemId.getText());
        root.getRunSettings().setCustomer(txtCustomer.getText());
        root.getRunSettings().setDeveloperName(txtDeveloperName.getText());
        root.getRunSettings().setTesterName(txtTesterName.getText());
    }

}
