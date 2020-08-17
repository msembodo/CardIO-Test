package com.idemia.tec.jkt.cardiotest.model;

public class RfmGsm {

    private boolean includeRfmGsm;
    private boolean includeRfmGsmUpdateRecord;
    private boolean includeRfmGsmExpandedMode;
    private String tar;
    private String targetEf;
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
    private boolean testRfmGsmOk;
    private String testRfmGsmMessage;
    private boolean testRfmGsmUpdateRecordOk;
    private String testRfmGsmUpdateRecordMessage;
    private boolean testRfmGsmExpandedModeOk;
    private String testRfmGsmExpandedModeMessage;

    public RfmGsm() {}

    public RfmGsm(boolean includeRfmGsm, boolean includeRfmGsmUpdateRecord, boolean includeRfmGsmExpandedMode,
                  String tar, String targetEf, String targetEfBadCase, boolean fullAccess, String customTargetAcc,
                  String customTargetEf, String customTargetAccBadCase, String customTargetEfBadCase,
                  boolean useSpecificKeyset, SCP80Keyset cipheringKeyset, SCP80Keyset authKeyset,
                  MinimumSecurityLevel minimumSecurityLevel) {
        this.includeRfmGsm = includeRfmGsm;
        this.includeRfmGsmUpdateRecord = includeRfmGsmUpdateRecord;
        this.includeRfmGsmExpandedMode = includeRfmGsmExpandedMode;
        this.tar = tar;
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

    public boolean isIncludeRfmGsm() {
        return includeRfmGsm;
    }

    public void setIncludeRfmGsm(boolean includeRfmGsm) {
        this.includeRfmGsm = includeRfmGsm;
    }

    public boolean isIncludeRfmGsmUpdateRecord() {
        return includeRfmGsmUpdateRecord;
    }

    public void setIncludeRfmGsmUpdateRecord(boolean includeRfmGsmUpdateRecord) {
        this.includeRfmGsmUpdateRecord = includeRfmGsmUpdateRecord;
    }

    public boolean isIncludeRfmGsmExpandedMode() {
        return includeRfmGsmExpandedMode;
    }

    public void setIncludeRfmGsmExpandedMode(boolean includeRfmGsmExpandedMode) {
        this.includeRfmGsmExpandedMode = includeRfmGsmExpandedMode;
    }

    public String getTar() {
        return tar;
    }

    public void setTar(String tar) {
        this.tar = tar;
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

    public boolean isTestRfmGsmOk() {
        return testRfmGsmOk;
    }

    public void setTestRfmGsmOk(boolean testRfmGsmOk) {
        this.testRfmGsmOk = testRfmGsmOk;
    }

    public String getTestRfmGsmMessage() {
        return testRfmGsmMessage;
    }

    public void setTestRfmGsmMessage(String testRfmGsmMessage) {
        this.testRfmGsmMessage = testRfmGsmMessage;
    }

    public boolean isTestRfmGsmUpdateRecordOk() {
        return testRfmGsmUpdateRecordOk;
    }

    public void setTestRfmGsmUpdateRecordOk(boolean testRfmGsmUpdateRecordOk) {
        this.testRfmGsmUpdateRecordOk = testRfmGsmUpdateRecordOk;
    }

    public String getTestRfmGsmUpdateRecordMessage() {
        return testRfmGsmUpdateRecordMessage;
    }

    public void setTestRfmGsmUpdateRecordMessage(String testRfmGsmUpdateRecordMessage) {
        this.testRfmGsmUpdateRecordMessage = testRfmGsmUpdateRecordMessage;
    }

    public boolean isTestRfmGsmExpandedModeOk() {
        return testRfmGsmExpandedModeOk;
    }

    public void setTestRfmGsmExpandedModeOk(boolean testRfmGsmExpandedModeOk) {
        this.testRfmGsmExpandedModeOk = testRfmGsmExpandedModeOk;
    }

    public String getTestRfmGsmExpandedModeMessage() {
        return testRfmGsmExpandedModeMessage;
    }

    public void setTestRfmGsmExpandedModeMessage(String testRfmGsmExpandedModeMessage) {
        this.testRfmGsmExpandedModeMessage = testRfmGsmExpandedModeMessage;
    }

}
