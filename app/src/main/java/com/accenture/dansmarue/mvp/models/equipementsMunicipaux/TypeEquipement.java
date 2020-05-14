package com.accenture.dansmarue.mvp.models.equipementsMunicipaux;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d4v1d on 20/10/2017.
 */

public class TypeEquipement {

    private String typeEquipementId;

    private String nomTypeEquipement;
    private String msg_alert_no_equipement;
    private String msg_alert_photo;
    private String placeholder_searchbar;
    private String iconTypeEquipement;
    private String imageTypeEquipement;
    private String libelleEcranMobile;

    public String getLibelleEcranMobile() {
        return libelleEcranMobile;
    }

    public void setLibelleEcranMobile(String libelleEcranMobile) {
        this.libelleEcranMobile = libelleEcranMobile;
    }

    public String getTypeEquipementId() {
        return typeEquipementId;
    }

    public void setTypeEquipementId(String typeEquipementId) {
        this.typeEquipementId = typeEquipementId;
    }

    public String getImageTypeEquipement() {
        return imageTypeEquipement;
    }

    public void setImageTypeEquipement(String imageTypeEquipement) {
        this.imageTypeEquipement = imageTypeEquipement;
    }

    private List<Equipement> listEquipementByType;

    public List<Equipement> getListEquipementByType() {
        if (listEquipementByType == null) {
            listEquipementByType = new ArrayList<>();
        }
        return listEquipementByType;
    }

    public void setListEquipementByType(List<Equipement> listEquipementByType) {
        this.listEquipementByType = listEquipementByType;
    }

    public String getMsg_alert_photo() {
        return msg_alert_photo;
    }

    public void setMsg_alert_photo(String msg_alert_photo) {
        this.msg_alert_photo = msg_alert_photo;
    }

    public String getPlaceholder_searchbar() {
        return placeholder_searchbar;
    }

    public void setPlaceholder_searchbar(String placeholder_searchbar) {
        this.placeholder_searchbar = placeholder_searchbar;
    }

    public String getMsg_alert_no_equipement() {
        return msg_alert_no_equipement;
    }

    public void setMsg_alert_no_equipement(String msg_alert_no_equipement) {
        this.msg_alert_no_equipement = msg_alert_no_equipement;
    }

    public String getIconTypeEquipement() {
        return iconTypeEquipement;
    }

    public void setIconTypeEquipement(String iconTypeEquipement) {
        this.iconTypeEquipement = iconTypeEquipement;
    }

    public String getIdTypEquipement() {
        return typeEquipementId;
    }

    public void setIdTypEquipement(String idTypEquipement) {
        this.typeEquipementId = idTypEquipement;
    }

    public String getNomTypeEquipement() {
        return nomTypeEquipement;
    }

    public void setNomTypeEquipement(String nomTypeEquipement) {
        this.nomTypeEquipement = nomTypeEquipement;
    }


}
