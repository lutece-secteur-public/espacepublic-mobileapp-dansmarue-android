package com.accenture.dansmarue.mvp.presenters;

import com.accenture.dansmarue.mvp.views.BaseView;
import com.accenture.dansmarue.mvp.views.SliderView;

import javax.inject.Inject;

/**
 * Created by PK on 27/03/2017.
 * Presenter of the {@link SliderView} MVP view
 */
public class SliderPresenter extends BasePresenter<SliderView> {

    @SuppressWarnings("WeakerAccess")
    protected SliderView view;

    @Inject
    public SliderPresenter(final SliderView view) {
        this.view = view;
    }

    public void onBeginButtonPressed() {
        view.launchRuntimeGeolocPermissionRequestActivity();
    }

    @Override
    protected BaseView getView() {
        return view;
    }
}
