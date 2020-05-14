package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * {@link EquipementAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link Equipement} objects.
 */
public class EquipementAdapter extends ArrayAdapter<Equipement> {

    /**
     * Resource ID for the background color for this list of equipements
     */
    private int mColorResourceId;

    private final List<Equipement> equipements;
    protected List<Equipement> filteredEquipements = new ArrayList<>();

    private UpdateFragmentSide updateFragmentSide;


    /**
     * Create a new {@link EquipementAdapter} object.
     *
     * @param context         is the current context (i.e. Activity) that the equipementAdapter is being created in.
     * @param equipements     is the list of {@link Equipement}s to be displayed.
     * @param colorResourceId is the resource ID for the background color for this list of equipements
     */
    public EquipementAdapter(Context context, ArrayList<Equipement> equipements, int colorResourceId, UpdateFragmentSide update) {
        super(context, 0, equipements);
        mColorResourceId = colorResourceId;
        this.equipements = equipements;
        this.updateFragmentSide = update;
    }

    @Override
    public int getCount() {
        return filteredEquipements.size();
    }

    @Override
    public Filter getFilter() {
        return new EquipementFilter(this, equipements);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.equipement_list_item, parent, false);
        }

        // Get the {@link Equipement} object located at this position in the list
        final Equipement currentEquipement = filteredEquipements.get(position);

        TextView equipementAddressTv = (TextView) listItemView.findViewById(R.id.equipement_address);
        equipementAddressTv.setText(currentEquipement.getAdresse());

        TextView equipementNameTv = (TextView) listItemView.findViewById(R.id.equipement_name);
        equipementNameTv.setText(currentEquipement.getName());

        // Find the ImageView in the equipement_list_item.xmlst_item.xml layout with the ID image.
        ImageView imageEquipement = (ImageView) listItemView.findViewById(R.id.image_equipement);
        // Check if an image is provided for this word or not
        if (currentEquipement.hasImage()) {
            // If an image is available, display the provided image based on the resource ID
            imageEquipement.setImageResource(currentEquipement.getImageResourceId());
        } else {
            imageEquipement.setImageResource(R.drawable.ic_broken_image);
        }

        // Set the theme color for the list item
        View textContainer = listItemView.findViewById(R.id.equipement_text_container);
        // Find the color that the resource ID maps to
        int color = ContextCompat.getColor(getContext(), mColorResourceId);
        // Set the background color of the text container View
        textContainer.setBackgroundColor(color);

        // Use interface to communicate the selected equipement
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: passage adapter au clic");
                try {
                    if (null != currentEquipement) {
                        updateFragmentSide.updateAutocomplete(currentEquipement);

                        // Hide Keyboard
                        InputMethodManager in = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);


                    }

                } catch (ClassCastException e) {
                    Log.e(TAG, "onClick: " + e.toString());
                }
            }
        });

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }


    public interface UpdateFragmentSide {
        void updateAutocomplete(Equipement equipement);
    }
}