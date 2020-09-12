package com.idemia.tec.jkt.cardiotest.model;

public class RfmIsim {
    
    private boolean includeRfmIsim;
    private boolean includeRfmIsimUpdateRecord;
    private boolean includeRfmIsimExpandedMode;
    private String tar;
    private String targetEf;
    private String targetEfBadCase;
    private boolean fullAccess;
    private String customTargetAcc;
    private String customTargetEf;
    private String customTargetEfIsc1;
    private String customTargetEfIsc2;
    private String customTargetEfIsc3;
    private String customTargetEfIsc4;
    private String customTargetEfGPin1;
    private String customTargetEfLPin1;
    private String customTargetAccBadCase;
    private String customTargetEfBadCase;
    private boolean useSpecificKeyset;
    private AccessDomain accessDomain;
    private SCP80Keyset cipheringKeyset;
    private SCP80Keyset authKeyset;
    private MinimumSecurityLevel minimumSecurityLevel;
    private boolean testRfmIsimOk;
    private String testRfmIsimMessage;
    private boolean testRfmIsimUpdateRecordOk;
    private String testRfmIsimUpdateRecordMessage;
    private boolean testRfmIsimExpandedModeOk;
    private String testRfmIsimExpandedModeMessage;

    public RfmIsim() {}

    public RfmIsim(boolean includeRfmIsim, boolean includeRfmIsimUpdateRecord, boolean includeRfmIsimExpandedMode,
                   String tar, String targetEf, String targetEfBadCase, boolean fullAccess, String customTargetAcc,
                   String customTargetEf, String customTargetEfIsc1, String customTargetEfIsc2, String customTargetEfIsc3, String customTargetEfGPin1, String customTargetEfLPin1, String customTargetAccBadCase, String customTargetEfBadCase,
                   boolean useSpecificKeyset, AccessDomain accessDomain, SCP80Keyset cipheringKeyset, SCP80Keyset authKeyset,
                   MinimumSecurityLevel minimumSecurityLevel) {
        this.includeRfmIsim = includeRfmIsim;
        this.includeRfmIsimUpdateRecord = includeRfmIsimUpdateRecord;
        this.includeRfmIsimExpandedMode = includeRfmIsimExpandedMode;
        this.tar = tar;
        this.targetEf = targetEf;
        this.targetEfBadCase = targetEfBadCase;
        this.fullAccess = fullAccess;
        this.customTargetAcc = customTargetAcc;
        this.customTargetEf = customTargetEf;
        this.customTargetEfIsc1 = customTargetEfIsc1;
        this.customTargetEfIsc2 = customTargetEfIsc2;
        this.customTargetEfIsc3 = customTargetEfIsc3;
        this.customTargetEfGPin1 = customTargetEfGPin1;
        this.customTargetEfLPin1 = customTargetEfLPin1;
        this.customTargetAccBadCase = customTargetAccBadCase;
        this.customTargetEfBadCase = customTargetEfBadCase;
        this.useSpecificKeyset = useSpecificKeyset;
        this.accessDomain = accessDomain;
        this.cipheringKeyset = cipheringKeyset;
        this.authKeyset = authKeyset;
        this.minimumSecurityLevel = minimumSecurityLevel;
    }

    public boolean isIncludeRfmIsim() {
        return includeRfmIsim;
    }

    public void setIncludeRfmIsim(boolean includeRfmIsim) {
        this.includeRfmIsim = includeRfmIsim;
    }

    public boolean isIncludeRfmIsimUpdateRecord() {
        return includeRfmIsimUpdateRecord;
    }

    public void setIncludeRfmIsimUpdateRecord(boolean includeRfmIsimUpdateRecord) {
        this.includeRfmIsimUpdateRecord = includeRfmIsimUpdateRecord;
    }

    public boolean isIncludeRfmIsimExpandedMode() {
        return includeRfmIsimExpandedMode;
    }

    public void setIncludeRfmIsimExpandedMode(boolean includeRfmIsimExpandedMode) {
        this.includeRfmIsimExpandedMode = includeRfmIsimExpandedMode;
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

    public boolean isTestRfmIsimOk() {
        return testRfmIsimOk;
    }

    public void setTestRfmIsimOk(boolean testRfmIsimOk) {
        this.testRfmIsimOk = testRfmIsimOk;
    }

    public String getTestRfmIsimMessage() {
        return testRfmIsimMessage;
    }

    public void setTestRfmIsimMessage(String testRfmIsimMessage) {
        this.testRfmIsimMessage = testRfmIsimMessage;
    }

    public boolean isTestRfmIsimUpdateRecordOk() {
        return testRfmIsimUpdateRecordOk;
    }

    public void setTestRfmIsimUpdateRecordOk(boolean testRfmIsimUpdateRecordOk) {
        this.testRfmIsimUpdateRecordOk = testRfmIsimUpdateRecordOk;
    }

    public String getTestRfmIsimUpdateRecordMessage() {
        return testRfmIsimUpdateRecordMessage;
    }

    public void setTestRfmIsimUpdateRecordMessage(String testRfmIsimUpdateRecordMessage) {
        this.testRfmIsimUpdateRecordMessage = testRfmIsimUpdateRecordMessage;
    }

    public boolean isTestRfmIsimExpandedModeOk() {
        return testRfmIsimExpandedModeOk;
    }

    public void setTestRfmIsimExpandedModeOk(boolean testRfmIsimExpandedModeOk) {
        this.testRfmIsimExpandedModeOk = testRfmIsimExpandedModeOk;
    }

    public String getTestRfmIsimExpandedModeMessage() {
        return testRfmIsimExpandedModeMessage;
    }

    public void setTestRfmIsimExpandedModeMessage(String testRfmIsimExpandedModeMessage) {
        this.testRfmIsimExpandedModeMessage = testRfmIsimExpandedModeMessage;
    }

    public AccessDomain getRfmIsimAccessDomain() {
        return accessDomain;
    }

    public void setRfmIsimAccessDomain(AccessDomain accessDomain) {
        this.accessDomain = accessDomain;
    }

}
