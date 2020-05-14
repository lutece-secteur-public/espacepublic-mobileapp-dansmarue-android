package com.accenture.dansmarue.mvp.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class FavoriteAddress implements Serializable {

    private String address;
    private double latitude;
    private double longitude;
    private boolean isSelect;

    public FavoriteAddress(String address, LatLng latLng) {
        this.address = address;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.isSelect = false;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
