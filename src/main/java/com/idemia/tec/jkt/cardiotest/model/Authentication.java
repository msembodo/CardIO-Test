package com.idemia.tec.jkt.cardiotest.model;

public class Authentication {

    private boolean includeDeltaTest;
    private boolean includeSqnMax;
    private String resLength;
    private String akaC1;
    private String akaC2;
    private String akaC3;
    private String akaC4;
    private String akaC5;
    private String akaRi;
    private String rand;
    private String sqn;
    private String sqnMax;
    private String delta;
    private String amf;
    private String ki;
    private String opc;
    private boolean comp1282;
    private boolean comp1283;
    private boolean milenage;
    private boolean isimAuth;
    private boolean gsmAlgo;

    public Authentication() {}

    public Authentication(boolean includeDeltaTest, boolean includeSqnMax, String resLength, String akaC1, String akaC2, String akaC3, String akaC4, String akaC5, String akaRi, String rand, String sqn, String sqnMax, String delta, String amf, String ki, String opc, boolean comp1282, boolean comp1283, boolean milenage, boolean isimAuth, boolean gsmAlgo) {
        this.includeDeltaTest = includeDeltaTest;
        this.includeSqnMax = includeSqnMax;
        this.resLength = resLength;
        this.akaC1 = akaC1;
        this.akaC2 = akaC2;
        this.akaC3 = akaC3;
        this.akaC4 = akaC4;
        this.akaC5 = akaC5;
        this.akaRi = akaRi;
        this.rand = rand;
        this.sqn = sqn;
        this.sqnMax = sqnMax;
        this.delta = delta;
        this.amf = amf;
        this.ki = ki;
        this.opc = opc;
        this.comp1282 = comp1282;
        this.comp1283 = comp1283;
        this.milenage = milenage;
        this.isimAuth = isimAuth;
        this.gsmAlgo = gsmAlgo;
    }

    public boolean isIncludeDeltaTest() {
        return includeDeltaTest;
    }

    public void setIncludeDeltaTest(boolean includeDeltaTest) {
        this.includeDeltaTest = includeDeltaTest;
    }

    public boolean isIncludeSqnMax() {
        return includeSqnMax;
    }

    public void setIncludeSqnMax(boolean includeSqnMax) {
        this.includeSqnMax = includeSqnMax;
    }

    public String getResLength() {
        return resLength;
    }

    public void setResLength(String resLength) {
        this.resLength = resLength;
    }

    public String getAkaC1() {
        return akaC1;
    }

    public void setAkaC1(String akaC1) {
        this.akaC1 = akaC1;
    }

    public String getAkaC2() {
        return akaC2;
    }

    public void setAkaC2(String akaC2) {
        this.akaC2 = akaC2;
    }

    public String getAkaC3() {
        return akaC3;
    }

    public void setAkaC3(String akaC3) {
        this.akaC3 = akaC3;
    }

    public String getAkaC4() {
        return akaC4;
    }

    public void setAkaC4(String akaC4) {
        this.akaC4 = akaC4;
    }

    public String getAkaC5() {
        return akaC5;
    }

    public void setAkaC5(String akaC5) {
        this.akaC5 = akaC5;
    }

    public String getAkaRi() {
        return akaRi;
    }

    public void setAkaRi(String akaRi) {
        this.akaRi = akaRi;
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getSqnMax() {
        return sqnMax;
    }

    public void setSqnMax(String sqnMax) {
        this.sqnMax = sqnMax;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }

    public String getAmf() {
        return amf;
    }

    public void setAmf(String amf) {
        this.amf = amf;
    }

    public String getKi() {
        return ki;
    }

    public void setKi(String ki) {
        this.ki = ki;
    }

    public String getOpc() {
        return opc;
    }

    public void setOpc(String opc) {
        this.opc = opc;
    }

    public boolean isComp1282() {
        return comp1282;
    }

    public void setComp1282(boolean comp1282) {
        this.comp1282 = comp1282;
    }

    public boolean isComp1283() {
        return comp1283;
    }

    public void setComp1283(boolean comp1283) {
        this.comp1283 = comp1283;
    }

    public boolean isMilenage() {
        return milenage;
    }

    public void setMilenage(boolean milenage) {
        this.milenage = milenage;
    }

    public boolean isIsimAuth() {
        return isimAuth;
    }

    public void setIsimAuth(boolean isimAuth) {
        this.isimAuth = isimAuth;
    }

    public boolean isGsmAlgo() {
        return gsmAlgo;
    }

    public void setGsmAlgo(boolean gsmAlgo) {
        this.gsmAlgo = gsmAlgo;
    }

}
