package com.accenture.dansmarue.services.models;

/**
 * Created by PK on 01/06/2017.
 */

public class IdentityResponse extends SiraResponse {


    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }


    public class Answer {

        private String status;
        private User user;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public class User {

            private String name;
            private String firstname;
            private String mail;
            private Boolean isAgent;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFirstname() {
                return firstname;
            }

            public void setFirstname(String firstname) {
                this.firstname = firstname;
            }

            public String getMail() {
                return mail;
            }

            public void setMail(String mail) {
                this.mail = mail;
            }

            public Boolean isAgent() { return isAgent; }
        }
    }
}
