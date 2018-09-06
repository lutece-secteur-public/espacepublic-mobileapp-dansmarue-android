package com.accenture.dansmarue.services.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PK on 27/03/2017.
 * Request that will be transform to Json to call the WS {@link com.accenture.dansmarue.services.SiraApiService#getCategories(CategoryRequest)}
 */
public class CategoryRequest extends SiraRequest {

    private static final String SERVICE_NAME = "getCategories";

    @SerializedName("curVersion")
    @Expose
    private String curVersion;

    public CategoryRequest(String curVersion) {
        this.curVersion = curVersion;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    public String getCurVersion() {
        return curVersion;
    }

    public void setCurVersion(String curVersion) {
        this.curVersion = curVersion;
    }


}
