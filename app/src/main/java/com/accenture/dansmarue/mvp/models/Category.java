package com.accenture.dansmarue.mvp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PK on 27/03/2017.
 * Category bean
 */
public class Category {

    // Change parameter in function of equipement
    public static final String ID_FIRST_PARENT = "0";
    private String id;
    private String parentId;
    private String name;
    private String alias;
    private List<Category> children;
    private String image;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<Category> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
