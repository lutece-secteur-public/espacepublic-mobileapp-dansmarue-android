package com.accenture.dansmarue.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class MySpaceNewsResponse  extends SiraResponse{

    private String request;
    private MySpaceNewsResponse.Answer answer;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public MySpaceNewsResponse.Answer getAnswer() {
        if (answer == null) {
            answer = new MySpaceNewsResponse.Answer();
        }
        return answer;
    }

    public void setAnswer(MySpaceNewsResponse.Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "MySpaceNewsResponse{" +
                "request='" + request + '\'' +
                ", answer=" + answer +
                '}';
    }

    public class Answer {
        private String status;
        private int version;
        private List<News> actualites;

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

        public List<News> getNews() {
            return actualites;
        }

        public void setNews(List<News> news) {
            this.actualites = news;
        }

        @Override
        public String toString() {

            return "Answer{" +
                    "status='" + status + '\'' +
                    ", version='" + version + '\'' +
                    ", news=" + actualites +
                    '}';
        }

        public class News {

            @SerializedName("id")
            private Integer idNews;
            private String libelle;
            private String texte;
            @SerializedName("image_url")
            private String imageUrl;
            private int ordre;

            public Integer getIdNews() {
                return idNews;
            }

            public void setIdNews(Integer idNews) {
                this.idNews = idNews;
            }

            public String getLibelle() {
                return libelle;
            }

            public void setLibelle(String libelle) {
                this.libelle = libelle;
            }

            public String getTexte() {
                return texte;
            }

            public void setTexte(String texte) {
                this.texte = texte;
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
                return "News{" +
                        "idNews=" + idNews +
                        ", libelle='" + libelle + '\'' +
                        ", texte='" + texte + '\'' +
                        ", ordre='" + ordre + '\'' +
                        '}';
            }
        }
    }
}

