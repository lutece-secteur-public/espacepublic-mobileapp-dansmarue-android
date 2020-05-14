package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;
import android.util.Log;

import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.Position;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.TypeEquipement;
import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.MapParisEquipementView;
import com.accenture.dansmarue.services.ApiServiceEquipement;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.SiraApiServiceMock;
import com.accenture.dansmarue.services.models.equipements.GetEquipementsByPositionRequest;
import com.accenture.dansmarue.services.models.equipements.GetEquipementsByPositionResponse;
import com.accenture.dansmarue.services.models.equipements.GetIncidentsByEquipementRequest;
import com.accenture.dansmarue.services.models.equipements.GetIncidentsByEquipementResponse;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.PrefManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PK on 19/04/2017.
 */
public class MapParisEquipementPresenter extends BasePresenter<MapParisEquipementView> {

    private static final String TAG = MapParisEquipementPresenter.class.getCanonicalName();

    private MapParisEquipementView view;
    private SiraApiService service;
    private Application application;
    private PrefManager prefManager;
    private SiraApiServiceMock serviceMock;
    private ApiServiceEquipement apiServiceEquipement;

    @Inject
    public MapParisEquipementPresenter(final MapParisEquipementView view, final SiraApiService service, final Application application, PrefManager prefManager, final SiraApiServiceMock serviceMock, final ApiServiceEquipement apiServiceEquipement) {
        this.view = view;
        this.service = service;
        this.application = application;
        this.prefManager = prefManager;
        this.serviceMock = serviceMock;
        this.apiServiceEquipement = apiServiceEquipement;
    }

    public void locationChanged(final Position newLocation) {
        if(null!=prefManager.getEquipementTypeByDefault()) {
            GetEquipementsByPositionRequest request = new GetEquipementsByPositionRequest();
            request.setPosition(newLocation);
            request.setTypeEquipementId(prefManager.getEquipementTypeByDefault().getIdTypEquipement());
            apiServiceEquipement.getEquipementsByPosition(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RequestEquipementsByPositionObserver());
        }
    }

    public boolean checkTypeCurrentEquipementIsSelected(String typeEquipementId){

        if(prefManager.getEquipementTypeByDefault().getIdTypEquipement().equals(typeEquipementId)){
            return true;
        }else{
            return false;

        }

    }

    private class RequestEquipementsByPositionObserver implements SingleObserver<GetEquipementsByPositionResponse> {

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onSuccess(GetEquipementsByPositionResponse value) {

            if (null != value) {

                final List<MarkerOptions> markers = new ArrayList<>();

                if (null != value.getAnswer().getEquipementsByProximityList()) {

                    for (String allIds : value.getAnswer().getEquipementsByProximityList().keySet()) {

                        Equipement e = value.getAnswer().getEquipementsByProximityList().get(allIds);
                        e.setId(allIds);
                        e.setType_equipement_id(prefManager.getEquipementTypeByDefault().getTypeEquipementId());
                        e.setIconEquipement(prefManager.getEquipementTypeByDefault().getIconTypeEquipement());

                        String str = new GsonBuilder().create().toJson(e);
                        Log.i(TAG, "infos du marker : " + str);

                        LatLng newLatLng = new LatLng(value.getAnswer().getEquipementsByProximityList().get(allIds).getLatitude(), value.getAnswer().getEquipementsByProximityList().get(allIds).getLongitude());

                        // image de l'icône
                        String imageString = prefManager.getEquipementTypeByDefault().getIconTypeEquipement();
                        Log.i(TAG, "onSuccess: icon "+imageString);

                        markers.add(new MarkerOptions()
                                .position(newLatLng)
                                .snippet(str)
                                .icon(BitmapDescriptorFactory.fromBitmap(MiscTools.base64ToBitmap(imageString, 54))));
                    }

                    view.updateAnomalyMarkers(markers);
                    view.updateAnomalyList(null);
                    view.updatePosMarker();

                }
            }
        }

        @Override
        public void onError(Throwable e) {
        }
    }

    public void callListIncidentsByEquipement(String equipementId) {
        GetIncidentsByEquipementRequest request = new GetIncidentsByEquipementRequest();
        request.setEquipementId(equipementId);
        apiServiceEquipement.getIncidentsByEquipement(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestListIncidentsByEquipementObserver());
    }

    private class RequestListIncidentsByEquipementObserver implements SingleObserver<GetIncidentsByEquipementResponse> {

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onSuccess(GetIncidentsByEquipementResponse value) {

            Log.i(TAG, "onSuccess ");
            Integer nbAnomalies = 0;

            if (null != value) {

                String allIds = prefManager.getEquipementIdSelected() + "";
                List<Incident> maListe = null;

                Log.i(TAG, "onSuccess: " + value.toString());

                if (null != value.getAnswer()) {

                    List<Incident> anomalyList = new ArrayList<>(value.getAnswer().getClosestIncidents().size());

                    for (Incident i : value.getAnswer().getClosestIncidents()) {
                        Log.i(TAG, "ano : id - alias - nom : " + i.getId() + " / " + i.getAlias() + i.getDescriptive()+" - pour equipement : " + allIds);

                        Map<String,Category> mesCat = CategoryHelper.getAllCategories(application, true,prefManager.getEquipementTypeByDefault().getIdTypEquipement());

                        final String idParentCategory = CategoryHelper.getFirstParent(i.getCategoryId(), mesCat);
                        if(null != mesCat && null != mesCat.get(idParentCategory) && null != mesCat.get(idParentCategory).getImageMobile()){
                            i.setIconIncident(mesCat.get(idParentCategory).getImageMobile());
                        }

//                        i.setIconIncident(mesCat.get(i.getCategoryId()).getImageMobile());

                        anomalyList.add(i);
                    }

                    view.updateAnomalyList(anomalyList);

                    // A refactorer - ajout du nb d'anos à l'équipement
                    nbAnomalies = value.getAnswer().getClosestIncidents().size();

                    if (null != value.getAnswer().getClosestIncidents()) {
                        maListe = value.getAnswer().getClosestIncidents();
                    }
                }

                if (null == nbAnomalies) nbAnomalies = 0;
                Log.i(TAG, "nb anomalies : " + nbAnomalies);
                view.displayMarkerEquipementFromSearchBar(null, nbAnomalies, maListe);
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            networkError();
        }
    }

    public TypeEquipement getEquipementDefaultFromPresenter() {
        return prefManager.getEquipementTypeByDefault();
    }

    public void saveIdEquipementSelected(String id) {
        prefManager.setEquipementIdSelected(id);
    }

    public List<Equipement> getListEquipementsOfTypeByDefault() {
        if ( prefManager.getEquipementTypeByDefault() == null ) {
            Log.w(TAG, "unexpected case");
            if (prefManager.getTypesEquipement() != null && ! prefManager.getTypesEquipement().isEmpty() ) {
                prefManager.setEquipementTypeByDefault(prefManager.getTypesEquipement().get(0));
            }
        }
        return prefManager.getEquipementTypeByDefault().getListEquipementByType();
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