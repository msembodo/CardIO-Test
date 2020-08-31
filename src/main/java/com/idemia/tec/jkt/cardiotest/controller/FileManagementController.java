package com.idemia.tec.jkt.cardiotest.controller;

import javafx.scene.control.CheckBox;
import com.idemia.tec.jkt.cardiotest.model.FileManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.scene.control.TextField;



@Component
public class FileManagementController  {

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    @FXML private CheckBox chkIncludeLinkFile;

    //[LINK FILE TEST] configure the table
    @FXML private TableView<FileManagement> tblLinkFileTest;
    @FXML private TableColumn<FileManagement, String> clmMaster;
    @FXML private TableColumn<FileManagement, String> clmGhost;

    //[LINK FILE TEST] These instance variables ure used tadd a new path link file
    @FXML private TextField path_MasterTextField;
    @FXML private TextField path_GhostTextField;

    public FileManagementController() {}


     //[LINK FILE TEST] This method will allow to user to double click on a cell and update
    public void changePath_MasterCellEvent(TableColumn.CellEditEvent edittedCell)
    {
        FileManagement pathSelected = tblLinkFileTest.getSelectionModel().getSelectedItem();
        pathSelected.setPath_Master( edittedCell.getNewValue().toString());
    }

    public void changePath_GhostCellEvent(TableColumn.CellEditEvent edittedCell)
    {
        FileManagement pathSelected = tblLinkFileTest.getSelectionModel().getSelectedItem();
        pathSelected.setPath_Ghost( edittedCell.getNewValue().toString());
    }


    public void initialize() {

       // chkIncludeLinkFile.setSelected(root.getRunSettings().getFileManagement().isIncludeLinkFile());
       // handleIncludeLinkFileCheck();

        //[LINK FILE TEST] setup for columns
        clmMaster.setCellValueFactory(new PropertyValueFactory<FileManagement, String>("path_Master"));
        clmGhost.setCellValueFactory(new PropertyValueFactory<FileManagement, String> ("path_Ghost"));

        //[LINK FILE TEST] load the data
        tblLinkFileTest.setItems(getLinkPath());

        //[LINK FILE TEST] Update the table to allow for the path to be edittable
        tblLinkFileTest.setEditable(true);
        clmMaster.setCellFactory(TextFieldTableCell.forTableColumn());
        clmGhost.setCellFactory(TextFieldTableCell.forTableColumn());

        //[LINK FILE TEST] This will allowed to select multiple table
        tblLinkFileTest.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * [LINK FILE TEST] This method will remove the selected row(s) from the table
     */

    public void deletePathButtonPusshed()
    {
        ObservableList<FileManagement> selectedRows, allPath;
        allPath = tblLinkFileTest.getItems();

        //[LINK FILE TEST] This gives us the rows that were selected
        selectedRows = tblLinkFileTest.getSelectionModel().getSelectedItems();

        //[LINK FILE TEST] loop over the selected rows and remove the path from table
        for (FileManagement fileManagement : selectedRows)
        {
            allPath.remove(fileManagement);
        }

    }


    /**
     * [LINK FILE TEST] This method will create a new linked file path to the table
     */
    public void newPathButtonPushed ()
    {
        FileManagement newFileManagement = new FileManagement(true,path_MasterTextField.getText(),
                                                              path_GhostTextField.getText());

        //[LINK FILE TEST] Get all the items from the table, then add a new path link file
        tblLinkFileTest.getItems().add(newFileManagement);
    }


    //[LINK FILE TEST] This method will return an observable List of link path data

    public ObservableList<FileManagement> getLinkPath()
    {
        ObservableList<FileManagement> linkfile = FXCollections.observableArrayList();

        //USIM-GSM
        linkfile.add(new FileManagement(true,"3F007FF06F37" , "3F007F206F37"));
        linkfile.add(new FileManagement(true,"3F007FF06F32" , "3F007F206F32"));
        linkfile.add(new FileManagement(true,"3F007FF06F07" , "3F007F206F07"));
        linkfile.add(new FileManagement(true,"3F007FF06F31" , "3F007F206F31"));
        linkfile.add(new FileManagement(true,"3F007FF06F39" , "3F007F206F39"));
        linkfile.add(new FileManagement(true,"3F007FF06F3E" , "3F007F206F3E"));
        linkfile.add(new FileManagement(true,"3F007FF06F3F" , "3F007F206F3F"));
        linkfile.add(new FileManagement(true,"3F007FF06F41" , "3F007F206F41"));
        linkfile.add(new FileManagement(true,"3F007FF06F45" , "3F007F206F45"));
        linkfile.add(new FileManagement(true,"3F007FF06F46" , "3F007F206F46"));
        linkfile.add(new FileManagement(true,"3F007FF06F50" , "3F007F206F50"));
        linkfile.add(new FileManagement(true,"3F007FF06F61" , "3F007F206F61"));
        linkfile.add(new FileManagement(true,"3F007FF06F62" , "3F007F206F62"));
        linkfile.add(new FileManagement(true,"3F007FF06F73" , "3F007F206F53"));
        linkfile.add(new FileManagement(true,"3F007FF06F78" , "3F007F206F78"));
        linkfile.add(new FileManagement(true,"3F007FF06F7B" , "3F007F206F7B"));
        linkfile.add(new FileManagement(true,"3F007FF06F7E" , "3F007F206F7E"));

        //ACCESS-GSM
        linkfile.add(new FileManagement(true,"3F007FF055F3B4F20" , "3F007F206F20"));
        linkfile.add(new FileManagement(true,"3F007FF055F3B4F52" , "3F007F206F52"));
        linkfile.add(new FileManagement(true,"3F007FF055F3B4F63" , "3F007F206F63"));
        linkfile.add(new FileManagement(true,"3F007FF055F3B4F64" , "3F007F206F64"));

/*
        //TELCO-GSM
        linkfile.add(new FileManagement("3F007F106F54" , "3F007F206F54"));

        //TELCO-PHONEBOOK
        linkfile.add(new FileManagement("3F007F106F3A" , "3F007F105F3A4F3A"));
        linkfile.add(new FileManagement("3F007F106F4A" , "3F007F105F3A4F4A"));
        linkfile.add(new FileManagement("3F007F106F4F" , "3F007F105F3A4F4F"));

        //USIM-TELCO
        linkfile.add(new FileManagement("3F007FF06F3B" , "3F007F106F3B"));
        linkfile.add(new FileManagement("3F007FF06F3C" , "3F007F106F3C"));
        linkfile.add(new FileManagement("3F007FF06F42" , "3F007F106F42"));
        linkfile.add(new FileManagement("3F007FF06F43" , "3F007F106F43"));
        linkfile.add(new FileManagement("3F007FF06F47" , "3F007F106F47"));
        linkfile.add(new FileManagement("3F007FF06F49" , "3F007F106F49"));
        linkfile.add(new FileManagement("3F007FF06F4B" , "3F007F106F4B"));
        linkfile.add(new FileManagement("3F007FF06F4C" , "3F007F106F4C"));
         */

        return linkfile;
    }

    @FXML private void handleIncludeLinkFileCheck() { root.getMenuLinkFile().setDisable(!chkIncludeLinkFile.isSelected()); }

    public void saveControlState() {
        root.getRunSettings().getFileManagement().setIncludeLinkFile(chkIncludeLinkFile.isSelected());
    }

}
