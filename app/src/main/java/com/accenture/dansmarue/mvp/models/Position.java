package com.accenture.dansmarue.mvp.models;

import com.google.gson.annotations.Expose;

/**
 * Created by PK on 11/04/2017.
 */

public class Position {
    @Expose
    private double latitude;
    @Expose
    private double longitude;

    public Position() {
    }

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
