package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.FileManagement;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class FileManagementController implements Initializable {


    //configure the table
    @FXML private TableView<FileManagement> tblLinkFileTest;
    @FXML private TableColumn<FileManagement, String> clmMaster;
    @FXML private TableColumn<FileManagement, String> clmGhost;


     // This method will allow to user to double click on a cell and update
    public void changePath_MasterCellEvent(TableColumn.CellEditEvent edittedCell)
    {
        FileManagement pathSelected = tblLinkFileTest.getSelectionModel().getSelectedItem();
        pathSelected.setPath_Master((SimpleStringProperty) edittedCell.getNewValue());
    }

    public void changePath_GhostCellEvent(TableColumn.CellEditEvent edittedCell)
    {
        FileManagement pathSelected = tblLinkFileTest.getSelectionModel().getSelectedItem();
        pathSelected.setPath_Ghost((SimpleStringProperty) edittedCell.getNewValue());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setup for columns
        clmMaster.setCellValueFactory(new PropertyValueFactory<FileManagement, String>("path_Master"));
        clmGhost.setCellValueFactory(new PropertyValueFactory<FileManagement, String> ("path_Ghost"));

        //load the data
        tblLinkFileTest.setItems(getLinkPath());

        //Update the table to allow for the path to be edittable
        tblLinkFileTest.setEditable(true);
        clmMaster.setCellFactory(TextFieldTableCell.forTableColumn());
        clmGhost.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    //This method will return an observable List of link path data
    public ObservableList<FileManagement> getLinkPath()
    {
        ObservableList<FileManagement> linkfile = FXCollections.observableArrayList();
        linkfile.add(new FileManagement("3F007FF06FC5" , "3F007F206FC5"));
        linkfile.add(new FileManagement("3F007FF06FC6" , "3F007F206FC6"));
        linkfile.add(new FileManagement("3F007FF06F07" , "3F007F206F07"));
        linkfile.add(new FileManagement("3F007FF06F31" , "3F007F206F31"));
        return linkfile;
    }


}
