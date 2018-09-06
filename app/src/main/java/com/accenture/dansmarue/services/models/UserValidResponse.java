package com.accenture.dansmarue.services.models;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Created by PK on 30/05/2017.
 */

public class UserValidResponse {
    @SerializedName("username")
    private String userName;
    @SerializedName("uid")
    private List<String> guid;
    private List<String> mail;
    private List<Boolean> validatedAccount;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getMail() {
        return mail.iterator().next();
    }

    public void setMail(List<String> mail) {
        this.mail = mail;
    }

    public boolean isValidatedAccount() {
        if (CollectionUtils.isNotEmpty(validatedAccount)) {
            return validatedAccount.iterator().next();
        }

        return false;

    }

    public void setValidatedAccount(List<Boolean> validatedAccount) {
        this.validatedAccount = validatedAccount;
    }


    public String getGuid() {
        return guid.iterator().next();
    }

    public void setGuid(List<String> guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "UserValidResponse{" +
                "userName='" + userName + '\'' +
                ", guid='" + guid + '\'' +
                ", mail='" + mail + '\'' +
                ", validatedAccount=" + validatedAccount +
                '}';
    }

}
