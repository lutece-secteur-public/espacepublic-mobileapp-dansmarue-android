package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.webkit.WebView;

import com.accenture.dansmarue.R;

import butterknife.BindView;


public class PrefCGUActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

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


    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        WebView textViewCguPartOne = (WebView)  findViewById(R.id.cgu_one);
        textViewCguPartOne.loadDataWithBaseURL(null, getString(R.string.cgu_part_one), "text/html", "utf-8", null);

        WebView textViewCguPartTwo = (WebView)  findViewById(R.id.cgu_two);
        textViewCguPartTwo.loadDataWithBaseURL(null, getString(R.string.cgu_part_two), "text/html", "utf-8", null);

        WebView textViewCguPartThree = (WebView)  findViewById(R.id.cgu_three);
        textViewCguPartThree.loadDataWithBaseURL(null, getString(R.string.cgu_part_three), "text/html", "utf-8", null);

        WebView textViewCguPartFour = (WebView)  findViewById(R.id.cgu_four);
        textViewCguPartFour.loadDataWithBaseURL(null, getString(R.string.cgu_part_four), "text/html", "utf-8", null);

        WebView textViewCguPartFive = (WebView)  findViewById(R.id.cgu_five);
        textViewCguPartFive.loadDataWithBaseURL(null, getString(R.string.cgu_part_five), "text/html", "utf-8", null);

        WebView textViewCguPartSix = (WebView)  findViewById(R.id.cgu_six);
        textViewCguPartSix.loadDataWithBaseURL(null, getString(R.string.cgu_part_six), "text/html", "utf-8", null);

        WebView textViewCguPartSeven = (WebView)  findViewById(R.id.cgu_seven);
        textViewCguPartSeven.loadDataWithBaseURL(null, getString(R.string.cgu_part_seven), "text/html", "utf-8", null);

        WebView textViewCguPartHeight = (WebView)  findViewById(R.id.cgu_height);
        textViewCguPartHeight.loadDataWithBaseURL(null, getString(R.string.cgu_part_height), "text/html", "utf-8", null);
    }

    @Override
    protected int getContentView() {
        return R.layout.pref_cgu_activity_layout;
    }
}
