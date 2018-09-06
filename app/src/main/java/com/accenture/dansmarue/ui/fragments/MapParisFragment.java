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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.Position;
import com.accenture.dansmarue.mvp.presenters.MapParisPresenter;
import com.accenture.dansmarue.mvp.views.MapParisView;
import com.accenture.dansmarue.ui.activities.TypeEquipementChooser;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.NetworkUtils;
import com.crashlytics.android.Crashlytics;
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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapParisFragment.OnMapParisFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapParisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapParisFragment extends BaseFragment implements MapParisView, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapParisFragment.class.getCanonicalName();

    @Inject
    protected MapParisPresenter presenter;
    protected OnMapParisFragmentInteractionListener activity;

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
    private Boolean myPrecisionMode = false;

    private Boolean searchBarMode = false;

    private String searchBarAddress;

    /**
     * My position (choosen) marker
     */
    private Marker myPosMarker;
    /**
     * Restriction Paris
     */
    private LatLngBounds parisBounds = new LatLngBounds(
            new LatLng(48.811310, 2.217569),
            new LatLng(48.905509, 2.469839));

    private int permissionCheck;

    private Location locationNetwork;

    private boolean longPress;

    public MapParisFragment() {
        Log.d(TAG, "MapParisFragment: ");
        // Required empty public constructor
    }

    public static MapParisFragment newInstance() {
        Log.d(TAG, "newInstance: ");
        // Required empty public constructor
        return new MapParisFragment();
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
            Log.i(TAG, "onMapReady: ");
            initMap(map);
            isNetworkOkReceiver = NetworkUtils.isConnected(getActivity());
            initAutoCompleteSearchBar();
            initLocationButton();
            intLongPress();

        }

    }

    private void intLongPress() {
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (myPrecisionMode == true) activity.onClickPrecisePosition();
                longPress = true;
                searchBarMode = false;
                updateLocation(latLng);
            }
        });
    }

    private void popInGps() {
        if (!NetworkUtils.isGpsEnable(getContext())) {
//            Toast.makeText(getContext(), "Merci d'activer votre GPS.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "popInGps: +1");
            enableLocationViaGooglePopUp();
        }
    }

    private void initMap(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (null == myCurrentLocationPosition || validLocation == false) {

            updateMapToCentralParis();

//            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    if (null != googleMap) {
//                        if (null != googleMap.getMyLocation()) {
//                            updateLocation(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()));
//                            googleMap.setOnMyLocationChangeListener(null);
//                        }
//                    }
//                }
//            });
        } else {
            updateLocation(myCurrentLocationPosition);
        }


    }

    private void updateMapToCentralParis() {

        if (null != googleMap) {
            LatLng centralParis = new LatLng(48.864716, 2.349014);
//            myPosMarker = googleMap.addMarker(new MarkerOptions().position(centralParis).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pink)));

//        By Default, we center the map on Notre Dame
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centralParis, 12));
        }
    }

    private void initAutoCompleteSearchBar() {
        //        Search and place pin
        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

//        Be more specific : limit result to a rectangle Meudon > Bobigny
        autocompleteFragment.setBoundsBias(parisBounds);
        autocompleteFragment.setHint(getString(R.string.google_searchbar_wording));

//        Limit to France
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setCountry("Fr")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(autocompleteFilter);

//        Search Results
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (null != place) {
                    searchBarMode = true;
                    LatLng latLngPlace = place.getLatLng();
                    if (myPrecisionMode == true) {
                        precisePositionModeFunction(false);
                        updateLocation(latLngPlace);
                        precisePositionModeFunction(true);
                    } else {
                        updateLocation(latLngPlace);
                    }
                }
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "onError: " + status);
            }
        });


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
                    if (!myPrecisionMode) {
                        searchBarMode = false;
                        searchPositionAndUpdate(true);
                    }
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
                Log.i(TAG, "searchPositionAndUpdate: ma position");
            } catch (Exception e) {
                try {
                    lastLat = locationNetwork.getLatitude();
                    lastLong = locationNetwork.getLongitude();
                    Log.i(TAG, "searchPositionAndUpdate: une ancienne position");

                } catch (Exception f) {
                    Log.i(TAG, "searchPositionAndUpdate: rien");

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


    /**
     * Enable/Disable Anomaly mode precision
     * You can move the map and the marker is over - Uber effect
     */
    public void precisePositionModeFunction(boolean precisePositionMode) {

        myPrecisionMode = precisePositionMode;

        if (permissionCheck == PackageManager.PERMISSION_GRANTED && (NetworkUtils.isGpsEnable(getContext()) || NetworkUtils.isConnected(getContext()))) {

            ImageView anomalyPinBlack = (ImageView) getView().findViewById(R.id.anomaly_pin_black);
            TextView anomalyHowToPinBlack = (TextView) getView().findViewById(R.id.anomaly_how_to_pin_black);

            TextView anomalyChoiceBox = (TextView) getView().findViewById(R.id.anomaly_choice_box);

            if (precisePositionMode) {

                anomalyPinBlack.setVisibility(View.VISIBLE);
                anomalyHowToPinBlack.setVisibility(View.VISIBLE);
                anomalyChoiceBox.setVisibility(View.GONE);

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        //Get latlng at the center by calling
                        final LatLng midLatLng = googleMap.getCameraPosition().target;
                        if (myCurrentLocationPosition == null || !myCurrentLocationPosition.equals(midLatLng)) {
                            updateLocation(midLatLng);
                        }

                    }
                });

            } else {
                googleMap.setOnCameraIdleListener(null);

                if (null != myCurrentLocationPosition)
                    myPosMarker = googleMap.addMarker(new MarkerOptions().position(myCurrentLocationPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pink)));


                anomalyPinBlack.setVisibility(View.GONE);
                anomalyHowToPinBlack.setVisibility(View.GONE);
                anomalyChoiceBox.setVisibility(View.VISIBLE);

            }

        }


    }


    public LatLng getMyCurrentLocationPosition() {
        return myCurrentLocationPosition;
    }


    public String getSearchBarAdress() {
        return searchBarAddress;
    }

    public boolean isSearchBarMode() {
        return searchBarMode;
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
                            updateMapToCentralParis();
                        }
                    }).show();
        }
        validLocation = false;
        searchBarMode = false;
    }

    public boolean isValidLocation() {
        return validLocation;
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
        Log.i(TAG, "locationChanged: LAT current / location" + myCurrentLocationPosition.latitude + " - " + location.latitude);

        if (myPosMarker != null) {
            myPosMarker.remove();
        }

//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centralParis, 12));

        if (null != googleMap) {
            if (myPrecisionMode == false && longPress == false)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocationPosition, 18));
        }
        longPress = false;

//        myPosMarker = googleMap.addMarker(new MarkerOptions().position(myCurrentLocationPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pink)));

        if (NetworkUtils.isConnected(getContext())) {

            // Pay Attention... My MapFragement has disappeared - click on bottom bar but geocoder is still running ...
            if (getContext() != null) {

                if (Geocoder.isPresent() && null != location) {

                    // Transform my location into adress
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {

                        final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 2);
                        int index = 0;

                        if(addresses.get(index).getThoroughfare() == null) {
                            //fisrt address as consider invalid
                            index = 1;
                        }


                        final String address = addresses.get(index).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        Log.i(TAG, "adress " + address);
                        final String city = addresses.get(index).getLocality();
                        Log.i(TAG, "city " + city);

                        final String country = addresses.get(index).getCountryName();
                        Log.i(TAG, "country " + country);
                        final String postalCode = addresses.get(index).getPostalCode();
                        Log.i(TAG, "cp " + postalCode);

                        final String currentAdress = address;
                        Log.i(TAG, "current " + currentAdress);



                        Log.i(TAG, "Map Paris Refactor Adress " + MiscTools.reformatArrondissement(currentAdress));

                        EditText searchBarText = (EditText) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment).getView().findViewById(R.id.place_autocomplete_search_input);
                        if (searchBarMode) {
                            searchBarAddress =  currentAdress.replace(currentAdress.substring(0,currentAdress.indexOf(",")),searchBarText.getText().toString());
                            activity.onUpdateLocation(myCurrentLocationPosition, searchBarAddress);
                        } else {
                            activity.onUpdateLocation(myCurrentLocationPosition, currentAdress);
                        }

                    } catch (IOException e) {
                        Crashlytics.logException(e);
                        Log.e(TAG, e.getMessage(), e);
                        Log.i(TAG, "locationChanged: " + " Waiting 4 Geocoder");
//                        Toast.makeText(getContext(),"En attente du service de géocoding inversé.\nMerci de votre patience",Toast.LENGTH_SHORT).show();
//                        activity.onUpdateLocation(location, "En cours de résolution");
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
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
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

//                Do Something in this case

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewFragment = inflater.inflate(R.layout.fragment_map_paris, container, false);

        TextView anomalyChoiceBox = (TextView) viewFragment.findViewById(R.id.anomaly_choice_box);
        // chevron inversé
        String libelle = getString(R.string.anomalie_espace_public_libelle) + "  " + Html.fromHtml("&#8744;");
        anomalyChoiceBox.setText(libelle);

        anomalyChoiceBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Passage anos equipements");
                Intent i = new Intent(getActivity(), TypeEquipementChooser.class);
                startActivity(i);
            }
        });

        return viewFragment;


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (OnMapParisFragmentInteractionListener) getActivity();
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
        myPrecisionMode = false;
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

                        cardview_place_autocomplete_fragment.setVisibility(View.VISIBLE);

                        isNetworkOkReceiver = true;

                        if (null != myCurrentLocationPosition) {
                            locationChanged(myCurrentLocationPosition);

                        }

                        if (null != googleMap && null != locationButton)
                            myProperFloatActionButton.setVisibility(View.VISIBLE);


                    } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {

                        Log.i(TAG, "internet : KO");

                        isNetworkOkReceiver = false;
                        cardview_place_autocomplete_fragment.setVisibility(View.GONE);
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
        if (!myPrecisionMode && googleMap != null)
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
        if (null != myPosMarker) {
            if (!myPosMarker.equals(marker) && null != activity) {
                final Incident incident = new GsonBuilder().create().fromJson(marker.getSnippet(), Incident.class);
                activity.onMarkerClicked(incident);
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


    public interface OnMapParisFragmentInteractionListener {
        void onUpdateClosestIncidents(final List<Incident> closestIncidents);

        void onUpdateLocation(LatLng location, String myAddr);

        void onMarkerClicked(Incident incident);

        void onClickPrecisePosition();
    }
}