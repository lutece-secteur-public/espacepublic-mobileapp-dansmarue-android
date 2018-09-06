package com.accenture.dansmarue.mvp.views;

import com.accenture.dansmarue.mvp.models.Incident;

/**
 * Created by d4v1d on 11/05/2017.
 */

public interface AnomalyDetailsView extends BaseView {

    void displayFollow();

    void displayFollowFailure();

    void displayUnfollow();

    void displayUnfollowFailure();

    void populateFields(final Incident incident);

    void displayGreetingsOk();

    void displayGreetingsKo();

    void inviteToLogin();

    void displayResolveKo();

    void displayResolveOk();

    void closeActvity();


    void showPicture(String absolutePath);
}
