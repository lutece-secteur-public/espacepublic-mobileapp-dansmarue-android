package com.accenture.dansmarue.services.models;

/**
 * Created by PK on 28/03/2017.
 * Response from WS {@link com.accenture.dansmarue.services.SiraApiService#saveIncident(SaveIncidentRequest)}
 */
public class SaveIncidentResponse extends SiraResponse {

    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {
        private String status;
        private Integer incidentId;

        public Integer getIncidentId() {
            return incidentId;
        }

        public void setIncidentId(Integer incidentId) {
            this.incidentId = incidentId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
