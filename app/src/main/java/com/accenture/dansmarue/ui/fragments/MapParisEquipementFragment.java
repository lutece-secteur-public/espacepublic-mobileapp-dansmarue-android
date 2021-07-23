package com.accenture.dansmarue.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.Position;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.TypeEquipement;
import com.accenture.dansmarue.mvp.presenters.MapParisEquipementPresenter;
import com.accenture.dansmarue.mvp.views.MapParisEquipementView;
import com.accenture.dansmarue.ui.activities.TypeEquipementChooser;
import com.accenture.dansmarue.ui.adapters.EquipementAdapter;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.NetworkUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapParisEquipementFragment.OnMapParisEquipementFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapParisEquipementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapParisEquipementFragment extends BaseFragment implements EquipementAdapter.UpdateFragmentSide, MapParisEquipementView, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapParisEquipementFragment.class.getCanonicalName();

    @Inject
    protected MapParisEquipementPresenter presenter;

    protected OnMapParisEquipementFragmentInteractionListener activity;


    private MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;


    private boolean validLocation = true;
    private LatLng myCurrentLocationPosition;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static int REQUEST_LOCATION = 199;

    private ImageView locationButton;
    private FloatingActionButton myProperFloatActionButton;
    private BroadcastReceiver gpsReceiver;
    private BroadcastReceiver internetReceiver;

    private Boolean isNetworkOkReceiver;

    private Boolean searchBarMode = false;

    private Equipement currentEquipement = null;


    /**
     * My position (choosen) marker
     */
    private Marker myPosMarker;
    /**
     * Restriction Paris
     */
    private LatLngBounds parisBounds = new LatLngBounds(
            new LatLng(48.811310, 2.217569),
            new LatLng(48.905509, 2.413468));

    private Marker markerEquipementFromSearchBarOrGeoloc;

    private int permissionCheck;

    private Location locationNetwork;

    private AutoCompleteTextView autocompleteView;

    ImageView autocompleteErase;

    private int zoomLevel = 18;
    private LatLng centralParis = new LatLng(48.864716, 2.349014);


    public MapParisEquipementFragment() {
        Log.d(TAG, "MapParisFragment: ");
        // Required empty public constructor
    }

    public static MapParisEquipementFragment newInstance() {
        Log.d(TAG, "newInstance: ");
        // Required empty public constructor
        return new MapParisEquipementFragment();
    }

    @Override
    protected void resolveDaggerDependency(DansMaRueApplication application) {
        DaggerPresenterComponent.builder()
                .applicationComponent(application.getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        if (isAdded()) {

            if (null != getCurrentEquipement()) {
                if (presenter.checkTypeCurrentEquipementIsSelected(getCurrentEquipement().getType_equipement_id())) {
                    searchBarMode = true;
                }
            }


            Log.i(TAG, "onMapReady: BACK HERE");
            initMap(map);
            isNetworkOkReceiver = NetworkUtils.isConnected(getActivity());
            initCustomSearchBar();
            initLocationButton();
            changeMenu();

            if (null != getCurrentEquipement()) {
                if (presenter.checkTypeCurrentEquipementIsSelected(getCurrentEquipement().getType_equipement_id()))
                    updateAutocomplete(getCurrentEquipement());
            }

        }

    }


    private void popInGps() {
        if (!NetworkUtils.isGpsEnable(getContext())) {
            enableLocationViaGooglePopUp();
        }
    }

    private void initMap(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (null == myCurrentLocationPosition || validLocation == false) {


            updateLocation(centralParis);

        } else {

            updateLocation(myCurrentLocationPosition);
        }
    }


    private void initCustomSearchBar() {

        //        Search and place pin
        autocompleteView = (AutoCompleteTextView) getView().findViewById(R.id.autocomplete_dogs);

        // Fake Datas : create a list of equipements
        ArrayList<Equipement> equipements = new ArrayList<Equipement>();

        equipements = (ArrayList) presenter.getListEquipementsOfTypeByDefault();

        // Setup autocomplete
        EquipementAdapter adapter = new EquipementAdapter(getContext(), equipements, R.color.colorWhite, this);

        autocompleteView = (AutoCompleteTextView) getView().findViewById(R.id.autocomplete_dogs);
        autocompleteView.setAdapter(adapter);

        // Setup Erase Button
        autocompleteErase = (ImageView) getView().findViewById(R.id.autocomplete_erase);
        autocompleteErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocompleteView.setText("");
                myProperFloatActionButton.performClick();
            }
        });

    }


    @Override
    public void updateAutocomplete(Equipement equipement) {

        searchBarMode = true;

        //save id equipement selected
        presenter.saveIdEquipementSelected(equipement.getId());

        // calcul du nombre d'anomalies

//        presenter.calculateNumberOfAnomalies(new Position(equipement.getLatitude(), equipement.getLongitude()));

        presenter.callListIncidentsByEquipement(equipement.getId());

        autocompleteView = (AutoCompleteTextView) getView().findViewById(R.id.autocomplete_dogs);
        autocompleteView.setText(equipement.getName() + " ");
        // Cursor @ the end
        autocompleteView.setSelection(equipement.getName().length());

        // Add marker without anos and add response from another WS after (The presenter re-calls this method)
        displayMarkerEquipementFromSearchBar(equipement, null, null);


    }

    // Display the equipement selected via searchBarCustom
    public void displayMarkerEquipementFromSearchBar(Equipement e, Integer nbAnos, List<Incident> listeAnos) {
        if (googleMap != null) {
            if (null != e) {
                googleMap.clear();
                Log.i(TAG, "Marker W*I*T*H*O*U*T Anomalys");
                markerEquipementFromSearchBarOrGeoloc = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(e.getLatitude(), e.getLongitude()))
                        .snippet(new GsonBuilder().create().toJson(e))
                        .icon(BitmapDescriptorFactory.fromBitmap(MiscTools.base64ToBitmap(e.getIconEquipement(), Constants.SIZE_MARKER))));
                currentEquipement = e;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(e.getLatitude(), e.getLongitude()), 18));
                activity.onMarkerClicked(e);
            }

            if (null != nbAnos) {
                if (null != markerEquipementFromSearchBarOrGeoloc) {
                    Log.i(TAG, "Marker W*I*T*H Anomalys");
                    Equipement eWithAnos = new GsonBuilder().create().fromJson(markerEquipementFromSearchBarOrGeoloc.getSnippet(), Equipement.class);
                    eWithAnos.setNbAnomalies(nbAnos);
                    Log.i(TAG, "displayMarkerEquipementFromSearchBar: " + eWithAnos.getIconEquipement());
                    if (null != listeAnos) eWithAnos.setListeIncidents(listeAnos);
//                    markerEquipementFromSearchBarOrGeoloc.setSnippet(new GsonBuilder().create().toJson(eWithAnos));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(eWithAnos.getLatitude(), eWithAnos.getLongitude()), 18));
                    currentEquipement = eWithAnos;

                    markerEquipementFromSearchBarOrGeoloc = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(eWithAnos.getLatitude(), eWithAnos.getLongitude()))
                            .snippet(new GsonBuilder().create().toJson(eWithAnos))
                            .icon(BitmapDescriptorFactory.fromBitmap(MiscTools.base64ToBitmap(eWithAnos.getIconEquipement(), Constants.SIZE_MARKER))));

                    activity.onMarkerClicked(eWithAnos);
                    if (nbAnos == 0) activity.onUpdateClosestIncidents(null);
                }
            }
        }
    }


    private void initLocationButton() {
        permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        //        No Geolocation Button if Permission has not been granted and Gps is NOT enable
        isNetworkOkReceiver = NetworkUtils.isConnected(getContext());
        if (permissionCheck == PackageManager.PERMISSION_GRANTED && ((isNetworkOkReceiver) || NetworkUtils.isGpsEnable(getContext()))) {
            googleMap.setMyLocationEnabled(true);

            // Retreive LastKnowPosition if GPS is off
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationNetwork = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            // Zoom on my position at the beginnning
            if (!searchBarMode) searchPositionAndUpdate(false);

            // TODO Check LastLocation
            // How to find, customize and position the location button
            locationButton = (ImageView) getView().findViewWithTag("GoogleMapMyLocationButton");
            locationButton.setVisibility(View.INVISIBLE);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBarMode = false;
                    searchPositionAndUpdate(true);
                }
            });

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(160, 160); // size of button in dp
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

//            Fix the position of the LocationButton (convert DP to pixels)
            int rightMargin = (int) ((13) * Resources.getSystem().getDisplayMetrics().density);
            int bottomMargin = (int) ((204) * Resources.getSystem().getDisplayMetrics().density);
            params.setMargins(rightMargin, 0, 0, bottomMargin);
            locationButton.setLayoutParams(params);

            myProperFloatActionButton = (FloatingActionButton) getView().findViewById(R.id.my_proper_location_button);
            myProperFloatActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
            myProperFloatActionButton.setVisibility(View.VISIBLE);

            myProperFloatActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetworkUtils.isGpsEnable(getContext())) enableLocationViaGooglePopUp();
                    googleMap.clear();
                    if (null != getCurrentEquipement()) currentEquipement = null;
                    autocompleteView.setText("");
                    locationButton.performClick();
                }
            });
        }
    }

    private void searchPositionAndUpdate(boolean popUpEnable) {
        if (null != googleMap) {

            Double lastLat = null;
            Double lastLong = null;

            try {
                lastLat = googleMap.getMyLocation().getLatitude();
                lastLong = googleMap.getMyLocation().getLongitude();
            } catch (Exception e) {
                try {
                    lastLat = locationNetwork.getLatitude();
                    lastLong = locationNetwork.getLongitude();

                } catch (Exception f) {

                }
            }

            if (popUpEnable) {
                if (null != lastLat && null != lastLong)
                    updateLocation(new LatLng(lastLat, lastLong));
            } else {
                //avoid double popup
                if (null != lastLat && null != lastLong && parisBounds.contains(new LatLng(lastLat, lastLong)))
                    updateLocation(new LatLng(lastLat, lastLong));
            }
        }
    }


    public Equipement getCurrentEquipement() {
        return currentEquipement;
    }

    public void invalidLocation() {
//        if (validLocation && getContext() != null) {
        if (getContext() != null) {

            new AlertDialog.Builder(getContext()).setMessage(R.string.not_in_paris)
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
        validLocation = false;

    }

    /**
     * Refresh list of incidents by position and markers on the map
     */
    public void refreshDatas() {
        presenter.locationChanged(new Position(myPosMarker.getPosition().latitude, myPosMarker.getPosition().longitude));
    }

    public void locationChanged(LatLng location) {
        validLocation = true;
        myCurrentLocationPosition = location;

        if (myPosMarker != null) {
            myPosMarker.remove();
        }

        if (null != googleMap) {
            if (location == centralParis) {
                zoomLevel = 12;
            } else {
                zoomLevel = 18;
            }
            ;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocationPosition, zoomLevel));
        }

        if (NetworkUtils.isConnected(getContext())) {

            // Pay Attention... My MapFragement has disappeared - click on bottom bar but geocoder is still running ...
            if (getContext() != null) {

                if (Geocoder.isPresent() && null != location) {

                    // Transform my location into adress
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {

                        final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                        final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        Log.i(TAG, "adress " + address);
                        final String city = addresses.get(0).getLocality();
                        Log.i(TAG, "city " + city);

                        final String country = addresses.get(0).getCountryName();
                        Log.i(TAG, "country " + country);
                        final String postalCode = addresses.get(0).getPostalCode();
                        Log.i(TAG, "cp " + postalCode);

                        final String currentAdress = address;
                        Log.i(TAG, "current " + currentAdress);

                        Log.i(TAG, "Map Paris Refactor Adress " + MiscTools.reformatArrondissement(currentAdress));

                        activity.onUpdateLocation(myCurrentLocationPosition, currentAdress);

                    } catch (IOException e) {
                        FirebaseCrashlytics.getInstance().log(e.getMessage());
                        Log.e(TAG, e.getMessage(), e);
                    }


                } else {
                    Log.i(TAG, "locationChanged: Geocoder unavailable");
                }
            }
        } else {
            activity.onUpdateLocation(myCurrentLocationPosition, "");
        }

        presenter.locationChanged(new Position(myCurrentLocationPosition.latitude, myCurrentLocationPosition.longitude));

    }

    // Update location : geocoding and populate bottom sheet
    public void updateLocation(LatLng location) {
        presenter.validateNewLocation(location, parisBounds);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(gpsReceiver);
        getContext().unregisterReceiver(internetReceiver);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }


    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    //.addApi(Places.GEO_DATA_API)
                    //.addApi(Places.PLACE_DETECTION_API)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "This device is supported");

            } else {
                Log.i(TAG, "This device is NOT supported");
            }
            return false;
        }
        return true;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.map_anomaly_paris);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);

        // Test GPS and PopInEnableGps via Google Dialog
        popInGps();


    }

    public void changeMenu() {
        if (null != presenter) {
            TypeEquipement t = presenter.getEquipementDefaultFromPresenter();

            TextView anomalyChoiceBox = (TextView) getView().findViewById(R.id.anomaly_choice_box);
            // ajout chevron inversé du menu choix equipement
            anomalyChoiceBox.setText(presenter.getEquipementDefaultFromPresenter().getLibelleEcranMobile() + "  " + Html.fromHtml("&#8744;"));
            autocompleteView.setHint(t.getPlaceholder_searchbar());

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_map_paris_equipement, container, false);

        TextView anomalyChoiceBox = (TextView) viewFragment.findViewById(R.id.anomaly_choice_box);
        anomalyChoiceBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                autocompleteView.setText("");

                Intent i = new Intent(getActivity(), TypeEquipementChooser.class);
                startActivity(i);
            }
        });

        return viewFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (OnMapParisEquipementFragmentInteractionListener) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }

    @Override
    public void updateAnomalyList(List<Incident> closestIncidents) {
        if (activity != null)
            activity.onUpdateClosestIncidents(closestIncidents);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (null != googleMap) googleMap.setMyLocationEnabled(false);
        } catch (SecurityException e) {
        } finally {
            googleMap = null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);

        checkPlayServices();

        /**
         * Receive the information about user action on GPS switch
         */
        gpsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                    //Do your stuff on GPS status change

                    LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        if (null != locationButton) {
                            myProperFloatActionButton.setVisibility(View.VISIBLE);
                            Log.i(TAG, "Localisation : OK");
                        }

                    } else {
                        if (null != googleMap && null != locationButton && null != googleMap.getMyLocation() && NetworkUtils.isConnected(getContext()) == false) {
                            myProperFloatActionButton.setVisibility(View.GONE);
                            Log.i(TAG, "Localisation : KO");
                        }
                    }

                }
            }
        };
        getContext().registerReceiver(gpsReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));


        internetReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {


                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {


                    NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

                    CardView cardview_place_autocomplete_fragment = (CardView) getView().findViewById(R.id.cardview_place_autocomplete_fragment);

                    if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {

                        Log.i(TAG, "internet : OK");

//                        cardview_place_autocomplete_fragment.setVisibility(View.VISIBLE);

                        isNetworkOkReceiver = true;

                        if (null != myCurrentLocationPosition && searchBarMode == false) {
                            locationChanged(myCurrentLocationPosition);

                        }

                        if (null != googleMap && null != locationButton)
                            myProperFloatActionButton.setVisibility(View.VISIBLE);


                    } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {

                        Log.i(TAG, "internet : KO");

                        isNetworkOkReceiver = false;
//                        cardview_place_autocomplete_fragment.setVisibility(View.GONE);
                        if (null != googleMap) {
                            if (null != locationButton && null != googleMap.getMyLocation() && NetworkUtils.isGpsEnable(getContext()) == false)
                                myProperFloatActionButton.setVisibility(View.GONE);
                        }
                    }

                }

            }
        };
        getContext().registerReceiver(internetReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));


    }


    @Override
    public void updateAnomalyMarkers(List<MarkerOptions> markers) {
        if (googleMap != null) {
            googleMap.clear();

            for (MarkerOptions marker : markers) {
                googleMap.addMarker(marker);
            }
        }
    }

    public void updatePosMarker() {
        if (googleMap != null)
            myPosMarker = googleMap.addMarker(new MarkerOptions().position(myCurrentLocationPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pink)));
    }

    @Override
    public void clearAnomaly() {
        if (googleMap != null) {
            googleMap.clear();
            myPosMarker = googleMap.addMarker(new MarkerOptions().position(myCurrentLocationPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pink)));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.i(TAG, "onMarkerClick : clic");

        if (null != myPosMarker) {
            if (!myPosMarker.equals(marker) && null != activity) {
                final Equipement equipement = new GsonBuilder().create().fromJson(marker.getSnippet(), Equipement.class);
                currentEquipement = equipement;

                markerEquipementFromSearchBarOrGeoloc = marker;

                Log.i(TAG, "onMarkerClick: equipement id > " + equipement.getId());
                presenter.callListIncidentsByEquipement(equipement.getId());

            } else {
                Log.i(TAG, "onMarkerClick: activity null");
            }
        }
        return false;
    }

    private void enableLocationViaGooglePopUp() {

        if (null != mGoogleApiClient) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                if (null != getActivity())
                                    status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    public interface OnMapParisEquipementFragmentInteractionListener {
        void onUpdateClosestIncidents(final List<Incident> closestIncidents);

        void onUpdateLocation(LatLng location, String myAddr);

        void onMarkerClicked(Equipement equipement);
    }
}