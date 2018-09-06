package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.utils.MiscTools;

import java.util.List;

/**
 * Created by PK on 30/03/2017.
 */
public class CategoryEquipementAdapter extends ArrayAdapter<Category> {


    public CategoryEquipementAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
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

        if (category != null) {
            textView.setText(category.getName());

            Log.i("cat", "name : " + category.getName() + "id : " + category.getParentId());

            if (Category.ID_FIRST_PARENT.equals(category.getParentId())) {

                    if (null != category.getImageMobile() && !("").equals(category.getImageMobile())) {
                        try {
                            icon.setImageBitmap(MiscTools.base64ToBitmap(category.getImageMobile(), 54));
                        } catch (Exception e) {
                            icon.setImageResource(0);
                        }

                }
                icon.setVisibility(View.VISIBLE);

            } else {
                icon.setVisibility(View.GONE);
            }

        }
        return convertView;
    }
}
