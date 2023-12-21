package com.accenture.dansmarue.services.models;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UserInfoResponse {

    @SerializedName("uid")
    private String guid;
    private String mail;
    private Boolean validatedAccount;


    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Boolean isValidatedAccount() {
        return validatedAccount;
    }

    public void setValidatedAccount(Boolean validatedAccount) {
        this.validatedAccount = validatedAccount;
    }

    @Override
    public String toString() {
        return "UserInfoResponse{" +
                ", guid='" + guid + '\'' +
                ", mail='" + mail + '\'' +
                ", validatedAccount=" + validatedAccount +
                '}';
    }
}
