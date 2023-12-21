package com.accenture.dansmarue.mvp.views;

import com.accenture.dansmarue.di.modules.PresenterModule;

/**
 * Created by PK on 24/03/2017.
 * Base view that all views from MVP need to implements in order to enable DaggerInjection (see {@link PresenterModule}
 */
public interface BaseView {

    void displayConnectionError();

}
