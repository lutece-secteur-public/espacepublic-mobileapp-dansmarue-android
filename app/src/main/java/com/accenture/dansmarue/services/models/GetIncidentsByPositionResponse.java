package com.accenture.dansmarue.services.models;

import com.accenture.dansmarue.mvp.models.Incident;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PK on 11/04/2017.
 */

public class GetIncidentsByPositionResponse extends SiraResponse {

    public static final String REQUEST_SEARCH_INCIDENTS_BY_ID_FDT = "searchIncidentsByIdFdt";

    private String request;

    private Answer answer;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {
        private String status;
        @SerializedName("closest_incidents")
        private List<Incident> closestIncidents;

        private List<Incident> incident;
        private List<Incident> incidents;

        private String infosAvantTournee;

        private String infosApresTournee;

        public List<Incident> getClosestIncidents() {
            return closestIncidents;
        }

        public void setClosestIncidents(List<Incident> closestIncidents) {
            this.closestIncidents = closestIncidents;
        }

        public List<Incident> getIncident() {
            return incident;
        }

        public void setIncident(List<Incident> incident) {
            this.incident = incident;
        }

        public List<Incident> getIncidentsFDT() {
            return incidents;
        }

        public void setIncidents(List<Incident> incidents) { this.incidents = incidents; }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInfosAvantTournee() {
            return infosAvantTournee;
        }

        public void setInfosAvantTournee(String infosAvantTournee) {
            this.infosAvantTournee = infosAvantTournee;
        }

        public String getInfosApresTournee() {
            return infosApresTournee;
        }

        public void setInfosApresTournee(String infosApresTournee) {
            this.infosApresTournee = infosApresTournee;
        }
    }
}
