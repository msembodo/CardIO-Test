package com.idemia.tec.jkt.cardiotest.controller;

import com.google.gson.Gson;
import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.AdvSaveVariable;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.hildan.fxgson.FxGson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    static Logger logger = Logger.getLogger(CardiotestController.class);

    private CardiotestApplication application;

    @FXML
    private TabPane modulesPane;

    @Autowired
    private RootLayoutController root;

    public CardiotestController() {}

    public void setMainApp(CardiotestApplication application) {
        this.application = application;

        // add observable list data to table
        tblAdvSave.setItems(application.getAdvSaveVariables());
        tblMapping.setItems(application.getMappings());

        // initialise MCC variable combo box
        // in real case this list will be populated from MCC advance save
//    	for (VariableMapping mapping : application.getMappings()) {
//    		if (!mapping.isFixed())
//    			cmbMccVar.getItems().add(mapping.getMccVariable());
//    	}
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
        chkFixedVal.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (chkFixedVal.isSelected()) {
                    cmbMccVar.setDisable(true);
                    txtMccVar.setDisable(false);
                } else {
                    cmbMccVar.setDisable(false);
                    txtMccVar.setDisable(true);
                }
            }
        });
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

    public TabPane getModulesPane() {
        return modulesPane;
    }

    @FXML
    private void handleBtnUpdateMapping() {
        if (mappedVariableExist(txtMappedTo.getText())) {
            VariableMapping selectedMapping = tblMapping.getSelectionModel().getSelectedItem();
            if (chkFixedVal.isSelected()) {
                selectedMapping.setFixed(true);
                selectedMapping.setMccVariable(null);
                selectedMapping.setValue(txtMccVar.getText());
                logger.info(String.format("Update mapping: %s -> %s", selectedMapping.getMappedVariable(), selectedMapping.getValue()));
            } else {
                selectedMapping.setFixed(false);
                selectedMapping.setMccVariable(cmbMccVar.getValue());
                selectedMapping.setValue(null);
                logger.info(String.format("Update mapping: %s -> %s", selectedMapping.getMappedVariable(), selectedMapping.getMccVariable()));
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
                logger.info(String.format("Add mapping: %s -> %s", mapping.getMappedVariable(), mapping.getValue()));
            else
                logger.info(String.format("Add mapping: %s -> %s", mapping.getMappedVariable(), mapping.getMccVariable()));
        }
    }

    @FXML
    private void handleBtnDeleteMapping() {
        if (application.getMappings().size() > 0) {
            logger.info(String.format("Delete mapping: %s", tblMapping.getSelectionModel().getSelectedItem().getMappedVariable()));
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

    private void listMapping() {
        Gson gson = FxGson.coreBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        System.out.println(gson.toJson(application.getMappings()));
    }

}
