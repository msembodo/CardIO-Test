package com.idemia.tec.jkt.cardiotest.response;

import com.idemia.tec.jkt.cardiotest.model.TestSuite;

public class TestSuiteResponse {

    private boolean success;
    private String message;
    private TestSuite testSuite;

    public TestSuiteResponse() {}

    public TestSuiteResponse(boolean success, String message, TestSuite testSuite) {
        this.success = success;
        this.message = message;
        this.testSuite = testSuite;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

}
