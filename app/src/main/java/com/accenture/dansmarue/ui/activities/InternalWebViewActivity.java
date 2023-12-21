package com.accenture.dansmarue.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;
import com.accenture.dansmarue.utils.PrefManager;


import butterknife.OnClick;

public class InternalWebViewActivity extends BaseActivity{


    public static final String WEBSITE_ADDRESS = "website_address";
    public static final String EXECUTE_EXTRA_JS = "extra_js";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url  = getIntent().getStringExtra(WEBSITE_ADDRESS);
        boolean executeExtraJs =  getIntent().getBooleanExtra(EXECUTE_EXTRA_JS,false);

        if (url == null || url.isEmpty()) finish();

        setContentView(R.layout.internal_web_view_activity_layout);
        WebView webView = (WebView) findViewById(R.id.dmr_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.loadUrl(url);
        if (executeExtraJs) {
            webView.setWebViewClient(executeJsForMonParisAuthent());
        } else {
            webView.setWebViewClient(new WebViewClient());
        }


        ImageView imageArrowBack = (ImageView) findViewById(R.id.arrow_back_fa);
        imageArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearHistory();
                webView.clearCache(true);
                onBackPressed();
            }
        });
    }


    @Override
    protected int getContentView() {
        return R.layout.internal_web_view_activity_layout;
    }

    @OnClick(R.id.arrow_back_fa)
    public void backType() {
        finish();
    }

    /**
     * Execution d'un code sur la page d'authentification
     * Mon Paris.
     *
     */
    private WebViewClient executeJsForMonParisAuthent() {

        return new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                super.onPageFinished(view, url);
                if(Build.VERSION.SDK_INT >= 19 && !url.startsWith (BuildConfig.URL_SOLEN)){

                    PrefManager prefManager = new PrefManager(getApplicationContext());

                    //injection du Login et Mot de passe
                    String js = "javascript:document.getElementById('username').value='"+prefManager.getEmail()+"';";
                           // +"document.getElementById('password').value='"+prefManager.getMonParisPwd()+"';"
                           // + "document.getElementsByName('Submit')[0].click()";

                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.i("a", s);
                        }
                    });
                }
            }

        };
    }
}
