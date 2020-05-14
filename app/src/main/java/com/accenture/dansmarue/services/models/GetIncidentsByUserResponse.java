package com.accenture.dansmarue.services.models;

import com.accenture.dansmarue.mvp.models.Incident;

import java.util.List;

/**
 * Created by PK on 11/05/2017.
 */
public class GetIncidentsByUserResponse extends SiraResponse {

    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {
        private String status;
        private List<Incident> incidents;


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<Incident> getIncidents() {
            return incidents;
        }

        public void setIncidents(List<Incident> incidents) {
            this.incidents = incidents;
        }
    }
}
