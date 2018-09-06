package com.accenture.dansmarue.mvp.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.LoginView;
import com.accenture.dansmarue.services.AuthentParisAccountService;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.models.AuthenticationResponse;
import com.accenture.dansmarue.services.models.IdentityRequest;
import com.accenture.dansmarue.services.models.IdentityResponse;
import com.accenture.dansmarue.services.models.UserValidResponse;
import com.accenture.dansmarue.utils.PrefManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PK on 18/04/2017.
 */

public class LoginPresenter extends BasePresenter<LoginView> {

    private static final String TAG = LoginPresenter.class.getName();

    private LoginView view;
    private PrefManager prefManager;
    private AuthentParisAccountService authentService;
    private SiraApiService siraService;

    @Inject
    public LoginPresenter(final LoginView view, final PrefManager prefManager, final AuthentParisAccountService authentService, final SiraApiService siraService) {
        this.view = view;
        this.prefManager = prefManager;
        this.authentService = authentService;
        this.siraService = siraService;
    }

    public String getLastUsedEmail() {
        return prefManager.getEmail();
    }

    /**
     * TODO refacto ?
     *
     * @param myLogin
     * @param myPwd
     */
    public void login(@NonNull final String myLogin, @NonNull final String myPwd) {

        final Map<String, String> headers = new HashMap<>(2);
        headers.put("X-OpenAM-Username", myLogin);
        headers.put("X-OpenAM-Password", myPwd);

        authentService.authenticate(headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AuthenticationResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(AuthenticationResponse response) {
                        final Map<String, String> headers = new HashMap<>(1);
                        headers.put(BuildConfig.AUTHENT_HEADER, response.getTokenId());
                        authentService.userValid(headers, myLogin)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<UserValidResponse>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(UserValidResponse value) {
                                        if (value.isValidatedAccount()) {
                                            prefManager.setConnected(value.getMail(), value.getGuid());

                                            // A ce stade : compte ok mais pas encore de récupération des autres infos ; cependant je fixe l'email
                                            prefManager.setEmail(value.getMail());
                                            Log.i(TAG, "onSuccess Step Connexion Compte 1 : "+value.getMail());

                                            siraService.getUserInformations(new IdentityRequest(prefManager.getGuid()))
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new SingleObserver<IdentityResponse>() {
                                                        @Override
                                                        public void onSubscribe(Disposable d) {

                                                        }

                                                        @Override
                                                        public void onSuccess(IdentityResponse value) {
                                                            if (value.getAnswer() != null && "0".equals(value.getAnswer().getStatus())) {
                                                                prefManager.setFirstName(value.getAnswer().getUser().getFirstname());
                                                                prefManager.setLastName(value.getAnswer().getUser().getName());
                                                                prefManager.setEmail(value.getAnswer().getUser().getMail());
                                                                view.loginSuccess();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            Log.e(TAG, "onError: ", e);
                                                            view.loginFailed();
                                                        }
                                                    });
                                        } else {
                                            view.loginFailed();
                                        }
                                    }


                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "onError: ", e);
                                        if (e instanceof IOException) {
                                            networkError();
                                        } else {
                                            view.loginFailed();
                                        }
                                    }
                                });

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        if (e instanceof IOException) {
                            networkError();
                        } else {
                            view.loginFailed();
                        }
                    }
                });
    }

    @Override
    protected BaseView getView() {
        return view;
    }

}
