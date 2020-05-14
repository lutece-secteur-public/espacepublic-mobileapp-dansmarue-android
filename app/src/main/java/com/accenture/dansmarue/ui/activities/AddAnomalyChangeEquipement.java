package com.accenture.dansmarue.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;
import com.accenture.dansmarue.ui.adapters.EquipementAdapter;
import com.accenture.dansmarue.utils.PrefManager;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import butterknife.BindView;


public class AddAnomalyChangeEquipement extends BaseActivity implements EquipementAdapter.UpdateFragmentSide {

    private static final String TAG = AddAnomalyChangeEquipement.class.getCanonicalName();

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private PrefManager prefManager;

    private AutoCompleteTextView autocompleteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.change_equipement_libelle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        prefManager = new PrefManager(getApplicationContext());


        //        Search and place pin
        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete_dogs);

        // Fake Datas : create a list of equipements
        ArrayList<Equipement> equipements = new ArrayList<Equipement>();
        equipements = (ArrayList) prefManager.getEquipementTypeByDefault().getListEquipementByType();


        // Setup autocomplete
        EquipementAdapter adapter = new EquipementAdapter(getApplicationContext(), equipements, R.color.colorWhite, this);

        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete_dogs);
        autocompleteView.setAdapter(adapter);

        // Setup Erase Button
        ImageView autocompleteErase = (ImageView) findViewById(R.id.autocomplete_erase);
        autocompleteErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocompleteView.setText("");
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    // Avoid user to quit the app
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected int getContentView() {
        return R.layout.add_anomaly_change_equipement_layout;
    }

    @Override
    public void updateAutocomplete(Equipement equipement) {
        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete_dogs);
        autocompleteView.setText(equipement.getName() + " ");
        // Cursor @ the end
        autocompleteView.setSelection(equipement.getName().length());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("equipement_choisi", new GsonBuilder().create().toJson(equipement));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}
