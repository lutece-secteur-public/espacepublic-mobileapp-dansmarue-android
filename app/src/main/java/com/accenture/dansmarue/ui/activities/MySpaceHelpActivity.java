package com.accenture.dansmarue.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.ui.adapters.HelpAdapter;
import com.accenture.dansmarue.utils.PrefManager;

import java.util.ArrayList;

import butterknife.BindView;

public class MySpaceHelpActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.list_help)
    protected ListView listView;

    private HelpAdapter adapter;

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
            adapter = new HelpAdapter(this, R.layout.help_item, new ArrayList(prefManager.getMySpaceHelp().values()));
            listView.setAdapter(adapter);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected int getContentView() {
        return R.layout.my_space_help_activity_layout;
    }
}
