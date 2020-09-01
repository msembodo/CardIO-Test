package com.idemia.tec.jkt.cardiotest.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LinkFile {

    private SimpleStringProperty path_Master, path_Ghost;

    public LinkFile() {}


    public LinkFile(String path_Master, String path_Ghost) {
        this.path_Master = new SimpleStringProperty (path_Master);
        this.path_Ghost = new SimpleStringProperty (path_Ghost);
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
