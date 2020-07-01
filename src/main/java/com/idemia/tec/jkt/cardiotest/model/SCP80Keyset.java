package com.idemia.tec.jkt.cardiotest.model;

import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SCP80Keyset {

    private StringProperty keysetName;
    private int keysetVersion;
    private String keysetType;
    private String kicValuation;
    private String kicMode;
    private String kidValuation;
    private String kidMode;

    public SCP80Keyset() {}

    public SCP80Keyset(String keysetName, int keysetVersion, String keysetType, String kicValuation, String kicMode, String kidValuation, String kidMode) {
        this.keysetName = new SimpleStringProperty(keysetName);
        this.keysetVersion = keysetVersion;
        this.keysetType = keysetType;
        this.kicValuation = kicValuation;
        this.kicMode = kicMode;
        this.kidValuation = kidValuation;
        this.kidMode = kidMode;
    }

    public String getKeysetName() {
        return keysetName.get();
    }

    public StringProperty keysetNameProperty() {
        return keysetName;
    }

    public void setKeysetName(String keysetName) {
        this.keysetName = new SimpleStringProperty(keysetName);
    }

    public int getKeysetVersion() {
        return keysetVersion;
    }

    public void setKeysetVersion(int keysetVersion) {
        this.keysetVersion = keysetVersion;
    }

    public String getKeysetType() {
        return keysetType;
    }

    public void setKeysetType(String keysetType) {
        this.keysetType = keysetType;
    }

    public String getKicValuation() {
        return kicValuation;
    }

    public void setKicValuation(String kicValuation) {
        this.kicValuation = kicValuation;
    }

    public String getKicMode() {
        return kicMode;
    }

    public void setKicMode(String kicMode) {
        this.kicMode = kicMode;
    }

    public String getKidValuation() {
        return kidValuation;
    }

    public void setKidValuation(String kidValuation) {
        this.kidValuation = kidValuation;
    }

    public String getKidMode() {
        return kidMode;
    }

    public void setKidMode(String kidMode) {
        this.kidMode = kidMode;
    }

    // for debug
    public String toJson() {
        return new Gson().toJson(this);
    }

}
