package com.accenture.dansmarue.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by d4v1d on 09/05/2017.
 */

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static boolean isConnected(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }

    // Test if GPS is enable
    public static boolean isGpsEnable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
