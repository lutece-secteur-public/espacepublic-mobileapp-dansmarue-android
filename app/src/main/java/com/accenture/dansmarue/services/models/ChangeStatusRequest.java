package com.accenture.dansmarue.services.models;

public class ChangeStatusRequest extends SiraRequest {

    public static final String SERVICE_NAME = "changeStatus";

    private Answer answer;

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    public class Answer {
       private long id;
       private String reference;
       private String token;
       private String status;
       private String action_date="";
       private String comment="";
       private String rejection_reason="";
       private String programming_date="";
       private int id_type_anomalie=-1;
       private String chosenMessage="";
       private String photo;
       private String email;

    }

    public ChangeStatusRequest() {
        this.answer = new Answer();
    }

    public Answer getAnswer() {
        return this.answer;
    }

    public void setId(long id) {
        this.answer.id = id;
    }
    public void setReference(String reference) {
        this.answer.reference = reference;
    }

    public void setToken(String token) {
        this.answer.token = token;
    }

    public void setStatus(String status) {
        this.answer.status = status;
    }

    public void setComment(String comment) {
        this.answer.comment = comment;
    }

    public void setIdTypeAnomalie(int idTypeAnomalie) {
        this.answer.id_type_anomalie = idTypeAnomalie;
    }

    public void setEmail( String email) { this.answer.email = email;}
}


