package com.accenture.dansmarue.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MySpaceHelpResponse  extends SiraResponse{

    private String request;
    private MySpaceHelpResponse.Answer answer;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public MySpaceHelpResponse.Answer getAnswer() {
        if (answer == null) {
            answer = new MySpaceHelpResponse.Answer();
        }
        return answer;
    }

    public void setAnswer(MySpaceHelpResponse.Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "MySpaceHelpResponse{" +
                "request='" + request + '\'' +
                ", answer=" + answer +
                '}';
    }

    public class Answer {
        private String status;
        private int version;
        private List<MySpaceHelpResponse.Answer.Help> aides;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<MySpaceHelpResponse.Answer.Help> getAides() {
            return aides;
        }

        public void setAides(List<MySpaceHelpResponse.Answer.Help> helps) {
            this.aides = helps;
        }

        @Override
        public String toString() {

            return "Answer{" +
                    "status='" + status + '\'' +
                    ", version='" + version + '\'' +
                    ", aides=" + aides +
                    '}';
        }

        public class Help {

            private Integer id;
            private String libelle;
            @SerializedName("hypertexte_url")
            private String url;
            @SerializedName("image_url")
            private String imageUrl;
            private int ordre;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getLibelle() {
                return libelle;
            }

            public void setLibelle(String libelle) {
                this.libelle = libelle;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getImage() {
                return imageUrl;
            }

            public void setImage(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public int getOrdre() {
                return ordre;
            }

            public void setOrdre(int ordre) {
                this.ordre = ordre;
            }


            @Override
            public String toString() {
                return "Help{" +
                        "id=" + id +
                        ", libelle='" + libelle + '\'' +
                        ", url='" + url + '\'' +
                        ", ordre='" + ordre + '\'' +
                        '}';
            }
        }
    }

}
