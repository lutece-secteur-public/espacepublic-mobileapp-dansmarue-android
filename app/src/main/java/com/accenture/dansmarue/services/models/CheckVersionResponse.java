package com.accenture.dansmarue.services.models;

public class CheckVersionResponse extends SiraResponse {

    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {

        private String androidVersionStore;
        private String androidMajObligatoire;
        private String derniereVersionObligatoire;

        public String getAndroidVersionStore() {
            return androidVersionStore;
        }

        public String getAndroidMajObligatoire() {
            return androidMajObligatoire;
        }

        public String getDerniereVersionObligatoire() {
            return derniereVersionObligatoire;
        }

    }

}
