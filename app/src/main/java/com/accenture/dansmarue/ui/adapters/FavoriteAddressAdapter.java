package com.accenture.dansmarue.ui.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.FavoriteAddress;
import com.accenture.dansmarue.ui.activities.FavoriteAddressActivity;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.ViewHolder;

import java.util.List;

public class FavoriteAddressAdapter extends RecyclerView.Adapter<ViewHolder> {

    private boolean editMode = false;

    private Context context;
    private List<FavoriteAddress> favoriteAddress;
    PrefManager prefManager;


    public FavoriteAddressAdapter(Context context, List<FavoriteAddress> favoriteAddress) {
        this.context = context;
        this.favoriteAddress = favoriteAddress;
        prefManager = new PrefManager(this.context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_address, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FavoriteAddress item = favoriteAddress.get(position);

        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! editMode ) {
                    ((FavoriteAddressActivity) context).onSelectAddress(item);
                }
            }
        });
        holder.textView.setText(item.getAddress());
        if(editMode) {
            holder.iconMove.setVisibility(View.VISIBLE);
        } else {
            holder.iconMove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return favoriteAddress.size();
    }

    public void removeItem(int position) {
        favoriteAddress.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void moveItem(int oldPosition, int newPosition ){
        FavoriteAddress item = favoriteAddress.get(oldPosition);
        favoriteAddress.remove(oldPosition);
        favoriteAddress.add(newPosition, item);
        notifyItemMoved(oldPosition, newPosition);
    }

    public void removeAll() {
        int size = favoriteAddress.size();
        favoriteAddress.clear();
        notifyItemRangeRemoved(0,size);
    }

    public void saveAllChanges() {
        prefManager.setFavorisAddress(favoriteAddress);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }

}
