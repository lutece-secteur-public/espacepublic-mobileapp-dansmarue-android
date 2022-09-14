package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.utils.CategoryHelper;

import java.util.ArrayList;
import java.util.List;

public class CategorySearchAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<Category> categoryList = null;
    private ArrayList<Category> arraylist;

    public CategorySearchAdapter(Context context, List<Category> categoryList) {
        mContext = context;
        this.categoryList = categoryList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Category>();
        this.arraylist.addAll(categoryList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.category, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.cat_name);
            view.setTag(holder);

            ((ImageView)view.findViewById(R.id.cat_icon)).setVisibility(View.GONE);
            ((ImageView)view.findViewById(R.id.cat_favoris)).setVisibility(View.GONE);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(CategoryHelper.getCompleteCategoryHierarchy(categoryList.get(position)));
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase();
        categoryList.clear();
        if (charText.length() == 0) {
            categoryList.addAll(arraylist);
        } else {
            for (Category cat : arraylist) {
                if (CategoryHelper.getCompleteCategoryHierarchy(cat).toLowerCase().contains(charText)) {
                    categoryList.add(cat);
                }
            }
        }
        notifyDataSetChanged();
    }
}
