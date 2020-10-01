package com.idemia.tec.jkt.cardiotest.model;

public class CheckPoint {

    private String checkPointName;
    private String checkPointMsg;
    private boolean success;
    private String outputData;
    private String expectedData;
    private String status;
    private String expectedStatus;

    public CheckPoint() {}

    public CheckPoint(String checkPointName, String checkPointMsg, boolean success, String outputData, String expectedData, String status, String expectedStatus) {
        this.checkPointName = checkPointName;
        this.checkPointMsg = checkPointMsg;
        this.success = success;
        this.outputData = outputData;
        this.expectedData = expectedData;
        this.status = status;
        this.expectedStatus = expectedStatus;
    }

    public String getCheckPointName() { return checkPointName; }
    public void setCheckPointName(String checkPointName) { this.checkPointName = checkPointName; }
    public String getCheckPointMsg() { return checkPointMsg; }
    public void setCheckPointMsg(String checkPointMsg) { this.checkPointMsg = checkPointMsg; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getOutputData() { return outputData; }
    public void setOutputData(String outputData) { this.outputData = outputData; }
    public String getExpectedData() { return expectedData; }
    public void setExpectedData(String expectedData) { this.expectedData = expectedData; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getExpectedStatus() { return expectedStatus; }
    public void setExpectedStatus(String expectedStatus) { this.expectedStatus = expectedStatus; }

}
