package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.presenters.CategoryPresenter;
import com.accenture.dansmarue.mvp.views.CategoryView;
import com.accenture.dansmarue.ui.adapters.CategoryAdapter;
import com.accenture.dansmarue.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class CategoryActivity extends BaseActivity implements AdapterView.OnItemClickListener, CategoryView {

    private static final String TAG = CategoryActivity.class.getName();

    @Inject
    protected CategoryPresenter presenter;
    @BindView(R.id.categories)
    protected ListView listView;

    @BindView(R.id.text_title_category)
    protected TextView titleCategory;

    private CategoryAdapter adapter;
    private String idPreviousParent;

    @Override
    protected void onViewReady(final Bundle savedInstanceState, final Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        listView.setOnItemClickListener(this);

        Log.i(TAG, "onViewReady: load type outdoor");
        presenter.loadCategories();
        idPreviousParent = null;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed: ");
    }

    @OnClick(R.id.arrow_back_type)
    public void backType() {
        Log.i(TAG, "customBack: " + idPreviousParent);
        if (idPreviousParent == null) {
            finish();
        } else {
            presenter.onCategorySelected(idPreviousParent);
        }

    }


    @Override
    protected int getContentView() {
        return R.layout.category_activity_layout;
    }


    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        Log.i(TAG, "onItemClick: " + idPreviousParent);
        if (adapter.getItem(position) != null)
            presenter.onCategorySelected(adapter.getItem(position).getId());
    }

    @Override
    protected void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void updateListView(final List<Category> categories) {
        if (adapter == null) {
            adapter = new CategoryAdapter(this, R.layout.category, categories);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(categories);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildSelected(final Category category) {
        final Intent intent = new Intent(CategoryActivity.this, AddAnomalyActivity.class);
        intent.putExtra(Constants.EXTRA_CATEGORY_ID, category.getId());
        if (null != category.getAlias()) {
            intent.putExtra(Constants.EXTRA_CATEGORY_NAME, category.getAlias());
        } else {
            intent.putExtra(Constants.EXTRA_CATEGORY_NAME, category.getName());
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onParentSelected(final Category catParent) {
        idPreviousParent = catParent.getParentId();
        if (catParent.getName() != null ) {
            titleCategory.setText(catParent.getName());
        } else {
            titleCategory.setText(R.string.text_type);
        }
        presenter.extractChildren(catParent.getId());
    }

    @Override
    public void categoriesReady() {
        presenter.extractChildren(Category.ID_FIRST_PARENT);
    }
}
