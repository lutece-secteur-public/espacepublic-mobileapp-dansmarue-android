package com.accenture.dansmarue.ui.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;

import butterknife.BindView;


public class PrefAboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.about_version)
    protected TextView aboutVersion;

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

        aboutVersion.setText(getString(R.string.about_version_title) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ").");
    }

    @Override
    protected int getContentView() {
        return R.layout.pref_about_activity_layout;
    }
}
