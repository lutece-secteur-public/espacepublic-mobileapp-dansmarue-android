package com.accenture.dansmarue.utils;

import com.accenture.dansmarue.mvp.models.Incident;

import java.util.Comparator;

/**
 * Created by PK on 15/05/2017.
 */

public class IncidentComparator implements Comparator<Incident> {

    private static IncidentComparator instance;

    private IncidentComparator() {

    }

    public static synchronized IncidentComparator getInstance() {
        if (instance == null) {
            instance = new IncidentComparator();
        }
        return instance;
    }

    @Override
    public int compare(Incident o1, Incident o2) {
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        int dayComp = o2.getDate().compareTo(o1.getDate());
        if (dayComp == 0) {
            return o2.getHour().compareTo(o1.getHour());
        }
        return dayComp;
    }
}
