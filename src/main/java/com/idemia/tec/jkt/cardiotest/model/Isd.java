package com.idemia.tec.jkt.cardiotest.model;

public class Isd {

    private String methodForGpCommand;
    private String scpMode;
    private String scLevel;
    private boolean securedState;
    private String cardManagerEnc;
    private String cardManagerMac;
    private String cardManagerKey;
    private String cardManagerPin;


    public Isd() {}

    public Isd(String methodForGpCommand, String scpMode, String scLevel, boolean securedState,
               String cardManagerEnc, String cardManagerMac, String cardManagerKey, String cardManagerPin) {
        this.methodForGpCommand = methodForGpCommand;
        this.scpMode = scpMode;
        this.scLevel = scLevel;
        this.securedState = securedState;
        this.cardManagerEnc = cardManagerEnc;
        this.cardManagerMac = cardManagerMac;
        this.cardManagerKey = cardManagerKey;
        this.cardManagerPin = cardManagerPin;
    }

    public String getMethodForGpCommand() {
        return methodForGpCommand;
    }

    public void setMethodForGpCommand(String methodForGpCommand) {
        this.methodForGpCommand = methodForGpCommand;
    }

    public String getScpMode() {
        return scpMode;
    }

    public void setScpMode(String scpMode) {
        this.scpMode = scpMode;
    }

    public String getScLevel() {
        return scLevel;
    }

    public void setScLevel(String scLevel) {
        this.scLevel = scLevel;
    }

    public boolean getSecuredSate() {
        return securedState;
    }

    public void setSecuredState(boolean securedState) {
        this.securedState = securedState;
    }

    public String getCardManagerEnc() {
        return cardManagerEnc;
    }

    public void setCardManagerEnc(String cardManagerEnc) {
        this.cardManagerEnc = cardManagerEnc;
    }

    public String getCardManagerMac() {
        return cardManagerMac;
    }

    public void setCardManagerMac(String cardManagerMac) {
        this.cardManagerMac = cardManagerMac;
    }

    public String getCardManagerKey() {
        return cardManagerKey;
    }

    public void setCardManagerKey(String cardManagerKey) {
        this.cardManagerKey = cardManagerKey;
    }

    public String getCardManagerPin() {
        return cardManagerPin;
    }

    public void setCardManagerPin(String cardManagerPin) {
        this.cardManagerPin = cardManagerPin;
    }

}
