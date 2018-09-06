package com.accenture.dansmarue.services.models;

/**
 * Created by d4v1d on 15/05/2017.
 */

public class GetIncidentByIdRequest extends SiraRequest {

    public static final String SERVICE_NAME = "getIncidentById";

    private Long id;
    private String source;
    private String guid;

    public GetIncidentByIdRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }
}
