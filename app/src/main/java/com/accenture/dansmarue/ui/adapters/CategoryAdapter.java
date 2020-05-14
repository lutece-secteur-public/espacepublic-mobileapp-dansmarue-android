package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.PrefManager;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by PK on 30/03/2017.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {

    private PrefManager prefManager;
    private  boolean displayFavoriteItems = false;


    public CategoryAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        prefManager =  new PrefManager(this.getContext());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Category category = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.cat_name);
        ImageView icon = (ImageView) convertView.findViewById(R.id.cat_icon);
        ImageView iconFavoris = (ImageView) convertView.findViewById(R.id.cat_favoris);

        if (category != null) {
            if(displayFavoriteItems && category.getAlias() != null) {
                textView.setText(category.getAlias());
            } else {
                textView.setText(category.getName());
            }

            if (category.isAgent()) {
                textView.setTypeface(null, Typeface.BOLD);
            } else {
                textView.setTypeface(null, Typeface.NORMAL);
            }

            if (Category.ID_FIRST_PARENT.equals(category.getParentId())) {
                if (null != CategoryHelper.CAT_ICONS.get(category.getId())) {
                    icon.setImageResource(CategoryHelper.CAT_ICONS.get(category.getId()));
                } else {
                    icon.setImageResource(0);
                }
                icon.setVisibility(View.VISIBLE);
            } else {
                icon.setVisibility(View.GONE);
            }


            if (!CategoryHelper.hasChildren(category.getId()) && !displayFavoriteItems) {
                //Last level Item
                if(prefManager.getFavorisItems().containsKey(category.getId())) {
                    iconFavoris.setImageResource(R.drawable.ic_full_star);
                } else {
                    iconFavoris.setImageResource(R.drawable.ic_empty_star);
                }

                iconFavoris.setVisibility(View.VISIBLE);
                iconFavoris.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(TAG, "onClick: favoris");
                                if(prefManager.getFavorisItems().containsKey(category.getId())) {
                                    // remove fovoris
                                    iconFavoris.setImageResource(R.drawable.ic_empty_star);
                                    prefManager.setFavorisItem(category,true);
                                } else {
                                    // add favoris
                                    iconFavoris.setImageResource(R.drawable.ic_full_star);
                                    prefManager.setFavorisItem(category,false);
                                }

                            }
                        }
                );
            } else {
                iconFavoris.setVisibility(View.GONE);
            }

        }


        return convertView;
    }

    public void setDisplayFavoriteItems(boolean displayFavoriteItems) {
        this.displayFavoriteItems = displayFavoriteItems;
    }

    public boolean getDisplayFavoriteItems() {
        return this.displayFavoriteItems;
    }
}
