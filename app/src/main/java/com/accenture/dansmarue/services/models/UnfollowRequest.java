package com.accenture.dansmarue.services.models;

/**
 * Created by d4v1d on 11/05/2017.
 */

public class UnfollowRequest extends SiraRequest {

    public static final String SERVICE_NAME = "unfollow";

    private String incidentId;
    private String guid;
    private String udid;

    public UnfollowRequest(String incidentId) {
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

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }
}
