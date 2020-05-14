package com.accenture.dansmarue.ui.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.accenture.dansmarue.R;

import butterknife.BindView;


public class PrefPrefActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.pref_pref_activity_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        }


    }


    @Override
    protected int getContentView() {
        return R.layout.pref_pref_activity_layout;
    }
}
