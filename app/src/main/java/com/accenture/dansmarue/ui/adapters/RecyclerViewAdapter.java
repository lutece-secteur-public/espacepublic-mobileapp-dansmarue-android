package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.PrefManager;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private int recyclerItemRes;
    private List<Incident> data;
    private Context context;
    private PrefManager prefManager;


    public RecyclerViewAdapter(Context context, int recyclerItemRes, List<Incident> data) {
        this.recyclerItemRes = recyclerItemRes;
        this.context = context;
        this.data = data;
        prefManager = new PrefManager(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(recyclerItemRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Incident incident = data.get(position);

        if (null == incident.getEquipementId()) {
            if (incident.getFirstAvailablePicture() != null) {
                Glide.with(context)
                        .load(incident.getFirstAvailablePicture())
                        .fallback(incident.getPictures().getGenericPictureId())
                        .error(incident.getPictures().getGenericPictureId())
                        .into(holder.icon);
            } else {
                Glide.with(context).load(incident.getPictures().getGenericPictureId()).into(holder.icon);
            }
        }
        else{
            if (incident.getFirstAvailablePicture() != null) {
                Glide.with(context)
                        .load(incident.getFirstAvailablePicture())
                        .fallback(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_broken_image)
                        .into(holder.icon);

            } else {
            if(null!= incident.getIconIncident()) holder.icon.setImageBitmap(MiscTools.base64ToBitmap(incident.getIconIncident(), 256));
            }
        }

        if(incident.isFromRamen()) {
            holder.title.setText(R.string.desc_ramen);
        } else {
            holder.title.setText(incident.getAlias());
        }
        holder.subtitle.setText(incident.getAddress());
        holder.number.setText(incident.getReference());

        if(prefManager.getIsAgent()) {
            holder.stateName.setVisibility(View.VISIBLE);
            holder.stateName.setText(incident.getStateName());
        }

    }

    public Incident getData(int position) {
        if (data != null) {
            return data.get(position);
        }
        return null;

    }

    public List<Incident> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subtitle;
        private ImageView icon;
        private TextView number;
        private TextView stateName;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            number = (TextView) itemView.findViewById(R.id.number);
            stateName = (TextView) itemView.findViewById(R.id.state_name);
        }
    }


}
