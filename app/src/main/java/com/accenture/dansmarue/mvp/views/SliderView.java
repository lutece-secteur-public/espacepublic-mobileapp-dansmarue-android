package com.accenture.dansmarue.mvp.views;

/**
 * Created by PK on 24/03/2017.
 * MVP View Interface for {@link com.accenture.dansmarue.ui.activities.SliderActivity}
 */
public interface SliderView extends BaseView {

    /**
     * CallBack when the the slider button is clicked.
     * Starts the {@link com.accenture.dansmarue.ui.activities.RuntimeGeolocPermissionRequestActivity}
     */
    void launchRuntimeGeolocPermissionRequestActivity();
}
