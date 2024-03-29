package com.accenture.dansmarue.mvp.views;

/**
 * Created by PK on 02/04/2017.
 */
public interface AddAnomalyEquipementView extends BaseView {

    void onIncidentCreated(final Integer incidentId);

    void onIncidentNotCreated(final String errorMessage);

    void showGreetingsDialog(final boolean askEmail);

    void showDialogErrorSaveDraft();

    void showIncidentAndPhotosOkThankYou(boolean smile);

    void proposeDraft();

    void navigateBack();

    void showPicture(final String fileName);
}
