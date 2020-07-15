package com.idemia.tec.jkt.cardiotest.model;

public class MinimumSecurityLevel {

    private String computedMsl;
    private boolean useCipher;
    private String cipherAlgo;
    private String authVerification;
    private String signingAlgo;
    private String porRequirement;
    private String porSecurity;
    private boolean cipherPor;
    private String counterChecking;

    public MinimumSecurityLevel() {}

    public MinimumSecurityLevel(boolean useCipher, String authVerification, String counterChecking) {
        this.useCipher = useCipher;
        this.authVerification = authVerification;
        this.counterChecking = counterChecking;
        computeMsl();
    }

    public void computeMsl() {
        int msl = 0;
        if (counterChecking.equals("Counter available no checking")) msl += 1 << 3;
        if (counterChecking.equals("Counter must be higher")) msl += 2 << 3;
        if (counterChecking.equals("Counter must be one higher")) msl += 3 << 3;
        if (useCipher) msl += 1 << 2;
        if (authVerification.equals("Redundancy Check")) msl += 1;
        if (authVerification.equals("Cryptographic Checksum")) msl += 2;
        if (authVerification.equals("Digital Signature")) msl += 3;
//        return String.format("%02X", msl);
        computedMsl = String.format("%02X", msl);
    }

    public String getComputedMsl() {
        return computedMsl;
    }

    public void setComputedMsl(String computedMsl) {
        this.computedMsl = computedMsl;
    }

    public boolean isUseCipher() {
        return useCipher;
    }

    public void setUseCipher(boolean useCipher) {
        this.useCipher = useCipher;
    }

    public String getCipherAlgo() {
        return cipherAlgo;
    }

    public void setCipherAlgo(String cipherAlgo) {
        this.cipherAlgo = cipherAlgo;
    }

    public String getAuthVerification() {
        return authVerification;
    }

    public void setAuthVerification(String authVerification) {
        this.authVerification = authVerification;
    }

    public String getSigningAlgo() {
        return signingAlgo;
    }

    public void setSigningAlgo(String signingAlgo) {
        this.signingAlgo = signingAlgo;
    }

    public String getPorSecurity() {
        return porSecurity;
    }

    public void setPorSecurity(String porSecurity) {
        this.porSecurity = porSecurity;
    }

    public String getCounterChecking() {
        return counterChecking;
    }

    public void setCounterChecking(String counterChecking) {
        this.counterChecking = counterChecking;
    }

    public String getPorRequirement() {
        return porRequirement;
    }

    public void setPorRequirement(String porRequirement) {
        this.porRequirement = porRequirement;
    }

    public boolean isCipherPor() {
        return cipherPor;
    }

    public void setCipherPor(boolean cipherPor) {
        this.cipherPor = cipherPor;
    }

}
