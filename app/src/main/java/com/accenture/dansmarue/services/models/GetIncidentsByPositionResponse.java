package com.accenture.dansmarue.services.models;

import com.accenture.dansmarue.mvp.models.Incident;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PK on 11/04/2017.
 */

public class GetIncidentsByPositionResponse extends SiraResponse {

    private Answer answer;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
