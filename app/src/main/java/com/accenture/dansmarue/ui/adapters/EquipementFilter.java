package com.accenture.dansmarue.ui.adapters;

import android.widget.Filter;

import com.accenture.dansmarue.mvp.models.equipementsMunicipaux.Equipement;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by d4v1d on 07/09/2017.
 */

public class EquipementFilter extends Filter {

    EquipementAdapter equipementAdapter;
    List<Equipement> originalList;
    List<Equipement> filteredList;

    public EquipementFilter(EquipementAdapter adapter, List<Equipement> originalList) {
        super();
        this.equipementAdapter = adapter;
        this.originalList = originalList;
        this.filteredList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        // Constraint : text you type in autocompleteTxtView

        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {

            //regex ignore accent caractere
            final String regex = "\\p{InCombiningDiacriticalMarks}+";

            final String filterPattern = Normalizer.normalize(constraint, Normalizer.Form.NFD)
                    .replaceAll(regex, "").toLowerCase();

            // Your filtering logic goes in here
            for (final Equipement equipement : originalList) {

                String normalizeNameEquipement = Normalizer.normalize(equipement.getName(), Normalizer.Form.NFD)
                        .replaceAll(regex, "").toLowerCase();

                String normalizeAdresseEquipement = Normalizer.normalize(equipement.getAdresse(), Normalizer.Form.NFD)
                        .replaceAll(regex, "").toLowerCase();

                if (normalizeNameEquipement.contains(filterPattern) || normalizeAdresseEquipement.contains(filterPattern)) {
                    filteredList.add(equipement);
                }
            }
        }

        //Sort By alphabetical order
        if (!filteredList.isEmpty()) {
            Collections.sort(filteredList, new Comparator<Equipement>() {
                @Override
                public int compare(Equipement e1, Equipement e2) {
                    return e1.getName().compareTo(e2.getName());
                }
            });
        }


        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        equipementAdapter.filteredEquipements.clear();
        equipementAdapter.filteredEquipements.addAll((List) results.values);
        equipementAdapter.notifyDataSetChanged();
    }
}
