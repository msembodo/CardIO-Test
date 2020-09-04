package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.FMRuwi;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import javafx.scene.control.CheckBox;
import com.idemia.tec.jkt.cardiotest.model.FMLinkFiles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.scene.control.TextField;

import java.util.ArrayList;

@Component
public class FileManagementController  {

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    @FXML private CheckBox chkIncludeLinkFileTest;
    @FXML private TableView<FMLinkFiles> tblLinkFileTest;
    @FXML private TableColumn<FMLinkFiles, String> clmMaster;
    @FXML private TableColumn<FMLinkFiles, String> clmGhost;
    @FXML private Button addLinkFileButton;
    @FXML private Button deleteLinkFileButton;
    @FXML private Button ModifyLinkFileButton;
    @FXML private Button SaveLinkFileButton;
    @FXML private TextField path_MasterTextField;
    @FXML private TextField path_GhostTextField;

    @FXML private CheckBox chkIncludeRuwiTest;
    @FXML private TableView<FMRuwi> tblRuwi;
    @FXML private TableColumn<FMRuwi, String> clmRuwi;
    @FXML private Button ModifyRuwiButton;
    @FXML private Button SaveRuwiButton;
    @FXML private Button addRuwiButton;
    @FXML private Button deleteRuwiButton;
    @FXML private TextField path_RuwiTextField;

    @FXML private CheckBox chkIncludeSfiTest;
    @FXML private CheckBox SFI_Iccid_2FE2_02_Checkbox , SFI_ARR_2F06_06_Checkbox ;
    @FXML private CheckBox SFI_PL_2F05_05_Checkbox , SFI_Dir_2F00_1E_Checkbox;
    @FXML private CheckBox SFI_ECC_6F7B_01_Checkbox , SFI_StartHFN_6F5B_0F_Checkbox;
    @FXML private CheckBox SFI_LI_6F05_02_Checkbox , SFI_TRESHOLD_6F5C_10_Checkbox;
    @FXML private CheckBox SFI_AD_6FAD_03_Checkbox , SFI_OPLMNwACT_6F61_11_Checkbox;
    @FXML private CheckBox SFI_UST_6F38_04_Checkbox , SFI_HPPLMN_6F31_12_Checkbox;
    @FXML private CheckBox SFI_EST_6F56_05_Checkbox , SFI_HPLMNwACT_6F62_13_Checkbox;
    @FXML private CheckBox SFI_ACC_6F78_06_Checkbox , SFI_ICI_6F80_14_Checkbox;
    @FXML private CheckBox SFI_IMSI_6F07_07_Checkbox , SFI_OCI_6F81_15_Checkbox;
    @FXML private CheckBox SFI_KEYS_6F08_08_Checkbox , SFI_CCP2_6F4F_16_Checkbox;
    @FXML private CheckBox SFI_KEYSPS_6F09_09_Checkbox , SFI_ARR_6F06_17_Checkbox;
    @FXML private CheckBox SFI_PLMNwACT_6F60_0A_Checkbox , SFI_ePDGIdEm_6F65_18_Checkbox;
    @FXML private CheckBox SFI_LOCI_6F7E_0B_Checkbox , SFI_PNN_6FC5_19_Checkbox;
    @FXML private CheckBox SFI_PSLOCI_6F73_0C_Checkbox , SFI_OPL_6FC6_1A_Checkbox;
    @FXML private CheckBox SFI_FPLMN_6F7B_0D_Checkbox , SFI_SPDI_6FCD_1B_Checkbox;
    @FXML private CheckBox SFI_CBMID_6F48_0E_Checkbox , SFI_ACM_6F39_1C_Checkbox;
    @FXML private CheckBox SFI_Kc_4F20_01_Checkbox , SFI_KcGPRS_4F52_02_Checkbox;

    private ObservableList<FMLinkFiles> linkfiles = FXCollections.observableArrayList();
    private ObservableList<FMRuwi> ruwiandSfi = FXCollections.observableArrayList();

    public FileManagementController() {}

    public void initialize() {

        //[LINK FILE TEST] This will allowed to select multiple table
        //tblLinkFileTest.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //[LINK FILE TEST] This will allowed to select cell table
        //tblLinkFileTest.getSelectionModel().setCellSelectionEnabled(true);

        // ============================================================

        chkIncludeLinkFileTest.setSelected(root.getRunSettings().getFileManagement().isIncludeLinkFilesTest());
        handleIncludeLinkFileCheck();


        //[LINK FILE TEST] setup for columns
        clmMaster.setCellValueFactory(new PropertyValueFactory<FMLinkFiles, String>("path_Master"));
        clmGhost.setCellValueFactory(new PropertyValueFactory<FMLinkFiles, String> ("path_Ghost"));

        //[LINK FILE TEST] load the data
        tblLinkFileTest.setItems(initLinkFiles());
        ModifyLinkFileButton.setDisable(true);
        path_MasterTextField.setPromptText("File Path [MASTER]");
        path_GhostTextField.setPromptText("File Path [GHOST]");

        //[LINK FILE TEST] Update the table to allow for the path to be edittable
        tblLinkFileTest.setEditable(true);
        clmMaster.setCellFactory(TextFieldTableCell.forTableColumn());
        clmGhost.setCellFactory(TextFieldTableCell.forTableColumn());

        // ============================================================

        chkIncludeRuwiTest.setSelected(root.getRunSettings().getFileManagement().isIncludeRuwiTest());
        handleIncludeRuwiCheck();

        clmRuwi.setCellValueFactory(new PropertyValueFactory<FMRuwi, String>("path_Ruwi"));
        path_RuwiTextField.setPromptText("File Path");

        tblRuwi.setEditable(true);
        ModifyRuwiButton.setDisable(true);

        clmRuwi.setCellFactory(TextFieldTableCell.forTableColumn());

        // ============================================================

        chkIncludeSfiTest.setSelected(root.getRunSettings().getFileManagement().isIncludeSfiTest());
        handleIncludeSfiCheck();

        // ============================================================
    }

    // ============================================================

    public void changePath_MasterCellEvent(TableColumn.CellEditEvent edittedCell) {
        FMLinkFiles pathSelected = tblLinkFileTest.getSelectionModel().getSelectedItem();
        pathSelected.setPath_Master( edittedCell.getNewValue().toString());
    }
    public void changePath_GhostCellEvent(TableColumn.CellEditEvent edittedCell) {
        FMLinkFiles pathSelected = tblLinkFileTest.getSelectionModel().getSelectedItem();
        pathSelected.setPath_Ghost( edittedCell.getNewValue().toString());
    }

    public void deletePathButtonPusshed() {

        ObservableList<FMLinkFiles> selectedRows, allPath;

        //[LINK FILE TEST] This gives us the rows that were selected
        selectedRows = tblLinkFileTest.getSelectionModel().getSelectedItems();

        allPath = tblLinkFileTest.getItems();

        //[LINK FILE TEST] loop over the selected rows and remove the path from table
        for (FMLinkFiles FMLinkFiles : selectedRows)
        {
            allPath.remove(FMLinkFiles);
        }

    }
    public void newPathButtonPushed () {
        FMLinkFiles newFMLinkFiles = new FMLinkFiles(path_MasterTextField.getText(),
                                            path_GhostTextField.getText());

        //[LINK FILE TEST] Get all the items from the table, then add a new path link file
        tblLinkFileTest.getItems().add(newFMLinkFiles);
        path_MasterTextField.clear();
        path_GhostTextField.clear();
    }

    public void SaveLinkFileButtonPushed() {
        tblLinkFileTest.setEditable(false);
        ModifyLinkFileButton.setDisable(false);
        SaveLinkFileButton.setDisable(true);
        path_GhostTextField.setDisable(true);
        path_MasterTextField.setDisable(true);
        addLinkFileButton.setDisable(true);
        deleteLinkFileButton.setDisable(true);
    }
    public void ModifyLinkFileButtonPushed() {
        tblLinkFileTest.setEditable(true);
        ModifyLinkFileButton.setDisable(true);
        SaveLinkFileButton.setDisable(false);
        path_GhostTextField.setDisable(false);
        path_MasterTextField.setDisable(false);
        addLinkFileButton.setDisable(false);
        deleteLinkFileButton.setDisable(false);
    }

    @FXML private void handleIncludeLinkFileCheck() { root.getMenuLinkFile().setDisable(!chkIncludeLinkFileTest.isSelected()); }


    public ObservableList<FMLinkFiles> initLinkFiles() {

        //USIM-GSM
        linkfiles.add(new FMLinkFiles("3F007FF06F37" , "3F007F206F37"));
        linkfiles.add(new FMLinkFiles("3F007FF06F32" , "3F007F206F32"));
        linkfiles.add(new FMLinkFiles("3F007FF06F07" , "3F007F206F07"));
        linkfiles.add(new FMLinkFiles("3F007FF06F31" , "3F007F206F31"));
        linkfiles.add(new FMLinkFiles("3F007FF06F39" , "3F007F206F39"));
        linkfiles.add(new FMLinkFiles("3F007FF06F3E" , "3F007F206F3E"));
        linkfiles.add(new FMLinkFiles("3F007FF06F3F" , "3F007F206F3F"));
        linkfiles.add(new FMLinkFiles("3F007FF06F41" , "3F007F206F41"));
        linkfiles.add(new FMLinkFiles("3F007FF06F45" , "3F007F206F45"));
        linkfiles.add(new FMLinkFiles("3F007FF06F46" , "3F007F206F46"));
        linkfiles.add(new FMLinkFiles("3F007FF06F50" , "3F007F206F50"));
        linkfiles.add(new FMLinkFiles("3F007FF06F61" , "3F007F206F61"));
        linkfiles.add(new FMLinkFiles("3F007FF06F62" , "3F007F206F62"));
        linkfiles.add(new FMLinkFiles("3F007FF06F73" , "3F007F206F53"));
        linkfiles.add(new FMLinkFiles("3F007FF06F78" , "3F007F206F78"));
        linkfiles.add(new FMLinkFiles("3F007FF06F7B" , "3F007F206F7B"));
        linkfiles.add(new FMLinkFiles("3F007FF06F7E" , "3F007F206F7E"));

        //ACCESS-GSM
        linkfiles.add(new FMLinkFiles("3F007FF055F3B4F20" , "3F007F206F20"));
        linkfiles.add(new FMLinkFiles("3F007FF055F3B4F52" , "3F007F206F52"));
        linkfiles.add(new FMLinkFiles("3F007FF055F3B4F63" , "3F007F206F63"));
        linkfiles.add(new FMLinkFiles("3F007FF055F3B4F64" , "3F007F206F64"));

        //TELCO-GSM
        linkfiles.add(new FMLinkFiles("3F007F106F54" , "3F007F206F54"));

        //TELCO-PHONEBOOK
        linkfiles.add(new FMLinkFiles("3F007F106F3A" , "3F007F105F3A4F3A"));
        linkfiles.add(new FMLinkFiles("3F007F106F4A" , "3F007F105F3A4F4A"));
        linkfiles.add(new FMLinkFiles("3F007F106F4F" , "3F007F105F3A4F4F"));

        //USIM-TELCO
        linkfiles.add(new FMLinkFiles("3F007FF06F3B" , "3F007F106F3B"));
        linkfiles.add(new FMLinkFiles("3F007FF06F3C" , "3F007F106F3C"));
        linkfiles.add(new FMLinkFiles("3F007FF06F42" , "3F007F106F42"));
        linkfiles.add(new FMLinkFiles("3F007FF06F43" , "3F007F106F43"));
        linkfiles.add(new FMLinkFiles("3F007FF06F47" , "3F007F106F47"));
        linkfiles.add(new FMLinkFiles("3F007FF06F49" , "3F007F106F49"));
        linkfiles.add(new FMLinkFiles("3F007FF06F4B" , "3F007F106F4B"));
        linkfiles.add(new FMLinkFiles("3F007FF06F4C" , "3F007F106F4C"));

        return linkfiles;
    }

    // ============================================================

    public void changePath_RuwiCellEvent(TableColumn.CellEditEvent edittedCell) {
        FMRuwi pathSelected = tblRuwi.getSelectionModel().getSelectedItem();
        pathSelected.setPath_Ruwi( edittedCell.getNewValue().toString());
    }

    public void deletePathButtonRuwiPusshed() {

        ObservableList<FMRuwi> selectedRows, allPath;
        selectedRows = tblRuwi.getSelectionModel().getSelectedItems();

        allPath = tblRuwi.getItems();

        for (FMRuwi ruwiandSfi : selectedRows)
        {
            allPath.remove(ruwiandSfi);
        }

    }
    public void newPathButtonRuwiPushed () {
        FMRuwi newruwi = new FMRuwi(path_RuwiTextField.getText());

        tblRuwi.getItems().add(newruwi);
        path_RuwiTextField.clear();
    }

    public void SaveRuwiButtonPushed() {
        tblRuwi.setEditable(false);
        ModifyRuwiButton.setDisable(false);
        SaveRuwiButton.setDisable(true);
        path_RuwiTextField.setDisable(true);
        addRuwiButton.setDisable(true);
        deleteRuwiButton.setDisable(true);
    }
    public void ModifyRuwiButtonPushed() {
        tblRuwi.setEditable(true);
        ModifyRuwiButton.setDisable(true);
        SaveRuwiButton.setDisable(false);
        path_RuwiTextField.setDisable(false);
        addRuwiButton.setDisable(false);
        deleteRuwiButton.setDisable(false);
    }

    @FXML private void handleIncludeRuwiCheck() { root.getMenuRuwi().setDisable(!chkIncludeRuwiTest.isSelected()); }

    // ============================================================

    @FXML private void handleIncludeSfiCheck() { root.getMenuSfi().setDisable(!chkIncludeSfiTest.isSelected()); }

    // ============================================================

    public void saveControlState() {
        root.getRunSettings().getFileManagement().setIncludeLinkFilesTest(chkIncludeLinkFileTest.isSelected());
        root.getRunSettings().getFileManagement().setIncludeRuwiTest(chkIncludeRuwiTest.isSelected());
        root.getRunSettings().getFileManagement().setIncludeSfiTest(chkIncludeSfiTest.isSelected());

        root.getRunSettings().getFileManagement().setSFI_Iccid_2FE2_02_bool(SFI_Iccid_2FE2_02_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_PL_2F05_05_bool(SFI_PL_2F05_05_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_ECC_6F7B_01_bool(SFI_ECC_6F7B_01_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_LI_6F05_02_bool(SFI_LI_6F05_02_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_AD_6FAD_03_bool(SFI_AD_6FAD_03_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_UST_6F38_04_bool(SFI_UST_6F38_04_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_EST_6F56_05_bool(SFI_EST_6F56_05_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_ACC_6F78_06_bool(SFI_ACC_6F78_06_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_IMSI_6F07_07_bool(SFI_IMSI_6F07_07_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_KEYS_6F08_08_bool(SFI_KEYS_6F08_08_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_KEYSPS_6F09_09_bool(SFI_KEYSPS_6F09_09_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_PLMNwACT_6F60_0A_bool(SFI_PLMNwACT_6F60_0A_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_LOCI_6F7E_0B_bool(SFI_LOCI_6F7E_0B_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_PSLOCI_6F73_0C_bool(SFI_PSLOCI_6F73_0C_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_FPLMN_6F7B_0D_bool(SFI_FPLMN_6F7B_0D_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_CBMID_6F48_0E_bool(SFI_CBMID_6F48_0E_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_Kc_4F20_01_bool(SFI_Kc_4F20_01_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_ARR_2F06_06_bool(SFI_ARR_2F06_06_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_Dir_2F00_1E_bool(SFI_Dir_2F00_1E_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_StartHFN_6F5B_0F_bool(SFI_StartHFN_6F5B_0F_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_TRESHOLD_6F5C_10_bool(SFI_TRESHOLD_6F5C_10_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_OPLMNwACT_6F61_11_bool(SFI_OPLMNwACT_6F61_11_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_HPPLMN_6F31_12_bool(SFI_HPPLMN_6F31_12_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_HPLMNwACT_6F62_13_bool(SFI_HPLMNwACT_6F62_13_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_ICI_6F80_14_bool(SFI_ICI_6F80_14_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_OCI_6F81_15_bool(SFI_OCI_6F81_15_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_CCP2_6F4F_16_bool(SFI_CCP2_6F4F_16_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_ARR_6F06_17_bool(SFI_ARR_6F06_17_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_ePDGIdEm_6F65_18_bool(SFI_ePDGIdEm_6F65_18_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_PNN_6FC5_19_bool(SFI_PNN_6FC5_19_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_OPL_6FC6_1A_bool(SFI_OPL_6FC6_1A_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_SPDI_6FCD_1B_bool(SFI_SPDI_6FCD_1B_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_ACM_6F39_1C_bool(SFI_ACM_6F39_1C_Checkbox.isSelected());
        root.getRunSettings().getFileManagement().setSFI_KcGPRS_4F52_02_bool(SFI_KcGPRS_4F52_02_Checkbox.isSelected());

    }
}
