package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.presenters.ProfilePresenter;
import com.accenture.dansmarue.ui.activities.InternalWebViewActivity;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.PrefManager;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

/**
 * Created by PK on 15/05/2017.
 */
public class ProfileSection extends Section {

    private List<Incident> data;
    private Context context;
    private String title;
    private boolean displayResponsableQuartier;
    private ProfilePresenter presenter;
    private PrefManager prefManager;

    public ProfileSection(final Context context, final String title, final boolean displayResponsableQuartier, final ProfilePresenter presenter) {
        super(R.layout.section_profile_header, R.layout.recycler_view_item, R.layout.section_profile_loading, R.layout.section_profile_failed);
        this.data = new ArrayList<>();
        this.context = context;
        this.title = title;
        this.displayResponsableQuartier = displayResponsableQuartier;
        this.presenter = presenter;

        prefManager = new PrefManager(context);
    }


    public void setData(final List<Incident> data) {
        this.data = data;
    }

    public void deleteItem(int position) {
        this.data.remove(position);
    }

    public Incident getItem(int position) {
        if (data != null && data.size() > 0) {
            return data.get(position);
        }
        return null;
    }


    @Override
    public int getContentItemsTotal() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ProfileSection.ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProfileSection.ViewHolder itemHolder = (ProfileSection.ViewHolder) holder;
        final Incident incident = data.get(position);

        if (incident != null) {

            itemHolder.title.setText(incident.getAlias());
            itemHolder.subtitle.setText(incident.getAddress());
            itemHolder.date.setVisibility(View.VISIBLE);
            itemHolder.date.setText(incident.getFormatedDate());
            itemHolder.number.setText(incident.getReference());

            if (prefManager.getIsAgent()) {
                itemHolder.stateName.setVisibility(View.VISIBLE);
                itemHolder.stateName.setText(incident.getStateName());
            }

            itemHolder.llAno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onItemClicked(context.getString(R.string.section_drafts).equals(title), incident);
                }
            });

            if (displayResponsableQuartier) {
                itemHolder.imgResponsableQuartier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String complementURL = "&id_dmr=" + incident.getReference() + "&Y=" + incident.getLat() + "&X=" + incident.getLng();
                        Intent intent = new Intent(context, InternalWebViewActivity.class);
                        intent.putExtra(InternalWebViewActivity.WEBSITE_ADDRESS, BuildConfig.URL_SOLEN + complementURL);
                        intent.putExtra(InternalWebViewActivity.EXECUTE_EXTRA_JS, true);
                        context.startActivity(intent);

                    }
                });
            }

            // Traitement des icones différents si anos outdoor ou équipement
            if (null != incident.getEquipementId()) {

                //indoor
                if (incident.getFirstAvailablePicture() != null) {
                    Glide.with(context)
                            .load(incident.getFirstAvailablePicture())
                            .fallback(R.drawable.ic_broken_image)
                            .error(R.drawable.ic_broken_image)
                            .into(itemHolder.icon);
                } else {
                    itemHolder.icon.setImageBitmap(MiscTools.base64ToBitmap(incident.getIconIncident(), 256));
                }

                itemHolder.iconTypeAno.setImageBitmap(MiscTools.base64ToBitmap(incident.getIconIncident(), 28));
//            itemHolder.titleCategorieAno.setTextColor(context.getResources().getColor(R.color.blue_slider));
                itemHolder.titleCategorieAno.setTextColor(context.getResources().getColor(R.color.orangeInProgress));
                itemHolder.titleCategorieAno.setText(incident.getTypeEquipementName());

            } else {

                // outdoor
                if (incident.getFirstAvailablePicture() != null) {
                    Glide.with(context)
                            .load(incident.getFirstAvailablePicture())
                            .error(incident.getPictures().getGenericPictureId())
                            .fallback(incident.getPictures().getGenericPictureId())
                            .override(1600, 1600)
                            .into(itemHolder.icon);
                } else {
                    if (0 != incident.getPictures().getGenericPictureId()) {
                        Glide.with(context).load(incident.getPictures().getGenericPictureId()).into(itemHolder.icon);
                    } else if (0 != incident.getIconIncidentSignalement()) {
                        itemHolder.icon.setImageResource(incident.getIconIncidentSignalement());
                    } else {
                        Glide.with(context).load(R.drawable.ano_outdoor_type).into(itemHolder.icon);
                    }
                }

                if (incident.isResolu()) {
                    itemHolder.titleCategorieAno.setTextColor(context.getResources().getColor(R.color.orangeInProgress));
                    itemHolder.titleCategorieAno.setText(R.string.anomalie_espace_public_libelle);

                } else {
                    itemHolder.titleCategorieAno.setTextColor(context.getResources().getColor(R.color.orangeInProgress));
                    itemHolder.titleCategorieAno.setText(R.string.anomalie_espace_public_libelle);
                }

            }

        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getFailedViewHolder(View view) {
        return new FailedViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.title.setText(title);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleCategorieAno;
        private TextView title;
        private TextView subtitle;
        private ImageView icon;
        private ImageView iconTypeAno;
        private TextView date;
        private TextView number;
        private TextView stateName;
        private ImageView imgResponsableQuartier;
        private ConstraintLayout llAno;


        public ViewHolder(View itemView) {
            super(itemView);
            titleCategorieAno = (TextView) itemView.findViewById(R.id.title_categorie_ano);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            date = (TextView) itemView.findViewById(R.id.date);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            iconTypeAno = (ImageView) itemView.findViewById(R.id.icon_type_ano);
            number = (TextView) itemView.findViewById(R.id.number);
            stateName = (TextView) itemView.findViewById(R.id.state_name);
            imgResponsableQuartier = (ImageView) itemView.findViewById(R.id.img_responsable_quartier);
            if (displayResponsableQuartier) {
                imgResponsableQuartier.setVisibility(View.VISIBLE);
            }

            llAno = (ConstraintLayout) itemView.findViewById(R.id.id_ll_ano);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.section_profile_title);
        }

        public TextView getTitle() {
            return title;
        }
    }

    public class FailedViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;

        public FailedViewHolder(View itemView) {
            super(itemView);

            rootView = itemView.findViewById(R.id.layout_section_failed);


        }
    }

}