package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;
import android.util.Log;

import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.CategoryEquipementView;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.PrefManager;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by PK on 30/03/2017.
 */
public class CategoryEquipementPresenter extends BasePresenter<CategoryEquipementView> {

    private static final String TAG = CategoryEquipementPresenter.class.getName();

    private Map<String, Category> allCategories;

    private CategoryEquipementView view;
    private Application application;
    private PrefManager prefManager;

    @Inject
    public CategoryEquipementPresenter(final Application application, final CategoryEquipementView view,final PrefManager prefManager) {
        this.application = application;
        this.view = view;
        this.prefManager = prefManager;
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
        Log.i(TAG, "onViewReady: load type equipement cathelper call");
        // FLAG TRUE POUR EQUIPEMENT
        allCategories = CategoryHelper.getAllCategories(application, true,prefManager.getEquipementTypeByDefault().getIdTypEquipement());
        view.categoriesReady();
    }

    @Override
    protected BaseView getView() {
        return view;
    }
}
