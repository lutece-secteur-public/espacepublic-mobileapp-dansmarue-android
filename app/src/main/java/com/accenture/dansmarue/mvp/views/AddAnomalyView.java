package com.accenture.dansmarue.mvp.views;

/**
 * Created by PK on 02/04/2017.
 */
public interface AddAnomalyView extends BaseView {

    void onIncidentCreated(final Integer incidentId);

    void onIncidentNotCreated(String errorMessage);

    void showGreetingsDialog(final boolean askEmail);

    void showDialogErrorSaveDraft();

    void showIncidentAndPhotosOkThankYou(boolean smile);

    void proposeDraft();

    void navigateBack();

    void showPicture(final String fileName);

    void showHideAgentCommentaryField(final boolean agentConnected);

    void displayDialogDmrOffline();

    void showConnectMonParis();
}
