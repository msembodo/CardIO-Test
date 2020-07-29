package com.idemia.tec.jkt.cardiotest.model;

public class SmsUpdate {

    private String udhiFirstByte;
    private String scAddress;
    private String tpPid;
    private boolean useWhiteList;
    private String tpOa;
    private String porFormat;

    public SmsUpdate() {}

    public SmsUpdate(String udhiFirstByte, String scAddress, String tpPid, boolean useWhiteList, String tpOa, String porFormat) {
        this.udhiFirstByte = udhiFirstByte;
        this.scAddress = scAddress;
        this.tpPid = tpPid;
        this.useWhiteList = useWhiteList;
        this.tpOa = tpOa;
        this.porFormat = porFormat;
    }

    public String getUdhiFirstByte() {
        return udhiFirstByte;
    }

    public void setUdhiFirstByte(String udhiFirstByte) {
        this.udhiFirstByte = udhiFirstByte;
    }

    public String getScAddress() {
        return scAddress;
    }

    public void setScAddress(String scAddress) {
        this.scAddress = scAddress;
    }

    public String getTpPid() {
        return tpPid;
    }

    public void setTpPid(String tpPid) {
        this.tpPid = tpPid;
    }

    public boolean isUseWhiteList() {
        return useWhiteList;
    }

    public void setUseWhiteList(boolean useWhiteList) {
        this.useWhiteList = useWhiteList;
    }

    public String getTpOa() {
        return tpOa;
    }

    public void setTpOa(String tpOa) {
        this.tpOa = tpOa;
    }

    public String getPorFormat() {
        return porFormat;
    }

    public void setPorFormat(String porFormat) {
        this.porFormat = porFormat;
    }

}
