package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;

import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.CategoryView;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.PrefManager;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private PrefManager prefManager;

    @Inject
    public CategoryPresenter(final Application application, final CategoryView view, final PrefManager prefManager) {
        this.application = application;
        this.view = view;
        this.prefManager = prefManager;
    }

    public void extractChildren(final String parentId) {
        List<Category> lstCategory  = CategoryHelper.extractChildren(parentId, allCategories);
        if ( ! prefManager.getIsAgent()) {
            view.updateListView(removeCategoriesAgent(lstCategory),false);
        } else {
            view.updateListView(lstCategory, false);
        }

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

    /**
     * remove Categories Agent for standard user.
     */
    private List<Category>  removeCategoriesAgent( List<Category> lstCategory ) {
       List<Category> tmpList = new ArrayList<>(lstCategory);
       int index = 0;
       for ( Category cat : tmpList) {
           if (cat.isAgent()) {
               lstCategory.remove(cat);
           }
           index++;
       }
       return lstCategory;
    }

    /**
     * remove Categories hors DMR.
     * @param lstCategory
     *         all category
     * @return DMR category.
     */
    private List<Category>  removeCategoriesHorsDMR( List<Category> lstCategory ) {
        List<Category> tmpList = new ArrayList<>(lstCategory);
        int index = 0;
        for ( Category cat : tmpList) {
            if (cat.isHasMessageHorsDMR()) {
                lstCategory.remove(cat);
            }
            index++;
        }
        return lstCategory;
    }

    @Override
    protected BaseView getView() {
        return view;
    }
}
