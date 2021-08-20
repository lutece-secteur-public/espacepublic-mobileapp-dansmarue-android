package com.accenture.dansmarue.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by PK on 27/03/2017.
 * * Response from the WS {@link com.accenture.dansmarue.services.SiraApiService#getCategories(CategoryRequest)}.
 * The response is auto generate from JSON via GSONConverter (see {@link retrofit2.Retrofit configuration in {@link com.accenture.dansmarue.di.modules.ApplicationModule}}
 */
public class CategoryResponse extends SiraResponse {

    private String request;
    private Answer answer;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Answer getAnswer() {
        if (answer == null) {
            answer = new Answer();
        }
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "CategoryResponse{" +
                "request='" + request + '\'' +
                ", answer=" + answer +
                '}';
    }

    public class Answer {
        private String status;
        private String version;
        private Map<String, Category> categories;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Map<String, Category> getCategories() {
            return categories;
        }

        public void setCategories(Map<String, Category> categories) {
            this.categories = categories;
        }

        @Override
        public String toString() {

            return "Answer{" +
                    "status='" + status + '\'' +
                    ", version='" + version + '\'' +
                    ", categories=" + categories +
                    '}';
        }

        public class Category {
            @SerializedName("children_id")
            private List<String> childrenIds;
            @SerializedName("parent_id")
            private String parentId;
            private String name;
            private boolean isAgent;
            @SerializedName("horsDMR")
            private boolean hasMessageHorsDMR;
            private String messageHorsDMR;
            private String alias;
            private String image;
            @SerializedName("image_mobile")
            private String imageMobile;

            public String getImageMobile() {
                return imageMobile;
            }

            public void setImageMobile(String imageMobile) {
                this.imageMobile = imageMobile;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getAlias() {
                return alias;
            }

            public void setAlias(String alias) {
                this.alias = alias;
            }

            public List<String> getChildrenIds() {
                return childrenIds;
            }

            public void setChildrenIds(List<String> childrenIds) {
                this.childrenIds = childrenIds;
            }

            public String getParentId() {
                return parentId;
            }

            public void setParentId(String parentId) {
                this.parentId = parentId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public boolean isAgent() { return isAgent;}

            public boolean isHasMessageHorsDMR() {return hasMessageHorsDMR;}

            public String getMessageHorsDMR() { return  messageHorsDMR;}


            @Override
            public String toString() {
                return "Category{" +
                        "childrenIds=" + childrenIds +
                        ", parentId='" + parentId + '\'' +
                        ", name='" + name + '\'' +
                        ", isAgent='" + isAgent + '\'' +
                        '}';
            }
        }
    }
}
