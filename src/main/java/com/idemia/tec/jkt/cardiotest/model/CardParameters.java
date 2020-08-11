package com.idemia.tec.jkt.cardiotest.model;

public class CardParameters {

    private String cardManagerAid;
    private String usimAid;
    private String dfUsim;
    private String dfGsm;
    private String dfGsmAccess;
    private String dfTelecom;
    private String isimAid;
    private String dfIsim;
    private String csimAid;
    private String dfCsim;

    public CardParameters() {}

    public CardParameters(String cardManagerAid, String usimAid, String dfUsim, String dfGsm, String dfGsmAccess, String dfTelecom, String isimAid, String dfIsim, String csimAid, String dfCsim) {
        this.cardManagerAid = cardManagerAid;
        this.usimAid = usimAid;
        this.dfUsim = dfUsim;
        this.dfGsm = dfGsm;
        this.dfGsmAccess = dfGsmAccess;
        this.dfTelecom = dfTelecom;
        this.isimAid = isimAid;
        this.dfIsim = dfIsim;
        this.csimAid = csimAid;
        this.dfCsim = dfCsim;
    }

    public String getCardManagerAid() {
        return cardManagerAid;
    }

    public void setCardManagerAid(String cardManagerAid) {
        this.cardManagerAid = cardManagerAid;
    }

    public String getUsimAid() {
        return usimAid;
    }

    public void setUsimAid(String usimAid) {
        this.usimAid = usimAid;
    }

    public String getDfUsim() {
        return dfUsim;
    }

    public void setDfUsim(String dfUsim) {
        this.dfUsim = dfUsim;
    }

    public String getDfGsm() {
        return dfGsm;
    }

    public void setDfGsm(String dfGsm) {
        this.dfGsm = dfGsm;
    }

    public String getDfGsmAccess() {
        return dfGsmAccess;
    }

    public void setDfGsmAccess(String dfGsmAccess) {
        this.dfGsmAccess = dfGsmAccess;
    }

    public String getDfTelecom() {
        return dfTelecom;
    }

    public void setDfTelecom(String dfTelecom) {
        this.dfTelecom = dfTelecom;
    }

    public String getIsimAid() {
        return isimAid;
    }

    public void setIsimAid(String isimAid) {
        this.isimAid = isimAid;
    }

    public String getDfIsim() {
        return dfIsim;
    }

    public void setDfIsim(String dfIsim) {
        this.dfIsim = dfIsim;
    }

    public String getCsimAid() {
        return csimAid;
    }

    public void setCsimAid(String csimAid) {
        this.csimAid = csimAid;
    }

    public String getDfCsim() {
        return dfCsim;
    }

    public void setDfCsim(String dfCsim) {
        this.dfCsim = dfCsim;
    }

}
