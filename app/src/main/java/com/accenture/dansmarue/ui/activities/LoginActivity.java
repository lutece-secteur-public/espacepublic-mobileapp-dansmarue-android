package com.accenture.dansmarue.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.presenters.LoginPresenter;
import com.accenture.dansmarue.mvp.views.LoginView;
import com.accenture.dansmarue.utils.PrefManager;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.CodeVerifierUtil;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.browser.BrowserAllowList;
import net.openid.appauth.browser.VersionedBrowserMatcher;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * LoginActivity
 * Login view.
 */
public class LoginActivity extends BaseActivity implements LoginView {

    @Inject
    protected LoginPresenter presenter;


    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private static final String URL_AUTHORIZATION = BuildConfig.AUTHENT_API_URL + "auth";
    private static final String URL_TOKEN_EXCHANGE = BuildConfig.AUTHENT_API_URL + "token";
    private static final String URI_REDIRECT = "fr.paris.android.signalement:/oauth2redirect";
    private static final String clientID = BuildConfig.RHSSO_CLIENT_ID;

    private AuthState authState;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (prefManager.isConnected()) {
            presenter.logout(prefManager.getMonParisidAccessToken());
        } else {
            AuthorizationService authorizationService = buildAuthService();
            AuthorizationRequest authRequest = buildAuthRequest();
            try {
                Intent authIntent = authorizationService.getAuthorizationRequestIntent(authRequest);
                performAuthentification(authIntent, authorizationService);
            } catch (Exception e) {
                Log.e("Login", "loginFailure ");
            }
        }


        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.login_screen_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

    }

    private AuthorizationRequest buildAuthRequest() {

        AuthorizationServiceConfiguration authConfig = new AuthorizationServiceConfiguration(
                Uri.parse(URL_AUTHORIZATION), // authorization endpoint
                Uri.parse(URL_TOKEN_EXCHANGE) // token endpoint
        );

        String codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier();
        String codeChallenge = CodeVerifierUtil.deriveCodeVerifierChallenge(codeVerifier);

        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(authConfig, clientID, ResponseTypeValues.CODE, Uri.parse(URI_REDIRECT));
        authRequestBuilder.setCodeVerifier(codeVerifier, codeChallenge, CodeVerifierUtil.getCodeVerifierChallengeMethod());
        authRequestBuilder.setScopes("profile", "openid");

        AuthorizationRequest authRequest = authRequestBuilder.build();

        return authRequest;
    }

    private AuthorizationService buildAuthService() {

        AppAuthConfiguration appAuthConfiguration = new AppAuthConfiguration.Builder().setBrowserMatcher(new BrowserAllowList(
                VersionedBrowserMatcher.CHROME_BROWSER,
                VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                VersionedBrowserMatcher.FIREFOX_CUSTOM_TAB,
                VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
        )).build();

        return new AuthorizationService(getApplication(), appAuthConfiguration);
    }

    private void performAuthentification(Intent authentificationIntent, AuthorizationService authorizationService) {
        ActivityResultLauncher<Intent> authResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == Activity.RESULT_OK) {

                AuthorizationResponse authorizationResponse = AuthorizationResponse.fromIntent(result.getData());
                AuthorizationException authorizationException = AuthorizationException.fromIntent(result.getData());
                authState = new AuthState(authorizationResponse, authorizationException);

                if (authorizationResponse != null) {
                    TokenRequest tokenRequest = authorizationResponse.createTokenExchangeRequest();

                    authorizationService.performTokenRequest(tokenRequest, (response, ex) -> {
                        if (ex != null) {
                            authState = new AuthState();
                        } else if (response != null) {
                            authState.update(response, ex);
                        }

                        if (authState.getAccessToken() != null) {
                            authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction() {
                                @Override
                                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException ex) {
                                    presenter.getUserInfo(authState.getAccessToken(), authState.getIdToken());
                                }
                            });
                        }
                    });

                }

            } else {
                finish();
            }
        });


        authResult.launch(authentificationIntent);

    }

    // Avoid user to quit the app
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected int getContentView() {
        return R.layout.login_activity_layout;
    }

    @Override
    protected void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void loginFailed() {
        Toast.makeText(getApplicationContext(), R.string.failed_authentication, Toast.LENGTH_LONG).show();

        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (prefManager.isConnected()) {
            presenter.logout(prefManager.getMonParisidAccessToken());
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }
    }

    @Override
    public void loginSuccess() {
        setResult(RESULT_OK);
        Log.i("Login", "loginSuccess: ");
        finish();
    }

    @Override
    public void afterLogout() {
        finish();
    }


}

