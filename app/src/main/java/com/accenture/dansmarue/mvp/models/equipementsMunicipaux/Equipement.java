package com.accenture.dansmarue.mvp.models.equipementsMunicipaux;

import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.models.Incident;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d4v1d on 19/10/2017.
 */

public class Equipement {

    public static final String ID_FIRST_PARENT = "0";
    private String id;
    private String parentId;
    private String name;
    private String msg_erreur;
    private String adresse;
    private double longitude;
    private double latitude;
    private int nbAnomalies = 0;
    private List<Category> children;
    private String iconEquipement;
    private String type_equipement_id;


    public String getType_equipement_id() {
        return type_equipement_id;
    }

    public void setType_equipement_id(String type_equipement_id) {
        this.type_equipement_id = type_equipement_id;
    }

    private List<Incident> listeIncidents;

    public List<Incident> getListeIncidents() {
        if (listeIncidents == null) {
            listeIncidents = new ArrayList<>();
        }
        return listeIncidents;
    }

    public void setListeIncidents(List<Incident> listeIncidents) {
        this.listeIncidents = listeIncidents;
    }

    public String getIconEquipement() {
        return iconEquipement;
    }

    public void setIconEquipement(String iconEquipement) {
        this.iconEquipement = iconEquipement;
    }

    private int equipementImage = NO_IMAGE_PROVIDED;
    // Constant value that represents no image was provided for this word
    private static final int NO_IMAGE_PROVIDED = -1;

    public int getEquipementImage() {
        return equipementImage;
    }

    public void setEquipementImage(int equipementImage) {
        this.equipementImage = equipementImage;
    }

    public int getImageResourceId() {
        return equipementImage;
    }


    /**
     * Returns whether or not there is an image for this equipement.
     */
    public boolean hasImage() {
        return equipementImage != NO_IMAGE_PROVIDED;
    }

    public Equipement() {

    }

    public Equipement(String equipementName, String equipementAddress) {
        this.name = equipementName;
        this.adresse = equipementAddress;
    }

    public Equipement(String equipementName, String equipementAddress, int equipementImage) {
        this.name = equipementName;
        this.adresse = equipementAddress;
        this.equipementImage = equipementImage;
    }


    public Equipement(String id, String parentId, String name, String msg_erreur, String adresse, double longitude, double latitude, int nbAnomalies, List<Category> children) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.msg_erreur = msg_erreur;
        this.adresse = adresse;
        this.longitude = longitude;
        this.latitude = latitude;
        this.nbAnomalies = nbAnomalies;
        this.children = children;
    }


    public int getNbAnomalies() {
        return nbAnomalies;
    }

    public void setNbAnomalies(int nbAnomalies) {
        this.nbAnomalies = nbAnomalies;
    }

    public static String getIdFirstParent() {
        return ID_FIRST_PARENT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg_erreur() {
        return msg_erreur;
    }

    public void setMsg_erreur(String msg_erreur) {
        this.msg_erreur = msg_erreur;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
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

    public List<Category> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }
}
