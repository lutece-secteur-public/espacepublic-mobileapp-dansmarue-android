package com.accenture.dansmarue.services.models;

import android.os.Parcelable;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.Position;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Created by PK on 28/03/2017.
 * Request that will be transform to Json to call the WS {@link com.accenture.dansmarue.services.SiraApiService#saveIncident(SaveIncidentRequest)}
 */
public class SaveIncidentRequest extends SiraRequest {

    private static final String SERVICE_NAME = "saveIncident";

    private String udid = "0000";
    @Expose
    private Position position;
    @Expose
    private Incident incident;
    private String email = "";
    private String guid;
    private String userToken;

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public Position getPosition() {
        if (position == null) {
            position = new Position();
        }
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Incident getIncident() {
        if (incident == null) {
            incident = new Incident();
        }
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }


    @Override
    public String toString() {
        return toJsonArray(new GsonBuilder().create().toJson(this));
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
