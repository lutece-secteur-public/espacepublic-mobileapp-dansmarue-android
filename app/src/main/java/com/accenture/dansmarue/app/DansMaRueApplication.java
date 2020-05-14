package com.accenture.dansmarue.app;


import androidx.multidex.MultiDexApplication;

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
        initializeApplicationComponent();
        initializeCaligraphy();

    }


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
