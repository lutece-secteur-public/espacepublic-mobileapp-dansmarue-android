package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.services.models.SiraRequest;

/**
 * Created by PK on 11/04/2017.
 */

public class GetIncidentsByEquipementRequest extends SiraRequest {

    private static final String SERVICE_NAME = "getIncidentsByEquipement";

    private String equipementId;

    public String getEquipementId() {
        return equipementId;
    }

    public void setEquipementId(String equipementId) {
        this.equipementId = equipementId;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

}
