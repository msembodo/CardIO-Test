package com.idemia.tec.jkt.cardiotest.model;

public class SecretCodes {

    private boolean include3gScript;
    private boolean include2gScript;

    private String gpin;
    private String lpin;
    private String gpuk;
    private String lpuk;
    private int gpinRetries;
    private int lpinRetries;
    private int gpukRetries;
    private int lpukRetries;

    private String chv1;
    private String chv2;
    private String puk1;
    private String puk2;
    private int chv1Retries;
    private int chv2Retries;
    private int puk1Retries;
    private int puk2Retries;

    private boolean pin1disabled;
    private boolean pin2disabled;

    public SecretCodes() {}

    public SecretCodes(boolean include3gScript, boolean include2gScript, boolean pin1disabled, boolean pin2disabled) {
        this.include3gScript = include3gScript;
        this.include2gScript = include2gScript;
        this.pin1disabled = pin1disabled;
        this.pin2disabled = pin2disabled;
    }

    public SecretCodes(boolean include3gScript, boolean include2gScript, String gpin, String lpin, String gpuk, String lpuk, int gpinRetries, int lpinRetries, int gpukRetries, int lpukRetries, String chv1, String chv2, String puk1, String puk2, int chv1Retries, int chv2Retries, int puk1Retries, int puk2Retries, boolean pin1disabled, boolean pin2disabled) {
        this.include3gScript = include3gScript;
        this.include2gScript = include2gScript;
        this.gpin = gpin;
        this.lpin = lpin;
        this.gpuk = gpuk;
        this.lpuk = lpuk;
        this.gpinRetries = gpinRetries;
        this.lpinRetries = lpinRetries;
        this.gpukRetries = gpukRetries;
        this.lpukRetries = lpukRetries;
        this.chv1 = chv1;
        this.chv2 = chv2;
        this.puk1 = puk1;
        this.puk2 = puk2;
        this.chv1Retries = chv1Retries;
        this.chv2Retries = chv2Retries;
        this.puk1Retries = puk1Retries;
        this.puk2Retries = puk2Retries;
        this.pin1disabled = pin1disabled;
        this.pin2disabled = pin2disabled;
    }

    public boolean isInclude3gScript() {
        return include3gScript;
    }

    public void setInclude3gScript(boolean include3gScript) {
        this.include3gScript = include3gScript;
    }

    public boolean isInclude2gScript() {
        return include2gScript;
    }

    public void setInclude2gScript(boolean include2gScript) {
        this.include2gScript = include2gScript;
    }

    public String getGpin() {
        return gpin;
    }

    public void setGpin(String gpin) {
        this.gpin = gpin;
    }

    public String getLpin() {
        return lpin;
    }

    public void setLpin(String lpin) {
        this.lpin = lpin;
    }

    public String getGpuk() {
        return gpuk;
    }

    public void setGpuk(String gpuk) {
        this.gpuk = gpuk;
    }

    public String getLpuk() {
        return lpuk;
    }

    public void setLpuk(String lpuk) {
        this.lpuk = lpuk;
    }

    public int getGpinRetries() {
        return gpinRetries;
    }

    public void setGpinRetries(int gpinRetries) {
        this.gpinRetries = gpinRetries;
    }

    public int getLpinRetries() {
        return lpinRetries;
    }

    public void setLpinRetries(int lpinRetries) {
        this.lpinRetries = lpinRetries;
    }

    public int getGpukRetries() {
        return gpukRetries;
    }

    public void setGpukRetries(int gpukRetries) {
        this.gpukRetries = gpukRetries;
    }

    public int getLpukRetries() {
        return lpukRetries;
    }

    public void setLpukRetries(int lpukRetries) {
        this.lpukRetries = lpukRetries;
    }

    public String getChv1() {
        return chv1;
    }

    public void setChv1(String chv1) {
        this.chv1 = chv1;
    }

    public String getChv2() {
        return chv2;
    }

    public void setChv2(String chv2) {
        this.chv2 = chv2;
    }

    public String getPuk1() {
        return puk1;
    }

    public void setPuk1(String puk1) {
        this.puk1 = puk1;
    }

    public String getPuk2() {
        return puk2;
    }

    public void setPuk2(String puk2) {
        this.puk2 = puk2;
    }

    public int getChv1Retries() {
        return chv1Retries;
    }

    public void setChv1Retries(int chv1Retries) {
        this.chv1Retries = chv1Retries;
    }

    public int getChv2Retries() {
        return chv2Retries;
    }

    public void setChv2Retries(int chv2Retries) {
        this.chv2Retries = chv2Retries;
    }

    public int getPuk1Retries() {
        return puk1Retries;
    }

    public void setPuk1Retries(int puk1Retries) {
        this.puk1Retries = puk1Retries;
    }

    public int getPuk2Retries() {
        return puk2Retries;
    }

    public void setPuk2Retries(int puk2Retries) {
        this.puk2Retries = puk2Retries;
    }

    public boolean isPin1disabled() {
        return pin1disabled;
    }

    public void setPin1disabled(boolean pin1disabled) {
        this.pin1disabled = pin1disabled;
    }

    public boolean isPin2disabled() {
        return pin2disabled;
    }

    public void setPin2disabled(boolean pin2disabled) {
        this.pin2disabled = pin2disabled;
    }

}
