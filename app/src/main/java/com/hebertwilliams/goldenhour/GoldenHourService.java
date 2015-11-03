package com.hebertwilliams.goldenhour;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by kylehebert on 11/2/15. Will use WundergroundApiUtility
 * to poll the Weather Underound API on a scheduled basis to determine when
 * the sun will set each day and notify the user
 */
public class GoldenHourService extends IntentService {

    private static final String TAG = "GoldenHourService";

    public static Intent newIntent(Context context) {
        return new Intent(context, GoldenHourService.class);
    }

    //default constructor required
    public GoldenHourService() {
        //this is only used for debugging
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //make sure there is an available network and device is connected
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        Log.i(TAG, "Received an intent: " + intent);
    }

    /*
    make sure the network is available and that the device is fully connected
    if no network, or not fully connected onHandleIntent will return and not execute
    the rest of the method.
     */
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService
                (CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && connectivityManager
                .getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
