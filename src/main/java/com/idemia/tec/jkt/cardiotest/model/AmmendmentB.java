package com.idemia.tec.jkt.cardiotest.model;

public class AmmendmentB {

    private String sdTar;
    private SCP80Keyset scp80CipherKeyset;
    private SCP80Keyset scp80AuthKeyset;
    private MinimumSecurityLevel sdMsl;
    private ConnectionParameters connectionParameters;

    public AmmendmentB() {}

    public AmmendmentB(String sdTar, SCP80Keyset scp80CipherKeyset, SCP80Keyset scp80AuthKeyset, MinimumSecurityLevel sdMsl, ConnectionParameters connectionParameters) {
        this.sdTar = sdTar;
        this.scp80CipherKeyset = scp80CipherKeyset;
        this.scp80AuthKeyset = scp80AuthKeyset;
        this.sdMsl = sdMsl;
        this.connectionParameters = connectionParameters;
    }

    public String getSdTar() {
        return sdTar;
    }

    public void setSdTar(String sdTar) {
        this.sdTar = sdTar;
    }

    public SCP80Keyset getScp80CipherKeyset() {
        return scp80CipherKeyset;
    }

    public void setScp80CipherKeyset(SCP80Keyset scp80CipherKeyset) {
        this.scp80CipherKeyset = scp80CipherKeyset;
    }

    public SCP80Keyset getScp80AuthKeyset() {
        return scp80AuthKeyset;
    }

    public void setScp80AuthKeyset(SCP80Keyset scp80AuthKeyset) {
        this.scp80AuthKeyset = scp80AuthKeyset;
    }

    public MinimumSecurityLevel getSdMsl() {
        return sdMsl;
    }

    public void setSdMsl(MinimumSecurityLevel sdMsl) {
        this.sdMsl = sdMsl;
    }

    public ConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }

    public void setConnectionParameters(ConnectionParameters connectionParameters) {
        this.connectionParameters = connectionParameters;
    }

}
