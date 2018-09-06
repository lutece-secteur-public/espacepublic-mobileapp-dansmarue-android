package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.services.models.SiraResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PK on 11/04/2017.
 */

public class GetIncidentsByEquipementResponse extends SiraResponse {

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

        public List<Incident> getClosestIncidents() {
            return closestIncidents;
        }

        public void setClosestIncidents(List<Incident> closestIncidents) {
            this.closestIncidents = closestIncidents;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
