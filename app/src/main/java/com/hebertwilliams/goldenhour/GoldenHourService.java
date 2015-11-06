package com.hebertwilliams.goldenhour;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import com.hebertwilliams.goldenhour.api.WundergroundApiUtility;
import com.hebertwilliams.goldenhour.model.AstroResponse;
import com.hebertwilliams.goldenhour.model.GeoResponse;

import java.util.List;

/**
 * Created by kylehebert on 11/2/15. Will use WundergroundApiUtility
 * to poll the Weather Underound API on a scheduled basis to determine when
 * the sun will set each day and notify the user
 */
public class GoldenHourService extends IntentService {

    private static final String TAG = "GoldenHourService";

    private static final int POLL_INTERVAL = 1000 * 60 * 10; // milliseconds * seconds * minutes

    public static Intent newIntent(Context context) {
        return new Intent(context, GoldenHourService.class);
    }

    public static void setServiceAlarm(Context context, boolean alarmIsOn) {
        Intent intent = GoldenHourService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmIsOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock
                    .elapsedRealtime(),POLL_INTERVAL,pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
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

        //first fetch the current location
        List<Object> geoResponses = new WundergroundApiUtility().fetchGeoResponse();

        //then fetch astronomy data using the location response
        List<Object> astroResponses = new WundergroundApiUtility().fetchAstroResponse
                ((GeoResponse)geoResponses.get(0));

        //now use the astronomy data to determine when the sun will set
        AstroResponse astroResponse = (AstroResponse)astroResponses.get(0);
        String goldenHourBegins = "The golden hour begins at " + astroResponse.getGoldenHour();
        Log.i(TAG, goldenHourBegins);




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
