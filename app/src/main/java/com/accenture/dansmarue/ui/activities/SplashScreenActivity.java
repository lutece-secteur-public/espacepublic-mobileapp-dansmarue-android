package com.accenture.dansmarue.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.presenters.SplashScreenPresenter;
import com.accenture.dansmarue.mvp.views.SplashScreenView;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
import com.aritraroy.rxmagneto.core.RxMagneto;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Launch SplashScreen, wait and show slider the first time
 * or redirect to the next Activity
 */
public class SplashScreenActivity extends BaseActivity implements SplashScreenView {

    private String TAG = SplashScreenActivity.class.getCanonicalName();

    private Long incidentId = null;

    private String anomalyType ="";

    @SuppressWarnings("WeakerAccess")
    @Inject
    protected SplashScreenPresenter presenter;


    @Override
    protected int getContentView() {
        return R.layout.splashscreen_activity_layout;
    }

    @Override
    protected void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        final String anomalyId = intent.getStringExtra(Constants.NOTIF_ANOMALY_ID_KEY);
        anomalyType = intent.getStringExtra(Constants.NOTIF_ANOMALY_TYPE);

        if (anomalyId != null) {
            incidentId = Long.valueOf(anomalyId);
        }

        // Check Version With RxMagneto and Go
       retreiveAppVersionOnThePlayStore();
    }

    public void retreiveAppVersionOnThePlayStore(){
        RxMagneto rxMagneto = RxMagneto.getInstance();
        rxMagneto.initialize(this);

        Log.i(TAG, "checkVersionAndPopup: package before " + BuildConfig.APPLICATION_ID);
        final String packageDMRFinal = BuildConfig.APPLICATION_ID.endsWith(".debug") ? BuildConfig.APPLICATION_ID.substring(0, BuildConfig.APPLICATION_ID.length() - 6) : BuildConfig.APPLICATION_ID;

        Single<String> version = rxMagneto.grabVersion(packageDMRFinal);
        version.observeOn(AndroidSchedulers.mainThread())
                .subscribe(versionStore -> {
                    Log.i(TAG, "checkVersionAndPopup: package after " + packageDMRFinal);
                    checkVersionAndPopup(String.valueOf(BuildConfig.VERSION_NAME), versionStore, packageDMRFinal);
                }, throwable -> {
                    Log.d("RxMagneto", "Error");
                    presenter.checkAndLoadCategories();
                });
    }

    public void checkVersionAndPopup(String versionAppTel, String versionAppStore, String packageDmrName) {

//        versionAppTel = "2.0.2";
        Integer result = MiscTools.versionCompare(versionAppTel, versionAppStore);

        Log.i(TAG, "checkVersionAndPopup: " + "onMyMobile : " + versionAppTel + " - on store : " + versionAppStore);
        if (result < 0) {

            Log.i(TAG, "checkVersionAndPopup: " + "update please");

            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this, R.style.MyDialogTheme);
            builder
                    .setMessage(getString(R.string.popup_msg))
                    .setCancelable(false)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            dialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=" + packageDmrName));
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                            presenter.checkAndLoadCategories();
                        }
                    });

            builder.create().show();


        } else {
            Log.i(TAG, "checkVersionAndPopup: " + "no update is needed");
            presenter.checkAndLoadCategories();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onFirstTimeLaunch() {
        startActivity(new Intent(SplashScreenActivity.this, SliderActivity.class));
        // close this activity
        finish();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onLaunch() {
        if (incidentId != null) {

            Intent intent;

            if(anomalyType.equals("OUTDOOR")){
                intent = new Intent(SplashScreenActivity.this, AnomalyDetailsActivity.class);
            } else {
                intent = new Intent(SplashScreenActivity.this, AnomalyEquipementDetailsActivity.class);
            }
            intent.putExtra(Constants.EXTRA_INCIDENT_ID, incidentId);
            intent.putExtra(Constants.EXTRA_INCIDENT_TYPE, anomalyType);
            startActivity(intent);
        } else {
            startActivity(new Intent(SplashScreenActivity.this, RuntimeGeolocPermissionRequestActivity.class));
        }

        // close this activity
        finish();
    }

    public void endCauseNoCategories() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this, R.style.MyDialogTheme);
        builder
                .setMessage(getString(R.string.popup_no_categories))
                .setCancelable(false)
                .setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                        finish();
                    }
                });

        builder.create().show();

    }


    @Override
    public void dataReady() {
        presenter.onDataReady();
    }


}

