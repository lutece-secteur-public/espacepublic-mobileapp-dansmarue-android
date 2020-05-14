package com.accenture.dansmarue.services.models;

import com.google.gson.GsonBuilder;

/**
 * Created by PK on 01/06/2017.
 */

public class IdentityRequest {
    private String guid;


    public IdentityRequest(final String guid) {
        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
