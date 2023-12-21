package com.accenture.dansmarue.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.FavoriteAddress;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.presenters.WelcomeMapPresenter;
import com.accenture.dansmarue.mvp.views.WelcomeMapView;
import com.accenture.dansmarue.ui.adapters.RecyclerViewAdapter;
import com.accenture.dansmarue.ui.fragments.MapParisFragment;
import com.accenture.dansmarue.ui.fragments.MySpaceFragment;
import com.accenture.dansmarue.ui.fragments.ProfileFragment;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.PrefManager;
import com.accenture.dansmarue.utils.RecyclerItemClickListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * WelcomeMapActivity
 * Activity for main view
 */
public class WelcomeMapActivity extends BaseActivity implements WelcomeMapView, MapParisFragment.OnMapParisFragmentInteractionListener, MySpaceFragment.OnProfileFragmentInteractionListener, ProfileFragment.OnProfileFragmentInteractionListener {

    private static final String TAG = WelcomeMapActivity.class.getCanonicalName();

    @Inject
    protected WelcomeMapPresenter presenter;

    private MapParisFragment mapParisFragment;

    @BindView(R.id.avoid_duplicate)
    protected Button avoidDuplicateButton;
    @BindView(R.id.showAnomalyLinearLayout)
    protected LinearLayout showAnomalyLinearLayout;
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
    @BindView(R.id.layout_around_me)
    protected LinearLayout aroundMe;
    @BindView(R.id.txt_around_me)
    protected TextView txtArroundMe;
    @BindView(R.id.title_bottom_sheet_my_incident)
    protected TextView selectedIncidentTitle;
    @BindView(R.id.image_bottom_sheet_my_incident)
    protected ImageView selectedIncidentPic;
    @BindView(R.id.adress_bottom_sheet_my_incident)
    protected TextView selectedIncidentAdress;
    @BindView(R.id.layout_bottom_sheet_my_adress)
    protected LinearLayout layoutSelectedAdress;
    @BindView(R.id.layout_bottom_sheet_infos_avant_tournee)
    protected LinearLayout layoutInfoAvantFDT;
    @BindView(R.id.bottom_sheet_infos_avant_tournee)
    protected TextView txtInfoAvantFDT;
    @BindView(R.id.layout_bottom_sheet_my_incident)
    protected LinearLayout layoutSelectedIncident;
    @BindView(R.id.image_bottom_sheet_my_adress)
    protected ImageButton buttonSelectedAdress;
    @BindView(R.id.image_bottom_sheet_favorite_address)
    protected ImageButton buttonFavoriteAdress;
    @BindView(R.id.navigation)
    protected BottomNavigationView bottomNavigationView;
    @BindView(R.id.my_recycler_view)
    protected RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @BindView(R.id.bottom_sheet_add_anomaly)
    protected LinearLayout layoutAddAnomaly;
    @BindView(R.id.bottom_sheet_anomaly_position_precision)
    protected LinearLayout layoutPrecisePosition;
    @BindView(R.id.bottom_sheet_anomaly_text)
    protected TextView bottomSheetAnomalyTextview;
    @BindView(R.id.bottomSheetLinearLayout)
    protected LinearLayout bottomSheetLinearLayout;

    // setup bottom sheet
    // To handle FAB animation upon entrance and exit
    private Animation growAnimation;
    private Animation shrinkAnimation;

    //BottomSheet behavior
    private BottomSheetBehavior behavior;

    private boolean showFAB = true;
    private boolean fabFollowed = false;
    private boolean preciseMode = false;
    private MySpaceFragment mySpaceFragment;

    private Incident summarizedIncident;

    private final static int REQUEST_CODE_LOGIN = 7;

    private boolean congratulated;

    private String selectedAddressWithCodePostal;

    private Dialog dialogInfosApresFdt;

    private EditText inputInfosApresTournee;

    private FragmentTransaction fragmentTransaction;
    private Boolean isFDTMode = false;

    private final String labelInfosAvantTourneeTitle = "<b>Infos avant tournée : </b><br>";
    private LatLng location;

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
        bottomNavigationView.setSelectedItemId(R.id.bottom_menu_map);
    }

    @Override
    protected int getContentView() {
        return R.layout.welcome_map_activity_layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(Constants.SELECTED_ITEM_ID, bottomNavigationView.getSelectedItemId());
        savedInstanceState.putInt(Constants.BOTTOM_SHEET_STATE, behavior.getState());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemId = savedInstanceState.getInt(Constants.SELECTED_ITEM_ID);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        int bottomSheetState = savedInstanceState.getInt(Constants.BOTTOM_SHEET_STATE);
        handleBottomSheetBehavior(bottomSheetState);
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
    @Override public void onClick(DialogInterface dialog, int which) {
    adtagInitializer.updateOptin(OPTIN.USER_DATA, true);
    adtagInitializer.allOptinsAreUpdated();
    dialog.dismiss();
    }
    }).setNegativeButton("Non",new DialogInterface.OnClickListener() {
    @Override public void onClick(DialogInterface dialog, int which) {
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
        ArrayList<View> items = new ArrayList<View>();
        items.add(findViewById(R.id.bottom_menu_map));
        items.add(findViewById(R.id.bottom_menu_profile));
        bottomNavigationView.addChildrenForAccessibility(items);
        findViewById(R.id.bottom_menu_map).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        findViewById(R.id.bottom_menu_profile).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_menu_map:
                    findViewById(R.id.bottom_menu_map).setContentDescription(getString(R.string.bottom_map_active_txt));
                    findViewById(R.id.bottom_menu_profile).setContentDescription(getString(R.string.bottom_profile_desc_txt));
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    initMapFragment();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        showAnomalyLinearLayout.setAccessibilityTraversalBefore(R.id.bottom_menu_map);
                    }
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheet.scrollTo(0, 0);
                    break;
                case R.id.bottom_menu_profile:
                    findViewById(R.id.bottom_menu_map).setContentDescription(getString(R.string.bottom_map_desc_txt));
                    findViewById(R.id.bottom_menu_profile).setContentDescription(getString(R.string.bottom_profile_active_txt));
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    presenter.profileClicked();
                    break;
            }
            return true;
        });
    }

    private void handleBottomSheetBehavior(int state) {
        switch (state) {
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
                addAnomalyFloatingButton.setVisibility(View.GONE);
                followAnomalyFloatingButton.setVisibility(View.GONE);
                greetingsAnomalyFloatingButton.setVisibility(View.GONE);
                expandListOfActions();
                break;

            default:
                break;
        }
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * Init bottom sheet application.
     */
    private void initBottomSheet() {
        behavior = BottomSheetBehavior.from(bottomSheet);

        addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        followAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.pink)));
        greetingsAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));

        behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));

        avoidDuplicateButton.setOnClickListener(view -> {
            expandListOfActions();
            slideToTop();
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));
            bottomSheet.scrollTo(0, 0);
        });

        showAnomalyLinearLayout.setOnClickListener(view -> {
            expandListOfActions();
            slideToTop();
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));
            bottomSheet.scrollTo(0, 0);
        });

        txtArroundMe.setOnClickListener(view -> {
            txtArroundMe.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                txtArroundMe.setAccessibilityHeading(true);
            }
        });

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                handleBottomSheetBehavior(newState);
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
                new RecyclerItemClickListener(getApplicationContext(), (view, position) -> {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));
                    bottomSheet.scrollTo(0, 0);
                    presenter.onThumbnailClicked(adapter.getData(position));
                })
        );

    }

    /**
     * On expand bottom sheet.
     */
    private void expandListOfActions() {
        showFAB = false;
        if (mapParisFragment != null && mapParisFragment.getView() != null) {
            mapParisFragment.onBottomSheetExpanded();
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(0, -25, 0, 0);
        bottomSheetLinearLayout.setLayoutParams(layoutParams);
        layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        if (mapParisFragment.getSearchIdFdt() > 0) {
            layoutInfoAvantFDT.setVisibility(View.VISIBLE);
            selectedAdress.setText(getString(R.string.label_id_fdt) + " " + mapParisFragment.getSearchIdFdt());
        }

        buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        buttonSelectedAdress.setImageResource(R.drawable.ic_close);
        buttonSelectedAdress.setContentDescription(getString(R.string.close));
        buttonSelectedAdress.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

        layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
        selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        txtInfoAvantFDT.setText(mapParisFragment.getInfosAvantTournee());

        layoutPrecisePosition.setVisibility(View.VISIBLE);
        aroundMe.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        layoutSelectedAdress.setVisibility(View.VISIBLE);
        layoutSelectedIncident.setVisibility(View.GONE);

        showAnomalyLinearLayout.setVisibility(View.GONE);
        addAnomalyFloatingButton.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);

    }

    private void slideToTop() {
        Animation slide = null;
        slide = new TranslateAnimation(0f, 0f, 5f, 0f);
        slide.setDuration(500);
        bottomSheet.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // bottomSheet.clearAnimation();
                bottomSheet.setContentDescription(getString(R.string.text_around_incident));
                bottomSheet.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bottomSheet.setAccessibilityHeading(true);
                }
            }

        });
    }

    /**
     * On collapse bottom sheet.
     */
    private void collapseListOfActions() {
        if (mapParisFragment != null && mapParisFragment.getView() != null) {
            mapParisFragment.onBottomSheetCollapsed();
        }
        if (showFAB) {
            layoutSelectedAdress.setVisibility(View.VISIBLE);
            layoutSelectedIncident.setVisibility(View.GONE);
        } else {
            showFAB = true;
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(0, 80, 0, 0);
        bottomSheetLinearLayout.setLayoutParams(layoutParams);

        layoutAddAnomaly.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);
        showAnomalyLinearLayout.setVisibility(View.VISIBLE);
        layoutPrecisePosition.setVisibility(View.GONE);
        aroundMe.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        if (mapParisFragment.getSearchIdFdt() > 0) {
            avoidDuplicateButton.setText(R.string.show_anomaly_declared_fdt_button);
            avoidDuplicateButton.setContentDescription(getString(R.string.show_anomaly_declared_fdt_button));
            addAnomalyFloatingButton.setVisibility(View.GONE);
            layoutPrecisePosition.setVisibility(View.GONE);
            layoutInfoAvantFDT.setVisibility(View.GONE);
            selectedAdress.setText(Html.fromHtml(labelInfosAvantTourneeTitle));
            if (mapParisFragment.getInfosAvantTournee() != null && mapParisFragment.getInfosAvantTournee().length() > 0) {
                selectedAdress.setText(Html.fromHtml(labelInfosAvantTourneeTitle + mapParisFragment.getInfosAvantTournee()));
            }
        } else {
            avoidDuplicateButton.setText(R.string.show_anomaly_declared_button);
            avoidDuplicateButton.setContentDescription(getString(R.string.show_anomaly_declared_button));
            addAnomalyFloatingButton.setVisibility(View.VISIBLE);
            layoutPrecisePosition.setVisibility(View.VISIBLE);
        }

        addAnomalyFloatingButton.startAnimation(growAnimation);

        layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_tranparent));
        selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));


        layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        if (isFDTMode) {
            buttonSelectedAdress.setImageResource(android.R.color.transparent);
        } else {
            buttonSelectedAdress.setImageResource(R.drawable.ic_geoloc_blue_circle);
        }
        buttonSelectedAdress.setContentDescription(getString(R.string.text_my_position));
        buttonSelectedAdress.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        displayIconFavoriteAddress(selectedAddressWithCodePostal);

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

    }

    @OnClick({R.id.bottom_sheet_my_adress, R.id.adress_bottom_sheet_my_incident, R.id.title_bottom_sheet_my_incident})
    public void onClickMyAdress() {

        // Depends on bottom sheet position
        float density = (198) * Resources.getSystem().getDisplayMetrics().density;
        if (Math.abs(behavior.getPeekHeight() - density) < Constants.EPSILON) {
            layoutSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            selectedAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            buttonSelectedAdress.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            addAnomalyFloatingButton.setImageResource(R.drawable.ic_add_pink_24px);
            addAnomalyFloatingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));

            layoutSelectedIncident.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            selectedIncidentAdress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
            selectedIncidentTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
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
            behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));
        }
    }

    @OnClick(R.id.image_bottom_sheet_my_adress)
    public void onClickCross() {
        if (preciseMode) {
            onClickPrecisePosition();
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));
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
            behavior.setPeekHeight(Math.round((198) * Resources.getSystem().getDisplayMetrics().density));
        }
    }

    @OnClick({R.id.wbsa_fab, R.id.bottom_sheet_add_anomaly})
    public void addAnomalyClicked() {
        // Add another check about the address
        if (mapParisFragment != null && mapParisFragment.isValidLocation() && !selectedAdress.getText().equals("")) {

            // Descendre la bottom sheet avant l'ajout d'ano suivant si la bottom-sheet est à mi-hauteur ou on top
            float mihauteur = (354) * Resources.getSystem().getDisplayMetrics().density;
            if (Math.abs(behavior.getPeekHeight() - mihauteur) < Constants.EPSILON) {
                Log.i(TAG, "mi-hauteur");
                onClickMyAdress();
            } else {
                Log.i(TAG, "on top");
                onClickCross();
                bottomSheet.scrollTo(0, 0);
            }

            // Send my current position (geoloc || search bar)
            // Log.i(TAG, "ma position avant + : " + mapParisFragment.getMyCurrentLocationPosition().latitude + " longitude : " + mapParisFragment.getMyCurrentLocationPosition().longitude);

            Intent intentAddAnomaly = new Intent();
            intentAddAnomaly.setClass(WelcomeMapActivity.this, AddAnomalyActivity.class);
            intentAddAnomaly.putExtra(Constants.EXTRA_CURRENT_LOCATION, (mapParisFragment.getMyCurrentLocationPosition()));
            if (mapParisFragment.isSearchBarMode()) {
                intentAddAnomaly.putExtra(Constants.EXTRA_SEARCH_BAR_ADDRESS, (mapParisFragment.getSearchBarAdress()));
            } else if (mapParisFragment.getFavoriteAddressSelect().length() > 0) {
                intentAddAnomaly.putExtra(Constants.EXTRA_SEARCH_BAR_ADDRESS, (mapParisFragment.getFavoriteAddressSelect()));
            }

            startActivityForResult(intentAddAnomaly, 9821);

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            if (mapParisFragment.isNotConnectedMode()) {
                Toast.makeText(getApplicationContext(), R.string.add_anomaly_offline_mode, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.add_anomaly_no_location, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void initMapFragment() {
        // setup fragment and hide or not bottom sheet
        if (mapParisFragment == null) {
            mapParisFragment = MapParisFragment.newInstance();
        }
        displayFragment(mapParisFragment);
    }

    public void enterInSearchFDTMode() {
        isFDTMode = true;
        addAnomalyFloatingButton.setVisibility(View.GONE);

        selectedAdress.setText(Html.fromHtml(labelInfosAvantTourneeTitle));
        if (mapParisFragment.getInfosAvantTournee() != null && mapParisFragment.getInfosAvantTournee().length() > 0) {
            selectedAdress.setText(Html.fromHtml(labelInfosAvantTourneeTitle + mapParisFragment.getInfosAvantTournee()));
            selectedAdress.setMaxLines(3);
            selectedAdress.setEllipsize(TextUtils.TruncateAt.END);
        }
        avoidDuplicateButton.setText(R.string.show_anomaly_declared_fdt_button);
        avoidDuplicateButton.setContentDescription(getString(R.string.show_anomaly_declared_fdt_button));
        layoutPrecisePosition.setVisibility(View.GONE);
        buttonFavoriteAdress.setVisibility(View.GONE);
        buttonSelectedAdress.setImageResource(android.R.color.transparent);
        aroundMe.setVisibility(View.GONE);
    }

    public void exitSearchFDTMode() {
        isFDTMode = false;
        addAnomalyFloatingButton.setVisibility(View.VISIBLE);
        selectedAdress.setText("");
        avoidDuplicateButton.setText(R.string.show_anomaly_declared_button);
        avoidDuplicateButton.setContentDescription(getString(R.string.show_anomaly_declared_button));
        layoutPrecisePosition.setVisibility(View.VISIBLE);
        buttonFavoriteAdress.setVisibility(View.VISIBLE);
        buttonSelectedAdress.setImageResource(R.drawable.ic_geoloc_blue_circle);
        aroundMe.setVisibility(View.VISIBLE);
        layoutInfoAvantFDT.setVisibility(View.GONE);
        onUpdateClosestIncidents(null, true);
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
        PrefManager prefManager = new PrefManager(getApplicationContext());
        Incident incidentToRefresh = prefManager.getIncidentFdtToRefresh();
        if (mapParisFragment.getSearchIdFdt() > 0 && incidentToRefresh != null) {
            mapParisFragment.refreshIncidentsFdt(incidentToRefresh);
        }
        prefManager.setIncidentFdtToRefresh(null);
    }

    @Override
    public void showMySpace() {
        coordinatorLayout.setVisibility(View.GONE);
        if (mySpaceFragment == null) {
            mySpaceFragment = mySpaceFragment.newInstance();
        }
        displayFragment(mySpaceFragment);
    }

    @Override
    public void showIncidentDetails(Incident incident) {
        if (mapParisFragment.getSearchIdFdt() > 0) {
            location = new LatLng(Double.valueOf(incident.getLat()), Double.valueOf(incident.getLng()));
            mapParisFragment.setMyCurrentLocationPosition(location);
        }
        final Intent intent = new Intent(WelcomeMapActivity.this, AnomalyDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_INCIDENT_ID, incident.getId());
        intent.putExtra(Constants.EXTRA_INCIDENT_SOURCE, incident.getSource());
        startActivityForResult(intent, 9281);
    }


    @Override
    public void onUpdateClosestIncidents(List<Incident> closestIncidents, boolean reset) {

        if (reset) {
            adapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.recycler_view_item, null);
        } else {
            if (CollectionUtils.isNotEmpty(closestIncidents)) {
                if (adapter != null && adapter.getData() != null) {
                    //remove duplicate incident Begin
                    Map<Long, Incident> mapOrigin = new LinkedHashMap<>();
                    for (Incident incident : adapter.getData()) {
                        mapOrigin.put(incident.getId(), incident);
                    }
                    Map<Long, Incident> mapNew = new LinkedHashMap<>();
                    for (Incident incident : closestIncidents) {
                        mapNew.put(incident.getId(), incident);
                    }
                    mapOrigin.putAll(mapNew);
                    //remove duplicate incident End
                    closestIncidents.clear();
                    closestIncidents.addAll(mapOrigin.values());
                }
                adapter = new RecyclerViewAdapter(getApplicationContext(), R.layout.recycler_view_item, closestIncidents);
            }
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onUpdateLocation(LatLng location, String myAddr) {
        layoutSelectedIncident.setVisibility(View.GONE);
        greetingsAnomalyFloatingButton.setVisibility(View.GONE);
        followAnomalyFloatingButton.setVisibility(View.GONE);

        layoutSelectedAdress.setVisibility(View.VISIBLE);
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            addAnomalyFloatingButton.setVisibility(View.GONE);
        } else {
            addAnomalyFloatingButton.setVisibility(View.VISIBLE);
        }

        if ("".equals(myAddr)) {
            selectedAdress.setText(location.latitude + ", " + location.longitude);
        } else if (!myAddr.contains(getString(R.string.city_name))) {
            selectedAdress.setText("");
        } else {
            selectedAdress.setText(MiscTools.whichPostalCode(myAddr));
            selectedAddressWithCodePostal = myAddr;
            displayIconFavoriteAddress(myAddr);
        }
        Log.i(TAG, "onUpdateLocation: adresse ok");
    }

    private void displayFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void displayIconFavoriteAddress(String address) {

        PrefManager prefManager = new PrefManager(getApplicationContext());

        if (prefManager.getFavoriteAddress().containsKey(address)) {
            buttonFavoriteAdress.setImageResource(R.drawable.ic_full_star);
            buttonFavoriteAdress.setContentDescription(getString(R.string.favorite_address_remove_action, address));
        } else {
            buttonFavoriteAdress.setImageResource(R.drawable.ic_add_star);
            buttonFavoriteAdress.setContentDescription(getString(R.string.favorite_address_add_action, address));
        }

        buttonFavoriteAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(address != null && address.length()>0) {
                    if (!prefManager.getFavoriteAddress().containsKey(address)) {
                        // add favorite address
                        prefManager.setFavorisAddress(new FavoriteAddress(address, mapParisFragment.getMyCurrentLocationPosition()), false);
                        buttonFavoriteAdress.setImageResource(R.drawable.ic_full_star);
                        buttonFavoriteAdress.setContentDescription(getString(R.string.favorite_address_remove_action, address));
                    } else {
                        // delete favorite address
                        prefManager.setFavorisAddress(new FavoriteAddress(address, mapParisFragment.getMyCurrentLocationPosition()), true);
                        buttonFavoriteAdress.setImageResource(R.drawable.ic_add_star);
                        buttonFavoriteAdress.setContentDescription(getString(R.string.favorite_address_add_action, address));
                    }
                }
            }
        });
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
            if (summarizedIncident.isFromRamen()) {
                selectedIncidentTitle.setText(R.string.desc_ramen);
            }

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

            if (mapParisFragment.getSearchIdFdt() > 0) {
                layoutInfoAvantFDT.setVisibility(View.GONE);
            }
        }

    }

    @OnClick(R.id.button_open_infos_apres_fdt)
    public void openPopupInfosApresTournee() {
        dialogInfosApresFdt = new Dialog(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_infos_apres_fdt, null, false);
        mapParisFragment.getInfosApresTournee();
        dialogInfosApresFdt.setContentView(viewInflated);

        ImageView close = (ImageView) viewInflated.findViewById(R.id.btnCloseFailure);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfosApresFdt.dismiss();
            }
        });

        inputInfosApresTournee = (EditText) viewInflated.findViewById(R.id.input_txt_infos_apres_fdt);
        inputInfosApresTournee.setText(mapParisFragment.getInfosApresTournee());

        Button buttonValidate = (Button) viewInflated.findViewById(R.id.button_valider_infos_apres_fdt);
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputInfosApresTournee.getText().toString().trim().length() > 0) {
                    presenter.saveInfoApresTournee(mapParisFragment.getSearchIdFdt(), inputInfosApresTournee.getText().toString());
                }
            }
        });

        dialogInfosApresFdt.show();
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

    @Override
    public void displaySaveInfosApresTournee() {
        mapParisFragment.setInfosApresTournee(inputInfosApresTournee.getText().toString());
        dialogInfosApresFdt.dismiss();
        Toast.makeText(this, getString(R.string.text_save_infos_apres_fdt_ok), Toast.LENGTH_LONG).show();
    }

}
