package com.accenture.dansmarue.services.models;

/**
 * Created by PK on 30/05/2017.
 */

public class AuthenticationResponse {
    private String tokenId;
    private String successUrl;


    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
