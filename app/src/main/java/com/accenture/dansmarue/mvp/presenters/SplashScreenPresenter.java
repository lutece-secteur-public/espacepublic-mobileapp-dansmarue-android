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
import com.accenture.dansmarue.services.models.MySpaceHelpResponse;
import com.accenture.dansmarue.services.models.MySpaceNewsResponse;
import com.accenture.dansmarue.services.models.SiraSimpleResponse;
import com.accenture.dansmarue.services.models.equipements.CategoryEquipementResponse;
import com.accenture.dansmarue.services.models.equipements.EquipementRequest;
import com.accenture.dansmarue.services.models.equipements.EquipementResponse;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
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
    private final ApiServiceEquipement apiServiceEquipement;
    private final Application application;

    private final int NB_WS_TO_CALL = 3;
    private int cptWs = 0;
    private int cptWsError = 0;

    @Inject
    public SplashScreenPresenter(final Application application, final SplashScreenView view, final PrefManager prefManager, final SiraApiService service, final SiraApiServiceMock serviceMock, final ApiServiceEquipement apiServiceEquipement) {
        this.application = application;
        this.view = view;
        this.prefManager = prefManager;
        this.service = service;
        this.serviceMock = serviceMock;
        this.apiServiceEquipement = apiServiceEquipement;
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

        CategoryRequest requestEquipementCat = new CategoryRequest("0");
        apiServiceEquipement.getCategoriesEquipement(requestEquipementCat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestAnomaliesEquipementObserver());

        EquipementRequest equipementRequest = new EquipementRequest("0");
        apiServiceEquipement.getEquipements(equipementRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestEquipementListObserver());

    }

    /**
     * Call the service to retrieve news and help in back office
     */
    public void loadNewsAndHelp() {

        service.getMySpaceNews(prefManager.getMySpaceNewsVersion()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestNewsListObserver());

        service.getMySpaceHelp(prefManager.getMySpaceHelpVersion()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestHelpListObserver());
    }

    private class RequestEquipementListObserver implements SingleObserver<EquipementResponse> {

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onSuccess(EquipementResponse value) {
            if (null != value) {

                Log.i(TAG, "onSuccess: " + value);
                try {
                    //save the new version in a json file on the device
                    FileOutputStream fos = application.getApplicationContext().openFileOutput(Constants.FILE_LIST_EQUIPEMENTS, Context.MODE_PRIVATE);

                    try(Writer out = new OutputStreamWriter(fos)) {
                        String strObj = new GsonBuilder().create().toJson(value.getAnswer());
                        out.write(strObj);
                    }
                } catch (IOException e) {
                    FirebaseCrashlytics.getInstance().log(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }

                Log.i(TAG, "liste id types equipement : " + value.getAnswer().getEquipements().get("0").getChildrenIds());
                List<String> liste_id_equipements = value.getAnswer().getEquipements().get("0").getChildrenIds();

                // Liste des équipements municipaux avec ID - nom - msgErreur
                List<TypeEquipement> maListeDeTypesDequipements = new ArrayList<>();
                for (String typeIde : liste_id_equipements) {
                    Log.i(TAG, "Equipement > id : " + typeIde
                            + " - nom : " + value.getAnswer().getEquipements().get(typeIde).getName()
                            + " - msg no equipement : " + value.getAnswer().getEquipements().get(typeIde).getMsg_alert_no_equipement()
                            + " - msg photo : " + value.getAnswer().getEquipements().get(typeIde).getMsg_alert_photo()
                            + " - msg searchbar : " + value.getAnswer().getEquipements().get(typeIde).getPlaceholder_searchbar()
                            + " - icon : " + value.getAnswer().getEquipements().get(typeIde).getIcon()
                            + " - image : " + value.getAnswer().getEquipements().get(typeIde).getImage()
                    );

                    TypeEquipement typeEquipement = new TypeEquipement();
                    typeEquipement.setIdTypEquipement(typeIde);
                    typeEquipement.setNomTypeEquipement(value.getAnswer().getEquipements().get(typeIde).getName());
                    typeEquipement.setIconTypeEquipement(value.getAnswer().getEquipements().get(typeIde).getIcon());
                    typeEquipement.setImageTypeEquipement(value.getAnswer().getEquipements().get(typeIde).getImage());
                    typeEquipement.setMsg_alert_no_equipement(value.getAnswer().getEquipements().get(typeIde).getMsg_alert_no_equipement());
                    typeEquipement.setMsg_alert_photo(value.getAnswer().getEquipements().get(typeIde).getMsg_alert_photo());
                    typeEquipement.setPlaceholder_searchbar(value.getAnswer().getEquipements().get(typeIde).getPlaceholder_searchbar());
                    typeEquipement.setLibelleEcranMobile(value.getAnswer().getEquipements().get(typeIde).getLibelleEcranMobile());

                    List<Equipement> maListe = new ArrayList<>();
                    for (String allIds : value.getAnswer().getEquipements().keySet()) {
                        if (null != value.getAnswer().getEquipements().get(allIds).getParentId() && value.getAnswer().getEquipements().get(allIds).getParentId().equals(typeIde)) {
                            Log.i(TAG, "nom piscine : " + value.getAnswer().getEquipements().get(allIds).getName());

                            Equipement e = new Equipement();
                            e.setName(value.getAnswer().getEquipements().get(allIds).getName());
                            e.setAdresse(value.getAnswer().getEquipements().get(allIds).getAdresse());
                            e.setId(allIds);
                            e.setLatitude(value.getAnswer().getEquipements().get(allIds).getLatitude());
                            e.setLongitude(value.getAnswer().getEquipements().get(allIds).getLongitude());
                            e.setIconEquipement(value.getAnswer().getEquipements().get(typeIde).getIcon());
                            // Check if TypeIde OK
                            e.setParentId(typeIde);
                            e.setType_equipement_id(typeIde);

                            maListe.add(e);
                        }
                    }
                    typeEquipement.setListEquipementByType(maListe);
                    maListeDeTypesDequipements.add(typeEquipement);
                }

                // Sauvegarde des types d'équipements en Shared Prefs
                if ( ! maListeDeTypesDequipements.isEmpty()) {
                    prefManager.setTypesEquipement(maListeDeTypesDequipements);
                } else {
                    prefManager.setTypesEquipement(null);
                }

                // Setup equipementByDefault null => ano outdoor
                prefManager.setEquipementTypeByDefault(null);

                // In SharedPrefs, Map between
                Map<String, String> equipementIdMapTypeEquipementId = new HashMap<>();
                for (TypeEquipement type : maListeDeTypesDequipements) {
                    Log.i(TAG, "mon Type: " + type.getNomTypeEquipement());
                    for (Equipement e : type.getListEquipementByType()) {
                        Log.i(TAG, "nom du type : " + type.getNomTypeEquipement() + " + id type : " + type.getIdTypEquipement() + "equipement " + e.getName() + " et id : " + e.getId());
                        equipementIdMapTypeEquipementId.put(e.getId(), type.getIdTypEquipement());
                    }
                }
                prefManager.setEquipementIdMapTypeEquipementId(equipementIdMapTypeEquipementId);

                countWSandLaunch();

            } else {
                countWsError();
            }

        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError", e);
            countWSandLaunch();
            prefManager.setTypesEquipement(null);
        }
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
                            loadNewsAndHelp();
                        }
                    } else {
                        checkAndLoadCategories();
                        loadNewsAndHelp();
                    }
                } else {
                    checkAndLoadCategories();
                    loadNewsAndHelp();
                }

            }catch (Exception e) {
                Log.e(TAG, "Compare Version  error", e);
                checkAndLoadCategories();
                loadNewsAndHelp();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError", e);
            checkAndLoadCategories();
            loadNewsAndHelp();
        }
    }


    private class RequestAnomaliesEquipementObserver implements SingleObserver<CategoryEquipementResponse> {

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onSuccess(CategoryEquipementResponse value) {
            if (null != value) {
                Log.i(TAG, "onSuccess: " + value);
                try {
                    //save the new version in a json file on the device
                    FileOutputStream fos = application.getApplicationContext().openFileOutput(Constants.FILE_LIST_ANOS_PAR_EQUIPEMENT, Context.MODE_PRIVATE);
                    try(Writer out = new OutputStreamWriter(fos)) {
                        String strObj = new GsonBuilder().create().toJson(value.getAnswer());
                        Log.i(TAG, "Liste des catégories équipements : " + strObj);
                        out.write(strObj);
                    }

                    prefManager.setCatsInDoor(null);

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
        public void onError(Throwable e) {
            Log.e(TAG, "onError", e);
            countWSandLaunch();
        }
    }

    private class RequestNewsListObserver implements  SingleObserver<MySpaceNewsResponse> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(MySpaceNewsResponse mySpaceNewsResponse) {
           if (null != mySpaceNewsResponse && null != mySpaceNewsResponse.getAnswer() && ! mySpaceNewsResponse.getAnswer().getNews().isEmpty()) {
              prefManager.setMyspaceNews(mySpaceNewsResponse.getAnswer().getNews());
              prefManager.setMySpaceNewsVersion(mySpaceNewsResponse.getAnswer().getVersion());
           }
        }

        @Override
        public void onError(Throwable e) {

        }
    }


    private class RequestHelpListObserver implements  SingleObserver<MySpaceHelpResponse> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(MySpaceHelpResponse mySpaceHelpResponse) {
            if (null != mySpaceHelpResponse && null != mySpaceHelpResponse.getAnswer() && ! mySpaceHelpResponse.getAnswer().getAides().isEmpty()) {
                prefManager.setMyspaceHelp(mySpaceHelpResponse.getAnswer().getAides());
                prefManager.setMySpaceHelpVersion(mySpaceHelpResponse.getAnswer().getVersion());
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError", e);
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
                try(Writer out = new OutputStreamWriter(fos)) {
                    String strObj = new GsonBuilder().create().toJson(categoryResponses.getAnswer());
                    out.write(strObj);
                }

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
