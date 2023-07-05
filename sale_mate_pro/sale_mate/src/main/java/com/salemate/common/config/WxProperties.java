package com.salemate.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component( "corpWxConfig" )
@ConfigurationProperties(prefix = "wx")
public class WxProperties {
    private String suiteId;
    private String suiteSecret;
    private List<String> trustHostList;
    private String upload;
    private String callDataToken;
    private String callDataEncodingAesKey;
    private String loginToken;
    private String loginEncodingAESKey;
    private String loginSuiteID;
    private String loginSecret;

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getLoginEncodingAESKey() {
        return loginEncodingAESKey;
    }

    public void setLoginEncodingAESKey(String loginEncodingAESKey) {
        this.loginEncodingAESKey = loginEncodingAESKey;
    }

    public String getLoginSuiteID() {
        return loginSuiteID;
    }

    public void setLoginSuiteID(String loginSuiteID) {
        this.loginSuiteID = loginSuiteID;
    }

    public String getLoginSecret() {
        return loginSecret;
    }

    public void setLoginSecret(String loginSecret) {
        this.loginSecret = loginSecret;
    }

    public String getCallDataToken() {
        return callDataToken;
    }

    public void setCallDataToken(String callDataToken) {
        this.callDataToken = callDataToken;
    }

    public String getCallDataEncodingAesKey() {
        return callDataEncodingAesKey;
    }

    public void setCallDataEncodingAesKey(String callDataEncodingAesKey) {
        this.callDataEncodingAesKey = callDataEncodingAesKey;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    public String getSuiteSecret() {
        return suiteSecret;
    }

    public void setSuiteSecret(String suiteSecret) {
        this.suiteSecret = suiteSecret;
    }

    public List<String> getTrustHostList() {
        return trustHostList;
    }

    public void setTrustHostList(List<String> trustHostList) {
        this.trustHostList = trustHostList;
    }
}