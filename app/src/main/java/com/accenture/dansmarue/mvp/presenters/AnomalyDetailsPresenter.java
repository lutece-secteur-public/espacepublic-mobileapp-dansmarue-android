package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.views.AnomalyDetailsView;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.models.CongratulateAnomalieRequest;
import com.accenture.dansmarue.services.models.FollowRequest;
import com.accenture.dansmarue.services.models.GetIncidentByIdRequest;
import com.accenture.dansmarue.services.models.GetIncidentByIdResponse;
import com.accenture.dansmarue.services.models.IncidentResolvedRequest;
import com.accenture.dansmarue.services.models.SiraResponse;
import com.accenture.dansmarue.services.models.SiraSimpleResponse;
import com.accenture.dansmarue.services.models.UnfollowRequest;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by d4v1d on 11/05/2017.
 */
public class AnomalyDetailsPresenter extends BasePresenter implements SingleObserver<SiraResponse> {

    private static final String TAG = AnomalyDetailsPresenter.class.getCanonicalName();
    private AnomalyDetailsView view;
    private PrefManager prefManager;
    private SiraApiService service;
    private Application application;

    private String picture1;

    @Inject
    public AnomalyDetailsPresenter(final AnomalyDetailsView view, final PrefManager prefManager, SiraApiService service, Application application) {
        this.view = view;
        this.prefManager = prefManager;
        this.service = service;
        this.application = application;
    }

    public void followAnomaly(final String incidentId) {
        if (prefManager.isConnected()) {
            FollowRequest request = new FollowRequest(incidentId);
            request.setEmail(prefManager.getEmail());
            request.setGuid(prefManager.getGuid());
            request.setUdid(prefManager.getUdid());
            request.setUserToken(prefManager.getUserToken());
            Log.i(TAG, "followAnomaly: " + prefManager.getUserToken());
            service.follow(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        } else {
            view.inviteToLogin();
        }
    }

    public void unfollowAnomaly(final String incidentId) {
        UnfollowRequest request = new UnfollowRequest(incidentId);
        request.setGuid(prefManager.getGuid());
        request.setUdid(prefManager.getUdid());
        service.unfollow(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    public void congratulateAnomalie(final String incidentId) {
        CongratulateAnomalieRequest request = new CongratulateAnomalieRequest(incidentId);
        service.congratulateAnomalie(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onSuccess(SiraResponse value) {
        if (null != value && value instanceof SiraSimpleResponse) {
            SiraSimpleResponse simpleResponse = (SiraSimpleResponse) value;

            if (simpleResponse.getAnswer() != null && FollowRequest.SERVICE_NAME.equals(simpleResponse.getRequest())) {
                if ("0".equals(simpleResponse.getAnswer().getStatus())) {
                    view.displayFollow();
                } else {
                    view.displayFollowFailure();
                }
            }

            if (simpleResponse.getAnswer() != null && UnfollowRequest.SERVICE_NAME.equals(simpleResponse.getRequest())) {
                if ("0".equals(simpleResponse.getAnswer().getStatus())) {
                    view.displayUnfollow();
                } else {
                    view.displayUnfollowFailure();
                }
            }

            if (simpleResponse.getAnswer() != null && CongratulateAnomalieRequest.SERVICE_NAME.equals(simpleResponse.getRequest())) {
                if ("0".equals(simpleResponse.getAnswer().getStatus())) {
                    view.displayGreetingsOk();
                } else {
                    view.displayGreetingsKo();
                }
            }

            if (simpleResponse.getAnswer() != null && IncidentResolvedRequest.SERVICE_NAME.equals(simpleResponse.getRequest())) {
                if ("0".equals(simpleResponse.getAnswer().getStatus())) {
                    view.displayResolveOk();
                } else {
                    view.displayResolveKo();
                }
            }
        } else if (value instanceof GetIncidentByIdResponse) {
            final GetIncidentByIdResponse incidentByIdResponse = (GetIncidentByIdResponse) value;
            if ("0".equals(incidentByIdResponse.getAnswer().getStatus())) {

                Incident incident = incidentByIdResponse.getAnswer().getIncident();
                incident.setResolvable(incidentByIdResponse.getAnswer().isResolved_authorization());

                final String idParentCategory;
                if (incident.isFromRamen()) {
                    idParentCategory = CategoryHelper.ID_CATEGORIE_RAMEN;
                } else {
                    idParentCategory = CategoryHelper.getFirstParent(incident.getCategoryId(), CategoryHelper.getAllCategories(application));

                }
//                incident.getPictures().setGenericPictureId(CategoryHelper.MAP_GENERIC_PICTURES.get(idParentCategory));
                incident.getPictures().setGenericPictureId(CategoryHelper.MAP_GENERIC_PICTURES.get(idParentCategory));

                view.populateFields(incident);
            } else {
                view.closeActvity();
            }


        }
    }


    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage(), e);
        networkError();
    }

    public void loadIncident(@NonNull final Long incidentId, final String source) {
        GetIncidentByIdRequest request = new GetIncidentByIdRequest(incidentId);
        request.setGuid(prefManager.getGuid());
        if (source == null) {
            request.setSource("DansMaRue");
        } else {
            request.setSource(source);
        }
        service.getIncidentById(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    public boolean isResolvable(@NonNull final String reporterGuid) {
        return (reporterGuid != null && reporterGuid.equals(prefManager.getGuid())) || (prefManager.isConnected() && (prefManager.getEmail().endsWith("@paris.fr") || prefManager.getEmail().endsWith("@derichebourg.com")));
    }

    public void resolveIncident(final String incidentId) {
        final IncidentResolvedRequest request = new IncidentResolvedRequest();
        request.setIncidentId(incidentId);
        request.setGuid(prefManager.getGuid());
        request.setUdid(prefManager.getUdid());
        service.incidentResolved(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    protected BaseView getView() {
        return view;
    }

    public void addPictureToModel(final String path) {
        picture1 = path;
    }

    public void removePicture1() {
        application.deleteFile(picture1.substring(picture1.lastIndexOf('/') + 1, picture1.length()));
        picture1 = null;
    }

    public void savePictureInFile(final Bitmap thumbnail) {
        try {
            final String fileName = new Date().getTime() + Constants.FILE_PICTURE_SUFFIXE;
            FileOutputStream fos = application.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            // 100 is max value
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            thumbnail.recycle();
            fos.close();
            view.showPicture(application.getFileStreamPath(fileName).getAbsolutePath());
        } catch (IOException e) {
            Crashlytics.logException(e);
            Log.e(TAG, e.getMessage(), e);
        }
    }


    public void uploadPictureServiceFait(String id) {
        uploadPictureServiceFaitRequest(Integer.parseInt(id), picture1, "done");
    }

    private void uploadPictureServiceFaitRequest(@NonNull final Integer incidentId, @NonNull final String pictureUrl, final String type) {
        final File picture = new File(pictureUrl);
        final RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), picture);
        final Map<String, String> headers = new HashMap<>();
        headers.put("udid", prefManager.getUdid());
        headers.put("incident_id", incidentId.toString());
        headers.put("type", type);
        headers.put("INCIDENT_CREATION", Boolean.TRUE.toString());
        service.uploadPicture(headers, requestFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(new UploadPictureDoneObserver());
    }

    private class UploadPictureDoneObserver implements SingleObserver<ResponseBody> {

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onSuccess(ResponseBody value) {
            if (null != value) {
                if (picture1 != null) {
                    Log.i(TAG, "onSuccess: photo done");
                    new File(picture1).delete();
                    //removePicture1();
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            networkError();
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
