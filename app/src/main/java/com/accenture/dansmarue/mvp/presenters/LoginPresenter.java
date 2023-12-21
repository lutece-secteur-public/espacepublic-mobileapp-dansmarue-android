package com.accenture.dansmarue.mvp.presenters;

import android.util.Log;

import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.LoginView;
import com.accenture.dansmarue.services.AuthentParisAccountService;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.models.IdentityRequest;
import com.accenture.dansmarue.services.models.IdentityResponse;
import com.accenture.dansmarue.services.models.UserInfoResponse;
import com.accenture.dansmarue.utils.PrefManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Callback;

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



    public void getUserInfo(final String accessToken, final String idAccessToken) {

        prefManager.setMonidAccessToken(idAccessToken);

        final Map<String, String> headers = new HashMap<>(2);
        headers.put("Authorization", "Bearer "+accessToken);

        authentService.userInfo(headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<UserInfoResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onSuccess(UserInfoResponse value) {
                        if (value.isValidatedAccount()) {
                            prefManager.setConnected(value.getMail(), value.getGuid());

                            prefManager.setEmail(value.getMail());

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
                                                prefManager.setIsAgent(value.getAnswer().getUser().isAgent());
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
                    }
                });
    }

    public void logout(String idToken) {
        authentService.logout(idToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                prefManager.disconnect();
                view.afterLogout();
                Log.i(TAG, "logout OK");
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                view.afterLogout();
                Log.e(TAG, "onError: ", t);
            }
        });
    }

    @Override
    protected BaseView getView() {
        return view;
    }

}
