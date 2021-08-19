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
import android.widget.LinearLayout;
import android.widget.TextView;


import com.accenture.dansmarue.R;


import com.accenture.dansmarue.mvp.models.FavoriteAddress;
import com.accenture.dansmarue.ui.adapters.FavoriteAddressAdapter;


import com.accenture.dansmarue.ui.fragments.MapParisFragment;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.RecyclerItemTouchHelper;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class FavoriteAddressActivity extends BaseActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    @BindView(R.id.recycler_view_favorite_address)
    protected RecyclerView recyclerView;

    FavoriteAddressAdapter adapter;
    private List<FavoriteAddress> favoriteAddressList;
    ItemTouchHelper itemTouchHelper;

    @BindView(R.id.button_supprimer)
    protected Button buttonSupprimer;

    @BindView(R.id.text_modifier_terminer)
    protected TextView editFavoriteAddress;

    @BindView(R.id.actions_layout)
    protected LinearLayout buttonActionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoriteAddressList = new ArrayList<>();
        adapter = new FavoriteAddressAdapter(this,favoriteAddressList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerItemTouchHelper( this);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);

        //fill recycler view
        favoriteAddressList.clear();
        PrefManager prefManager = new PrefManager(getApplicationContext());
        for ( FavoriteAddress favoriteAddress : prefManager.getFavoriteAddress().values()) {
            favoriteAddressList.add(favoriteAddress);
        }
        // refreshing recycler view
        adapter.notifyDataSetChanged();

    }


    public void onSelectAddress(FavoriteAddress selectAddress) {
            Intent intent = new Intent(this, MapParisFragment.class);
            intent.putExtra (Constants.EXTRA_FAVORITE_ADDRESS_SELECT,selectAddress);
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

    @Override
    protected int getContentView() {
        return R.layout.favorite_address_activity_layout;
    }

    @OnClick(R.id.arrow_back_fa)
    public void backType() {
        finish();
    }

    @OnClick(R.id.text_modifier_terminer)
    public void editFavoriteAddress(){
        if(! adapter.isEditMode()) {
            editFavoriteAddress.setText(R.string.favorite_address_terminer);
            adapter.setEditMode(true);

            buttonSupprimer.setEnabled(true);
            buttonSupprimer.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.framboise));

            adapter.notifyDataSetChanged();

            itemTouchHelper.attachToRecyclerView(recyclerView);

        } else {
            editFavoriteAddress.setText(R.string.favorite_address_modifier);
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



}
