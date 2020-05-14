package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.services.models.SiraResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by d4v1d on 19/10/2017.
 */

public class EquipementResponse extends SiraResponse {

    private String request;
    private EquipementResponse.Answer answer;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public EquipementResponse.Answer getAnswer() {
        if (answer == null) {
            answer = new EquipementResponse.Answer();
        }
        return answer;
    }

    public void setAnswer(EquipementResponse.Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "EquipementResponse{" +
                "request='" + request + '\'' +
                ", answer=" + answer +
                '}';
    }

    public class Answer {
        private String status;
        private String version;
        private Map<String, EquipementResponse.Answer.Equipement> equipements;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Map<String, EquipementResponse.Answer.Equipement> getEquipements() {
            return equipements;
        }

        public void setEquipements(Map<String, EquipementResponse.Answer.Equipement> equipements) {
            this.equipements = equipements;
        }

        @Override
        public String toString() {

            return "Answer{" +
                    "status='" + status + '\'' +
                    ", version='" + version + '\'' +
                    ", equipements=" + equipements +
                    '}';
        }

        public class Equipement {
            @SerializedName("children_id")
            private List<String> childrenIds;
            @SerializedName("parent_id")
            private String parentId;
            private String msg_alert_no_equipement;
            private String msg_alert_photo;
            private String placeholder_searchbar;
            private String adresse;
            private double longitude;
            private double latitude;
            private String name;
            private String icon;
            private String image;

            private String libelleEcranMobile;

            public String getLibelleEcranMobile() {
                return libelleEcranMobile;
            }

            public void setLibelleEcranMobile(String libelleEcranMobile) {
                this.libelleEcranMobile = libelleEcranMobile;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
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

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getMsg_alert_no_equipement() {
                return msg_alert_no_equipement;
            }

            public void setMsg_alert_no_equipement(String msg_alert_no_equipement) {
                this.msg_alert_no_equipement = msg_alert_no_equipement;
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


            public List<String> getChildrenIds() {
                return childrenIds;
            }

            public void setChildrenIds(List<String> childrenIds) {
                this.childrenIds = childrenIds;
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


            @Override
            public String toString() {
                return "Equipements{" +
                        "childrenIds=" + childrenIds +
                        ", parentId='" + parentId + '\'' +
                        ", name='" + name + '\'' +
                        ", adresse='" + adresse + '\'' +
                        ", lat ='" + latitude + '\'' +
                        ", long ='" + longitude + '\'' +
                        ", msg='" + msg_alert_no_equipement + '\'' +

                        '}';
            }
        }
    }
}
