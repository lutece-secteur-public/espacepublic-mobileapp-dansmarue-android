package com.accenture.dansmarue.mvp.views;

import com.accenture.dansmarue.mvp.models.Incident;

/**
 * Created by PK on 19/04/2017.
 */

public interface WelcomeMapView extends BaseView {

    void showProfile();

    void showIncidentDetails(final Incident incident);

    void displayFollow();

    void displayFollowFailure();

    void displayUnfollow();

    void displayUnfollowFailure();

    void inviteToLogin();

    void displayGreetingsOk();

    void displayGreetingsKo();
}
