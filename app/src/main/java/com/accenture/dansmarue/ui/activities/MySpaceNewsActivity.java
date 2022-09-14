package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.ui.adapters.NewsAdapter;
import com.accenture.dansmarue.utils.PrefManager;

import java.util.ArrayList;

import butterknife.BindView;

public class MySpaceNewsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.list_news)
    protected ListView listView;

    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.title_mon_espace);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        }


        if (adapter == null) {
            PrefManager prefManager = new PrefManager(getApplicationContext());
            adapter = new NewsAdapter(this, R.layout.news_item, new ArrayList(prefManager.getMySpaceNews().values()));
            listView.setAdapter(adapter);
        }

    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState, final Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        listView.setOnItemClickListener(this);

    }


    @Override
    protected int getContentView() {
        return R.layout.my_space_news_activity_layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
