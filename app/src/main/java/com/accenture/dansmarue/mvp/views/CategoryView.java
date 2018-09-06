package com.accenture.dansmarue.mvp.views;

import com.accenture.dansmarue.mvp.models.Category;

import java.util.List;

/**
 * Created by PK on 30/03/2017.
 */
public interface CategoryView extends BaseView {

    void updateListView(final List<Category> categories);

    void onChildSelected(final Category category);

    void onParentSelected(final Category catParent);

    void categoriesReady();
}
