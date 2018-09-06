package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.services.models.SiraRequest;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by d4v1d on 19/10/2017.
 */

public class EquipementRequest extends SiraRequest {

    private static final String SERVICE_NAME = "getEquipements";

    @SerializedName("curVersion")
    @Expose
    private String curVersion;

    public EquipementRequest(String curVersion) {
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
