package com.accenture.dansmarue.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.presenters.WelcomeMapPresenter;
import com.accenture.dansmarue.mvp.views.WelcomeMapView;
import com.accenture.dansmarue.ui.adapters.RecyclerViewAdapter;
import com.accenture.dansmarue.ui.fragments.MapParisFragment;
import com.accenture.dansmarue.ui.fragments.ProfileFragment;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.RecyclerItemClickListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class WelcomeMapActivity extends BaseActivity implements WelcomeMapView, MapParisFragment.OnMapParisFragmentInteractionListener, ProfileFragment.OnProfileFragmentInteractionListener {

    private static final String TAG = WelcomeMapActivity.class.getCanonicalName();

    private static final int LOGIN_REQUEST_CODE = 1982;

    @Inject
    protected WelcomeMapPresenter presenter;

    private MapParisFragment mapParisFragment;

    @BindView(R.id.wbsa_fab)
    protected FloatingActionButton addAnomalyFloatingButton;
    @BindView(R.id.follow_fab)
    protected FloatingActionButton followAnomalyFloatingButton;
    @BindView(R.id.greetings_fab)
    protected FloatingActionButton greetingsAnomalyFloatingButton;
    @BindView(R.id.wbsa_coordinator)
    protected CoordinatorLayout coordinatorLayout;
    @BindView(R.id.wbsa_bottom_sheet)
    protected View bottomSheet;
    @BindView(R.id.bottom_sheet_my_adress)
    protected TextView selectedAdress;
    @BindView(R.id.title_bottom_sheet_my_incident)
    protected TextView selectedIncidentTitle;
    @BindView(R.id.image_bottom_sheet_my_incident)
    protected ImageView selectedIncidentPic;
    @BindView(R.id.adress_bottom_sheet_my_incident)
    protected TextView selectedIncidentAdress;
    @BindView(R.id.layout_bottom_sheet_my_adress)
    protected LinearLayout layoutSelectedAdress;
    @BindView(R.id.layout_bottom_sheet_my_incident)
    protected LinearLayout layoutSelectedIncident;
    @BindView(R.id.image_bottom_sheet_my_adress)
    protected ImageButton buttonSelectedAdress;
    @BindView(R.id.navigation)
    protected BottomNavigationView bottomNavigationView;
    @BindView(R.id.my_recycler_view)
    protected RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @BindView(R.id.bottom_sheet_add_anomaly)
    protected LinearLayout layoutAddAnomaly;
    @BindView(R.id.bottom_sheet_anomaly_position_precision)
    protected LinearLayout layoutPrecisePosition;

    // setup bottom sheet
    // To handle FAB animation upon entrance and exit
    private Animation growAnimation;
    private Animation shrinkAnimation;

    //BottomSheet behavior
    private BottomSheetBehavior behavior;

    private boolean showFAB = true;
    private boolean fabFollowed = false;
    private boolean preciseMode = false;

    private ProfileFragment profileFragment;

    private Incident summarizedIncident;

    private final static int REQUEST_CODE_LOGIN = 7;

    private boolean congratulated;


    @Override
    protected void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(((DansMaRueApplication) getApplication()).getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        //askForOptinsPermission();
        initAnimations();
        initBottomSheet();
        initBottomNavigationView();

        // setup fragment and hide or not bottom sheet
        mapParisFragment = MapParisFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, mapParisFragment);
        fragmentTransaction.commit();


    }

    @Override
    protected int getContentView() {
        return R.layout.welcome_map_activity_layout;
    }


    /**private void askForOptinsPermission() {

        //BEING COMPLIANT WITH GDPR
        // Manage opt-ins
        AdtagInitializer adtagInitializer = AdtagInitializer.getInstance();
        if (adtagInitializer.optinsNeverAsked()) {
            // ask the user to validate the optins
            new AlertDialog.Builder(this).setMessage(getString(R.string.ask_optins_permission_message))
                    .setCancelable(true)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adtagInitializer.updateOptin(OPTIN.USER_DATA, true);
                            adtagInitializer.allOptinsAreUpdated();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Non",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adtagInitializer.updateOptin(OPTIN.USER_DATA, false);
                    adtagInitializer.allOptinsAreUpdated();
                    dialog.dismiss();
                }
            }).show();
        }
    }**/

    /**
     * Menu map or profile
     */
    private void initBottomNavigationView() {

        PrefManager prefManager = new PrefManager(getApplicationContext());
        
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_menu_map:
                        coordinatorLayout.setVisibility(View.VISIBLE);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, mapParisFragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;
                    case R.id.bottom_menu_profile:
                        if (prefManager.isConnected()) {
                            presenter.profileClicked();
                        } else {
                            startActivityForResult(new Intent(WelcomeMapActivity.this, LoginActivity.class), LOGIN_REQUEST_CODE);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void initAnimations() {
        growAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_shrink);

        addAnomalyFloatingButton.setVisibility(View.VISIBLE);
        addAnomalyFloatingButton.startAnimation(growAnimation);

        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addAnomalyFloatingButton.setVisibility(View.GONE);
//                followAnomalyFloatingButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void initBottomSheet() {
        behavior = BottomSheetBehavior.from(bottomSheet);

        addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));

        behavior.setPeekHeight((int) ((198) * Resources.getSystem().getDisplayMetrics().density));

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (showFAB) {
                            if (addAnomalyFloatingButton.getVisibility() == View.VISIBLE)
                                addAnomalyFloatingButton.startAnimation(shrinkAnimation);
                            if (followAnomalyFloatingButton.getVisibility() == View.VISIBLE)
                                followAnomalyFloatingButton.startAnimation(shrinkAnimation);
                            if (greetingsAnomalyFloatingButton.getVisibility() == View.VISIBLE)
                                greetingsAnomalyFloatingButton.startAnimation(shrinkAnimation);
                        }
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        collapseListOfActions();
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        expandListOfActions();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        // RecyclerView de la BottomSheet
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerViewAdapter(this, R.layout.recycler_view_item, null);
        recyclerView.setAdapter(adapter);
        // Magic option to keep recyclerview at the right place
        recyclerView.setFocusable(false);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        behavior.setPeekHeight((int) ((198) * Resources.getSystem().getDisplayMetrics().density));
                        bottomSheet.scrollTo(0,0);

                        presenter.onThumbnailClicked(adapter.getData(position));
                    }
                })
        );

    }

    private void expandListOfActions() {
        showFAB = false;

        layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        buttonSelectedAdress.setImageResource(R.drawable.ic_close);

        layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
        selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        layoutSelectedAdress.setVisibility(View.VISIBLE);
        layoutSelectedIncident.setVisibility(View.GONE);

        addAnomalyFloatingButton.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);

    }

    private void collapseListOfActions() {
        if (showFAB) {
            layoutSelectedAdress.setVisibility(View.VISIBLE);
            layoutSelectedIncident.setVisibility(View.GONE);
        } else {
            showFAB = true;
        }

        layoutPrecisePosition.setVisibility(View.VISIBLE);
        layoutAddAnomaly.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);
        addAnomalyFloatingButton.setVisibility(View.VISIBLE);
        addAnomalyFloatingButton.startAnimation(growAnimation);

        layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_tranparent));
        selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));


        layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        buttonSelectedAdress.setImageResource(R.drawable.ic_geoloc_blue_circle);

        addAnomalyFloatingButton.setImageResource(R.drawable.ic_add_anomaly_fab);
        addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));

        if (fabFollowed) {
            followAnomalyFloatingButton.setImageResource(R.drawable.ic_followed);
            followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        } else {
            followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow);
            followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        }

        behavior.setPeekHeight((int) ((198) * Resources.getSystem().getDisplayMetrics().density));

//        if (preciseMode) {
//            preciseMode = false;
//            mapParisFragment.precisePositionModeFunction(preciseMode);
//        }


    }

    @OnClick({R.id.bottom_sheet_my_adress, R.id.adress_bottom_sheet_my_incident, R.id.title_bottom_sheet_my_incident})
    public void onClickMyAdress() {

        // Depends on bottom sheet position

        if (behavior.getPeekHeight() == (int) ((198) * Resources.getSystem().getDisplayMetrics().density)) {
            layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            addAnomalyFloatingButton.setImageResource(R.drawable.ic_add_pink_24px);
            addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));

            layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
            selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            behavior.setPeekHeight((int) ((354) * Resources.getSystem().getDisplayMetrics().density));

            if (fabFollowed) {
                followAnomalyFloatingButton.setImageResource(R.drawable.ic_followed_pink);
                followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            } else {
                followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow_pink);
                followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            }

            if (congratulated) {
                greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_grey);
                greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));
            } else {
                greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_green);
                greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            }


        } else {
            layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            addAnomalyFloatingButton.setImageResource(R.drawable.ic_add_anomaly_fab);
            addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));


            if (fabFollowed) {
                followAnomalyFloatingButton.setImageResource(R.drawable.ic_followed);
                followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
            } else {
                followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow);
                followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
            }

            layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_tranparent));
            selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            if (congratulated) {
                greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_grey);
                greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));
            } else {
                greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_white);
                greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));
            }

            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setPeekHeight((int) ((198) * Resources.getSystem().getDisplayMetrics().density));
        }
    }

    @OnClick(R.id.image_bottom_sheet_my_adress)
    public void onClickCross() {
        if (preciseMode) {
            onClickPrecisePosition();
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setPeekHeight((int) ((198) * Resources.getSystem().getDisplayMetrics().density));
        }
    }

    @OnClick(R.id.bottom_sheet_anomaly_position_precision)
    public void onClickPrecisePosition() {


        mapParisFragment = (MapParisFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (null != mapParisFragment && mapParisFragment.isAdded()) {
            preciseMode = !preciseMode;
            mapParisFragment.precisePositionModeFunction(preciseMode);

            layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            addAnomalyFloatingButton.setImageResource(R.drawable.ic_add_anomaly_fab);
            addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));

            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setPeekHeight((int) ((198) * Resources.getSystem().getDisplayMetrics().density));
        }
    }

    @OnClick({R.id.wbsa_fab, R.id.bottom_sheet_add_anomaly})
    public void addAnomalyClicked() {
        // Add another check about the address
        if (mapParisFragment != null && mapParisFragment.isValidLocation() && !selectedAdress.getText().equals("")) {

            // Descendre la bottom sheet avant l'ajout d'ano suivant si la bottom-sheet est à mi-hauteur ou on top
            if (behavior.getPeekHeight() == (int) ((354) * Resources.getSystem().getDisplayMetrics().density)) {
                Log.i(TAG, "mi-hauteur");
                onClickMyAdress();
            } else {
                Log.i(TAG, "on top");
                onClickCross();
                bottomSheet.scrollTo(0, 0);
            }

            // Send my current position (geoloc || search bar)
            Log.i(TAG, "ma position avant + : "+mapParisFragment.getMyCurrentLocationPosition().latitude+" longitude : "+mapParisFragment.getMyCurrentLocationPosition().longitude);

            Intent intentAddAnomaly = new Intent();
            intentAddAnomaly.setClass(WelcomeMapActivity.this, AddAnomalyActivity.class);
            intentAddAnomaly.putExtra(Constants.EXTRA_CURRENT_LOCATION, (mapParisFragment.getMyCurrentLocationPosition()));
            if (mapParisFragment.isSearchBarMode()) {
                intentAddAnomaly.putExtra(Constants.EXTRA_SEARCH_BAR_ADDRESS, (mapParisFragment.getSearchBarAdress()));
            }

            startActivityForResult(intentAddAnomaly,9821);

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            Toast.makeText(getApplicationContext(), "Merci de préciser votre position", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.profileClicked();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                bottomNavigationView.setSelectedItemId(R.id.bottom_menu_map);
            }
        }
    }

    @Override
    public void showProfile() {
        coordinatorLayout.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance();
        }
        fragmentTransaction.replace(R.id.frameLayout, profileFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void showIncidentDetails(Incident incident) {
        final Intent intent = new Intent(WelcomeMapActivity.this, AnomalyDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_INCIDENT_ID, incident.getId());
        intent.putExtra(Constants.EXTRA_INCIDENT_SOURCE, incident.getSource());
        startActivityForResult(intent,9281);
    }


    @Override
    public void onUpdateClosestIncidents(List<Incident> closestIncidents) {
        if (CollectionUtils.isNotEmpty(closestIncidents)) {
            adapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.recycler_view_item, closestIncidents);
            recyclerView.setAdapter(adapter);

        } else {
            adapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.recycler_view_item, null);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onUpdateLocation(LatLng location, String myAddr) {
        layoutSelectedIncident.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);

        layoutSelectedAdress.setVisibility(View.VISIBLE);
        addAnomalyFloatingButton.setVisibility(View.VISIBLE);

        if ("".equals(myAddr)) {
            selectedAdress.setText(location.latitude + ", " + location.longitude);
        } else if (!myAddr.contains("Paris")) {
            selectedAdress.setText("");
        } else {
            selectedAdress.setText(MiscTools.whichPostalCode(myAddr));
        }
        Log.i(TAG, "onUpdateLocation: adresse ok");
    }


    // Avoid user to quit the app
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onMarkerClicked(final Incident incident) {

        congratulated = false;

        if (!preciseMode && incident != null) {

            // Collapse BottomSheet and init address bar
            collapseListOfActions();

            summarizedIncident = incident;

            layoutPrecisePosition.setVisibility(View.GONE);
            layoutAddAnomaly.setVisibility(View.VISIBLE);

            layoutSelectedAdress.setVisibility(View.GONE);
            layoutSelectedIncident.setVisibility(View.VISIBLE);

            selectedIncidentAdress.setText(summarizedIncident.getAddress());
            selectedIncidentTitle.setText(summarizedIncident.getAlias());

            addAnomalyFloatingButton.setVisibility(View.GONE);

            if (summarizedIncident.isFromRamen()) {
                greetingsAnomalyFloatingButton.setVisibility(View.GONE);
                followAnomalyFloatingButton.setVisibility(View.VISIBLE);
                followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow);
                followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));

            } else {

                if (summarizedIncident.isResolu()) {

                    followAnomalyFloatingButton.setVisibility(View.GONE);
                    greetingsAnomalyFloatingButton.setVisibility(View.VISIBLE);

                    if (congratulated) {
                        greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_grey);
                        greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));
                    } else {
                        greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_white);
                        greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));
                    }

                } else {

                    greetingsAnomalyFloatingButton.setVisibility(View.GONE);
                    followAnomalyFloatingButton.setVisibility(View.VISIBLE);

                    if (summarizedIncident.isIncidentFollowedByUser()) {
                        followAnomalyFloatingButton.setImageResource(R.drawable.ic_followed);
                        fabFollowed = true;
                    } else {
                        followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow);
                        fabFollowed = false;

                    }

                }
            }

            if (summarizedIncident.getFirstAvailablePicture() != null) {
                Glide.with(this).load(summarizedIncident.getFirstAvailablePicture()).fallback(summarizedIncident.getPictures().getGenericPictureId()).error(summarizedIncident.getPictures().getGenericPictureId()).into(selectedIncidentPic);
            } else {
                Glide.with(this).load(summarizedIncident.getPictures().getGenericPictureId()).into(selectedIncidentPic);
            }
        }
    }

    @OnClick(R.id.image_bottom_sheet_my_incident)
    public void onThumbnailClicked() {
        presenter.onThumbnailClicked(summarizedIncident);
    }


    /**
     * Fab Buttons
     */

    @OnClick(R.id.follow_fab)
    public void followClicked() {
        //Should always be true
        if (summarizedIncident != null) {
            if (!summarizedIncident.isFromRamen()) {
                if (!summarizedIncident.isIncidentFollowedByUser()) {
                    presenter.followAnomaly(String.valueOf(summarizedIncident.getId()));
                } else {
                    presenter.unfollowAnomaly(String.valueOf(summarizedIncident.getId()));
                }
            }
        }
    }

    @OnClick(R.id.greetings_fab)
    public void congratulate() {
        if (null != summarizedIncident) {
            if (!congratulated) {
                greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_grey);
                greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));

                Snackbar.make(findViewById(R.id.navigation), R.string.greetings_ok, Snackbar.LENGTH_LONG)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                switch (event) {
                                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                        presenter.congratulateAnomalie(String.valueOf(summarizedIncident.getId()));
                                        congratulated = true;
                                        break;
                                }
                            }
                        })
                        .setAction(R.string.greetings_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                changeGreetingsColorButton();
                            }
                        })
                        .show();
            }
        }
    }

    private void changeGreetingsColorButton() {
        if (behavior.getPeekHeight() == (int) ((198) * Resources.getSystem().getDisplayMetrics().density)) {
            greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_white);
            greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));
        } else {
            greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_green);
            greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
        }
    }

    /**
     * WS Results
     */

    public void displayFollow() {

        mapParisFragment.refreshDatas();
        followAnomalyFloatingButton.setImageResource(R.drawable.ic_followed);
        Snackbar.make(findViewById(R.id.navigation), R.string.follow_anomaly, Snackbar.LENGTH_LONG).show();
        summarizedIncident.setIncidentFollowedByUser(true);
    }

    public void displayFollowFailure() {
        Snackbar.make(findViewById(R.id.navigation), R.string.follow_anomaly_failure, Snackbar.LENGTH_LONG).show();
    }

    public void displayUnfollow() {
        mapParisFragment.refreshDatas();
        followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow);
        Snackbar.make(findViewById(R.id.navigation), R.string.unfollow_anomaly, Snackbar.LENGTH_LONG).show();
        summarizedIncident.setIncidentFollowedByUser(false);
    }

    public void displayUnfollowFailure() {
        Snackbar.make(findViewById(R.id.navigation), R.string.unfollow_anomaly_failure, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void inviteToLogin() {
        startActivityForResult(new Intent(WelcomeMapActivity.this, LoginActivity.class), REQUEST_CODE_LOGIN);
    }

    @Override
    public void displayGreetingsOk() {
        Log.d(TAG, "displayGreetingsOk: ");
    }

    @Override
    public void displayGreetingsKo() {
        Snackbar.make(findViewById(R.id.navigation), R.string.dmr_error, Snackbar.LENGTH_LONG).show();
        congratulated = false;
        changeGreetingsColorButton();
    }


}
