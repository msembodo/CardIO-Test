package com.idemia.tec.jkt.cardiotest.model;

import java.util.List;

public class RunSettings {

    private String projectPath;
    private String advSaveVariablesPath;
    private int readerNumber;
    private boolean stopOnError;
    private String requestId;
    private String requestName;
    private String profileName;
    private int profileVersion;
    private String cardImageItemId;
    private String customer;
    private String developerName;
    private String testerName;
    private List<VariableMapping> variableMappings;

    public RunSettings() {}

    public RunSettings(String projectPath, String advSaveVariablesPath, int readerNumber, boolean stopOnError,
                       String requestId, String requestName, String profileName, int profileVersion,
                       String cardImageItemId, String customer, String developerName, String testerName, List<VariableMapping> variableMappings) {
        this.projectPath = projectPath;
        this.advSaveVariablesPath = advSaveVariablesPath;
        this.readerNumber = readerNumber;
        this.stopOnError = stopOnError;
        this.requestId = requestId;
        this.requestName = requestName;
        this.profileName = profileName;
        this.profileVersion = profileVersion;
        this.cardImageItemId = cardImageItemId;
        this.customer = customer;
        this.developerName = developerName;
        this.testerName = testerName;
        this.variableMappings = variableMappings;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getAdvSaveVariablesPath() {
        return advSaveVariablesPath;
    }

    public void setAdvSaveVariablesPath(String advSaveVariablesPath) {
        this.advSaveVariablesPath = advSaveVariablesPath;
    }

    public int getReaderNumber() {
        return readerNumber;
    }

    public void setReaderNumber(int readerNumber) {
        this.readerNumber = readerNumber;
    }

    public boolean isStopOnError() {
        return stopOnError;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getProfileVersion() {
        return profileVersion;
    }

    public void setProfileVersion(int profileVersion) {
        this.profileVersion = profileVersion;
    }

    public String getCardImageItemId() {
        return cardImageItemId;
    }

    public void setCardImageItemId(String cardImageItemId) {
        this.cardImageItemId = cardImageItemId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getTesterName() {
        return testerName;
    }

    public void setTesterName(String testerName) {
        this.testerName = testerName;
    }

    public List<VariableMapping> getVariableMappings() {
        return variableMappings;
    }

    public void setVariableMappings(List<VariableMapping> variableMappings) {
        this.variableMappings = variableMappings;
    }

}
