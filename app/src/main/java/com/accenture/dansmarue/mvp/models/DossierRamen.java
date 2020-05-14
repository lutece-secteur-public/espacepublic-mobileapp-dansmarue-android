package com.accenture.dansmarue.mvp.models;

import com.accenture.dansmarue.R;

import java.util.ArrayList;
import java.util.List;

public class DossierRamen {

    private long id;
    private String adresse;
    private String dateCreation;
    private String heureCreation;
    private String lat;
    private String lng;
    private String source = "Ramen";


    public long getId() {
        return id;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public String getHeureCreation() {
        return heureCreation;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getSource() {
        return source;
    }

    /**
     * Convert Dossiers Ramen to incident
     * @param dossiers
     *          List of ramen dossier
     * @return List<Incident>
     */
    public static List<Incident> convertToIncident(DossierRamen[] dossiers) {

        List<Incident> listIncident = new ArrayList<>();
        for(int i = 0 ; i < dossiers.length; i++) {
             Incident incident = new Incident();
             incident.setId(dossiers[i].getId());
             incident.setAddress(dossiers[i].getAdresse());
             incident.setDate(dossiers[i].getDateCreation());
             incident.setHour(dossiers[i].getHeureCreation());
             incident.setLat(dossiers[i].getLat());
             incident.setLng(dossiers[i].getLng());
             incident.setSource(dossiers[i].getSource());
             listIncident.add(incident);
        }

        return listIncident;
    }

}
