package com.accenture.dansmarue.services.models.equipements;

import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;
import com.accenture.dansmarue.services.models.SiraResponse;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by d4v1d on 03/01/2018.
 */

public class GetEquipementsByPositionResponse extends SiraResponse {


    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public class Answer {
        private String status;
        @SerializedName("equipements")
        private Map<String, Equipement> equipementsByProximityList;

        @Override
        public String toString() {
            return "Answer{" +
                    "status='" + status + '\'' +
                    ", equipements=" + equipementsByProximityList +
                    '}';
        }


        public Map<String, Equipement> getEquipementsByProximityList() {
            return equipementsByProximityList;
        }

        public void setEquipementsByProximityList(Map<String, Equipement> equipementsByProximityList) {
            this.equipementsByProximityList = equipementsByProximityList;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


    }

}

