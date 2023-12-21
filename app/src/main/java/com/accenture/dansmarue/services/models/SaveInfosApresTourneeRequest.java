package com.accenture.dansmarue.services.models;

public class SaveInfosApresTourneeRequest extends SiraRequest{

    public static final String SERVICE_NAME = "saveInfosApresTournee";

    private int idFDT;
    private String infosApresTournee;

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    public int getIdFDT() {
        return idFDT;
    }

    public void setIdFDT(int idFDT) {
        this.idFDT = idFDT;
    }

    public String getInfosApresTournee() {
        return infosApresTournee;
    }

    public void setInfosApresTournee(String infosApresTournee) {
        this.infosApresTournee = infosApresTournee;
    }
}
