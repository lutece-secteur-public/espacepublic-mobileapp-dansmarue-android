package com.accenture.dansmarue.services.models;

public class ChangeStatusResponse extends SiraResponse {

    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {

        private String id;
        private String error;

        public String getId() { return id;}
        public String getError() { return error;}
    }
}
