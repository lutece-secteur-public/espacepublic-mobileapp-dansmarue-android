package com.accenture.dansmarue.services.models;

public class CheckVersionRequest extends SiraRequest {

    private static final String SERVICE_NAME = "checkVersion";


    public CheckVersionRequest() {

    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

}
