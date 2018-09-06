package com.accenture.dansmarue.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PK on 28/03/2017.
 */
public abstract class SiraResponse {

    private String error;
    @SerializedName("error_message")
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
