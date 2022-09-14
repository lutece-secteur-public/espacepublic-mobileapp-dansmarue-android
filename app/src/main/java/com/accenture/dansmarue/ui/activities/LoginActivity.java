package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.presenters.LoginPresenter;
import com.accenture.dansmarue.mvp.views.LoginView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * LoginActivity
 * Login view.
 */
public class LoginActivity extends BaseActivity implements LoginView {


    @Inject
    protected LoginPresenter presenter;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.login_screen_input_email)
    protected TextInputEditText login;
    @BindView(R.id.login_screen_input_pwd)
    protected TextInputEditText pwd;


    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.login_screen_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        final String lastUsedEmail = presenter.getLastUsedEmail();
        if (!TextUtils.isEmpty(lastUsedEmail)) {
            login.append(lastUsedEmail);
        }

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

    @OnClick(R.id.login_screen_txt_sign_up)
    public void signUp() {
        Intent intent = new Intent(LoginActivity.this, InternalWebViewActivity.class);
        intent.putExtra(InternalWebViewActivity.WEBSITE_ADDRESS, getString(R.string.url_signup_paris));
        startActivity(intent);
    }

    @OnClick(R.id.login_screen_txt_forgot_pwd)
    public void forgotPassword() {
        Intent intent = new Intent(LoginActivity.this, InternalWebViewActivity.class);
        intent.putExtra(InternalWebViewActivity.WEBSITE_ADDRESS, getString(R.string.url_forgot_pwd));
        startActivity(intent);
    }

    @OnClick(R.id.button_connexion)
    public void onConnection() {
        String myLogin = login.getText().toString();
        String myPwd = pwd.getText().toString();
        boolean valid = true;
        if (TextUtils.isEmpty(myLogin)) {
            login.setError(getString(R.string.required_email));
            valid = false;
        }
        if (TextUtils.isEmpty(myPwd)) {
            pwd.setError(getString(R.string.required_pwd));
            valid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(myLogin).matches()) {
            login.setError(getString(R.string.invalid_email));
            valid = false;
        }

        if (valid) {
            presenter.login(myLogin, myPwd);
        }

    }


    @Override
    public void loginFailed() {
        Toast.makeText(getApplicationContext(), R.string.failed_authentication, Toast.LENGTH_LONG).show();
    }

    @Override
    public void loginSuccess() {
        setResult(RESULT_OK);
        Log.i("Login", "loginSuccess: ");
        finish();
    }


}
