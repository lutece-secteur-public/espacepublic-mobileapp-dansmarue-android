package com.accenture.dansmarue.mvp.presenters;

import android.util.Log;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.WelcomeMapView;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.models.CongratulateAnomalieRequest;
import com.accenture.dansmarue.services.models.FollowRequest;
import com.accenture.dansmarue.services.models.SaveInfosApresTourneeRequest;
import com.accenture.dansmarue.services.models.SiraSimpleResponse;
import com.accenture.dansmarue.services.models.UnfollowRequest;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PK on 19/04/2017.
 */
public class WelcomeMapPresenter extends BasePresenter<WelcomeMapView> implements SingleObserver<SiraSimpleResponse> {


    private static final String TAG = WelcomeMapPresenter.class.getCanonicalName();
    private WelcomeMapView view;
    private PrefManager prefManager;
    private SiraApiService service;

    @Inject
    public WelcomeMapPresenter(final WelcomeMapView view, final PrefManager prefManager, SiraApiService service) {
        this.view = view;
        this.prefManager = prefManager;
        this.service = service;
    }

    public void profileClicked() {
        view.showMySpace();
    }

    /**
     * Call WS to fallow incident.
     * @param incidentId
     *         incident id.
     */
    public void followAnomaly(final String incidentId) {
        if (prefManager.isConnected()) {
            FollowRequest request = new FollowRequest(incidentId);
            request.setEmail(prefManager.getEmail());
            request.setGuid(prefManager.getGuid());
            request.setUdid(prefManager.getUdid());
            request.setUserToken(prefManager.getUserToken());
            service.follow(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        } else {
            view.inviteToLogin();
        }

    }

    /**
     * Call WS to unfallow incident.
     * @param incidentId
     *         incident id.
     */
    public void unfollowAnomaly(final String incidentId) {
        UnfollowRequest request = new UnfollowRequest(incidentId);
        request.setGuid(prefManager.getGuid());
        request.setUdid(prefManager.getUdid());
        service.unfollow(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    /**
     * Show incident on click thumbnail.
     *
     * @param summarizedIncident
     *         incident to show
     */
    public void onThumbnailClicked(Incident summarizedIncident) {
        if ( ! summarizedIncident.isFromRamen()) {
            view.showIncidentDetails(summarizedIncident);
        } else {
            Log.i(TAG, "onThumbnailClicked: is from Ramen do nothing");
        }
    }

    /**
     * Congrulate for service done.
     * @param incidentId
     *         id incident
     */
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

    public void saveInfoApresTournee(final int idFdt, final String messageInfoApresFdt) {
        SaveInfosApresTourneeRequest request = new SaveInfosApresTourneeRequest();
        request.setIdFDT(idFdt);
        request.setInfosApresTournee(messageInfoApresFdt);
        service.saveInfoApresTournee(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onSuccess(SiraSimpleResponse value) {
        if (value.getAnswer() != null && value.getAnswer().getStatus().equals(Constants.STATUT_WS_OK) && FollowRequest.SERVICE_NAME.equals(value.getRequest())) {
            if ("0".equals(value.getAnswer().getStatus())) {
                view.displayFollow();
            } else {
                view.displayFollowFailure();
            }
        }

        if (value.getAnswer() != null && UnfollowRequest.SERVICE_NAME.equals(value.getRequest())) {
            if ("0".equals(value.getAnswer().getStatus())) {
                view.displayUnfollow();
            } else {
                view.displayUnfollowFailure();
            }
        }

        if (value.getAnswer() != null && CongratulateAnomalieRequest.SERVICE_NAME.equals(value.getRequest())) {
            if ("0".equals(value.getAnswer().getStatus())) {
                view.displayGreetingsOk();
            } else {
                view.displayGreetingsKo();
            }
        }

        if (value.getAnswer() != null && SaveInfosApresTourneeRequest.SERVICE_NAME.equals(value.getRequest())) {
            if ("0".equals(value.getAnswer().getStatus())) {
                view.displaySaveInfosApresTournee();
            }
        }


    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage(), e);
        networkError();
    }

    @Override
    protected BaseView getView() {
        return view;
    }

}
