package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;
import android.util.Log;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.Position;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.MapParisView;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.models.GetIncidentsByPositionRequest;
import com.accenture.dansmarue.services.models.GetIncidentsByPositionResponse;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PK on 19/04/2017.
 */
public class MapParisPresenter extends BasePresenter<MapParisView> implements SingleObserver<GetIncidentsByPositionResponse> {

    private static final String TAG = MapParisPresenter.class.getCanonicalName();

    private MapParisView view;
    private SiraApiService service;
    private Application application;
    private PrefManager prefManager;

    @Inject
    public MapParisPresenter(final MapParisView view, final SiraApiService service, final Application application, PrefManager prefManager) {
        this.view = view;
        this.service = service;
        this.application = application;
        this.prefManager = prefManager;

    }

    public void locationChanged(final Position newLocation) {
        GetIncidentsByPositionRequest request = new GetIncidentsByPositionRequest();
        request.setPosition(newLocation);
        request.setGuid(prefManager.getGuid());
        service.getIncidentsByPosition(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onSuccess(final GetIncidentsByPositionResponse value) {
        if (null != value && null != value.getAnswer() && value.getAnswer().getStatus().equals(Constants.STATUT_WS_OK)) {
            if (CollectionUtils.isNotEmpty(value.getAnswer().getClosestIncidents())) {
                final List<MarkerOptions> markers = new ArrayList<>(value.getAnswer().getClosestIncidents().size());
                final List<Incident> anomalyList = new ArrayList<>(value.getAnswer().getClosestIncidents().size());
                for (Incident incident : value.getAnswer().getClosestIncidents()) {
                    LatLng newLatLng = new LatLng(Double.parseDouble(incident.getLat()), Double.parseDouble(incident.getLng()));
                    if (incident.isFromRamen()) {
                        // Category "Encombrants"
                        incident.setCategoryId(CategoryHelper.ID_CATEGORIE_RAMEN);
                    }
                    final String idParentCategory = CategoryHelper.getFirstParent(incident.getCategoryId(), CategoryHelper.getAllCategories(application));
                    if (CategoryHelper.CAT_ICONS.get(idParentCategory) != null) {
                        if (incident.isResolu()) {
                            incident.setIconIncidentSignalement(CategoryHelper.MAP_ICONS_RESOLVED.get(idParentCategory));
//                            incident.setIconIncidentSignalement(CategoryHelper.MAP_GENERIC_PICTURES.get(idParentCategory));

                        } else {
                            incident.setIconIncidentSignalement(CategoryHelper.MAP_ICONS.get(idParentCategory));
//                            incident.setIconIncidentSignalement(CategoryHelper.MAP_GENERIC_PICTURES.get(idParentCategory));

                            anomalyList.add(incident);
                        }
//                        incident.getPictures().setGenericPictureId(CategoryHelper.MAP_GENERIC_PICTURES.get(idParentCategory));
//                        incident.getPictures().setGenericPictureId(CategoryHelper.CAT_ICONS.get(idParentCategory));

                        incident.getPictures().setGenericPictureId(CategoryHelper.MAP_GENERIC_PICTURES.get(idParentCategory));


                        // anchor : centre de l'image = position gps
                        markers.add(new MarkerOptions()
                                .position(newLatLng)
                                .anchor(0.5f,0.5f)
                                .snippet(incident.toJson())
                                .icon(BitmapDescriptorFactory.fromResource(incident.getIconIncidentSignalement())));
                    }
                }
                view.updateAnomalyMarkers(markers);

                view.updateAnomalyList(anomalyList);
                view.updatePosMarker();
            } else {
                view.clearAnomaly();
                view.updateAnomalyList(null);
                view.updatePosMarker();
            }
        } else {
            view.clearAnomaly();
            view.updateAnomalyList(null);
            view.updatePosMarker();
        }

    }

    @Override
    public void onError(final Throwable e) {
        Log.e(TAG, e.getMessage(), e);
        networkError();
    }


    /**
     * Check if the given location is inside the given bounds. <br>
     * Callback to a different method in the view wether the location is correct or not.
     *
     * @param location location to check
     * @param bounds   bounds
     */
    public void validateNewLocation(final LatLng location, final LatLngBounds bounds) {
        if (bounds.contains(location)) {
            view.locationChanged(location);
        } else {
            view.invalidLocation();
        }
    }


    @Override
    protected BaseView getView() {
        return view;
    }
}