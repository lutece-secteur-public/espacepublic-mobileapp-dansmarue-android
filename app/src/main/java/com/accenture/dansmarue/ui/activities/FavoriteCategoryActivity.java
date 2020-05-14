package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.widget.Button;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.Category;

import com.accenture.dansmarue.ui.adapters.FavorisCategoryAdapter;


import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FavoriteCategoryActivity extends BaseActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    @BindView(R.id.recycler_view_favorite_category)
    protected RecyclerView recyclerView;

    @BindView(R.id.text_modifier_terminer)
    protected TextView editFavoriteItem;

    private List<Category> favoriteItemsList;
    ItemTouchHelper itemTouchHelper;

    @BindView(R.id.button_supprimer)
    protected Button buttonSupprimer;

    FavorisCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoriteItemsList = new ArrayList<>();
        adapter = new FavorisCategoryAdapter(this,favoriteItemsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerItemTouchHelper( this);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);

        //fill recycler view
        favoriteItemsList.clear();
        PrefManager prefManager = new PrefManager(getApplicationContext());
        for ( Category item : prefManager.getFavorisItems().values()) {
            favoriteItemsList.add(item);
        }
        // refreshing recycler view
        adapter.notifyDataSetChanged();

    }

    public void onSelectItem(Category selectItem) {

        final Intent intent = new Intent(FavoriteCategoryActivity.this, CategoryActivity.class);
        intent.putExtra(Constants.EXTRA_CATEGORY_ID, selectItem.getId());
        if (null != selectItem.getAlias()) {
            intent.putExtra(Constants.EXTRA_CATEGORY_NAME, selectItem.getAlias());
        } else {
            intent.putExtra(Constants.EXTRA_CATEGORY_NAME, selectItem.getName());
        }

        if(selectItem.getMessageHorsDMR() != null) {
            intent.putExtra(Constants.EXTRA_MESSAGE_HORS_DMR, selectItem.getMessageHorsDMR());
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        // remove the item from recycler view
        adapter.removeItem(viewHolder.getAdapterPosition());
    }

    @Override
    public void onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.moveItem(viewHolder.getAdapterPosition(),target.getAdapterPosition());
    }

    @OnClick(R.id.arrow_back_fa)
    public void backType() {
        finish();
    }

    @OnClick(R.id.text_modifier_terminer)
    public void editFavoriteItem(){
        if(! adapter.isEditMode()) {
            editFavoriteItem.setText(R.string.favorite_address_terminer);
            adapter.setEditMode(true);

            buttonSupprimer.setEnabled(true);
            buttonSupprimer.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));

            adapter.notifyDataSetChanged();

            itemTouchHelper.attachToRecyclerView(recyclerView);

        } else {
            editFavoriteItem.setText(R.string.favorite_address_modifier);
            adapter.setEditMode(false);
            adapter.saveAllChanges();

            buttonSupprimer.setEnabled(false);
            buttonSupprimer.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_icon));

            adapter.notifyDataSetChanged();

            //remove swipe event
            itemTouchHelper.attachToRecyclerView(null);
        }

    }

    @OnClick(R.id.button_supprimer)
    public void deleteAll() {
        adapter.removeAll();
    }

    @Override
    protected int getContentView() {
        return R.layout.favorite_category_activity_layout;
    }

}
