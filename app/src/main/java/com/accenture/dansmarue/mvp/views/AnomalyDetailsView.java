package com.accenture.dansmarue.mvp.views;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.models.MessageServiceFait;

import java.util.List;

/**
 * Created by d4v1d on 11/05/2017.
 */

public interface AnomalyDetailsView extends BaseView {

    void displayFollow();

    void displayFollowFailure();

    void displayUnfollow();

    void displayUnfollowFailure();

    void populateFields(final Incident incident);

    void populateMessageServiceFaitGeneric(List<MessageServiceFait> messages);

    void populateMessageServiceFaitType(List<MessageServiceFait> messages);

    void displayGreetingsOk();

    void displayGreetingsKo();

    void inviteToLogin();

    void displayResolveKo();

    void displayResolveOk();

    void closeActvity();

    void updatePicture(String absolutePath);

    void updatePictureRequalification(String absolutePath);

    void requalificationSuccess();

    void requalificationFaillure();

    void callResolveIncident();

    void uploadRequalificationDone();

    void displaySavePrecisionsTerrainSuccess();
}
