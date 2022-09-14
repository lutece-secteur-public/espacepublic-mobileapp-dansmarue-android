package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.services.models.MySpaceHelpResponse;
import com.accenture.dansmarue.ui.activities.InternalWebViewActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import static android.content.ContentValues.TAG;


public class HelpAdapter extends ArrayAdapter<MySpaceHelpResponse.Answer.Help> {

    public HelpAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<MySpaceHelpResponse.Answer.Help> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final MySpaceHelpResponse.Answer.Help help = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.help_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.help_libelle);
        textView.setText(help.getLibelle());
        textView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: news");
                        openNewsInBrowser(help.getUrl());
                    }
                }
        );

        ImageView imageNews = (ImageView) convertView.findViewById(R.id.help_image);
        Glide.with(getContext()).load(help.getImage()).into(imageNews);

        return convertView;
    }

    private void openNewsInBrowser(String url) {
        Intent intent = new Intent(getContext(), InternalWebViewActivity.class);
        intent.putExtra(InternalWebViewActivity.WEBSITE_ADDRESS, url);
        getContext().startActivity(intent);
    }

}
