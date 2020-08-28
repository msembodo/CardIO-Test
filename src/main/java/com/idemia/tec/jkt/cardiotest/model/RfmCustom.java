package com.idemia.tec.jkt.cardiotest.model;

public class RfmCustom {

    private boolean includeRfmCustom;
    private boolean includeRfmCustomUpdateRecord;
    private boolean includeRfmCustomExpandedMode;
    private String tar;
    private String targetEf;
    private String targetDf;
    private String customRfmDesc; // TODO
    private String targetEfBadCase;
    private boolean fullAccess;
    private String customTargetAcc;
    private String customTargetEf;
    private String customTargetAccBadCase;
    private String customTargetEfBadCase;
    private boolean useSpecificKeyset;
    private SCP80Keyset cipheringKeyset;
    private SCP80Keyset authKeyset;
    private MinimumSecurityLevel minimumSecurityLevel;
    private boolean testRfmCustomOk;
    private String testRfmCustomMessage;
    private boolean testRfmCustomUpdateRecordOk;
    private String testRfmCustomUpdateRecordMessage;
    private boolean testRfmCustomExpandedModeOk;
    private String testRfmCustomExpandedModeMessage;

    public RfmCustom() {}

    public RfmCustom(boolean includeRfmCustom, boolean includeRfmCustomUpdateRecord, boolean includeRfmCustomExpandedMode,
                     String tar, String targetEf,String targetDf,String customRfmDesc, String targetEfBadCase, boolean fullAccess, String customTargetAcc,
                     String customTargetEf, String customTargetAccBadCase, String customTargetEfBadCase,
                     boolean useSpecificKeyset, SCP80Keyset cipheringKeyset, SCP80Keyset authKeyset,
                     MinimumSecurityLevel minimumSecurityLevel) {
        this.includeRfmCustom = includeRfmCustom;
        this.includeRfmCustomUpdateRecord = includeRfmCustomUpdateRecord;
        this.includeRfmCustomExpandedMode = includeRfmCustomExpandedMode;
        this.tar = tar;
        this.targetDf = targetDf;
        this.customRfmDesc = customRfmDesc;
        this.targetEf = targetEf;
        this.targetEfBadCase = targetEfBadCase;
        this.fullAccess = fullAccess;
        this.customTargetAcc = customTargetAcc;
        this.customTargetEf = customTargetEf;
        this.customTargetAccBadCase = customTargetAccBadCase;
        this.customTargetEfBadCase = customTargetEfBadCase;
        this.useSpecificKeyset = useSpecificKeyset;
        this.cipheringKeyset = cipheringKeyset;
        this.authKeyset = authKeyset;
        this.minimumSecurityLevel = minimumSecurityLevel;
    }

    public boolean isIncludeRfmCustom() {
        return includeRfmCustom;
    }

    public void setIncludeRfmCustom(boolean includeRfmCustom) {
        this.includeRfmCustom = includeRfmCustom;
    }

    public boolean isIncludeRfmCustomUpdateRecord() {
        return includeRfmCustomUpdateRecord;
    }

    public void setIncludeRfmCustomUpdateRecord(boolean includeRfmCustomUpdateRecord) {
        this.includeRfmCustomUpdateRecord = includeRfmCustomUpdateRecord;
    }

    public boolean isIncludeRfmCustomExpandedMode() {
        return includeRfmCustomExpandedMode;
    }

    public void setIncludeRfmCustomExpandedMode(boolean includeRfmCustomExpandedMode) {
        this.includeRfmCustomExpandedMode = includeRfmCustomExpandedMode;
    }

    public String getTar() {
        return tar;
    }

    public void setTar(String tar) {
        this.tar = tar;
    }




    public String getTargetDf() {
        return targetDf;
    }

    public void setTargetDf(String targetDf) {
        this.targetDf = targetDf;
    }




    public String getCustomRfmDesc() {
        return customRfmDesc;
    }

    public void setCustomRfmDesc(String customRfmDesc) {
        this.customRfmDesc = customRfmDesc;
    }




    public String getTargetEf() {
        return targetEf;
    }

    public void setTargetEf(String targetEf) {
        this.targetEf = targetEf;
    }

    public String getTargetEfBadCase() {
        return targetEfBadCase;
    }

    public void setTargetEfBadCase(String targetEfBadCase) {
        this.targetEfBadCase = targetEfBadCase;
    }

    public boolean isFullAccess() {
        return fullAccess;
    }

    public void setFullAccess(boolean fullAccess) {
        this.fullAccess = fullAccess;
    }

    public String getCustomTargetAcc() {
        return customTargetAcc;
    }

    public void setCustomTargetAcc(String customTargetAcc) {
        this.customTargetAcc = customTargetAcc;
    }

    public String getCustomTargetEf() {
        return customTargetEf;
    }

    public void setCustomTargetEf(String customTargetEf) {
        this.customTargetEf = customTargetEf;
    }

    public String getCustomTargetAccBadCase() {
        return customTargetAccBadCase;
    }

    public void setCustomTargetAccBadCase(String customTargetAccBadCase) {
        this.customTargetAccBadCase = customTargetAccBadCase;
    }

    public String getCustomTargetEfBadCase() {
        return customTargetEfBadCase;
    }

    public void setCustomTargetEfBadCase(String customTargetEfBadCase) {
        this.customTargetEfBadCase = customTargetEfBadCase;
    }

    public boolean isUseSpecificKeyset() {
        return useSpecificKeyset;
    }

    public void setUseSpecificKeyset(boolean useSpecificKeyset) {
        this.useSpecificKeyset = useSpecificKeyset;
    }

    public SCP80Keyset getCipheringKeyset() {
        return cipheringKeyset;
    }

    public void setCipheringKeyset(SCP80Keyset cipheringKeyset) {
        this.cipheringKeyset = cipheringKeyset;
    }

    public SCP80Keyset getAuthKeyset() {
        return authKeyset;
    }

    public void setAuthKeyset(SCP80Keyset authKeyset) {
        this.authKeyset = authKeyset;
    }

    public MinimumSecurityLevel getMinimumSecurityLevel() {
        return minimumSecurityLevel;
    }

    public void setMinimumSecurityLevel(MinimumSecurityLevel minimumSecurityLevel) {
        this.minimumSecurityLevel = minimumSecurityLevel;
    }

    public boolean isTestRfmCustomOk() {
        return testRfmCustomOk;
    }

    public void setTestRfmCustomOk(boolean testRfmCustomOk) {
        this.testRfmCustomOk = testRfmCustomOk;
    }

    public String getTestRfmCustomMessage() {
        return testRfmCustomMessage;
    }

    public void setTestRfmCustomMessage(String testRfmCustomMessage) {
        this.testRfmCustomMessage = testRfmCustomMessage;
    }

    public boolean isTestRfmCustomUpdateRecordOk() {
        return testRfmCustomUpdateRecordOk;
    }

    public void setTestRfmCustomUpdateRecordOk(boolean testRfmCustomUpdateRecordOk) {
        this.testRfmCustomUpdateRecordOk = testRfmCustomUpdateRecordOk;
    }

    public String getTestRfmCustomUpdateRecordMessage() {
        return testRfmCustomUpdateRecordMessage;
    }

    public void setTestRfmCustomUpdateRecordMessage(String testRfmCustomUpdateRecordMessage) {
        this.testRfmCustomUpdateRecordMessage = testRfmCustomUpdateRecordMessage;
    }

    public boolean isTestRfmCustomExpandedModeOk() {
        return testRfmCustomExpandedModeOk;
    }

    public void setTestRfmCustomExpandedModeOk(boolean testRfmCustomExpandedModeOk) {
        this.testRfmCustomExpandedModeOk = testRfmCustomExpandedModeOk;
    }

    public String getTestRfmCustomExpandedModeMessage() {
        return testRfmCustomExpandedModeMessage;
    }

    public void setTestRfmCustomExpandedModeMessage(String testRfmCustomExpandedModeMessage) {
        this.testRfmCustomExpandedModeMessage = testRfmCustomExpandedModeMessage;
    }

}
