package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.services.models.SiraResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by PK on 11/04/2017.
 */

public class GetIncidentsEquipementsByPositionResponse extends SiraResponse {

    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {
        private String status;
        private Map<String, GetIncidentsEquipementsByPositionResponse.Answer.Equipements> equipements;

        @Override
        public String toString() {
            return "Answer{" +
                    "status='" + status + '\'' +
                    ", equipements=" + equipements +
                    '}';
        }

        public Map<String, GetIncidentsEquipementsByPositionResponse.Answer.Equipements> getEquipements() {
            return equipements;
        }

        public void setEquipements(Map<String, GetIncidentsEquipementsByPositionResponse.Answer.Equipements> equipements) {
            this.equipements = equipements;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public class Equipements {
            @SerializedName("parent_id")
            private String parentId;
            private String adresse;
            private double longitude;
            private double latitude;
            private String name;
            private String type_equipement_id;

            public String getType_equipement_id() {
                return type_equipement_id;
            }

            public void setType_equipement_id(String type_equipement_id) {
                this.type_equipement_id = type_equipement_id;
            }

            public String getParentId() {
                return parentId;
            }

            public void setParentId(String parentId) {
                this.parentId = parentId;
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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @SerializedName("closest_incidents")
            private List<Incident> closestIncidents;

            public List<Incident> getClosestIncidents() {
                return closestIncidents;
            }

            public void setClosestIncidents(List<Incident> closestIncidents) {
                this.closestIncidents = closestIncidents;
            }

        }
    }


}
