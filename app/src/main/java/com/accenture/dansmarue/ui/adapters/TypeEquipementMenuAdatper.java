package com.accenture.dansmarue.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.TypeEquipement;
import com.accenture.dansmarue.utils.MiscTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d4v1d on 22/11/2017.
 */

public class TypeEquipementMenuAdatper extends ArrayAdapter<TypeEquipement> {

    protected List<TypeEquipement> maListeTypeEquipement = new ArrayList<>();

    public TypeEquipementMenuAdatper(@NonNull Context context, @LayoutRes int resource, @NonNull List<TypeEquipement> objects) {
        super(context, resource, objects);
        this.maListeTypeEquipement = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final TypeEquipement typeEquipement = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.type_anos_chooser, parent, false);
        }


        if (typeEquipement != null) {

            TextView textView = (TextView) convertView.findViewById(R.id.cat_name);
            ImageView icon = (ImageView) convertView.findViewById(R.id.cat_icon);


            textView.setText(typeEquipement.getLibelleEcranMobile());

            // image de l'icône
            String imageString = typeEquipement.getImageTypeEquipement();
            Bitmap icone = MiscTools.base64ToBitmap(imageString, 68);
            icon.setImageBitmap(icone);

        }


        return convertView;
    }

}
