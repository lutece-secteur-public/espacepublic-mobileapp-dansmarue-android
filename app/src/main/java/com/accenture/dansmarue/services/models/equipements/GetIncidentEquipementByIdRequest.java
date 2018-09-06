package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.services.models.SiraRequest;

/**
 * Created by d4v1d on 15/05/2017.
 */

public class GetIncidentEquipementByIdRequest extends SiraRequest {

    public static final String SERVICE_NAME = "getIncidentById";

    private Long id;
    private String guid;

    public GetIncidentEquipementByIdRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
