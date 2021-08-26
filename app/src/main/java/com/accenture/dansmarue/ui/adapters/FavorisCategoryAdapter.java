package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.Category;

import com.accenture.dansmarue.ui.activities.FavoriteCategoryActivity;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.ViewHolder;

import java.util.List;

public class FavorisCategoryAdapter extends RecyclerView.Adapter<ViewHolder> {

    private boolean editMode = false;

    private Context context;
    private List<Category> favoriteCategory;
    PrefManager prefManager;


    public FavorisCategoryAdapter(Context context, List<Category> favoriteCategory) {
        this.context = context;
        this.favoriteCategory = favoriteCategory;
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
        final Category item = favoriteCategory.get(position);

        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! editMode ) {
                    ((FavoriteCategoryActivity) context).onSelectItem(item);
                }
            }
        });
        holder.textView.setText(item.getName());
        if(editMode) {
            holder.iconMove.setVisibility(View.VISIBLE);
        } else {
            holder.iconMove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return favoriteCategory.size();
    }

    public void removeItem(int position) {
        favoriteCategory.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void moveItem(int oldPosition, int newPosition ){
        Category item = favoriteCategory.get(oldPosition);
        favoriteCategory.remove(oldPosition);
        favoriteCategory.add(newPosition, item);
        notifyItemMoved(oldPosition, newPosition);
    }

    public void removeAll() {
        int size = favoriteCategory.size();
        favoriteCategory.clear();
        notifyItemRangeRemoved(0,size);
    }

    public void saveAllChanges() {
        prefManager.setFavorisItem(favoriteCategory);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
