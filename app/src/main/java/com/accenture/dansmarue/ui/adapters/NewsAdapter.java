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
import com.accenture.dansmarue.services.models.MySpaceNewsResponse;
import com.accenture.dansmarue.ui.activities.InternalWebViewActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import static android.content.ContentValues.TAG;

public class NewsAdapter extends ArrayAdapter<MySpaceNewsResponse.Answer.News> {

    public NewsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<MySpaceNewsResponse.Answer.News> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final MySpaceNewsResponse.Answer.News news = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.news_title);
        WebView webView = (WebView) convertView.findViewById(R.id.news_text);

        textView.setText(news.getLibelle());
        webView.loadDataWithBaseURL(null, news.getTexte(), "text/html", "utf-8", null);

        textView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (webView.getVisibility() == View.GONE) {
                            webView.setVisibility(View.VISIBLE);
                        } else {
                            webView.setVisibility(View.GONE);
                        }

                    }
                }
        );

        ImageView imageNews = (ImageView) convertView.findViewById(R.id.news_image);
        Glide.with(getContext()).load(news.getImage()).into(imageNews);

        return convertView;
    }


}
