package com.accenture.dansmarue.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;
import com.accenture.dansmarue.mvp.presenters.WelcomeMapEquipementPresenter;
import com.accenture.dansmarue.mvp.views.WelcomeMapEquipementView;
import com.accenture.dansmarue.ui.adapters.RecyclerViewAdapter;
import com.accenture.dansmarue.ui.fragments.MapParisEquipementFragment;
import com.accenture.dansmarue.ui.fragments.ProfileFragment;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.RecyclerItemClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class WelcomeMapEquipementActivity extends BaseActivity implements WelcomeMapEquipementView, MapParisEquipementFragment.OnMapParisEquipementFragmentInteractionListener, ProfileFragment.OnProfileFragmentInteractionListener {

    private static final String TAG = WelcomeMapEquipementActivity.class.getCanonicalName();

    private static final int LOGIN_REQUEST_CODE = 1982;

    @Inject
    protected WelcomeMapEquipementPresenter presenter;

    private MapParisEquipementFragment mapParisEquipementFragment;

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

    @BindView(R.id.adress_bottom_sheet_my_incident)
    protected TextView selectedIncidentAdress;
    @BindView(R.id.bottom_sheet_my_incident_nb_anomalies)
    protected TextView selectedIncidentEquipementNbAnomalies;
    @BindView(R.id.layout_bottom_sheet_my_adress)
    protected LinearLayout layoutSelectedAdress;
    @BindView(R.id.layout_bottom_sheet_my_incident)
    protected LinearLayout layoutSelectedIncident;
    @BindView(R.id.image_bottom_sheet_my_adress)
    protected ImageButton buttonSelectedAdress;

    @BindView(R.id.image_bottom_sheet_my_incident)
    protected ImageButton buttonSelectedIncident;

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

    private ProfileFragment profileFragment;

    private Incident summarizedIncident;

    private final static int REQUEST_CODE_LOGIN = 7;
    private final static int REQUEST_CODE_ADD_ANOMALY = 1926;

    private boolean congratulated;

    private boolean oneEquipementIsSelected = false;

    private Equipement lastEquipement;


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

        initAnimations();
        initBottomSheet();
        initBottomNavigationView();

        // setup fragment and hide or not bottom sheet
        mapParisEquipementFragment = MapParisEquipementFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, mapParisEquipementFragment);
        fragmentTransaction.commit();

        // Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

    }

    @Override
    protected int getContentView() {
        return R.layout.welcome_map_equipement_activity_layout;
    }

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
                        findViewById(R.id.bottom_menu_map).setContentDescription(getString(R.string.bottom_map_active_txt));
                        findViewById(R.id.bottom_menu_profile).setContentDescription(getString(R.string.bottom_profile_desc_txt));
                        coordinatorLayout.setVisibility(View.VISIBLE);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, mapParisEquipementFragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;
                    case R.id.bottom_menu_profile:
                        findViewById(R.id.bottom_menu_map).setContentDescription(getString(R.string.bottom_map_desc_txt));
                        findViewById(R.id.bottom_menu_profile).setContentDescription(getString(R.string.bottom_profile_active_txt));
                        if (prefManager.isConnected()) {
                            presenter.profileClicked();
                        } else {
                            startActivityForResult(new Intent(WelcomeMapEquipementActivity.this, LoginActivity.class), LOGIN_REQUEST_CODE);
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

        behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));

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

                        onClickCross();
                        bottomSheet.scrollTo(0, 0);
                        presenter.onThumbnailClicked(adapter.getData(position));
                    }
                })
        );

    }

    private void expandListOfActions() {
        showFAB = false;

        if (oneEquipementIsSelected) {
            layoutSelectedIncident.setVisibility(View.VISIBLE);
            layoutSelectedAdress.setVisibility(View.GONE);
            layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
            selectedIncidentEquipementNbAnomalies.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
            selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        } else {
            layoutSelectedAdress.setVisibility(View.VISIBLE);
            layoutSelectedIncident.setVisibility(View.GONE);
            layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            buttonSelectedAdress.setImageResource(R.drawable.ic_close);
        }

        buttonSelectedIncident.setVisibility(View.VISIBLE);
        buttonSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        buttonSelectedIncident.setImageResource(R.drawable.ic_close);

//        layoutSelectedAdress.setVisibility(View.VISIBLE);
//        layoutSelectedIncident.setVisibility(View.GONE);

        addAnomalyFloatingButton.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);

    }

    private void collapseListOfActions() {
        if (showFAB) {
//            layoutSelectedAdress.setVisibility(View.VISIBLE);
//            layoutSelectedIncident.setVisibility(View.GONE);
        } else {
            showFAB = true;
        }

//        layoutPrecisePosition.setVisibility(View.VISIBLE);
//        layoutAddAnomaly.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);
        addAnomalyFloatingButton.setVisibility(View.VISIBLE);
        addAnomalyFloatingButton.startAnimation(growAnimation);

        if (oneEquipementIsSelected) {
            layoutSelectedIncident.setVisibility(View.VISIBLE);
            layoutSelectedAdress.setVisibility(View.GONE);
            layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_tranparent));
            selectedIncidentEquipementNbAnomalies.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            buttonSelectedIncident.setVisibility(View.INVISIBLE);
        } else {
            layoutSelectedAdress.setVisibility(View.VISIBLE);
            layoutSelectedIncident.setVisibility(View.GONE);
            layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            buttonSelectedAdress.setImageResource(R.drawable.ic_geoloc_blue_circle);
        }


        addAnomalyFloatingButton.setImageResource(R.drawable.ic_add_anomaly_fab);
        addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));

        if (fabFollowed) {
            followAnomalyFloatingButton.setImageResource(R.drawable.ic_followed);
            followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        } else {
            followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow);
            followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        }

        behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));

//        if (preciseMode) {
//            preciseMode = false;
//            mapParisEquipementFragment.precisePositionModeFunction(preciseMode);
//        }


    }

    @OnClick({R.id.bottom_sheet_my_adress, R.id.adress_bottom_sheet_my_incident, R.id.title_bottom_sheet_my_incident})
    public void onClickMyAdress() {

        // Depends on bottom sheet position
        float density = (198) * Resources.getSystem().getDisplayMetrics().density;
        if (Math.abs(behavior.getPeekHeight() - density) < Constants.EPSILON) {

            if (!oneEquipementIsSelected) {
                layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            } else {

                layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                selectedIncidentEquipementNbAnomalies.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            }

            addAnomalyFloatingButton.setImageResource(R.drawable.ic_add_pink_24px);
            addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));


            behavior.setPeekHeight(Math.round((354) * Resources.getSystem().getDisplayMetrics().density));

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

            if (!oneEquipementIsSelected) {

                layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            } else {
                layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                selectedIncidentEquipementNbAnomalies.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            }


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
            selectedIncidentEquipementNbAnomalies.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_tranparent));
            selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            if (congratulated) {
                greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_grey);
                greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));
            } else {
                greetingsAnomalyFloatingButton.setImageResource(R.drawable.ic_greetings_white);
                greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));
            }

            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));
        }
    }

    @OnClick({R.id.image_bottom_sheet_my_incident, R.id.image_bottom_sheet_my_adress})
    public void onClickCross() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));

    }


    @OnClick({R.id.wbsa_fab, R.id.bottom_sheet_add_anomaly})
    public void addAnomalyClicked() {
        if (mapParisEquipementFragment != null) {

            // If one Equipment is selected
            // Send my current position (geoloc || search bar)
            if (null != mapParisEquipementFragment.getCurrentEquipement() && oneEquipementIsSelected) {

                // Descendre la bottom sheet avant l'ajout d'ano suivant si la bottom-sheet est à mi-hauteur ou on top
                float density = (354) * Resources.getSystem().getDisplayMetrics().density;
                if (Math.abs(behavior.getPeekHeight() - density) < Constants.EPSILON) {
                    Log.i(TAG, "mi-hauteur");
                    onClickMyAdress();
                } else {
                    Log.i(TAG, "on top");
                    onClickCross();
                    bottomSheet.scrollTo(0, 0);
                }

                Equipement e = mapParisEquipementFragment.getCurrentEquipement();

                // Send Light Object please
                Equipement eLight = e;
                eLight.setListeIncidents(null);


                String eJson = new GsonBuilder().create().toJson(eLight);

                startActivityForResult(new Intent()
                        .setClass(WelcomeMapEquipementActivity.this, AddAnomalyEquipementActivity.class)
                        .putExtra(Constants.CURRENT_EQUIPEMENT, eJson), 9281);

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


            } else {
                String msg = presenter.getTypeEquipementNoSelection();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle(R.string.popup_error_title)
                        .setMessage(msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: kodak suite" + requestCode);
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.profileClicked();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                bottomNavigationView.setSelectedItemId(R.id.bottom_menu_map);
            }
        } else if (requestCode == REQUEST_CODE_ADD_ANOMALY) {
            Log.i(TAG, "onActivityResult: kodak suite bis");

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Log.i(TAG, "onActivityResult: FIN" + lastEquipement.getName());

                refreshBottomSheetWithAnos();

            }
        }
    }

    public void refreshBottomSheetWithAnos() {
        Log.i(TAG, "Refresh bottom : " + lastEquipement.getName());
    }

    @Override
    public void showProfile() {
        coordinatorLayout.setVisibility(View.GONE);
        startActivity(new Intent(this, AnomaliesActivity.class));
    }

    @Override
    public void showIncidentDetails(Incident incident) {

        final Intent intent = new Intent(WelcomeMapEquipementActivity.this, AnomalyEquipementDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_INCIDENT_ID, incident.getId());
        intent.putExtra(Constants.EXTRA_INCIDENT_SOURCE, incident.getSource());
        startActivityForResult(intent, REQUEST_CODE_ADD_ANOMALY);


    }


    @Override
    public void onUpdateClosestIncidents(List<Incident> closestIncidents) {
        if (CollectionUtils.isNotEmpty(closestIncidents)) {
            adapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.recycler_view_item, closestIncidents);
            recyclerView.setAdapter(adapter);

        } else {
            Log.i(TAG, "onUpdateClosestIncidents: clear list");
            adapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.recycler_view_item, null);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    public void onUpdateLocation(LatLng location, String myAddr) {

        oneEquipementIsSelected = false;
        layoutSelectedAdress.setVisibility(View.VISIBLE);
        layoutAddAnomaly.setVisibility(View.VISIBLE);
        layoutSelectedIncident.setVisibility(View.GONE);

        if ("".equals(myAddr)) {
            selectedAdress.setText(location.latitude + ", " + location.longitude);
        } else if (!myAddr.contains(getString(R.string.city_name))) {
            selectedAdress.setText("");
        } else {
            selectedAdress.setText(MiscTools.whichPostalCode(myAddr));
        }
    }


    // Avoid user to quit the app
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onMarkerClicked(final Equipement equipement) {

        lastEquipement = equipement;
        oneEquipementIsSelected = true;
        congratulated = false;

        if (equipement != null) {

            Log.i(TAG, "onMarkerClicked: display");

            // Collapse BottomSheet and init address bar
            collapseListOfActions();
            layoutAddAnomaly.setVisibility(View.VISIBLE);

            layoutSelectedAdress.setVisibility(View.GONE);
            layoutSelectedIncident.setVisibility(View.VISIBLE);

            selectedIncidentAdress.setText(equipement.getAdresse());
            selectedIncidentTitle.setText(equipement.getName());
            selectedIncidentEquipementNbAnomalies.setText(equipement.getNbAnomalies() + " " + getString(R.string.nbAnomaliesEquipement));

        } else {
            Log.i(TAG, "onMarkerClicked: nothing");
        }
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
        float density = (198) * Resources.getSystem().getDisplayMetrics().density;
        if (Math.abs(behavior.getPeekHeight() - density) < Constants.EPSILON) {
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

        mapParisEquipementFragment.refreshDatas();
        followAnomalyFloatingButton.setImageResource(R.drawable.ic_followed);
        Snackbar.make(findViewById(R.id.navigation), R.string.follow_anomaly, Snackbar.LENGTH_LONG).show();
        summarizedIncident.setIncidentFollowedByUser(true);
    }

    public void displayFollowFailure() {
        Snackbar.make(findViewById(R.id.navigation), R.string.follow_anomaly_failure, Snackbar.LENGTH_LONG).show();
    }

    public void displayUnfollow() {
        mapParisEquipementFragment.refreshDatas();
        followAnomalyFloatingButton.setImageResource(R.drawable.ic_follow);
        Snackbar.make(findViewById(R.id.navigation), R.string.unfollow_anomaly, Snackbar.LENGTH_LONG).show();
        summarizedIncident.setIncidentFollowedByUser(false);
    }

    public void displayUnfollowFailure() {
        Snackbar.make(findViewById(R.id.navigation), R.string.unfollow_anomaly_failure, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void inviteToLogin() {
        startActivityForResult(new Intent(WelcomeMapEquipementActivity.this, LoginActivity.class), REQUEST_CODE_LOGIN);
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
