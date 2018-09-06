package com.accenture.dansmarue.app;


import android.support.multidex.MultiDexApplication;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.ApplicationComponent;
import com.accenture.dansmarue.di.components.DaggerApplicationComponent;
import com.accenture.dansmarue.di.modules.ApplicationModule;
import com.crashlytics.android.Crashlytics;


import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by PK on 22/03/2017.
 * DansMaRue Application. Used to start dagger dependency injection mechanism
 */
public class DansMaRueApplication extends MultiDexApplication {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
       // initializeAdTagPlatformSynchronize();
        initializeApplicationComponent();
        initializeCaligraphy();

    }


    /**private void initializeAdTagPlatformSynchronize() {
        //Synchronize with  AdTag Platform Connecthings ArMen
        //Initialize Beacons notification
        AdtagInitializer adtagInitializer = AdtagInitializer.getInstance()
                .initContext(this)
                .initUrlType(Url.UrlType.PROD)
                .initUser(getString(R.string.login_armen), getString(R.string.password_armen))
                .initCompany(getString(R.string.company_key_armen));
        adtagInitializer.synchronize();

        adtagInitializer.addPermissionToAsk(Manifest.permission.ACCESS_COARSE_LOCATION);
    }**/

    private void initializeCaligraphy() {
        // Initialise Calligraphy
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Montserrat-Regular.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    private void initializeApplicationComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
