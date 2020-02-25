package com.idemia.tec.jkt.cardiotest.model;

public class RunModule {

    private int moduleNumber;
    private String moduleName;
    private boolean executed;

    public RunModule() {}

    public RunModule(int moduleNumber, String moduleName, boolean executed) {
        this.moduleNumber = moduleNumber;
        this.moduleName = moduleName;
        this.executed = executed;
    }

    public int getModuleNumber() {
        return moduleNumber;
    }

    public void setModuleNumber(int moduleNumber) {
        this.moduleNumber = moduleNumber;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

}
