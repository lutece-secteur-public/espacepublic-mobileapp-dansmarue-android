package com.accenture.dansmarue.mvp.presenters;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.TypeEquipement;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.SplashScreenView;
import com.accenture.dansmarue.services.ApiServiceEquipement;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.SiraApiServiceMock;
import com.accenture.dansmarue.services.models.CategoryRequest;
import com.accenture.dansmarue.services.models.CategoryResponse;
import com.accenture.dansmarue.services.models.CheckVersionRequest;
import com.accenture.dansmarue.services.models.CheckVersionResponse;
import com.accenture.dansmarue.services.models.SiraSimpleResponse;
import com.accenture.dansmarue.services.models.equipements.CategoryEquipementResponse;
import com.accenture.dansmarue.services.models.equipements.EquipementRequest;
import com.accenture.dansmarue.services.models.equipements.EquipementResponse;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.NetworkUtils;
import com.accenture.dansmarue.utils.PrefManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PK on 22/03/2017.
 * {@link SplashScreenPresenter} Implementation
 */
public class SplashScreenPresenter extends BasePresenter<SplashScreenView> implements SingleObserver<CategoryResponse> {

    private static final String TAG = SplashScreenPresenter.class.getName();


    private final PrefManager prefManager;
    private final SplashScreenView view;
    private final SiraApiService service;
    private final SiraApiServiceMock serviceMock;
    //private final ApiServiceEquipement apiServiceEquipement;
    private final Application application;

    private final int NB_WS_TO_CALL = 1;
    private int cptWs = 0;
    private int cptWsError = 0;

    @Inject
    public SplashScreenPresenter(final Application application, final SplashScreenView view, final PrefManager prefManager, final SiraApiService service, final SiraApiServiceMock serviceMock) {
        this.application = application;
        this.view = view;
        this.prefManager = prefManager;
        this.service = service;
        this.serviceMock = serviceMock;

    }

    @Override
    protected BaseView getView() {
        return view;
    }

    /**
     * Check is back-office dmr is up.
     */
    public void dmrIsOnline() {
        service.isDmrOnline()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleResponseObserver());
    }

    /**
     * Check if user need update DMR application.
     */
    public void checkVersion() {
        CheckVersionRequest request = new CheckVersionRequest();
        service.checkVersion(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestCheckVersionObserver());
    }

    /**
     * Call the service to retrieve the categories with the last known version
     */
    public void checkAndLoadCategories() {

        CategoryRequest request = new CategoryRequest("0");
        service.getCategories(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

    }

    private class RequestCheckVersionObserver implements SingleObserver<CheckVersionResponse> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(CheckVersionResponse value) {
            try {
                if (null != value && null != value.getAnswer() && null != value.getAnswer().getDerniereVersionObligatoire()) {
                    int compareObli = MiscTools.versionCompare(BuildConfig.VERSION_NAME, value.getAnswer().getDerniereVersionObligatoire());
                    if (compareObli < 0) {
                        view.displayPopupUpdateMandory();
                    } else if (null != value.getAnswer().getAndroidVersionStore()) {
                        int compareCurVersion = MiscTools.versionCompare(BuildConfig.VERSION_NAME, value.getAnswer().getAndroidVersionStore());
                        if (compareCurVersion < 0) {
                            if ("true".equals(value.getAnswer().getAndroidMajObligatoire())) {
                                view.displayPopupUpdateMandory();
                            } else {
                                view.displayPopupUpdateNotMandory();
                            }
                        } else {
                            checkAndLoadCategories();
                        }
                    } else {
                        checkAndLoadCategories();
                    }
                } else {
                    checkAndLoadCategories();
                }

            }catch (Exception e) {
                Log.e(TAG, "Compare Version  error", e);
                checkAndLoadCategories();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError", e);
            checkAndLoadCategories();
        }
    }


    @Override
    public void onSubscribe(Disposable d) {
    }

    /**
     * Callback of the WS {@link SiraApiService#getCategories(CategoryRequest)}.<br>
     * If there is a new version of the data, save the WS response in a json file
     *
     * @param categoryResponses WS Response
     */
    @Override
    public void onSuccess(CategoryResponse categoryResponses) {
        if (null != categoryResponses && null != categoryResponses.getAnswer() && categoryResponses.getAnswer().getStatus().equals(Constants.STATUT_WS_OK)) {
            final String version = categoryResponses.getAnswer().getVersion();


            try {
                //save the new version in a json file on the device
                FileOutputStream fos = application.getApplicationContext().openFileOutput(Constants.FILE_CATEGORIES_JSON, Context.MODE_PRIVATE);
                Writer out = new OutputStreamWriter(fos);
                String strObj = new GsonBuilder().create().toJson(categoryResponses.getAnswer());
                out.write(strObj);
                out.close();

                prefManager.setCatsOutDoor(null);

                //update user favorite items
                Map<String, Category> mapItems = CategoryHelper.answerToCategories(categoryResponses.getAnswer().getCategories());
                Map<String, Category> mapFavoriteItems = prefManager.getFavorisItems();
                Set<String> keyFavoriteItems = mapFavoriteItems.keySet();
                Iterator<String> itr = keyFavoriteItems.iterator();
                while (itr.hasNext()) {
                    String idCategory = itr.next();
                    if(categoryResponses.getAnswer().getCategories().containsKey(idCategory)) {
                        prefManager.setFavorisItem(mapItems.get(idCategory), false);
                    } else {
                        prefManager.setFavorisItem(mapFavoriteItems.get(idCategory), true);
                    }
                }

            } catch (IOException e) {
                FirebaseCrashlytics.getInstance().log(e.getMessage());
                Log.e(TAG, e.getMessage(), e);
            }

            countWSandLaunch();

        } else {
            countWsError();
        }
    }

    @Override
    public void onError(final Throwable e) {

        Log.e(TAG, "onError", e);

        File file = application.getApplicationContext().getFileStreamPath(Constants.FILE_CATEGORIES_JSON);
        if (file.exists()) {
            countWSandLaunch();
            Log.i(TAG, "FILE_CATEGORIES_JSON existe ");
        } else {
            countWsError();
            Log.i(TAG, "FILE_CATEGORIES_JSON existe pas ");
        }

    }

    private class  SimpleResponseObserver implements  SingleObserver<SiraSimpleResponse> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(SiraSimpleResponse siraSimpleResponse) {
            if (! siraSimpleResponse.isOnline()) {
                view.displayDialogDmrOffline();
            } else {
                if ( siraSimpleResponse.getMessageInformation() != null && siraSimpleResponse.getMessageInformation().trim().length() > 0) {
                    view.displayDialogMessageInformation(siraSimpleResponse.getMessageInformation());
                } else {
                    checkVersion();
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            view.displayDialogDmrOffline();
        }
    }

    /**
     * if 3 WS ok ... we continue
     */
    public void countWSandLaunch() {
        cptWs++;
        Log.i(TAG, "countWSandLaunch: " + cptWs + "appels ok : "+cptWs+" - appels KO "+cptWsError);
        if (cptWs == NB_WS_TO_CALL) {
            cptWsError = 0;
            cptWs = 0;
            view.dataReady();
        } else if (cptWs + cptWsError == NB_WS_TO_CALL) {
            cptWsError = 0;
            cptWs = 0;
            view.endCauseNoCategories();
        }
    }

    /**
     * Display one popup for all errors
     */
    public void countWsError() {
        cptWsError++;
        Log.i(TAG, "countWsError: " + cptWsError);
        if (cptWs + cptWsError == NB_WS_TO_CALL) {
            cptWsError = 0;
            cptWs = 0;
            view.endCauseNoCategories();
        }
    }

    /**
     * onDataReady action from SplashScreenActivity.<br>
     * Decides which view to return depending on sharedPreferences.
     */
    public void onDataReady() {
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);
            view.onFirstTimeLaunch();
        } else {
            view.onLaunch();
        }
    }


}
