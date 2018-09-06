package com.accenture.dansmarue.services.models;

/**
 * Created by d4v1d on 20/06/2017.
 */

public class ProcessWorkflowRequest extends SiraRequest {


    public static final String SERVICE_NAME = "processWorkflow";


    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }
}
