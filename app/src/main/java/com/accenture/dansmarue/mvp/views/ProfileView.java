package com.accenture.dansmarue.mvp.views;

import com.accenture.dansmarue.mvp.models.Incident;

import java.util.List;

/**
 * Created by PK on 11/05/2017.
 */

public interface ProfileView extends BaseView {

    void showDrafts(final List<Incident> drafts);

    void showSolvedIncidents(final List<Incident> incidents);

    void showUnsolvedIncidents(final List<Incident> incidents);

    void modifyDraft(final Incident draft);

    void showAnomalyDetails(final Incident incident);

    void showPreferences();

    void showFailedLoading();

    void showMenuAll();

    void showMenuDrafts();

    void showMenuUnresolved();

    void showMenuResolved();

    void loadIncidents(final String filterState);

    void descriptionCurrentItem(final String filterState);

}
