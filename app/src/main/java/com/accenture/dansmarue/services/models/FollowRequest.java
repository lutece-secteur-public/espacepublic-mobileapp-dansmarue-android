package com.accenture.dansmarue.services.models;

import com.accenture.dansmarue.utils.Constants;

/**
 * Created by d4v1d on 04/05/2017.
 */

public class FollowRequest extends SiraRequest {

    public static final String SERVICE_NAME = "follow";


    private String incidentId;
    private String guid;
    private String email;
    private String device = Constants.ORIGIN_ANDROID;
    private String udid;
    private String userToken;

    public FollowRequest(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUdid() {
        return udid;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }


    public String getDevice() {
        return device;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

}
