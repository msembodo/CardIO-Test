package com.idemia.tec.jkt.cardiotest.model;

public class RfmUsim {

    private boolean includeRfmUsim;
    private boolean includeRfmUsimUpdateRecord;
    private boolean includeRfmUsimExpandedMode;
    private String tar;
    private String targetEf;
    private String targetEfBadCase;
    private boolean fullAccess;
    private String targetEfAlw;
    private String targetEfPin1;
    private String targetEfAdm1;
    private SCP80Keyset cipheringKeyset;
    private SCP80Keyset authKeyset;
    private MinimumSecurityLevel minimumSecurityLevel;

    public RfmUsim() {}

    public RfmUsim(boolean includeRfmUsim, boolean includeRfmUsimUpdateRecord, boolean includeRfmUsimExpandedMode,
                   String tar, String targetEf, String targetEfBadCase, boolean fullAccess, String targetEfAlw,
                   String targetEfPin1, String targetEfAdm1, SCP80Keyset cipheringKeyset, SCP80Keyset authKeyset,
                   MinimumSecurityLevel minimumSecurityLevel) {
        this.includeRfmUsim = includeRfmUsim;
        this.includeRfmUsimUpdateRecord = includeRfmUsimUpdateRecord;
        this.includeRfmUsimExpandedMode = includeRfmUsimExpandedMode;
        this.tar = tar;
        this.targetEf = targetEf;
        this.targetEfBadCase = targetEfBadCase;
        this.fullAccess = fullAccess;
        this.targetEfAlw = targetEfAlw;
        this.targetEfPin1 = targetEfPin1;
        this.targetEfAdm1 = targetEfAdm1;
        this.cipheringKeyset = cipheringKeyset;
        this.authKeyset = authKeyset;
        this.minimumSecurityLevel = minimumSecurityLevel;
    }

    public boolean isIncludeRfmUsim() {
        return includeRfmUsim;
    }

    public void setIncludeRfmUsim(boolean includeRfmUsim) {
        this.includeRfmUsim = includeRfmUsim;
    }

    public boolean isIncludeRfmUsimUpdateRecord() {
        return includeRfmUsimUpdateRecord;
    }

    public void setIncludeRfmUsimUpdateRecord(boolean includeRfmUsimUpdateRecord) {
        this.includeRfmUsimUpdateRecord = includeRfmUsimUpdateRecord;
    }

    public boolean isIncludeRfmUsimExpandedMode() {
        return includeRfmUsimExpandedMode;
    }

    public void setIncludeRfmUsimExpandedMode(boolean includeRfmUsimExpandedMode) {
        this.includeRfmUsimExpandedMode = includeRfmUsimExpandedMode;
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

    public String getTargetEfAlw() {
        return targetEfAlw;
    }

    public void setTargetEfAlw(String targetEfAlw) {
        this.targetEfAlw = targetEfAlw;
    }

    public String getTargetEfPin1() {
        return targetEfPin1;
    }

    public void setTargetEfPin1(String targetEfPin1) {
        this.targetEfPin1 = targetEfPin1;
    }

    public String getTargetEfAdm1() {
        return targetEfAdm1;
    }

    public void setTargetEfAdm1(String targetEfAdm1) {
        this.targetEfAdm1 = targetEfAdm1;
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

}
