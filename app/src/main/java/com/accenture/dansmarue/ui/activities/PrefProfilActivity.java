package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.utils.PrefManager;

import butterknife.BindView;
import butterknife.OnClick;


public class PrefProfilActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.profil_lastname)
    protected TextView lastName;

    @BindView(R.id.profil_firstname)
    protected TextView firstName;

    @BindView(R.id.profil_email)
    protected TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.pref_profil_activity_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        }

        PrefManager prefManager = new PrefManager(getApplicationContext());
        email.setText(prefManager.getEmail());

        if (null != prefManager.getLastName()) lastName.setText(prefManager.getLastName());

        if (null != prefManager.getFirstName()) firstName.setText(prefManager.getFirstName());


    }

    @OnClick(R.id.profil_deco)
    public void decoDMR() {
        Uri webpage = Uri.parse(getString(R.string.url_full_profile));
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.pref_profil_activity_layout;
    }
}
