package com.accenture.dansmarue.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.MessageServiceFait;
import com.accenture.dansmarue.mvp.presenters.AnomalyDetailsPresenter;
import com.accenture.dansmarue.mvp.views.AnomalyDetailsView;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.NetworkUtils;
import com.accenture.dansmarue.utils.PrefManager;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * AnomalyDetailsActivity
 * Activity to display incident detail.
 */
public class AnomalyDetailsActivity extends BaseAnomalyActivity implements AnomalyDetailsView, OnChangePictureListener {

    private static final String TAG = AnomalyDetailsActivity.class.getCanonicalName();
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Inject
    protected AnomalyDetailsPresenter anomalyDetailsPresenter;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.anomaly_details_picture)
    protected ImageView imageView;
    @BindView(R.id.anomaly_address)
    protected TextView anomalyAdress;
    @BindView(R.id.anomaly_category)
    protected TextView anomalyCategory;
    @BindView(R.id.anomaly_details)
    protected TextView anomalyDetail;
    @BindView(R.id.anomaly_commentaire_agent)
    @Nullable
    protected TextView anomalyCommentaireAgentTerrain;
    @BindView(R.id.anomaly_nb_followers)
    protected TextView nbFollowers;
    @BindView(R.id.anomaly_nb_greetings)
    protected TextView nbGreetings;
    @BindView(R.id.anomaly_time)
    protected TextView anomalyDateTime;
    @BindView(R.id.anomaly_number)
    protected TextView anomalyNumber;
    @BindView(R.id.anomaly_state_name)
    @Nullable
    protected TextView anomalyStateName;
    @BindView(R.id.anomaly_layout_followers)
    protected LinearLayout layoutFollowers;
    @BindView(R.id.fab_details_ano)
    protected FloatingActionButton fabDetailsAno;
    @BindView(R.id.resolve)
    protected Button resolveBtn;

    //AGENT LAYOUT

    //requalification
    @BindView(R.id.linear_layout_requalification)
    @Nullable
    protected LinearLayout requalificationLayout;
    @BindView(R.id.linear_layout_requalification2)
    @Nullable
    protected LinearLayout requalification2Layout;
    @BindView(R.id.image_languette_requalification)
    @Nullable
    protected ImageButton pictoLanguetteRequalification;

    //Type
    @BindView(R.id.image_choose_type)
    @Nullable
    protected ImageView chooseType;
    @BindView(R.id.text_choose_type_subtitle)
    @Nullable
    protected TextView chooseTypeSubtitle;
    @BindView(R.id.requalification_photo)
    @Nullable
    protected ImageButton pictureRequalificationButton;
    @BindView(R.id.requalification_photo_layout)
    @Nullable
    protected RelativeLayout photoRequalificationLayout;
    @BindView(R.id.requalification_choose_picture)
    @Nullable
    protected ImageView requalificationChoosePicture;
    @BindView(R.id.requalificationImageChoiceLayout)
    @Nullable
    protected RelativeLayout requalificationImageChoiceLayout;
    @BindView(R.id.requalificationImageChoice)
    @Nullable
    protected ImageView requalificationImage;
    @BindView(R.id.requalificationImageChoiceClose)
    @Nullable
    protected ImageView requalificationImageChoiceClose;

    //Commentaire agent
    @BindView(R.id.text_commentagent_subtitle)
    @Nullable
    protected AppCompatTextView textCommentAgent;
    @BindView(R.id.commentagent_choose_type)
    @Nullable
    protected ImageView chooseCommentAgent;
    @BindView(R.id.button_requalification)
    @Nullable
    protected Button resqualificationBtn;
    //requalification fin

    // precisions terrain
    @BindView(R.id.linear_layout_precterrain)
    @Nullable
    protected LinearLayout precisionsTerrainLayout;
    @BindView(R.id.linear_layout_precterrain2)
    @Nullable
    protected LinearLayout precisionsTerrain2Layout;
    @BindView(R.id.image_languette_precterrain)
    @Nullable
    protected ImageButton pictoLanguettePrecisionsTerrain;
    @BindView(R.id.text_commentcommentprecterrain_subtitle)
    @Nullable
    protected AppCompatTextView textPrecisionsTerrain;
    @BindView(R.id.commentprecterrain_choose_type)
    @Nullable
    protected ImageView choosePrecisionsTerrain;
    @BindView(R.id.button_save_precisions_terrain)
    @Nullable
    protected Button savePrecisionsTerrainBtn;
    // precisions terrain fin

    // service fait
    @BindView(R.id.linear_layout_service_fait)
    @Nullable
    protected LinearLayout serviceFaitLayout;
    @BindView(R.id.linear_layout_service_fait2)
    @Nullable
    protected LinearLayout serviceFait2Layout;
    @BindView(R.id.image_languette_service_fait)
    @Nullable
    protected ImageButton pictoLanguetteServiceFait;
    @BindView(R.id.photo_service_fait_layout)
    protected LinearLayout photoServiceFaitLayout;
    @BindView(R.id.image_choose_type_sf)
    @Nullable
    protected ImageView chooseMessageServiceFaitImage;
    @BindView(R.id.layout_choose_message_sf)
    @Nullable
    protected LinearLayout messageServiceFait;
    @BindView(R.id.text_choose_type_subtitle_sf)
    @Nullable
    protected TextView chooseMessageServiceFait;

    //FIN AGENT LAYOUT

    //Pictures
    @BindView(R.id.image_choose_picture)
    protected ImageView choosePicture;
    @BindView(R.id.myImageChoice)
    protected ImageView image1;
    @BindView(R.id.add_anomaly_photo)
    protected ImageButton addPictureButton;
    @BindView(R.id.myImageChoiceLayout)
    protected RelativeLayout myImageChoiceLayout;
    @BindView(R.id.myImageChoiceClose)
    protected ImageView myImageChoiceClose;
    @BindView(R.id.add_anomaly_photo_layout)
    protected RelativeLayout addAnomalyPhotoLayout;
    @BindView(R.id.navLeft)
    protected TextView leftPicNavigation;
    @BindView(R.id.navRight)
    protected TextView rightPicNavigation;

    private boolean fabDetailsAnoFollowed;
    private boolean congratulated;
    private Incident incident;
    private MessageServiceFait messageServiceFaitSelect;
    private int indexPicture = 0;
    private int maxIndex = 0;
    private int nbFollowersDisplay = 0;
    private int nbGreetingsDisplay = 0;
    private boolean incidentComeFromNotif;
    private boolean isLayoutAgent;
    private boolean isRequalificationPhoto;
    private boolean isMessageServiceFaitSelect;
    private boolean isPossibleToMakeRequalification = false;
    private HashMap<Integer, MessageServiceFait> messagesServiceFait;
    private Dialog bePatientDialog;
    private PrefManager prefManager;
    private String requalifTypeSubtitle;
    private String requalifCategoryType;
    private String requalifImagePath;
    private String requalifComment;
    private String precisionDetails;
    private String doneImagePath;
    private MessageServiceFait doneMessageType;
    private Boolean isRequalifEditMode = false;
    private Boolean isPrecisionEditMode = false;
    private Boolean isDoneEditMode = false;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        listener = this;
        Log.i(TAG, "onViewReady: detail");
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        final long incidentId = intent.getLongExtra(Constants.EXTRA_INCIDENT_ID, 0);
        isMessageServiceFaitSelect = false;

        String incidentByNotif = intent.getStringExtra(Constants.EXTRA_INCIDENT_TYPE);
        if (null != incidentByNotif) incidentComeFromNotif = true;
        final String source = intent.getStringExtra(Constants.EXTRA_INCIDENT_SOURCE);
        if (incidentId == 0) {
            finish();
        }

        anomalyDetailsPresenter.loadIncident(incidentId, source);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            getSupportActionBar().setTitle("Détail");

            // tips to fix weird behavior
            toolbar.setNavigationOnClickListener(v -> {
                if (incidentComeFromNotif) {
                    startActivity(new Intent(AnomalyDetailsActivity.this, WelcomeMapActivity.class));
                    finish();
                } else {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    protected void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(((DansMaRueApplication) getApplication()).getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected int getContentView() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (prefManager.getIsAgent()) {
            isLayoutAgent = true;
            return R.layout.anomaly_details_activity_agent_layout;
        } else {
            isLayoutAgent = false;
            return R.layout.anomaly_details_activity_layout;
        }
    }

    @OnClick(R.id.fab_details_ano)
    public void fabClicked() {
        //Should always be true
        if (incident != null) {
            if (incident.isResolu()) {
                congratulate();
            } else {
                if (!incident.isIncidentFollowedByUser()) {
                    anomalyDetailsPresenter.followAnomaly(String.valueOf(incident.getId()));
                } else {
                    anomalyDetailsPresenter.unfollowAnomaly(String.valueOf(incident.getId()));
                }
            }
        }
    }


    /**
     * On display fallow success
     */
    public void displayFollow() {
        incident.setIncidentFollowedByUser(true);
        fabDetailsAno.setImageResource(R.drawable.ic_followed);
        fabDetailsAno.setContentDescription(getString(R.string.unfollow_anomaly_action));
        Snackbar.make(findViewById(R.id.rl_add_ano_details), R.string.follow_anomaly, Snackbar.LENGTH_LONG).show();
        nbFollowersDisplay++;
        nbFollowers.setText(nbFollowersDisplay + " ");
    }

    /**
     * On display fallow failure
     */
    public void displayFollowFailure() {
        Snackbar.make(findViewById(R.id.rl_add_ano_details), R.string.follow_anomaly_failure, Snackbar.LENGTH_LONG).show();
    }

    /**
     * On display unfallow success
     */
    public void displayUnfollow() {
        incident.setIncidentFollowedByUser(false);
        fabDetailsAno.setImageResource(R.drawable.ic_follow);
        fabDetailsAno.setContentDescription(getString(R.string.follow_anomaly_action));
        Snackbar.make(findViewById(R.id.rl_add_ano_details), R.string.unfollow_anomaly, Snackbar.LENGTH_LONG).show();
        nbFollowersDisplay--;
        nbFollowers.setText(nbFollowersDisplay + " ");
    }

    /**
     * On display unfallow failure
     */
    public void displayUnfollowFailure() {
        Snackbar.make(findViewById(R.id.rl_add_ano_details), R.string.unfollow_anomaly_failure, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void populateFields(final Incident loadedIncident) {
        if (null == loadedIncident) {
            Toast.makeText(getApplicationContext(), "Erreur lors de la récuperation des informations du signalement", Toast.LENGTH_SHORT).show();
        } else {
            incident = loadedIncident;
            messageServiceFaitSelect = null;
            maxIndex = incident.getAllPictures().size() - 1;
            if (maxIndex == -1 && leftPicNavigation != null && rightPicNavigation != null) {
                leftPicNavigation.setVisibility(View.GONE);
                rightPicNavigation.setVisibility(View.GONE);
            }

            if (indexPicture == 0 && leftPicNavigation != null) {
                leftPicNavigation.setVisibility(View.GONE);
            }

            if (indexPicture == maxIndex && rightPicNavigation != null) {
                rightPicNavigation.setVisibility(View.GONE);
            }

            if (incident.isFromRamen()) {
                //TODO normalement on reçoit une imgae générique pour ramen
                Glide.with(this)
                        .load(incident.getPictures().getGenericPictureId())
                        .override(1600, 1600)
                        .into(imageView);
            } else if (incident.getFirstAvailablePicture() != null) {
                Glide.with(this)
                        .load(incident.getFirstAvailablePicture())
                        .fallback(incident.getPictures().getGenericPictureId())
                        .error(incident.getPictures().getGenericPictureId())
                        .override(1600, 1600)
                        .into(imageView);

            } else {
                Glide.with(this)
                        .load(incident.getPictures().getGenericPictureId())
                        .override(1600, 1600)
                        .into(imageView);
            }

            if (incident.isFromRamen()) {
                layoutFollowers.setVisibility(View.GONE);
                anomalyDetail.setText(incident.getRamenDescription());
                anomalyCategory.setText(R.string.desc_ramen);
                anomalyAdress.setTextColor(getResources().getColor(R.color.grey_icon));
            } else {
                anomalyDetail.setText(incident.getDescriptive());
                anomalyCategory.setText(incident.getAlias());
            }

            anomalyAdress.setText(incident.getAddress());
            anomalyDateTime.setText(incident.getFormatedDate());
            anomalyNumber.setText(incident.getReference());
            if (anomalyStateName != null) {
                anomalyStateName.setText(incident.getStateName());
            }

            if (incident.isResolu()) {
                TextView txtOverPicture = (TextView) findViewById(R.id.txt_over_picture);
                txtOverPicture.setBackgroundResource(R.drawable.round_corner_green);
                txtOverPicture.setText(R.string.anomaly_resolved_toast);
                txtOverPicture.setVisibility(View.VISIBLE);

                if (incident.isFromRamen()) {
                    fabDetailsAno.setImageResource(R.drawable.ic_greetings_grey);
                    fabDetailsAno.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));
                } else {
                    fabDetailsAno.setImageResource(R.drawable.ic_greetings_white);
                    fabDetailsAno.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));
                }

                //isAgentLayout and isResolve
                if (isLayoutAgent) {
                    requalificationLayout.setVisibility(View.GONE);
                    serviceFaitLayout.setVisibility(View.GONE);
                }
            } else {
                TextView txtOverPicture = (TextView) findViewById(R.id.txt_over_picture);
                txtOverPicture.setBackgroundResource(R.drawable.round_corner_orange);
                txtOverPicture.setText(R.string.anomaly_in_progress);
                txtOverPicture.setVisibility(View.VISIBLE);
                fabDetailsAno.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
                if (incident.isFromRamen()) {
                    fabDetailsAno.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.grey_icon)));
                } else if (incident.isIncidentFollowedByUser()) {
                    fabDetailsAno.setImageResource(R.drawable.ic_followed);
                    fabDetailsAno.setContentDescription(getString(R.string.unfollow_anomaly_action));
                    fabDetailsAnoFollowed = true;
                } else {
                    fabDetailsAno.setImageResource(R.drawable.ic_follow);
                    fabDetailsAno.setContentDescription(getString(R.string.follow_anomaly_action));
                    fabDetailsAnoFollowed = false;
                }

                fabDetailsAno.setClickable(!incident.isFromRamen());
                if (!incident.isFromRamen() && incident.isResolvable() && !isLayoutAgent) {
                    photoServiceFaitLayout.setVisibility(View.VISIBLE);
                    resolveBtn.setVisibility(View.VISIBLE);
                } else if (!incident.isFromRamen() && incident.isResolvable() && isLayoutAgent) {
                    serviceFaitLayout.setVisibility(View.VISIBLE);
                } else {
                    resolveBtn.setVisibility(View.GONE);
                    photoServiceFaitLayout.setVisibility(View.GONE);
                    if (isLayoutAgent) {
                        serviceFaitLayout.setVisibility(View.GONE);
                    }
                }
            }

            if (isLayoutAgent) {
                anomalyCommentaireAgentTerrain.setText(incident.getCommentaireAgent());
                anomalyDetailsPresenter.setPrecisionTerrain(incident.getPrecisionsTerrain());
                if (incident.getPrecisionsTerrain() != null && incident.getPrecisionsTerrain().length() > 40) {
                    textPrecisionsTerrain.setText(incident.getPrecisionsTerrain().substring(0, 40) + "...");
                } else if (incident.getPrecisionsTerrain() != null && !incident.getPrecisionsTerrain().isEmpty()) {
                    textPrecisionsTerrain.setText(incident.getPrecisionsTerrain());
                }
                if (Incident.STATE_TIERS.equals(incident.getState()) || Incident.SIATE_ID_ECHEC_ENVOI_WS == incident.getStateId()) {
                    requalificationLayout.setVisibility(View.GONE);
                }

                handleRequalificationContentDescription();
                handlePrecisionTerrainContentDescription();
                handleServiceFaitContentDescription();

            }
        }

        nbFollowersDisplay = incident.getFollowers();
        nbFollowers.setText(nbFollowersDisplay + " ");

        nbGreetingsDisplay = incident.getCongratulations();
        nbGreetings.setText(nbGreetingsDisplay + " ");

        prefManager = new PrefManager(getApplicationContext());
        prefManager.setIncidentFdtToRefresh(incident);
    }

    public void populateMessageServiceFaitGeneric(List<MessageServiceFait> messages) {
        messagesServiceFait = new HashMap<>();
        int index = 0;
        for (MessageServiceFait message : messages) {
            message.setIsGeneric(true);
            messagesServiceFait.put(index, message);
            index++;
        }
    }

    public void populateMessageServiceFaitType(List<MessageServiceFait> messages) {
        int index = messagesServiceFait.size();
        for (MessageServiceFait message : messages) {
            message.setIsGeneric(false);
            messagesServiceFait.put(index, message);
            index++;
        }
    }

    private void congratulate() {
        if (!congratulated) {
            fabDetailsAno.setImageResource(R.drawable.ic_greetings_grey);
            fabDetailsAno.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_grey)));
            Snackbar.make(findViewById(R.id.anomaly_line2), R.string.greetings_ok, Snackbar.LENGTH_LONG)
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                anomalyDetailsPresenter.congratulateAnomalie(String.valueOf(incident.getId()));
                                congratulated = true;
                            }
                        }
                    })
                    .setAction(R.string.greetings_undo, view -> {
                        fabDetailsAno.setImageResource(R.drawable.ic_greetings_white);
                        fabDetailsAno.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.greetings_green)));
                    })
                    .show();
        }
    }

    @OnClick(R.id.resolve)
    public void resolve() {
        Snackbar.make(findViewById(R.id.anomaly_line2), R.string.resolve_ok, Snackbar.LENGTH_LONG)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            resolveBtn.setEnabled(false);
                            if (image1.getVisibility() == View.VISIBLE) {
                                anomalyDetailsPresenter.uploadPicture(String.valueOf(incident.getId()), false, "done");
                                // after call resolveIncident
                            } else {
                                callResolveIncident();
                            }
                        }
                    }
                })
                .setAction(R.string.resolve_undo, view -> {
                })
                .show();
    }

    @Override
    public void callResolveIncident() {
        anomalyDetailsPresenter.resolveIncident(String.valueOf(incident.getId()), messageServiceFaitSelect);
    }

    @Override
    public void displayGreetingsOk() {
        Log.d(TAG, "displayGreetingsOk: ");
        nbGreetingsDisplay++;
        nbGreetings.setText(nbGreetingsDisplay + " ");
        fabDetailsAno.setVisibility(View.INVISIBLE);
    }

    @Override
    public void displayGreetingsKo() {
        Snackbar.make(findViewById(R.id.navigation), R.string.dmr_error, Snackbar.LENGTH_LONG).show();
        congratulated = false;
        fabDetailsAno.setImageResource(R.drawable.ic_greetings_green);
        fabDetailsAno.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
    }

    @Override
    public void inviteToLogin() {
        loginResultLauncher.launch(new Intent(AnomalyDetailsActivity.this, LoginActivity.class));
    }

    @Override
    public void displayResolveKo() {
        //TODO quelle message afficher
        Toast.makeText(this, "Erreur lors de la prise en copmpte de la déclaration", Toast.LENGTH_LONG).show();
        if (isLayoutAgent) {
            resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            resolveBtn.setEnabled(true);
        }
    }

    @Override
    public void displayResolveOk() {
        final Toast toastMessage = Toast.makeText(this, "Cette anomalie a été clôturée", Toast.LENGTH_LONG);
        if (isLayoutAgent) {
            finish();
            startActivity(getIntent());
        } else {
            toastMessage.setGravity(Gravity.TOP, 5, 5);
            toastMessage.show();
            incident.setState(Incident.STATE_RESOLVED);
            populateFields(incident);
            photoServiceFaitLayout.setVisibility(View.INVISIBLE);
            resolveBtn.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.navLeft)
    public void rollPictureLeft() {
        if (indexPicture > 0) {
            indexPicture--;
            Glide.with(this).load(incident.getPicture(indexPicture)).fallback(incident.getPictures().getGenericPictureId()).error(incident.getPictures().getGenericPictureId()).into(imageView);
        }

        if (indexPicture == 0) {
            leftPicNavigation.setVisibility(View.GONE);
        }
        rightPicNavigation.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.navRight)
    public void rollPictureRight() {
        if (indexPicture < maxIndex) {
            indexPicture++;
            Glide.with(this).load(incident.getPicture(indexPicture)).fallback(incident.getPictures().getGenericPictureId()).error(incident.getPictures().getGenericPictureId()).into(imageView);
        }

        if (indexPicture == maxIndex) {
            rightPicNavigation.setVisibility(View.GONE);
        }
        leftPicNavigation.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeActvity() {
        finish();
        startActivity(new Intent(this, WelcomeMapActivity.class));
    }


    @Optional
    @OnClick({R.id.linear_layout_requalification, R.id.image_languette_requalification})
    public void showhideLayoutRequalification() {
        if (requalification2Layout.getVisibility() == View.VISIBLE) {
            isRequalifEditMode = false;
            requalification2Layout.setVisibility(View.GONE);
            pictoLanguetteRequalification.setImageResource(R.drawable.ic_arrow_right);
        } else {
            isRequalifEditMode = true;
            requalification2Layout.setVisibility(View.VISIBLE);
            pictoLanguetteRequalification.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        handleRequalificationContentDescription();
    }

    @Optional
    @OnClick({R.id.linear_layout_precterrain, R.id.image_languette_precterrain})
    public void showhideLayoutPrecisionTerrain() {
        if (precisionsTerrain2Layout.getVisibility() == View.VISIBLE) {
            isPrecisionEditMode = false;
            precisionsTerrain2Layout.setVisibility(View.GONE);
            pictoLanguettePrecisionsTerrain.setImageResource(R.drawable.ic_arrow_right);

        } else {
            isPrecisionEditMode = true;
            precisionsTerrain2Layout.setVisibility(View.VISIBLE);
            pictoLanguettePrecisionsTerrain.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        handlePrecisionTerrainContentDescription();
    }

    @Optional
    @OnClick({R.id.linear_layout_service_fait, R.id.image_languette_service_fait})
    public void showhideLayoutServicefait() {
        if (serviceFait2Layout.getVisibility() == View.VISIBLE) {
            isDoneEditMode = false;
            serviceFait2Layout.setVisibility(View.GONE);
            photoServiceFaitLayout.setVisibility(View.GONE);
            messageServiceFait.setVisibility(View.GONE);
            resolveBtn.setVisibility(View.GONE);
            pictoLanguetteServiceFait.setImageResource(R.drawable.ic_arrow_right);
        } else {
            isDoneEditMode = true;
            serviceFait2Layout.setVisibility(View.VISIBLE);
            photoServiceFaitLayout.setVisibility(View.VISIBLE);
            if (!incident.isAnonyme()) {
                messageServiceFait.setVisibility(View.VISIBLE);
                if (isMessageServiceFaitSelect) {
                    resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                    resolveBtn.setEnabled(true);
                } else {
                    resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_icon));
                    resolveBtn.setEnabled(false);
                }
            } else {
                //anonymous report
                resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                resolveBtn.setEnabled(true);
            }
            resolveBtn.setVisibility(View.VISIBLE);
            pictoLanguetteServiceFait.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        handleServiceFaitContentDescription();
    }

    @Optional
    @OnClick(R.id.layout_choose_type)
    public void chooseType() {
        typeResultLauncher.launch(new Intent(this, CategoryActivity.class));
    }

    @Optional
    @OnClick({R.id.layout_commentagent, R.id.commentagent_layout_parent})
    public void setDescription() {
        final Intent intent = new Intent(AnomalyDetailsActivity.this, SetDescriptionActivity.class);
        intent.putExtra(Constants.EXTRA_COMMENTAIRE_AGENT_REQUALIFICATION, anomalyDetailsPresenter.getRequalificationComment());
        intent.putExtra(Constants.EXTRA_CALLING_ACTIVITY, Constants.ACTIVITY_DETAILS_ANOMALY);
        agentCommentResultLauncher.launch(intent);
    }

    @Optional
    @OnClick({R.id.layout_commentprecterrain, R.id.commentprecterrain_layout_parent})
    public void setCommentairePrecisionsTerrain() {
        final Intent intent = new Intent(AnomalyDetailsActivity.this, SetDescriptionActivity.class);
        intent.putExtra(Constants.EXTRA_COMMENTAIRE_PRECISIONS_TERRAIN, anomalyDetailsPresenter.getPrecisionTerrain());
        intent.putExtra(Constants.EXTRA_CALLING_ACTIVITY, Constants.ACTIVITY_DETAILS_ANOMALY);
        precisionTerrainResultLauncher.launch(intent);
    }

    @Optional
    @OnClick(R.id.button_requalification)
    public void onRequalification() {
        if (!isPossibleToMakeRequalification) {
            new AlertDialog.Builder(this).setMessage(R.string.requalification_anomaly_failure)
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
        } else if (NetworkUtils.isConnected(getApplicationContext())) {
            pleaseBePatientDialog();
            anomalyDetailsPresenter.doRequalification(incident);
        } else {
            Toast.makeText(this, R.string.info_no_network, Toast.LENGTH_LONG).show();
        }
    }

    @Optional
    @OnClick(R.id.button_save_precisions_terrain)
    public void onSavePrecisionsTerrain() {
        anomalyDetailsPresenter.doSavePrecisionsTerrain(incident.getId());
    }

    /**
     * Requalification work
     */
    public void requalificationSuccess() {
        if (requalificationImage.getVisibility() == View.VISIBLE) {
            String typePhoto = "far";
            if (!incident.getPictures().getFar().isEmpty() && incident.getPictures().getClose().isEmpty()) {
                typePhoto = "close";
            }
            anomalyDetailsPresenter.uploadPicture(String.valueOf(incident.getId()), true, typePhoto);
        } else {
            uploadRequalificationDone();
        }
    }

    public void uploadRequalificationDone() {
        bePatientDialog.dismiss();
        // reload activity with new type
        finish();
        startActivity(getIntent());
    }

    /**
     * Requalification fail
     */
    public void requalificationFaillure() {
        bePatientDialog.dismiss();
        Toast.makeText(this, R.string.dmr_error_requalification, Toast.LENGTH_LONG).show();
    }

    public void displaySavePrecisionsTerrainSuccess() {
        Toast.makeText(this, R.string.success_save_precisions_terrain, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.add_anomaly_photo)
    public void takePhotoOrViewGallery() {
        isRequalificationPhoto = false;
        selectImage();
    }

    @Optional
    @OnClick(R.id.requalification_photo)
    public void takePhotoOrViewGalleryRequalification() {
        isRequalificationPhoto = true;
        selectImage();
    }

    @Optional
    @OnClick(R.id.layout_choose_message_sf)
    public void chooseMessageServiceFait() {
        Intent intent = new Intent(this, MessageSFActivity.class);
        intent.putExtra(Constants.EXTRA_LIST_MESSAGE_SERVICE_FAIT, messagesServiceFait);
        messageServiceResultLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }
        }
    }

    public void updatePicture(final String fileName) {
        doneImagePath = fileName;
        addPictureButton.setVisibility(View.GONE);
        addAnomalyPhotoLayout.setVisibility(View.GONE);
        anomalyDetailsPresenter.addPictureToModel(fileName);

        //first picture set
        choosePicture.setImageResource(R.drawable.ic_check_circle_pink_24px);
        myImageChoiceLayout.setVisibility(View.VISIBLE);
        image1.setVisibility(View.VISIBLE);
        Glide.with(this).load(fileName).fitCenter().into(image1);
        myImageChoiceClose.setVisibility(View.VISIBLE);
        myImageChoiceClose.setContentDescription("Supprimer la photo, Bouton");
    }

    public void updatePictureRequalification(final String fileName) {
        requalifImagePath = fileName;
        pictureRequalificationButton.setVisibility(View.GONE);
        photoRequalificationLayout.setVisibility(View.GONE);
        anomalyDetailsPresenter.addPictureRequalificationToModel(fileName);
        requalificationChoosePicture.setImageResource(R.drawable.ic_check_circle_pink_24px);
        requalificationImageChoiceLayout.setVisibility(View.VISIBLE);
        requalificationImage.setVisibility(View.VISIBLE);
        Glide.with(this).load(fileName).fitCenter().into(requalificationImage);
        requalificationImageChoiceClose.setVisibility(View.VISIBLE);
    }

    private void onSelectFromGallery(final Intent data, final boolean isForRequalification) {
        if (data != null) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                addPictureToPlaceHolder(thumbnail, isForRequalification);
            } catch (IOException e) {
                FirebaseCrashlytics.getInstance().log(e.getMessage());
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void onTypeResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            requalifCategoryType = data.getStringExtra(Constants.EXTRA_CATEGORY_ID);
            requalifTypeSubtitle = data.getStringExtra(Constants.EXTRA_CATEGORY_NAME);
            typeTreatment(requalifTypeSubtitle, requalifCategoryType);
        }
    }

    private void onMessageServiceFaitResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            messageServiceFaitSelect = (MessageServiceFait) data.getSerializableExtra(Constants.EXTRA_MESSAGE_SERVICE_FAIT_SELECT);
            doneMessageType = messageServiceFaitSelect;
            chooseMessageServiceFait.setText(messageServiceFaitSelect.getMessage());
            chooseMessageServiceFait.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            chooseMessageServiceFaitImage.setImageResource(R.drawable.ic_check_circle_pink_24px);
            resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            resolveBtn.setEnabled(true);
            isMessageServiceFaitSelect = true;
        }
    }

    /**
     * Callback after SET_COMMENTAIRE_AGENT_REQUALIFICATION_REQUEST_CODE
     *
     * @param resultCode result
     * @param data       intent containig the description
     */
    private void onCommentaireAgentRequalificationResult(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            String txtCommentaireAgent = data.getStringExtra(Constants.EXTRA_COMMENTAIRE_AGENT_REQUALIFICATION);
            String txtCommentaireAgentLimit = txtCommentaireAgent;
            requalifComment = txtCommentaireAgent;
            if (txtCommentaireAgent.length() > 40) {
                txtCommentaireAgentLimit = txtCommentaireAgent.substring(0, 40) + "...";
            }
            textCommentAgent.setText(txtCommentaireAgentLimit);
            if (txtCommentaireAgent.length() > 0) {
                chooseCommentAgent.setImageResource(R.drawable.ic_check_circle_pink_24px);
            } else {
                chooseCommentAgent.setImageResource(R.drawable.ic_check_circle_grey_24px);
            }
            anomalyDetailsPresenter.setRequalificationComment(data.getStringExtra(Constants.EXTRA_COMMENTAIRE_AGENT_REQUALIFICATION));
        }
    }

    /**
     * Callback after SET_COMMENTAIRE_AGENT_REQUALIFICATION_REQUEST_CODE
     *
     * @param resultCode result
     * @param data       intent containig the description
     */
    private void onPrecisionsTerrainResult(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            String txtPrecisionsTerrain = data.getStringExtra(Constants.EXTRA_COMMENTAIRE_PRECISIONS_TERRAIN);
            String txtPrecisionsTerrainLimit = txtPrecisionsTerrain;
            precisionDetails = txtPrecisionsTerrain;
            if (txtPrecisionsTerrain.length() > 40) {
                txtPrecisionsTerrainLimit = txtPrecisionsTerrain.substring(0, 40) + "...";
            }
            if (txtPrecisionsTerrain.length() > 0) {
                textPrecisionsTerrain.setText(txtPrecisionsTerrainLimit);
                choosePrecisionsTerrain.setImageResource(R.drawable.ic_check_circle_pink_24px);
                savePrecisionsTerrainBtn.setEnabled(true);
                savePrecisionsTerrainBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            } else {
                textPrecisionsTerrain.setText(R.string.precisions_terrain_subtitle);
                choosePrecisionsTerrain.setImageResource(R.drawable.ic_check_circle_grey_24px);
                savePrecisionsTerrainBtn.setEnabled(false);
                savePrecisionsTerrainBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_icon));
            }
            anomalyDetailsPresenter.setPrecisionTerrain(data.getStringExtra(Constants.EXTRA_COMMENTAIRE_PRECISIONS_TERRAIN));
        }
    }

    private void typeTreatment(String typeSubtitle, String categoryType) {
        chooseTypeSubtitle.setText(typeSubtitle);
        anomalyDetailsPresenter.setCategory(categoryType);
        chooseType.setImageResource(R.drawable.ic_check_circle_pink_24px);
        resqualificationBtn.setEnabled(true);
        resqualificationBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        isPossibleToMakeRequalification = !incident.getAlias().equals(typeSubtitle);
    }

    @OnClick(R.id.myImageChoiceClose)
    public void disablePic1() {
        anomalyDetailsPresenter.removePicture1();
        myImageChoiceLayout.setVisibility(View.GONE);
        image1.setVisibility(View.GONE);
        myImageChoiceClose.setVisibility(View.GONE);
        addAnomalyPhotoLayout.setVisibility(View.VISIBLE);
        addPictureButton.setVisibility(View.VISIBLE);
        testPicturesAndGreyCheck();
    }

    @Optional
    @OnClick(R.id.requalificationImageChoiceClose)
    public void disablePicRequalification() {
        anomalyDetailsPresenter.removePictureRequalification();
        requalificationImageChoiceLayout.setVisibility(View.GONE);
        requalificationImage.setVisibility(View.GONE);
        requalificationImageChoiceLayout.setVisibility(View.GONE);
        photoRequalificationLayout.setVisibility(View.VISIBLE);
        pictureRequalificationButton.setVisibility(View.VISIBLE);
        requalificationChoosePicture.setImageResource(R.drawable.ic_check_circle_grey_24px);
    }

    private void addPictureToPlaceHolder(Bitmap thumbnail, boolean isForRequalification) {
        anomalyDetailsPresenter.savePictureInFile(thumbnail, isForRequalification);
    }

    public void testPicturesAndGreyCheck() {
        if (image1.getVisibility() == View.GONE) {
            choosePicture.setImageResource(R.drawable.ic_check_circle_grey_24px);
        }
    }

    /**
     * Show Be patient dialog
     */
    private void pleaseBePatientDialog() {
        bePatientDialog = new Dialog(this);
        bePatientDialog.setContentView(R.layout.greetings_dialog_send_incident);
        bePatientDialog.setCancelable(false);

        if (bePatientDialog.getWindow() != null) {
            bePatientDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Fix Dialog Size
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(bePatientDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            bePatientDialog.getWindow().setAttributes(lp);
            bePatientDialog.show();
        }
    }

    @Override
    public void showPicture(String fileName) {
        rotateResizeAndCompress();
        if (isRequalificationPhoto) {
            updatePictureRequalification(mCurrentPhotoPath);
        } else {
            updatePicture(mCurrentPhotoPath);
        }
    }

    @Override
    public void onSelectFromGalleryResult(Intent data) {
        onSelectFromGallery(data, isRequalificationPhoto);
    }

    ActivityResultLauncher<Intent> typeResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                onTypeResult(result.getResultCode(), data);
            });
    ActivityResultLauncher<Intent> agentCommentResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                onCommentaireAgentRequalificationResult(result.getResultCode(), data);
            });
    ActivityResultLauncher<Intent> messageServiceResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                onMessageServiceFaitResult(result.getResultCode(), data);
            });
    ActivityResultLauncher<Intent> precisionTerrainResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                onPrecisionsTerrainResult(result.getResultCode(), data);
            });

    ActivityResultLauncher<Intent> loginResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            });

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (incident != null)
            outState.putParcelable(Constants.INCIDENT_CONSTANT, incident);

        if (requalifCategoryType != null && requalifTypeSubtitle != null) {
            outState.putString(Constants.CATEGORY_TYPE_CONSTANT, requalifCategoryType);
            outState.putString(Constants.SUBTITLE_TYPE_CONSTANT, requalifTypeSubtitle);
        }
        if (requalifComment != null)
            outState.putString(Constants.REQUALIF_COMMENT_CONSTANT, requalifComment);

        if (precisionDetails != null)
            outState.putString(Constants.PRECISION_DETAILS_CONSTANT, precisionDetails);

        if (mCurrentPhotoPath != null)
            outState.putString(Constants.ADD_ANO_CURRENT_PHOTO_PATH, mCurrentPhotoPath);

        if (requalifImagePath != null)
            outState.putString(Constants.REQUALIF_IMAGE_PATH_CONSTANT, requalifImagePath);

        if (doneImagePath != null)
            outState.putString(Constants.DONE_IMAGE_PATH_CONSTANT, doneImagePath);

        if (doneMessageType != null)
            outState.putSerializable(Constants.MESSAGE_SERVICE_FAIT_CONSTANT, doneMessageType);

        outState.putBoolean(Constants.IS_REQUALIF_IMAGE_CONSTANT, isRequalificationPhoto);
        outState.putBoolean(Constants.IS_REQUALIF_MODE_CONSTANT, isRequalifEditMode);
        outState.putBoolean(Constants.IS_PRECISION_MODE_CONSTANT, isPrecisionEditMode);
        outState.putBoolean(Constants.IS_DONE_MODE_CONSTANT, isDoneEditMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        incident = savedInstanceState.getParcelable(Constants.INCIDENT_CONSTANT);

        requalifCategoryType = savedInstanceState.getString(Constants.CATEGORY_TYPE_CONSTANT);
        requalifTypeSubtitle = savedInstanceState.getString(Constants.SUBTITLE_TYPE_CONSTANT);
        typeTreatment(requalifTypeSubtitle, requalifCategoryType);

        requalifComment = savedInstanceState.getString(Constants.REQUALIF_COMMENT_CONSTANT);
        if (requalifComment != null) {
            String txtCommentaireAgentLimit = requalifComment;
            if (requalifComment.length() > 40) {
                txtCommentaireAgentLimit = requalifComment.substring(0, 40) + "...";
            }
            textCommentAgent.setText(txtCommentaireAgentLimit);
            if (requalifComment.length() > 0) {
                chooseCommentAgent.setImageResource(R.drawable.ic_check_circle_pink_24px);
            } else {
                chooseCommentAgent.setImageResource(R.drawable.ic_check_circle_grey_24px);
            }
            anomalyDetailsPresenter.setRequalificationComment(requalifComment);
        }

        precisionDetails = savedInstanceState.getString(Constants.PRECISION_DETAILS_CONSTANT);
        if (precisionDetails != null) {
            String limitPrecision = precisionDetails;
            if (precisionDetails.length() > 40) {
                limitPrecision = precisionDetails.substring(0, 40) + "...";
            }
            if (precisionDetails.length() > 0) {
                textPrecisionsTerrain.setText(limitPrecision);
                choosePrecisionsTerrain.setImageResource(R.drawable.ic_check_circle_pink_24px);
                savePrecisionsTerrainBtn.setEnabled(true);
                savePrecisionsTerrainBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            } else {
                textPrecisionsTerrain.setText(R.string.precisions_terrain_subtitle);
                choosePrecisionsTerrain.setImageResource(R.drawable.ic_check_circle_grey_24px);
                savePrecisionsTerrainBtn.setEnabled(false);
                savePrecisionsTerrainBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_icon));
            }
            anomalyDetailsPresenter.setPrecisionTerrain(precisionDetails);
        }

        if (savedInstanceState.getString(Constants.ADD_ANO_CURRENT_PHOTO_PATH) != null)
            mCurrentPhotoPath = savedInstanceState.getString(Constants.ADD_ANO_CURRENT_PHOTO_PATH);

        requalifImagePath = savedInstanceState.getString(Constants.REQUALIF_IMAGE_PATH_CONSTANT);
        doneImagePath = savedInstanceState.getString(Constants.DONE_IMAGE_PATH_CONSTANT);

        isRequalificationPhoto = savedInstanceState.getBoolean(Constants.IS_REQUALIF_IMAGE_CONSTANT);
        showPicture("");
        doneMessageType = (MessageServiceFait) savedInstanceState.getSerializable(Constants.MESSAGE_SERVICE_FAIT_CONSTANT);
        if (doneMessageType != null) {
            chooseMessageServiceFait.setText(doneMessageType.getMessage());
            chooseMessageServiceFait.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            chooseMessageServiceFaitImage.setImageResource(R.drawable.ic_check_circle_pink_24px);
            resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            resolveBtn.setEnabled(true);
            isMessageServiceFaitSelect = true;
        }
        isRequalifEditMode = savedInstanceState.getBoolean(Constants.IS_REQUALIF_MODE_CONSTANT);
        if (isRequalifEditMode) {
            requalification2Layout.setVisibility(View.VISIBLE);
            pictoLanguetteRequalification.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        isPrecisionEditMode = savedInstanceState.getBoolean(Constants.IS_PRECISION_MODE_CONSTANT);
        if (isPrecisionEditMode) {
            precisionsTerrain2Layout.setVisibility(View.VISIBLE);
            pictoLanguettePrecisionsTerrain.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        isDoneEditMode = savedInstanceState.getBoolean(Constants.IS_DONE_MODE_CONSTANT);
        if (isDoneEditMode) {
            serviceFait2Layout.setVisibility(View.VISIBLE);
            photoServiceFaitLayout.setVisibility(View.VISIBLE);
            if (!incident.isAnonyme()) {
                messageServiceFait.setVisibility(View.VISIBLE);
                if (isMessageServiceFaitSelect) {
                    resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                    resolveBtn.setEnabled(true);
                } else {
                    resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_icon));
                    resolveBtn.setEnabled(false);
                }
            } else {
                //anonymous report
                resolveBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                resolveBtn.setEnabled(true);
            }
            resolveBtn.setVisibility(View.VISIBLE);
            pictoLanguetteServiceFait.setImageResource(R.drawable.ic_arrow_drop_down);
        }

        if(isLayoutAgent) {
            handlePrecisionTerrainContentDescription();
            handleServiceFaitContentDescription();
            handleRequalificationContentDescription();
        }
    }


    private void handlePrecisionTerrainContentDescription() {
        if (precisionsTerrain2Layout.getVisibility() == View.VISIBLE) {
            precisionsTerrainLayout.setContentDescription("Précisions terrain, Appuyer deux fois pour cacher la rubrique");
            pictoLanguettePrecisionsTerrain.setContentDescription("Précisions terrain, Appuyer deux fois pour cacher la rubrique");
        } else {
            precisionsTerrainLayout.setContentDescription("Précisions terrain, Appuyer deux fois pour ouvrir la rubrique");
            pictoLanguettePrecisionsTerrain.setContentDescription("Précisions terrain, Appuyer deux fois pour ouvrir la rubrique");
        }
    }

    private void handleServiceFaitContentDescription() {
        if (serviceFait2Layout.getVisibility() == View.VISIBLE) {
            serviceFaitLayout.setContentDescription("Service fait, Appuyer deux fois pour cacher la rubrique");
            pictoLanguetteServiceFait.setContentDescription("Service fait, Appuyer deux fois pour cacher la rubrique");
        } else {
            serviceFaitLayout.setContentDescription("Service fait, Appuyer deux fois pour ouvrir la rubrique");
            pictoLanguetteServiceFait.setContentDescription("Service fait, Appuyer deux fois pour ouvrir la rubrique");
        }
    }

    private void handleRequalificationContentDescription() {
        if (requalification2Layout.getVisibility() == View.VISIBLE) {
            requalificationLayout.setContentDescription("Requalification, Appuyer deux fois pour cacher la rubrique");
            pictoLanguetteRequalification.setContentDescription("Requalification, Appuyer deux fois pour cacher la rubrique");
        } else {
            requalificationLayout.setContentDescription("Requalification, Appuyer deux fois pour ouvrir la rubrique");
            pictoLanguetteRequalification.setContentDescription("Requalification, Appuyer deux fois pour ouvrir la rubrique");
        }
    }
}