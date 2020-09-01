package com.idemia.tec.jkt.cardiotest.model;

public class FileManagement {

    private boolean includeLinkFileTest;
    private boolean testLinkFileOk;
    private String testLinkFileMessage;

    public FileManagement() {}

    public FileManagement(boolean includeLinkFileTest, boolean testLinkFileOk, String testLinkFileMessage) {
        this.includeLinkFileTest = includeLinkFileTest;
        this.testLinkFileOk = testLinkFileOk;
        this.testLinkFileMessage = testLinkFileMessage;
    }


    public boolean isIncludeLinkFileTest() {
        return includeLinkFileTest;
    }

    public void setIncludeLinkFileTest(boolean includeLinkFileTest) {
        this.includeLinkFileTest = includeLinkFileTest;
    }

    public boolean isTestLinkFileOk() {
        return testLinkFileOk;
    }

    public void setTestLinkFileOk(boolean testLinkFileOk) {
        this.testLinkFileOk = testLinkFileOk;
    }

    public String getTestLinkFileMessage() {
        return testLinkFileMessage;
    }

    public void setTestLinkFileMessage(String testLinkFileMessage) {
        this.testLinkFileMessage = testLinkFileMessage;
    }
}
