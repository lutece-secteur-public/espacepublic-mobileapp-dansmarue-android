package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.TypeEquipement;
import com.accenture.dansmarue.ui.adapters.TypeEquipementMenuAdatper;
import com.accenture.dansmarue.utils.PrefManager;

import java.util.List;

import butterknife.BindView;


public class TypeEquipementChooser extends BaseActivity {

    private static final String TAG = TypeEquipementChooser.class.getCanonicalName();

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.listPrefs)
    protected ListView listPrefs;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.chooser_equipement_type_libelle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        prefManager = new PrefManager(getApplicationContext());
        final List<TypeEquipement> maListeDeTypesDequipements = prefManager.getTypesEquipement();


        // Tips : add fake equipment for ano outdoor
        TypeEquipement anomaliesOutdoor = new TypeEquipement();
        anomaliesOutdoor.setLibelleEcranMobile(getString(R.string.anomalie_espace_public_libelle));
        anomaliesOutdoor.setImageTypeEquipement(" data:image/png;base64,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");
        maListeDeTypesDequipements.add(0, anomaliesOutdoor);

        for (TypeEquipement t : maListeDeTypesDequipements) Log.i(TAG, "t : "+t.getImageTypeEquipement());



        TypeEquipementMenuAdatper adapter = new TypeEquipementMenuAdatper(this, R.layout.type_anos_chooser, maListeDeTypesDequipements);
        listPrefs.setAdapter(adapter);

        listPrefs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "onItemClick: " + position);

                if (position > 0) {
                    prefManager.setEquipementTypeByDefault(maListeDeTypesDequipements.get(position));
                    Log.i(TAG, "Equipement type by default set : " + maListeDeTypesDequipements.get(position).getNomTypeEquipement() + " - id : " + maListeDeTypesDequipements.get(position).getIdTypEquipement());
                    Intent i = new Intent(getApplicationContext(), WelcomeMapEquipementActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    prefManager.setEquipementTypeByDefault(null);
                    Intent i = new Intent(getApplicationContext(), WelcomeMapActivity.class);
                    startActivity(i);
                    finish();
                }

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
        return R.layout.type_equipement_chooser_activity_layout;
    }
}
