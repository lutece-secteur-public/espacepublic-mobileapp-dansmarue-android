package com.accenture.dansmarue.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.FavoriteAddress;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.Position;
import com.accenture.dansmarue.mvp.presenters.MapParisPresenter;
import com.accenture.dansmarue.mvp.views.MapParisView;
import com.accenture.dansmarue.ui.activities.FavoriteAddressActivity;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.DrawableExtensionsKt;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.NetworkUtils;
import com.accenture.dansmarue.utils.PrefManager;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
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

    private PrefManager prefManager;

    private boolean validLocation = true;
    private LatLng myCurrentLocationPosition;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static int REQUEST_LOCATION = 199;
    private final static int REQUEST_FAVORITE_ADDRESS = 200;

    private ImageView locationButton;
    private FloatingActionButton myProperFloatActionButton;
    private FloatingActionButton findByNumberButton;
    private FloatingActionButton findFDTbyIdIn;
    private FloatingActionButton findFDTbyIdOut;
    private BroadcastReceiver gpsReceiver;
    private BroadcastReceiver internetReceiver;

    private Boolean isNetworkOkReceiver;
    private Boolean myPrecisionMode = false;

    private Boolean searchBarMode = false;

    private String searchBarAddress = "";
    private String favoriteAddressSelect = "";
    private String searchNumberIncident = "";
    private int searchIdFdt;
    private List<Incident> incidentsFDT;

    private String infosAvantTournee;

    private String infosApresTournee;

    private Dialog dialogFindByNumber;
    private Dialog dialogFindFDTById;
    private ProgressDialog loadingDialog;

    TextView anomalyChoiceBox;

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

        if (isAdded() && searchIdFdt <= 0) {
            Log.i(TAG, "onMapReady: ");
            initMap(map);
            isNetworkOkReceiver = NetworkUtils.isConnected(requireContext());
            initAutoCompleteSearchBar();
            initLocationButton();
            intLongPress();
            initFavoriteAddress();
        }

        if (searchIdFdt > 0) {
            Log.i(TAG, "onMapReady: ");
            initMap(map);
            initButtonForFdtMode();
            clearAnomaly();
            presenter.displayIncidents(incidentsFDT);
        }

    }

    /**
     * Init favorite address function.
     */
    public void initFavoriteAddress() {
        ImageView imgViewFavoriteAddress = getView().findViewById(R.id.favoris);
        imgViewFavoriteAddress.setOnClickListener(v -> {
            searchNumberIncident = null;
            Intent intent = new Intent(getActivity(), FavoriteAddressActivity.class);
            startActivityForResult(intent, REQUEST_FAVORITE_ADDRESS);
        });
    }

    /**
     * Init on long press on map function.
     */
    private void intLongPress() {
        googleMap.setOnMapLongClickListener(latLng -> {
            if (searchIdFdt <= 0) {
                searchNumberIncident = null;
                if (myPrecisionMode) activity.onClickPrecisePosition();
                longPress = true;
                searchBarMode = false;
                favoriteAddressSelect = "";
                updateLocation(latLng);
            }
        });
    }

    private void popInGps() {
        if (!NetworkUtils.isGpsEnable(requireContext())) {
            Log.i(TAG, "popInGps: +1");
            enableLocationViaGooglePopUp();
        }
    }

    /**
     * Init google map.
     *
     * @param map
     */
    private void initMap(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        if (null == myCurrentLocationPosition || !validLocation) {
            updateMapToCentralParis();
            searchPositionAndUpdate(true);
        } else if (searchIdFdt <= 0) {
            updateLocation(myCurrentLocationPosition);
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocationPosition, 12));
        }


    }

    /**
     * Center map on Paris.
     */
    private void updateMapToCentralParis() {
        if (null != googleMap) {
            LatLng centralParis = new LatLng(48.864716, 2.349014);
//        By Default, we center the map on Notre Dame
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centralParis, 12));
        }
    }

    /**
     * Init module auto complete search address.
     */
    private void initAutoCompleteSearchBar() {
        if (!Places.isInitialized()) {
            try {
                Bundle metaData = requireContext().getPackageManager().getApplicationInfo(requireContext().getPackageName(), PackageManager.GET_META_DATA).metaData;
                Places.initialize(requireContext(), metaData.getString("com.google.android.geo.API_KEY"));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(requireContext());

        //        Search and place pin
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));
//        Be more specific : limit result to a rectangle Meudon > Bobigny
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(parisBounds));
        ((EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setHint(R.string.google_searchbar_wording_desc);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setContentDescription(getString(R.string.google_searchbar_wording_desc));
        (autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button)).setContentDescription(getString(R.string.google_searchbar_wording_desc));
        (autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button)).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);


//        Limit to France
        autocompleteFragment.setCountry("Fr");
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

//        Search Results
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                searchNumberIncident = null;
                searchBarMode = true;
                favoriteAddressSelect = "";
                LatLng latLngPlace = place.getLatLng();
                if (myPrecisionMode) {
                    precisePositionModeFunction(false);
                    updateLocation(latLngPlace);
                    precisePositionModeFunction(true);
                } else {
                    updateLocation(latLngPlace);
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "onError: " + status);
            }
        });
    }

    /**
     * Init my current position function and find by number.
     */
    private void initLocationButton() {
        permissionCheck = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        //        No Geolocation Button if Permission has not been granted and Gps is NOT enable
        isNetworkOkReceiver = NetworkUtils.isConnected(requireContext());
        if (permissionCheck == PackageManager.PERMISSION_GRANTED && ((isNetworkOkReceiver) || NetworkUtils.isGpsEnable(requireContext()))) {
            googleMap.setMyLocationEnabled(true);

            // Retreive LastKnowPosition if GPS is off
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationNetwork = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            // Zoom on my position at the beginnning
            if (!searchBarMode && favoriteAddressSelect.length() == 0)
                searchPositionAndUpdate(false);

            // TODO Check LastLocation
            // How to find, customize and position the location button
            locationButton = (ImageView) getView().findViewWithTag("GoogleMapMyLocationButton");
            if (null != locationButton) {
                locationButton.setVisibility(View.GONE);
                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!myPrecisionMode && favoriteAddressSelect.length() == 0) {
                            searchBarMode = false;
                            searchPositionAndUpdate(true);
                        }
                    }
                });


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(160, 160); // size of button in dp
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

//            Fix the position of the LocationButton (convert DP to pixels)
                int rightMargin = Math.round((13) * Resources.getSystem().getDisplayMetrics().density);
                int bottomMargin = Math.round((204) * Resources.getSystem().getDisplayMetrics().density);
                params.setMargins(rightMargin, 0, 0, bottomMargin);
                locationButton.setLayoutParams(params);

                myProperFloatActionButton = (FloatingActionButton) getView().findViewById(R.id.my_proper_location_button);
                myProperFloatActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
                myProperFloatActionButton.setVisibility(View.VISIBLE);

                myProperFloatActionButton.setOnClickListener(v -> {
                    favoriteAddressSelect = "";
                    if (!NetworkUtils.isGpsEnable(requireContext())) enableLocationViaGooglePopUp();
                    locationButton.performClick();
                });

                //Find signalement by numero
                findByNumberButton = (FloatingActionButton) getView().findViewById(R.id.find_by_number_button);
                findByNumberButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
                findByNumberButton.setVisibility(View.VISIBLE);
                findByNumberButton.setOnClickListener(v -> showDialogFindByNumber(null));

                initSeachFDTById();
            }
        }

    }

    private void initButtonForFdtMode() {

        myProperFloatActionButton = (FloatingActionButton) getView().findViewById(R.id.my_proper_location_button);
        myProperFloatActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
        myProperFloatActionButton.setVisibility(View.VISIBLE);

        myProperFloatActionButton.setOnClickListener(v -> {
            favoriteAddressSelect = "";
            if (!NetworkUtils.isGpsEnable(requireContext())) enableLocationViaGooglePopUp();
            locationButton.performClick();
        });

        findFDTbyIdOut = (FloatingActionButton) getView().findViewById(R.id.find_fdt_by_id_out);
        findFDTbyIdOut.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
        findFDTbyIdOut.setVisibility(View.VISIBLE);
        findFDTbyIdOut.setOnClickListener(v -> {
            findFDTbyIdOut.setVisibility(View.GONE);
            findFDTbyIdIn.setVisibility(View.VISIBLE);
            findByNumberButton.setVisibility(View.VISIBLE);
            anomalyChoiceBox.setVisibility(View.GONE);
            searchIdFdt = -1;
            activity.initMapFragment();
            activity.exitSearchFDTMode();
        });

        searchPositionAndUpdate(false);

    }

    private void initSeachFDTById() {
        if (prefManager.getIsAgent()) {
            // Find sheet tour by id to display signalement list
            findFDTbyIdIn = (FloatingActionButton) getView().findViewById(R.id.find_fdt_by_id_in);
            findFDTbyIdIn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
            findFDTbyIdIn.setOnClickListener(v -> showDialogFindSheetTourById(null));

            findFDTbyIdOut = (FloatingActionButton) getView().findViewById(R.id.find_fdt_by_id_out);
            findFDTbyIdOut.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
            findFDTbyIdOut.setOnClickListener(v -> {
                findFDTbyIdOut.setVisibility(View.GONE);
                findFDTbyIdIn.setVisibility(View.VISIBLE);
                findByNumberButton.setVisibility(View.VISIBLE);
                anomalyChoiceBox.setVisibility(View.GONE);
                searchIdFdt = -1;
                activity.initMapFragment();
                activity.exitSearchFDTMode();
            });

            if (searchIdFdt <= 0) {
                findFDTbyIdIn.setVisibility(View.VISIBLE);
                findFDTbyIdOut.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Search select position.
     *
     * @param popUpEnable
     */
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

        searchNumberIncident = null;
        myPrecisionMode = precisePositionMode;

        if (permissionCheck == PackageManager.PERMISSION_GRANTED && (NetworkUtils.isGpsEnable(requireContext()) || NetworkUtils.isConnected(requireContext()))) {

            ImageView anomalyPinBlack = (ImageView) getView().findViewById(R.id.anomaly_pin_black);
            TextView anomalyHowToPinBlack = (TextView) getView().findViewById(R.id.anomaly_how_to_pin_black);

            TextView anomalyChoiceBox = (TextView) getView().findViewById(R.id.anomaly_choice_box);

            if (precisePositionMode) {

                anomalyPinBlack.setVisibility(View.VISIBLE);
                anomalyHowToPinBlack.setVisibility(View.VISIBLE);
                anomalyChoiceBox.setVisibility(View.GONE);

                googleMap.setOnCameraIdleListener(() -> {
                    favoriteAddressSelect = "";
                    //Get latlng at the center by calling
                    final LatLng midLatLng = googleMap.getCameraPosition().target;
                    if (myCurrentLocationPosition == null || !myCurrentLocationPosition.equals(midLatLng)) {
                        updateLocation(midLatLng);
                    }
                });

            } else {
                googleMap.setOnCameraIdleListener(null);

                if (null != myCurrentLocationPosition)
                    myPosMarker = googleMap.addMarker(
                            new MarkerOptions().position(
                                    myCurrentLocationPosition).icon(BitmapDescriptorFactory.fromBitmap(DrawableExtensionsKt.toBitmap(R.drawable.dmr_pin_map_v2, requireActivity(), null))
                            )
                    );


                anomalyPinBlack.setVisibility(View.GONE);
                anomalyHowToPinBlack.setVisibility(View.GONE);
                if (prefManager.getTypesEquipement() != null) {
                    anomalyChoiceBox.setVisibility(View.VISIBLE);
                }

            }

        }


    }

    /**
     * Check if network or gps is enable.
     *
     * @return true is No network and No Gps
     */
    public boolean isNotConnectedMode() {

        return !NetworkUtils.isConnected(requireContext()) && !NetworkUtils.isGpsEnable(requireContext());
    }

    public LatLng getMyCurrentLocationPosition() {
        return myCurrentLocationPosition;
    }

    public void setMyCurrentLocationPosition(LatLng myCurrentLocationPosition) {
        this.myCurrentLocationPosition = myCurrentLocationPosition;
    }

    public String getSearchBarAdress() {
        return searchBarAddress;
    }

    public void setSearchBarAdress(String searchBarAddress) {
        this.searchBarAddress = searchBarAddress;
    }

    public boolean isSearchBarMode() {
        return searchBarMode;
    }

    public String getFavoriteAddressSelect() {
        return favoriteAddressSelect;
    }

    /**
     * Selected location is invalid.
     */
    public void invalidLocation(LatLng location) {
        if (requireContext() != null) {

            new AlertDialog.Builder(requireContext()).setMessage(R.string.not_in_paris)
                    .setCancelable(true)
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                        updateMapToCentralParis();
                    }).show();
        }
        validLocation = false;
        searchBarMode = false;
        activity.onUpdateLocation(location, "");
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

        //location not changed
        Boolean locationChanged = true;
        if (myCurrentLocationPosition != null && location.latitude == myCurrentLocationPosition.latitude && location.longitude == myCurrentLocationPosition.longitude) {
            locationChanged = false;
        }

        validLocation = true;
        myCurrentLocationPosition = location;
        Log.i(TAG, "locationChanged: LAT current / location" + myCurrentLocationPosition.latitude + " - " + location.latitude);

        if (myPosMarker != null) {
            myPosMarker.remove();
        }

        if (null != googleMap) {
            if (!myPrecisionMode && !longPress)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocationPosition, 18));
        }
        longPress = false;

        if (NetworkUtils.isConnected(requireContext())) {

            // Pay Attention... My MapFragement has disappeared - click on bottom bar but geocoder is still running ...
            requireContext();
            if (Geocoder.isPresent()) {

                // Transform my location into adress
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

                try {

                    String currentAdress = "";
                    final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 4);
                    if (addresses != null && !addresses.isEmpty()) {
                        EditText searchBarText = (EditText) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment).getView().findViewById(R.id.places_autocomplete_search_input);

                        final Address addressSelect = MiscTools.selectAddress(addresses, getString(R.string.city_name), searchBarMode, searchBarText.getText().toString());

                        final String address = MiscTools.formatAddress(addressSelect); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        Log.i(TAG, "adress " + address);
                        final String city = addressSelect.getLocality();
                        Log.i(TAG, "city " + city);

                        final String country = addressSelect.getCountryName();
                        Log.i(TAG, "country " + country);
                        final String postalCode = addressSelect.getPostalCode();
                        Log.i(TAG, "cp " + postalCode);

                        currentAdress = address;
                        Log.i(TAG, "current " + currentAdress);


                        Log.i(TAG, "Map Paris Refactor Adress " + MiscTools.reformatArrondissement(currentAdress));

                        //not in Paris
                        if (locationChanged && city != null && (!city.toUpperCase().toUpperCase().contains(getString(R.string.city_name).toUpperCase()))) {
                            invalidLocation(location);
                            return;
                        }
                        if (favoriteAddressSelect.length() > 0) {
                            activity.onUpdateLocation(myCurrentLocationPosition, favoriteAddressSelect);
                            searchBarMode = false;
                        } else if (searchBarMode) {
                            if (!searchBarText.getText().toString().isEmpty()) {

                                if (searchBarText.getText().toString().split(",").length > 1) {
                                    //fix commercial address
                                    String commercialAddress = searchBarText.getText().toString();
                                    String noCommercialAddress = searchBarText.getText().toString().replace(commercialAddress.substring(0, commercialAddress.indexOf(",") + 1), "");
                                    searchBarText.setText(noCommercialAddress);
                                }
                                searchBarAddress = currentAdress.replace(currentAdress.substring(0, currentAdress.indexOf(",")), searchBarText.getText().toString());
                            }
                            activity.onUpdateLocation(myCurrentLocationPosition, searchBarAddress);
                        } else {
                            activity.onUpdateLocation(myCurrentLocationPosition, currentAdress);
                        }
                    } else {
                        activity.onUpdateLocation(myCurrentLocationPosition, currentAdress);
                    }
                } catch (IOException e) {
                    FirebaseCrashlytics.getInstance().log(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                    Log.i(TAG, "locationChanged: " + " Waiting 4 Geocoder");
                }


            } else {
                Log.i(TAG, "locationChanged: Geocoder unavailable");
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
        requireContext().unregisterReceiver(gpsReceiver);
        requireContext().unregisterReceiver(internetReceiver);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.HOME_CURRENT_LOCATION, myCurrentLocationPosition);
        outState.putString(Constants.SEARCHBAR_ADDRESS, getSearchBarAdress());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            LatLng currentLocation = savedInstanceState.getParcelable(Constants.HOME_CURRENT_LOCATION);
            setMyCurrentLocationPosition(currentLocation);
            String searchBarAddress = savedInstanceState.getString(Constants.SEARCHBAR_ADDRESS);
            setSearchBarAdress(searchBarAddress);
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(requireContext())
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
        prefManager = new PrefManager(getActivity());
        anomalyChoiceBox = (TextView) viewFragment.findViewById(R.id.anomaly_choice_box);
        if (searchIdFdt <= 0) {
            anomalyChoiceBox.setVisibility(View.GONE);
        } else {
            anomalyChoiceBox.setText("FDT n°" + searchIdFdt + " en consultation");
            anomalyChoiceBox.setVisibility(View.VISIBLE);
        }
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
    public void updateAnomalyList(List<Incident> closestIncidents, boolean reset) {
        if (activity != null)
            activity.onUpdateClosestIncidents(closestIncidents, reset);
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
            Log.e(TAG, "onStop: " + e.getMessage());
        } finally {
            googleMap = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchIdFdt > 0) {
            activity.enterInSearchFDTMode();
        }
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

                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        if (null != locationButton) {
                            myProperFloatActionButton.setVisibility(View.VISIBLE);
                            findByNumberButton.setVisibility(View.VISIBLE);
                            Log.i(TAG, "Localisation : OK");
                        }

                    } else {
                        if (null != googleMap && null != locationButton && null != googleMap.getMyLocation() && !NetworkUtils.isConnected(requireContext())) {
                            myProperFloatActionButton.setVisibility(View.GONE);
                            findByNumberButton.setVisibility(View.GONE);
                            Log.i(TAG, "Localisation : KO");
                        }
                    }
                }
            }
        };

        requireContext().registerReceiver(gpsReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));

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
                        if (null != myCurrentLocationPosition && searchIdFdt <= 0) {
                            locationChanged(myCurrentLocationPosition);
                        }
                        if (null != googleMap && null != locationButton) {
                            myProperFloatActionButton.setVisibility(View.VISIBLE);
                            if (searchIdFdt <= 0) {
                                findByNumberButton.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                        Log.i(TAG, "internet : KO");
                        isNetworkOkReceiver = false;
                        cardview_place_autocomplete_fragment.setVisibility(View.GONE);
                        if (null != googleMap) {
                            if (null != locationButton && null != googleMap.getMyLocation() && NetworkUtils.isGpsEnable(requireContext()) == false) {
                                myProperFloatActionButton.setVisibility(View.GONE);
                                findByNumberButton.setVisibility(View.GONE);
                            }
                        }
                    }
                }

            }
        };
        requireContext().registerReceiver(internetReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }


    @Override
    public void updateAnomalyMarkers(List<MarkerOptions> markers) {
        if (googleMap != null) {
            //googleMap.clear();
            for (MarkerOptions marker : markers) {
                googleMap.addMarker(marker);
            }
        }
    }

    public void updatePosMarker() {
        if (!myPrecisionMode && googleMap != null && myCurrentLocationPosition != null)
            myPosMarker = googleMap.addMarker(new MarkerOptions().position(myCurrentLocationPosition).icon(BitmapDescriptorFactory.fromBitmap(DrawableExtensionsKt.toBitmap(R.drawable.dmr_pin_map_v2, requireActivity(), null))));
    }

    @Override
    public void clearAnomaly() {
        if (googleMap != null) {
            googleMap.clear();
            if (myCurrentLocationPosition != null) {
                myPosMarker = googleMap.addMarker(new MarkerOptions().position(myCurrentLocationPosition).icon(BitmapDescriptorFactory.fromBitmap(DrawableExtensionsKt.toBitmap(R.drawable.dmr_pin_map_v2, requireActivity(), null))));
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null != activity) {
            final Incident incident = new GsonBuilder().create().fromJson(marker.getSnippet(), Incident.class);
            activity.onMarkerClicked(incident);
        } else {
            Log.i(TAG, "onMarkerClick: activity null");
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
                                Log.e(TAG, "onResult: " + e.getMessage());
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
                    // The user was asked to change settings, but chose not to
                    default: {
                        break;
                    }
                }
            case REQUEST_FAVORITE_ADDRESS: {
                if (Activity.RESULT_OK == resultCode && data.getSerializableExtra(Constants.EXTRA_FAVORITE_ADDRESS_SELECT) != null) {
                    FavoriteAddress favAddress = (FavoriteAddress) data.getSerializableExtra(Constants.EXTRA_FAVORITE_ADDRESS_SELECT);
                    myCurrentLocationPosition = new LatLng(favAddress.getLatitude(), favAddress.getLongitude());
                    favoriteAddressSelect = favAddress.getAddress();
                }
            }
            default: {
                break;
            }
        }
    }

    /**
     * Display modal dialog find incident by number.
     *
     * @param errorMessage
     */
    private void showDialogFindByNumber(String errorMessage) {
        dialogFindByNumber = new Dialog(requireContext());
        View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_find_by_number, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input_number);
        final TextView errorMessageText = viewInflated.findViewById(R.id.dialog_message_error);
        if (errorMessage != null) {
            input.setText(searchNumberIncident);
            errorMessageText.setText(errorMessage);
        } else {
            searchNumberIncident = null;
        }

        Button buttonPublish = (Button) viewInflated.findViewById(R.id.button_search);
        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().toUpperCase().matches("[BSGA][2][0-9]{3}[A-L][0-9]+")) {
                    searchNumberIncident = input.getText().toString().toUpperCase();
                    presenter.findByNumber(searchNumberIncident);
                } else {
                    errorMessageText.setText(R.string.incorect_number);
                    viewInflated.announceForAccessibility(getString(R.string.incorect_number));
                }
            }
        });

        // set the custom dialog components - text, image and button
        ImageView close = (ImageView) viewInflated.findViewById(R.id.btnCloseFailure);
        // Close Button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNumberIncident = null;
                dialogFindByNumber.dismiss();
            }
        });

        dialogFindByNumber.setContentView(viewInflated);
        dialogFindByNumber.setCancelable(true);
        if (dialogFindByNumber.getWindow() != null) {
            dialogFindByNumber.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Fix Dialog Size
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialogFindByNumber.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialogFindByNumber.getWindow().setAttributes(lp);
            dialogFindByNumber.show();
        }
    }

    /**
     * callbak for ws find incident by number.
     *
     * @param errorMessage ws response is an error message
     * @param dmrOffline   true if backoffice is down
     * @param posIncident  position of the incident find.
     */
    public void callBackFindByNumber(String errorMessage, boolean dmrOffline, LatLng posIncident) {
        if (dialogFindByNumber != null) {
            dialogFindByNumber.dismiss();
        }
        if (dmrOffline) {
            searchNumberIncident = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.MyDialogTheme);
            builder
                    .setTitle(R.string.information)
                    .setMessage(getString(R.string.dmr_offline))
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else if (errorMessage != null) {
            showDialogFindByNumber(errorMessage);
        } else {
            updateLocation(posIncident);
        }
    }

    /**
     * Display modal dialog find sheet tour by id.
     *
     * @param errorMessage
     */
    private void showDialogFindSheetTourById(String errorMessage) {
        searchNumberIncident = null;
        dialogFindFDTById = new Dialog(requireContext());

        View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_search_fdt_by_id, null, false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input_number);

        final TextView errorMessageText = viewInflated.findViewById(R.id.dialog_message_error);
        if (errorMessage != null) {
            input.setText(String.valueOf(searchIdFdt));
            errorMessageText.setText(errorMessage);
        }

        Button buttonSearch = (Button) viewInflated.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().toUpperCase().matches("[0-9]+")) {

                    loadingDialog = new ProgressDialog(requireContext());
                    loadingDialog.setMessage("Chargement..");
                    loadingDialog.setIndeterminate(false);
                    loadingDialog.setCancelable(true);
                    loadingDialog.show();
                    searchIdFdt = Integer.parseInt(input.getText().toString());
                    presenter.searchFDTById(searchIdFdt);
                } else {
                    errorMessageText.setText(R.string.incorect_number);
                    viewInflated.announceForAccessibility(getString(R.string.incorect_number));
                }
            }
        });

        // set the custom dialog components - text, image and button
        ImageView close = (ImageView) viewInflated.findViewById(R.id.btnCloseFailure);
        // Close Button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchIdFdt = -1;
                dialogFindFDTById.dismiss();
            }
        });

        dialogFindFDTById.setContentView(viewInflated);
        dialogFindFDTById.setCancelable(true);
        if (dialogFindFDTById.getWindow() != null) {
            dialogFindFDTById.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Fix Dialog Size
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialogFindFDTById.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialogFindFDTById.getWindow().setAttributes(lp);
            dialogFindFDTById.show();
        }
    }

    /**
     * callbak for search FDT by id.
     *
     * @param errorMessage ws response is an error message
     * @param incidentsFDT
     */
    public void callBackSearchFDTById(String errorMessage, List<Incident> incidentsFDT, String infosAvantTournee, String infosApresTournee) {
        dialogFindFDTById.dismiss();
        updateMapToCentralParis();
        if (errorMessage != null) {
            showDialogFindSheetTourById(errorMessage);
        } else {
            findFDTbyIdOut.setVisibility(View.VISIBLE);
            findFDTbyIdIn.setVisibility(View.GONE);
            this.infosAvantTournee = infosAvantTournee;
            this.infosApresTournee = infosApresTournee;
            activity.enterInSearchFDTMode();
            findByNumberButton.setVisibility(View.GONE);
            this.incidentsFDT = incidentsFDT;
            anomalyChoiceBox.setText("FDT n°" + searchIdFdt + " en consultation");
            anomalyChoiceBox.setVisibility(View.VISIBLE);
        }
        loadingDialog.dismiss();
    }

    @Override
    public int getSearchIdFdt() {
        return searchIdFdt;
    }

    @Override
    public List<Incident> getIncidentsFDT() {
        return incidentsFDT;
    }


    public String getInfosAvantTournee() {
        return infosAvantTournee;
    }

    public String getInfosApresTournee() {
        return infosApresTournee;
    }

    public void setInfosApresTournee(String infosApresTournee) {
        this.infosApresTournee = infosApresTournee;
    }

    public void refreshIncidentsFdt(Incident incidentToRefresh) {
        List<Incident> incidents = new ArrayList<>();
        for (Incident incident : incidentsFDT) {
            if (incident.getId() == incidentToRefresh.getId()) {
                incidents.add(incidentToRefresh);
            } else {
                incidents.add(incident);
            }
        }
        incidentsFDT = incidents;
    }

    public String getFindByNumberValue() {
        return searchNumberIncident;
    }

    public void onBottomSheetCollapsed() {
        getView().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        getView().setVisibility(View.VISIBLE);
    }

    public void onBottomSheetExpanded() {
        getView().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getView().setVisibility(View.GONE);
    }

    public interface OnMapParisFragmentInteractionListener {
        void onUpdateClosestIncidents(final List<Incident> closestIncidents, boolean reset);

        void onUpdateLocation(LatLng location, String myAddr);

        void onMarkerClicked(Incident incident);

        void onClickPrecisePosition();

        void initMapFragment();

        void enterInSearchFDTMode();

        void exitSearchFDTMode();
    }
}