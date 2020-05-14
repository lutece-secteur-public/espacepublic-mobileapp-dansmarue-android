package com.accenture.dansmarue.mvp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageServiceFait implements Serializable {

    @SerializedName("numero_message")
    private int numero;

    private String message;

    private boolean isGeneric;

    public void setIsGeneric( boolean isGeneric) {
        this.isGeneric = isGeneric;
    }

    public String getMessage() { return message; }

    public int getNumero() {
        return numero;
    }


    public boolean isGeneric() {
        return isGeneric;
    }
}
