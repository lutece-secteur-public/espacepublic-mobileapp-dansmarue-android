package com.accenture.dansmarue.mvp.presenters;

import com.accenture.dansmarue.mvp.views.BaseView;

/**
 * Created by PK on 24/03/2017.
 * Base Presenter extendable by all presenter
 */
abstract class BasePresenter<V extends BaseView> {

    protected abstract BaseView getView();

    void networkError() {
        getView().displayConnectionError();
    }
}
