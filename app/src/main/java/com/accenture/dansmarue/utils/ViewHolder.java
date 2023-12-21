package com.accenture.dansmarue.utils;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accenture.dansmarue.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public TextView deleteTextView;
    public ImageView iconMove;
    public RelativeLayout viewBackground, viewForeground;

    public ViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.address_text);
        deleteTextView = view.findViewById(R.id.deleteTextView);
        iconMove = view.findViewById(R.id.move_icon);
        viewBackground = view.findViewById(R.id.view_background);
        viewForeground = view.findViewById(R.id.view_foreground);
    }
}
