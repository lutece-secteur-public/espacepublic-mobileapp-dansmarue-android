package com.accenture.dansmarue.mvp.models;


import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.DateUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by PK on 28/03/2017.
 * Incident Bean
 */
public class Incident {


    public static final String RAMEN_SOURCE = "Ramen";
    public static final String STATE_RESOLVED = "R";
    public static final String STATE_OPEN = "O";
    public static final String STATE_NOT_RESOLVABLE = "ONR";  //Anomalie dans un statut non résolvalble
    public static final String STATE_TIERS = "T";  //Anomalie chez un tiers

    @Expose
    private String address;
    @Expose
    private String descriptive = "";
    @Expose
    private String commentaireAgent = "";
    @Expose
    private String categoryId;
    private String date;
    private long id;
    @SerializedName("numero")
    private String reference;
    private String token;
    @Expose(serialize = false)
    private Pictures pictures;
    private int invalidations;
    @Expose
    private int priorityId;
    private String state;
    private String confirms;
    private String lat;
    private String lng;
    @Expose(serialize = false, deserialize = false)
    private int iconId;
    private String alias;
    private String guid;

    private boolean isIncidentFollowedByUser;
    private boolean isResolvable;
    private boolean isValidAddressWithNumber = true;
    @SerializedName("isIncidentAnonyme")
    private boolean isAnonyme;

    private List<Encombrants> encombrants;

    private String reporterGuid;
    private String source;
    private int congratulations;
    private int followers;
    private String origin = Constants.ORIGIN_ANDROID;
    private String hour;

    // add 4 icon in draft
    private String equipementId;
    private String iconIncident;
    private String iconParentId;
    private String typeEquipementName;

    private int iconIncidentSignalement;

    public int getIconIncidentSignalement() {
        return iconIncidentSignalement;
    }

    public void setIconIncidentSignalement(int iconIncidentSignalement) {
        this.iconIncidentSignalement = iconIncidentSignalement;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "address='" + address + '\'' +
                ", descriptive='" + descriptive + '\'' +
                ", commentaire agent='" + commentaireAgent + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", date='" + date + '\'' +
                ", id=" + id +
                ", pictures=" + pictures +
                ", invalidations=" + invalidations +
                ", priorityId=" + priorityId +
                ", state='" + state + '\'' +
                ", confirms='" + confirms + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", iconId=" + iconId +
                ", alias='" + alias + '\'' +
                ", guid='" + guid + '\'' +
                ", isIncidentFollowedByUser=" + isIncidentFollowedByUser +
                ", encombrants=" + encombrants +
                ", reporterGuid='" + reporterGuid + '\'' +
                ", source='" + source + '\'' +
                ", congratulations=" + congratulations +
                ", followers=" + followers +
                ", origin='" + origin + '\'' +
                ", hour='" + hour + '\'' +
                ", equipementId='" + equipementId + '\'' +
                ", iconIncident='" + iconIncident + '\'' +
                ", iconParentId='" + iconParentId + '\'' +
                ", typeEquipementName='" + typeEquipementName + '\'' +
                '}';
    }

    public boolean isResolvable() {
        return isResolvable;
    }

    public void setResolvable(boolean resolvable) {
        isResolvable = resolvable;
    }

    public String getTypeEquipementName() {
        return typeEquipementName;
    }

    public void setTypeEquipementName(String typeEquipementName) {
        this.typeEquipementName = typeEquipementName;
    }

    public String getEquipementId() {
        return equipementId;
    }



    public void setEquipementId(String equipementId) {
        this.equipementId = equipementId;
    }

    public String getIconIncident() {
        return iconIncident;
    }

    public void setIconIncident(String iconIncident) {
        this.iconIncident = iconIncident;
    }

    public String getIconParentId() {
        return iconParentId;
    }

    public void setIconParentId(String iconParentId) {
        this.iconParentId = iconParentId;
    }


    public int getCongratulations() {
        return congratulations;
    }

    public void setCongratulations(int congratulations) {
        this.congratulations = congratulations;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getInvalidations() {
        return invalidations;
    }

    public void setInvalidations(int invalidations) {
        this.invalidations = invalidations;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescriptive() {
        return descriptive;
    }

    public void setDescriptive(String descriptive) {
        this.descriptive = descriptive;
    }


    public String getCommentaireAgent() {
        return commentaireAgent;
    }

    public void setCommentaireAgent(String commentaireAgent) {
        this.commentaireAgent = commentaireAgent;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    public Pictures getPictures() {
        if (pictures == null) {
            pictures = new Pictures();
        }
        return pictures;
    }

    public void setPictures(Pictures pictures) {
        this.pictures = pictures;
    }

    public String getConfirms() {
        return confirms;
    }

    public void setConfirms(String confirms) {
        this.confirms = confirms;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public boolean isResolu() {
        return STATE_RESOLVED.equals(state);
    }

    public boolean isIncidentFollowedByUser() {
        return isIncidentFollowedByUser;
    }

    public void setIncidentFollowedByUser(boolean incidentFollowedByUser) {
        isIncidentFollowedByUser = incidentFollowedByUser;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getFirstAvailablePicture() {
        if (null != pictures && null != pictures.getIncidentPicture()) {
            return fixUrlPictures(pictures.getIncidentPicture());
        }
        if (CollectionUtils.isNotEmpty(getAllPictures())) {
            int idPhotoCur = 0;
            String firstUrlPicture = fixUrlPictures(getAllPictures().get(0));
            for(String urlPicture : getAllPictures()){
                String idPhoto =  urlPicture.substring(urlPicture.lastIndexOf("=")+1);
                try {
                    if (Integer.parseInt(idPhoto) > idPhotoCur) {
                        firstUrlPicture = urlPicture;
                        idPhotoCur = Integer.parseInt(idPhoto);
                    }
                } catch (NumberFormatException e) {
                    return firstUrlPicture;
                }
            }
            return firstUrlPicture;
        }
        return null;
    }

    public List<String> getAllPictures() {
        final List<String> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(getPictures().getFar())) {
            result.addAll(getPictures().getFar());
        }
        if (CollectionUtils.isNotEmpty(getPictures().getClose())) {
            result.addAll(getPictures().getClose());
        }
        if (CollectionUtils.isNotEmpty(getPictures().getDone())) {
            result.addAll(getPictures().getDone());
        }

        return result;
    }

    public String getPicture(int index) {
        if (CollectionUtils.isNotEmpty(getAllPictures())) {
            return fixUrlPictures(getAllPictures().get(index));
        }
        return null;
    }

    //FIXME pour tester en recette...
    private String fixUrlPictures(final String url) {
        if (null == url) {
            return null;
        }
//        return url.replace("r57-sira-ws.rec", "r57-sira.rec");
        return url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReporterGuid() {
        return reporterGuid;
    }

    public void setReporterGuid(String reporterGuid) {
        this.reporterGuid = reporterGuid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getOrigin() {
        return Constants.ORIGIN_ANDROID;
    }

    public boolean isAnonyme() { return isAnonyme; }

    /**
     * FIXME !!!!!!!!!!
     * mettre au propre :)
     *
     * @return
     */
    public String getFormatedDate() {
        if (date != null) {
            final Date creationDate;
            if (isFromRamen()) {
                creationDate = DateUtils.parse(date + " " + hour, DateUtils.DATE_FORMAT_RAMEN);
            } else {
                creationDate = DateUtils.parse(date + " " + hour, DateUtils.DATE_FORMAT_SIRA);
            }

            if (creationDate != null) {
                return date + " " + hour;
            }

        }
        return "";
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getHour() {
        return this.hour;
    }

    public boolean isFromRamen() {
        return RAMEN_SOURCE.equals(source);
    }

    public String getRamenDescription() {
        final StringBuilder result = new StringBuilder();
        if (CollectionUtils.isNotEmpty(getEncombrants())) {
            for (Encombrants encombrant : getEncombrants()) {
                result.append("- ").append(encombrant.getQuantity()).append(" ").append(encombrant.getName()).append("\n");
            }
        }
        return result.toString();
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isValidAddressWithNumber() {
        return isValidAddressWithNumber;
    }

    public void setValidAddressWithNumber(boolean validAddressWithNumber) {
        isValidAddressWithNumber = validAddressWithNumber;
    }

    /**
     * Inner class to hold pictures URL
     */
    public class Pictures {
        //picture 1 url
        private List<String> close;
        //picture 2 url
        private List<String> far;

        private List<String> done;

        public List<String> getDone() {
            if (done == null) {
                done = new ArrayList<>();
            }
            return done;
        }

        public void setDone(List<String> done) {
            this.done = done;
        }

        @SerializedName("incident_picture")
        private String incidentPicture;

        @Expose(serialize = false)
        private int genericPictureId;

        public int getGenericPictureId() {
            return genericPictureId;
        }

        public void setGenericPictureId(int genericPictureId) {
            this.genericPictureId = genericPictureId;
        }

        public String getIncidentPicture() {
            return incidentPicture;
        }

        public void setIncidentPicture(String incidentPicture) {
            this.incidentPicture = incidentPicture;
        }

        public List<String> getClose() {
            if (close == null) {
                close = new ArrayList<>();
            }
            return close;
        }

        public void setClose(List<String> close) {
            this.close = close;
        }

        public List<String> getFar() {
            if (far == null) {
                far = new ArrayList<>();
            }
            return far;
        }

        public void setFar(List<String> far) {
            this.far = far;
        }
    }

    public String toJson() {
        return new GsonBuilder().create().toJson(this);
    }

    public void addPicture1(final String picUrl) {
        getPictures().getClose().add(picUrl);
    }

    public void addPicture2(final String picUrl) {
        getPictures().getFar().add(picUrl);
    }

    public String getPicture1() {
        if (CollectionUtils.isNotEmpty(getPictures().getClose())) {
            return getPictures().getClose().iterator().next();
        }
        return null;
    }

    public String getPicture2() {
        if (CollectionUtils.isNotEmpty(getPictures().getFar())) {
            return getPictures().getFar().iterator().next();
        }
        return null;
    }

    public void deletePicture1() {
        getPictures().getClose().clear();
    }

    public void deletePicture2() {
        getPictures().getFar().clear();
    }

    public List<Encombrants> getEncombrants() {
        return encombrants;
    }

    public void setEncombrants(List<Encombrants> encombrants) {
        this.encombrants = encombrants;
    }

    public class Encombrants {
        private String name;
        private int quantity;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
