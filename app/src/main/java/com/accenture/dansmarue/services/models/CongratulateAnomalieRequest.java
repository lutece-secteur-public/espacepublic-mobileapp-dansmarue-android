package com.accenture.dansmarue.services.models;

/**
 * Created by d4v1d on 16/05/2017.
 */

public class CongratulateAnomalieRequest extends SiraRequest {

    public static final String SERVICE_NAME = "congratulateAnomalie";

    private String incidentId;

    public CongratulateAnomalieRequest(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }
}