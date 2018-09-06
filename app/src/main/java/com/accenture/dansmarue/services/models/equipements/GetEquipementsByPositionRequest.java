package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.mvp.models.Position;
import com.accenture.dansmarue.services.models.SiraRequest;

/**
 * Created by d4v1d on 03/01/2018.
 */

public class GetEquipementsByPositionRequest extends SiraRequest {

    private static final String SERVICE_NAME = "getEquipementsByPosition";

    private String typeEquipementId;

    private Position position;


    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getTypeEquipementId() {
        return typeEquipementId;
    }

    public void setTypeEquipementId(String typeEquipementId) {
        this.typeEquipementId = typeEquipementId;
    }
}

