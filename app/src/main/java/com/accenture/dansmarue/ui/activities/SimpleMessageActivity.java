package com.accenture.dansmarue.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;


import com.accenture.dansmarue.R;
import com.accenture.dansmarue.utils.Constants;

import butterknife.OnClick;


public class SimpleMessageActivity extends BaseActivity {


    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        String htmlAsString = intent.getStringExtra(Constants.EXTRA_MESSAGE_HORS_DMR);

        WebView webView = (WebView) findViewById(R.id.webViewMessage);
        webView.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null);

    }

    @Override
    protected int getContentView()  {
        return R.layout.simple_webview_message;
    }

    @OnClick(R.id.arrow_back_type)
    public void backType() {
        finish();
    }

}
