package com.accenture.dansmarue.services.models;

public class SavePrecisionsTerrainRequest extends SiraRequest {

    public static final String SERVICE_NAME = "savePrecisionsTerrain";

    private Long idSignalement;
    private String precisionsTerrain;

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    public Long getIdSignalement() {
        return idSignalement;
    }

    public void setIdSignalement(Long idSignalement) {
        this.idSignalement = idSignalement;
    }

    public String getPrecisionsTerrain() {
        return precisionsTerrain;
    }

    public void setPrecisionsTerrain(String precisionsTerrain) {
        this.precisionsTerrain = precisionsTerrain;
    }
}
