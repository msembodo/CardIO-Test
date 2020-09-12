package com.idemia.tec.jkt.cardiotest.model;

public class CardioUser {

    private String userName;
    private String securityToken;
    private String domain;

    public CardioUser() {}

    public CardioUser(String userName, String securityToken, String domain) {
        this.userName = userName;
        this.securityToken = securityToken;
        this.domain = domain;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getSecurityToken() { return securityToken; }

    public void setSecurityToken(String securityToken) { this.securityToken = securityToken; }

    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }

}
