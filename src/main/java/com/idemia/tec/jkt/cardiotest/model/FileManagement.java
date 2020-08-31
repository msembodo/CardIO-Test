package com.idemia.tec.jkt.cardiotest.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class FileManagement {

    private boolean includeLinkFile;
    private SimpleStringProperty path_Master, path_Ghost;

    public FileManagement() {}

    public FileManagement(boolean includeLinkFile, String path_Master, String path_Ghost) {
        this.includeLinkFile = includeLinkFile;
        this.path_Master = new SimpleStringProperty (path_Master);
        this.path_Ghost = new SimpleStringProperty (path_Ghost);
    }



    public boolean isIncludeLinkFile() {
        return includeLinkFile;
    }

    public void setIncludeLinkFile(boolean includeLinkFile) {
        this.includeLinkFile = includeLinkFile;
    }

     public String getPath_Master() {
     return path_Master.get();
     }

     public void setPath_Master(String path_Master) {
     this.path_Master = new SimpleStringProperty (path_Master);
     }

     public String getPath_Ghost() {
     return path_Ghost.get();
     }

     public void setPath_Ghost(String path_Ghost) {
     this.path_Ghost = new SimpleStringProperty (path_Ghost);
     }


}
