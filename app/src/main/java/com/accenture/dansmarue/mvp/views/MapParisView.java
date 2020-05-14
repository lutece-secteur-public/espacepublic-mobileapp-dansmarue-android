package com.accenture.dansmarue.mvp.views;

import com.accenture.dansmarue.mvp.models.Incident;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by PK on 19/04/2017.
 */

public interface MapParisView extends BaseView {

    void updateAnomalyList(List<Incident> closestIncidents, boolean reset);

    void updateAnomalyMarkers(List<MarkerOptions> markers);

    void clearAnomaly();

    void locationChanged(LatLng location);

    void invalidLocation();

    void updatePosMarker();

    void callBackFindByNumber(String errorMessage, boolean dmrOffline, LatLng posIncident);
}
