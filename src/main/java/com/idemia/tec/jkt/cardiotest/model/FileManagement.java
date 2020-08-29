package com.idemia.tec.jkt.cardiotest.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileManagement {

    private SimpleStringProperty path_Master, path_Ghost;

    public FileManagement(String path_Master, String path_Ghost) {
        this.path_Master = new SimpleStringProperty (path_Master);
        this.path_Ghost = new SimpleStringProperty (path_Ghost);
    }

    public String getPath_Master() {
        return path_Master.get();
    }

    public void setPath_Master(SimpleStringProperty path_Master) {
        this.path_Master = path_Master;
    }



    public String getPath_Ghost() {
        return path_Ghost.get();
    }

    public void setPath_Ghost(SimpleStringProperty path_Ghost) {
        this.path_Ghost = path_Ghost;
    }

}
