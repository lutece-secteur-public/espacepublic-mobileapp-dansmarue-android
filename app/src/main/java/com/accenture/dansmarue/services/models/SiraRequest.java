package com.accenture.dansmarue.services.models;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by PK on 28/03/2017.
 * Provides common property for all the {@link com.accenture.dansmarue.services.SiraApiService } calls
 */
public abstract class SiraRequest {

    @SerializedName("request")
    @Expose
    private String request;


    protected abstract String getServiceName();

    public SiraRequest() {
        this.request = getServiceName();
    }

    @Override
    public String toString() {
        return toJsonArray(new GsonBuilder().create().toJson(this));
    }

    /**
     * convert the given json string to a JsonArray string (needed for {@link com.accenture.dansmarue.services.SiraApiService}
     *
     * @param s json string
     * @return jsonArray representation of the json string
     */
    public String toJsonArray(String s) {
        return "[" + s + "]";
    }
}
