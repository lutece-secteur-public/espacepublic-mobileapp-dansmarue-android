package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;

import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.CategoryView;
import com.accenture.dansmarue.utils.CategoryHelper;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by PK on 30/03/2017.
 */
public class CategoryPresenter extends BasePresenter<CategoryView> {

    private static final String TAG = CategoryPresenter.class.getName();

    private Map<String, Category> allCategories;

    private CategoryView view;
    private Application application;

    @Inject
    public CategoryPresenter(final Application application, final CategoryView view) {
        this.application = application;
        this.view = view;
    }

    public void extractChildren(final String parentId) {
        view.updateListView(CategoryHelper.extractChildren(parentId, allCategories));
    }

    public void onCategorySelected(final String selectedId) {
        if (CollectionUtils.isNotEmpty(allCategories.get(selectedId).getChildren())) {
            view.onParentSelected(allCategories.get(selectedId));
        } else {
            view.onChildSelected(allCategories.get(selectedId));
        }
    }

    public void loadCategories() {
        // AVERTISSEMENT A CHANGER
        allCategories = CategoryHelper.getAllCategories(application);
        view.categoriesReady();
    }

    @Override
    protected BaseView getView() {
        return view;
    }
}
