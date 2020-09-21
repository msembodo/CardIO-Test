package com.idemia.tec.jkt.cardiotest.model;

public class RfmCustom {

    private boolean includeRfmCustom;
    private boolean includeRfmCustomUpdateRecord;
    private boolean includeRfmCustomExpandedMode;
    private String tar;
    private String targetEf;
    private String targetDf;
    private String customRfmDesc;
    private boolean fullAccess;
    private String customTargetAcc;
    private String customTargetEf;

    private String customTargetEfAlw;
    private String customTargetEfIsc1;
    private String customTargetEfIsc2;
    private String customTargetEfIsc3;
    private String customTargetEfIsc4;
    private String customTargetEfGPin1;
    private String customTargetEfLPin1;
    private String customTargetEfBadCaseAlw;
    private String customTargetEfBadCaseIsc1;
    private String customTargetEfBadCaseIsc2;
    private String customTargetEfBadCaseIsc3;
    private String customTargetEfBadCaseIsc4;
    private String customTargetEfBadCaseGPin1;
    private String customTargetEfBadCaseLPin1;

    private String customTargetAccBadCase;
    private String customTargetEfBadCase;
    private boolean useSpecificKeyset;

    private AccessDomain accessDomain;
    private AccessDomain badCaseAccessDomain;


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

    public RfmCustom(boolean includeRfmCustom, boolean includeRfmCustomUpdateRecord, boolean includeRfmCustomExpandedMode, String tar, String targetEf, String targetDf, String customRfmDesc, boolean fullAccess, String customTargetAcc, String customTargetEf, String customTargetEfAlw, String customTargetEfIsc1, String customTargetEfIsc2, String customTargetEfIsc3, String customTargetEfIsc4, String customTargetEfGPin1, String customTargetEfLPin1, String customTargetEfBadCaseAlw, String customTargetEfBadCaseIsc1, String customTargetEfBadCaseIsc2, String customTargetEfBadCaseIsc3, String customTargetEfBadCaseIsc4, String customTargetEfBadCaseGPin1, String customTargetEfBadCaseLPin1, String customTargetAccBadCase, String customTargetEfBadCase, boolean useSpecificKeyset, AccessDomain accessDomain, AccessDomain badCaseAccessDomain, SCP80Keyset cipheringKeyset, SCP80Keyset authKeyset, MinimumSecurityLevel minimumSecurityLevel, boolean testRfmCustomOk, String testRfmCustomMessage, boolean testRfmCustomUpdateRecordOk, String testRfmCustomUpdateRecordMessage, boolean testRfmCustomExpandedModeOk, String testRfmCustomExpandedModeMessage) {
        this.includeRfmCustom = includeRfmCustom;
        this.includeRfmCustomUpdateRecord = includeRfmCustomUpdateRecord;
        this.includeRfmCustomExpandedMode = includeRfmCustomExpandedMode;
        this.tar = tar;
        this.targetEf = targetEf;
        this.targetDf = targetDf;
        this.customRfmDesc = customRfmDesc;
        this.fullAccess = fullAccess;
        this.customTargetAcc = customTargetAcc;
        this.customTargetEf = customTargetEf;
        this.customTargetEfAlw = customTargetEfAlw;
        this.customTargetEfIsc1 = customTargetEfIsc1;
        this.customTargetEfIsc2 = customTargetEfIsc2;
        this.customTargetEfIsc3 = customTargetEfIsc3;
        this.customTargetEfIsc4 = customTargetEfIsc4;
        this.customTargetEfGPin1 = customTargetEfGPin1;
        this.customTargetEfLPin1 = customTargetEfLPin1;
        this.customTargetEfBadCaseAlw = customTargetEfBadCaseAlw;
        this.customTargetEfBadCaseIsc1 = customTargetEfBadCaseIsc1;
        this.customTargetEfBadCaseIsc2 = customTargetEfBadCaseIsc2;
        this.customTargetEfBadCaseIsc3 = customTargetEfBadCaseIsc3;
        this.customTargetEfBadCaseIsc4 = customTargetEfBadCaseIsc4;
        this.customTargetEfBadCaseGPin1 = customTargetEfBadCaseGPin1;
        this.customTargetEfBadCaseLPin1 = customTargetEfBadCaseLPin1;
        this.customTargetAccBadCase = customTargetAccBadCase;
        this.customTargetEfBadCase = customTargetEfBadCase;
        this.useSpecificKeyset = useSpecificKeyset;
        this.accessDomain = accessDomain;
        this.badCaseAccessDomain = badCaseAccessDomain;
        this.cipheringKeyset = cipheringKeyset;
        this.authKeyset = authKeyset;
        this.minimumSecurityLevel = minimumSecurityLevel;
        this.testRfmCustomOk = testRfmCustomOk;
        this.testRfmCustomMessage = testRfmCustomMessage;
        this.testRfmCustomUpdateRecordOk = testRfmCustomUpdateRecordOk;
        this.testRfmCustomUpdateRecordMessage = testRfmCustomUpdateRecordMessage;
        this.testRfmCustomExpandedModeOk = testRfmCustomExpandedModeOk;
        this.testRfmCustomExpandedModeMessage = testRfmCustomExpandedModeMessage;
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


    public String getCustomTargetEfAlw() {
        return customTargetEfAlw;
    }

    public void setCustomTargetEfAlw(String customTargetEfAlw) {
        this.customTargetEfAlw = customTargetEfAlw;
    }

    public String getCustomTargetEfIsc1() {
        return customTargetEfIsc1;
    }

    public void setCustomTargetEfIsc1(String customTargetEfIsc1) {
        this.customTargetEfIsc1 = customTargetEfIsc1;
    }

    public String getCustomTargetEfIsc2() {
        return customTargetEfIsc2;
    }

    public void setCustomTargetEfIsc2(String customTargetEfIsc2) {
        this.customTargetEfIsc2 = customTargetEfIsc2;
    }

    public String getCustomTargetEfIsc3() {
        return customTargetEfIsc3;
    }

    public void setCustomTargetEfIsc3(String customTargetEfIsc3) {
        this.customTargetEfIsc3 = customTargetEfIsc3;
    }

    public String getCustomTargetEfIsc4() {
        return customTargetEfIsc4;
    }

    public void setCustomTargetEfIsc4(String customTargetEfIsc4) {
        this.customTargetEfIsc4 = customTargetEfIsc4;
    }

    public String getCustomTargetEfGPin1() {
        return customTargetEfGPin1;
    }

    public void setCustomTargetEfGPin1(String customTargetEfGPin1) {
        this.customTargetEfGPin1 = customTargetEfGPin1;
    }

    public String getCustomTargetEfLPin1() {
        return customTargetEfLPin1;
    }

    public void setCustomTargetEfLPin1(String customTargetEfLPin1) {
        this.customTargetEfLPin1 = customTargetEfLPin1;
    }

    public String getCustomTargetEfBadCaseAlw() {
        return customTargetEfBadCaseAlw;
    }

    public void setCustomTargetEfBadCaseAlw(String customTargetEfBadCaseAlw) {
        this.customTargetEfBadCaseAlw = customTargetEfBadCaseAlw;
    }

    public String getCustomTargetEfBadCaseIsc1() {
        return customTargetEfBadCaseIsc1;
    }

    public void setCustomTargetEfBadCaseIsc1(String customTargetEfBadCaseIsc1) {
        this.customTargetEfBadCaseIsc1 = customTargetEfBadCaseIsc1;
    }

    public String getCustomTargetEfBadCaseIsc2() {
        return customTargetEfBadCaseIsc2;
    }

    public void setCustomTargetEfBadCaseIsc2(String customTargetEfBadCaseIsc2) {
        this.customTargetEfBadCaseIsc2 = customTargetEfBadCaseIsc2;
    }

    public String getCustomTargetEfBadCaseIsc3() {
        return customTargetEfBadCaseIsc3;
    }

    public void setCustomTargetEfBadCaseIsc3(String customTargetEfBadCaseIsc3) {
        this.customTargetEfBadCaseIsc3 = customTargetEfBadCaseIsc3;
    }

    public String getCustomTargetEfBadCaseIsc4() {
        return customTargetEfBadCaseIsc4;
    }

    public void setCustomTargetEfBadCaseIsc4(String customTargetEfBadCaseIsc4) {
        this.customTargetEfBadCaseIsc4 = customTargetEfBadCaseIsc4;
    }

    public String getCustomTargetEfBadCaseGPin1() {
        return customTargetEfBadCaseGPin1;
    }

    public void setCustomTargetEfBadCaseGPin1(String customTargetEfBadCaseGPin1) {
        this.customTargetEfBadCaseGPin1 = customTargetEfBadCaseGPin1;
    }

    public String getCustomTargetEfBadCaseLPin1() {
        return customTargetEfBadCaseLPin1;
    }

    public void setCustomTargetEfBadCaseLPin1(String customTargetEfBadCaseLPin1) {
        this.customTargetEfBadCaseLPin1 = customTargetEfBadCaseLPin1;
    }

    public AccessDomain getAccessDomain() {
        return accessDomain;
    }

    public void setAccessDomain(AccessDomain accessDomain) {
        this.accessDomain = accessDomain;
    }

    public AccessDomain getBadCaseAccessDomain() {
        return badCaseAccessDomain;
    }

    public void setBadCaseAccessDomain(AccessDomain badCaseAccessDomain) {
        this.badCaseAccessDomain = badCaseAccessDomain;
    }

    public AccessDomain getRfmCustomAccessDomain() {
        return accessDomain;
    }

    public void setRfmCustomAccessDomain(AccessDomain accessDomain) {
        this.accessDomain = accessDomain;
    }

    public AccessDomain getRfmCustomBadCaseAccessDomain() {
        return badCaseAccessDomain;
    }

    public void setRfmCustomBadCaseAccessDomain(AccessDomain badCaseAccessDomain) {
        this.badCaseAccessDomain = badCaseAccessDomain;
    }

}
