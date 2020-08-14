package com.idemia.tec.jkt.cardiotest.model;

import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class   SCP80Keyset {

    private StringProperty keysetName;
    private int keysetVersion;
    private String keysetType;
    private String kicValuation;
    private int kicKeyLength;
    private String kicMode;
    private String kidValuation;
    private int kidKeyLength;
    private String kidMode;
    private String computedKic;
    private String computedKid;
    private boolean customKic;
    private boolean customKid;
    private int cmacLength;

    public SCP80Keyset() {}

    public SCP80Keyset(String keysetName, int keysetVersion, String keysetType, String kicValuation, int kicKeyLength,
                       String kicMode, String kidValuation, int kidKeyLength, String kidMode, int cmacLength) {
        this.keysetName = new SimpleStringProperty(keysetName);
        this.keysetVersion = keysetVersion;
        this.keysetType = keysetType;
        this.kicValuation = kicValuation;
        this.kicKeyLength = kicKeyLength;
        this.kicMode = kicMode;
        this.kidValuation = kidValuation;
        this.kidKeyLength = kidKeyLength;
        this.kidMode = kidMode;
        this.cmacLength = cmacLength;
        this.computedKic = computeKic();
        this.computedKid = computeKid();
    }

    private String computeKic() {
        int kicValue = 0;
        kicValue += keysetVersion << 4;
        if (kicMode.equals("3DES - CBC 2 keys")) kicValue += 1 << 2;
        if (kicMode.equals("3DES - CBC 3 keys")) kicValue += 2 << 2;
        if (kicMode.equals("DES - ECB")) kicValue += 3 << 2;
        if (keysetType.equals("DES")) kicValue += 1;
        if (keysetType.equals("AES")) kicValue += 2;
        if (keysetType.equals("Proprietary Implementations")) kicValue += 3;
        return String.format("%02X", kicValue);
    }

    private String computeKid() {
        int kidValue = 0;
        kidValue += keysetVersion << 4;
        if (kidMode.equals("3DES - CBC 2 keys")) kidValue += 1 << 2;
        if (kidMode.equals("3DES - CBC 3 keys")) kidValue += 2 << 2;
        if (kidMode.equals("DES - ECB")) kidValue += 3 << 2;
        if (keysetType.equals("DES")) kidValue += 1;
        if (keysetType.equals("AES")) kidValue += 2;
        if (keysetType.equals("Proprietary Implementations")) kidValue += 3;
        return String.format("%02X", kidValue);
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

    public int getKicKeyLength() {
        return kicKeyLength;
    }

    public void setKicKeyLength(int kicKeyLength) {
        this.kicKeyLength = kicKeyLength;
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

    public int getKidKeyLength() {
        return kidKeyLength;
    }

    public void setKidKeyLength(int kidKeyLength) {
        this.kidKeyLength = kidKeyLength;
    }

    public String getKidMode() {
        return kidMode;
    }

    public void setKidMode(String kidMode) {
        this.kidMode = kidMode;
    }

    public String getComputedKic() {
        return computedKic;
    }

    public void setComputedKic(String computedKic) {
        this.computedKic = computedKic;
    }

    public String getComputedKid() {
        return computedKid;
    }

    public void setComputedKid(String computedKid) {
        this.computedKid = computedKid;
    }

    public boolean isCustomKic() {
        return customKic;
    }

    public void setCustomKic(boolean customKic) {
        this.customKic = customKic;
    }

    public boolean isCustomKid() {
        return customKid;
    }

    public void setCustomKid(boolean customKid) {
        this.customKid = customKid;
    }

    public int getCmacLength() {
        return cmacLength;
    }

    public void setCmacLength(int cmacLength) {
        this.cmacLength = cmacLength;
    }

    // for debug
    public String toJson() {
        return new Gson().toJson(this);
    }

}
