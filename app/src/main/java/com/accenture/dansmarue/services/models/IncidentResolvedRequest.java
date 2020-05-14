package com.accenture.dansmarue.services.models;

/**
 * Created by PK on 30/05/2017.
 */

public class IncidentResolvedRequest extends SiraRequest {

    public static final String SERVICE_NAME = "incidentResolved";

    private String incidentId;
    private int numeroMessage;
    private boolean isGeneric;
    private String guid;
    private String udid;
    private String email;

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
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

    public void setNumeroMessage(int numeroMessage) {
        this.numeroMessage = numeroMessage;
    }

    public void setGeneric(boolean generic) {
        isGeneric = generic;
    }

    public void setEmail( String email) { this.email = email;}
}
