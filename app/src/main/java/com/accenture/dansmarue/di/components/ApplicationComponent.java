package com.accenture.dansmarue.di.components;


import android.app.Application;

import com.accenture.dansmarue.di.modules.ApplicationModule;
import com.accenture.dansmarue.services.ApiServiceEquipement;
import com.accenture.dansmarue.services.AuthentParisAccountService;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.SiraApiServiceMock;
import com.accenture.dansmarue.utils.PrefManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by PK on 22/03/2017.
 * Component Interface linked to the {@link ApplicationModule}
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    SiraApiService exposeSiraApiService();

    SiraApiServiceMock exposeSiraApiServiceMock();

    ApiServiceEquipement exposeApiServiceEquipement();

    AuthentParisAccountService exposeAuthentParisAccountService();

    PrefManager exposePrefManager();

    Application exposeApplication();

    @Named("glideOkHttpClientt")
    OkHttpClient exposeOkHttpClient();
}
