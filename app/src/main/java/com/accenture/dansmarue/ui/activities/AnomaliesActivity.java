package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.presenters.ProfilePresenter;
import com.accenture.dansmarue.mvp.views.ProfileView;
import com.accenture.dansmarue.ui.adapters.ProfileSection;
import com.accenture.dansmarue.ui.fragments.ProfileFragment;
import com.accenture.dansmarue.utils.Constants;
import com.google.gson.GsonBuilder;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class AnomaliesActivity extends BaseActivity implements ProfileView {
    private static final String TAG = AnomaliesActivity.class.getCanonicalName();

    @Inject
    protected ProfilePresenter presenter;
    private ProfileFragment.OnProfileFragmentInteractionListener activity;

    private RecyclerView recyclerView;
    private SectionedRecyclerViewAdapter adapter;

    private ProfileSection draftSection;
    private ProfileSection resolvedSection;
    private ProfileSection unresolvedSection;

    private View rootView;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.menu_anos_drafts)
    protected TextView menuDraft;
    @BindView(R.id.menu_anos_unresolved)
    protected TextView menuUnresolved;
    @BindView(R.id.menu_anos_resolved)
    protected TextView menuResolved;

    @Override
    protected int getContentView() {
        return R.layout.fragment_profile;
    }

    @Override
    void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        adapter = new SectionedRecyclerViewAdapter();

        draftSection = new ProfileSection(this, getString(R.string.section_drafts), false, presenter);
        unresolvedSection = new ProfileSection(this, getString(R.string.section_unresolved), false, presenter);
        resolvedSection = new ProfileSection(this, getString(R.string.section_resolved), true, presenter);

        adapter.addSection(Constants.TAG_SECTION_DRAFTS, draftSection);
        adapter.addSection(Constants.TAG_SECTION_UNRESOLVED, unresolvedSection);
        adapter.addSection(Constants.TAG_SECTION_RESOLVED, resolvedSection);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_anomaly_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        initRecyclerActions();

        loadDraft();
        showMenuDrafts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDraft();
        Log.i(TAG, "onResume: ");
        presenter.oldPositionMenu();
    }

    @OnClick(R.id.img_back_arrow)
    public void onClickBackArrow() {
        finish();
    }

    @OnClick({R.id.menu_anos_drafts, R.id.menu_anos_unresolved, R.id.menu_anos_resolved})
    public void onMenuClicked(final View view) {
        presenter.onMenuClicked(view.getId());
    }


    @Override
    public void showDrafts(List<Incident> drafts) {
        draftSection.setData(drafts);
        draftSection.setState(Section.State.LOADED);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showSolvedIncidents(List<Incident> incidents) {
        resolvedSection.setData(incidents);
        resolvedSection.setState(Section.State.LOADED);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showUnsolvedIncidents(List<Incident> incidents) {
        unresolvedSection.setData(incidents);
        unresolvedSection.setState(Section.State.LOADED);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void modifyDraft(Incident draft) {
        Intent intent;

        // Add ano equipement or outdoor
        if (null != draft.getEquipementId()) {
            intent = new Intent(this, AddAnomalyEquipementActivity.class);

        } else {
            intent = new Intent(this, AddAnomalyActivity.class);
        }
        final String jsonDraft = new GsonBuilder().create().toJson(draft);
        intent.putExtra(Constants.EXTRA_DRAFT, jsonDraft);
        startActivityForResult(intent, 9281);

    }

    @Override
    public void showAnomalyDetails(Incident incident) {
        Intent intent;

        if (null != incident.getEquipementId()) {

            intent = new Intent(this, AnomalyEquipementDetailsActivity.class);
        } else {
            intent = new Intent(this, AnomalyDetailsActivity.class);
        }

        intent.putExtra(Constants.EXTRA_INCIDENT_ID, incident.getId());
        intent.putExtra(Constants.EXTRA_INCIDENT_SOURCE, incident.getSource());
        startActivityForResult(intent, 9281);

    }

    @Override
    public void showPreferences() {
        startActivityForResult(new Intent(this, PrefActivity.class), 9632);
    }

    @Override
    public void showFailedLoading() {
        unresolvedSection.setState(Section.State.FAILED);
        resolvedSection.setState(Section.State.FAILED);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showMenuAll() {
        adapter.removeAllSections();
        adapter.addSection(Constants.TAG_SECTION_DRAFTS, draftSection);
        adapter.addSection(Constants.TAG_SECTION_UNRESOLVED, unresolvedSection);
        adapter.addSection(Constants.TAG_SECTION_RESOLVED, resolvedSection);
        adapter.notifyDataSetChanged();
        menuDraft.setTextColor(getResources().getColor(R.color.pink));
        menuUnresolved.setTextColor(getResources().getColor(R.color.grey_tranparent));
        menuResolved.setTextColor(getResources().getColor(R.color.grey_tranparent));

    }

    @Override
    public void showMenuDrafts() {
        adapter.removeSection(Constants.TAG_SECTION_RESOLVED);
        adapter.removeSection(Constants.TAG_SECTION_UNRESOLVED);
        adapter.addSection(Constants.TAG_SECTION_DRAFTS, draftSection);
        adapter.notifyDataSetChanged();

        menuDraft.setTextColor(getResources().getColor(R.color.pink));
        menuUnresolved.setTextColor(getResources().getColor(R.color.grey_tranparent));
        menuResolved.setTextColor(getResources().getColor(R.color.grey_tranparent));

        menuDraft.setContentDescription(getResources().getString(R.string.section_drafts_active_txt));
        menuDraft.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        menuResolved.setContentDescription("");
        menuUnresolved.setContentDescription("");
    }

    @Override
    public void showMenuUnresolved() {
        adapter.removeSection(Constants.TAG_SECTION_DRAFTS);
        adapter.removeSection(Constants.TAG_SECTION_RESOLVED);
        adapter.addSection(Constants.TAG_SECTION_UNRESOLVED, unresolvedSection);
        adapter.notifyDataSetChanged();

        menuDraft.setTextColor(getResources().getColor(R.color.grey_tranparent));
        menuUnresolved.setTextColor(getResources().getColor(R.color.pink));
        menuResolved.setTextColor(getResources().getColor(R.color.grey_tranparent));

    }

    @Override
    public void showMenuResolved() {
        adapter.removeSection(Constants.TAG_SECTION_DRAFTS);
        adapter.removeSection(Constants.TAG_SECTION_UNRESOLVED);
        adapter.addSection(Constants.TAG_SECTION_RESOLVED, resolvedSection);
        adapter.notifyDataSetChanged();

        menuDraft.setTextColor(getResources().getColor(R.color.grey_tranparent));
        menuUnresolved.setTextColor(getResources().getColor(R.color.grey_tranparent));
        menuResolved.setTextColor(getResources().getColor(R.color.pink));

    }

    @Override
    public void loadIncidents(String filterState) {
        resolvedSection.setState(Section.State.LOADING);
        unresolvedSection.setState(Section.State.LOADING);
        adapter.notifyDataSetChanged();
        presenter.loadIncidentsByUser(filterState);
    }

    @Override
    public void descriptionCurrentItem(String filterState) {
        if(Incident.STATE_OPEN.equals(filterState)) {
            menuUnresolved.setContentDescription(getResources().getString(R.string.section_unresolved_active_txt));
            menuUnresolved.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            menuResolved.setContentDescription("");
            menuDraft.setContentDescription("");
        } else if (Incident.STATE_RESOLVED.equals(filterState)) {
            menuResolved.setContentDescription(getResources().getString(R.string.section_resolved_active_txt));
            menuResolved.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            menuUnresolved.setContentDescription("");
            menuDraft.setContentDescription("");
        }
    }

    private void initRecyclerActions() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final int position = viewHolder.getAdapterPosition();
                try {
                    ProfileSection section = (ProfileSection) adapter.getSectionForPosition(position);
                    Log.d(TAG, "getSwipeDirs: " + adapter.getPositionInSection(position));
                    if (draftSection == section || unresolvedSection == section || resolvedSection == section && adapter.getPositionInSection(position) > -1) {
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "getSwipeDirs", e);
                    return 0;
                }
                return 0;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                ProfileSection section = (ProfileSection) adapter.getSectionForPosition(position);

                int posInSection = adapter.getPositionInSection(position);
                final Incident draft = section.getItem(posInSection);

                // Swype for incident = Unfollow

                if (null != draft) {

                    if (draftSection == section) {
                        presenter.deleteDraft(String.valueOf(draft.getId()));
                        section.deleteItem(posInSection);
                        adapter.notifyItemRemoved(position);
                    }

                    if (unresolvedSection == section || resolvedSection == section) {
                        presenter.unFollowDraft(draft);
                        section.deleteItem(posInSection);
                        adapter.notifyItemRemoved(position);
                    }
                }

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void loadDraft() {
        draftSection.setState(Section.State.LOADING);
        adapter.notifyDataSetChanged();
        presenter.loadDrafts();
    }
}
