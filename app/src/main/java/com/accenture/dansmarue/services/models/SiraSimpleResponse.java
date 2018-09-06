package com.accenture.dansmarue.services.models;

/**
 * Created by d4v1d on 04/05/2017.
 */

public class SiraSimpleResponse extends SiraResponse {

    private Answer answer;

    private String request;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
