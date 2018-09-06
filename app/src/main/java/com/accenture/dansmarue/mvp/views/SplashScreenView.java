package com.accenture.dansmarue.mvp.views;

/**
 * Created by PK on 22/03/2017.
 * MVP View Interface for {@link com.accenture.dansmarue.ui.activities.SplashScreenActivity}
 */
public interface SplashScreenView extends BaseView {

    /**
     * CallBack when the app is launched for the first time
     */
    void onFirstTimeLaunch();

    /**
     * CallBack when the app is launched
     */
    void onLaunch();

    /**
     * CallBack after data loading
     */
    void dataReady();

    void endCauseNoCategories();
}
