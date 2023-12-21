package com.accenture.dansmarue.ui.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.FavoriteAddress;
import com.accenture.dansmarue.ui.activities.FavoriteAddressActivity;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.ViewHolder;

import java.util.List;

public class FavoriteAddressAdapter extends RecyclerView.Adapter<ViewHolder> {

    private boolean editMode = false;

    private Context context;
    private List<FavoriteAddress>  favoriteAddress;
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
        int index = position;
        final FavoriteAddress item = favoriteAddress.get(position);
        TranslateAnimation slideToLeft = new TranslateAnimation(0,  (holder.viewForeground.getWidth() * (-1))/2, 0, 0);
        slideToLeft.setDuration(50);
        slideToLeft.setFillAfter(true);

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
            holder.viewForeground.setContentDescription("Supprimer " + item.getAddress());
            holder.iconMove.setContentDescription("Supprimer " + item.getAddress());
            holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.viewForeground.startAnimation(slideToLeft);
                    favoriteAddress.remove(index);
                    notifyItemRemoved(index);
                    prefManager.setFavorisAddress(favoriteAddress);
                }
            });

            holder.iconMove.setVisibility(View.VISIBLE);
            holder.iconMove.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            holder.iconMove.setContentDescription("Double tap pour supprimer");
            holder.iconMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.viewForeground.startAnimation(slideToLeft);
                    favoriteAddress.remove(index);
                    notifyItemRemoved(index);
                    prefManager.setFavorisAddress(favoriteAddress);
                }
            });
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
