package com.accenture.dansmarue.services.models;

import com.accenture.dansmarue.mvp.models.Incident;

/**
 * Created by d4v1d on 15/05/2017.
 */

public class GetIncidentByIdResponse extends SiraResponse {

    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {

        private String status;

        private Incident incident;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Incident getIncident() {
            return incident;
        }

        public void setIncident(Incident incident) {
            this.incident = incident;
        }

        private boolean resolved_authorization;

        public boolean isResolved_authorization() {
            return resolved_authorization;
        }

        public void setResolved_authorization(boolean resolved_authorization) {
            this.resolved_authorization = resolved_authorization;
        }

    }
}
