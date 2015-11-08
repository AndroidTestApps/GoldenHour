package com.hebertwilliams.goldenhour;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.hebertwilliams.goldenhour.api.WundergroundApiUtility;
import com.hebertwilliams.goldenhour.model.AstroResponse;
import com.hebertwilliams.goldenhour.model.GeoResponse;

import java.util.List;

/**
 * Created by kylehebert on 11/2/15. Uses WundergroundApiUtility
 * to poll the Weather Underground API on a scheduled basis to determine when
 * the sun will set each day and notify the user
 */
public class GoldenHourService extends IntentService {

    private static final String TAG = "GoldenHourService";

    //private static final int POLL_INTERVAL = 1000 * 60 //1 minute - for debug only

    private static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    public static Intent newIntent(Context context) {
        return new Intent(context, GoldenHourService.class);
    }

    //default constructor required
    public GoldenHourService() {
        //this is only used for debugging
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean alarmIsOn) {
        Intent intent = GoldenHourService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmIsOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock
                    .elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /*
    check to see if the alarm is already on
     */
    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = GoldenHourService.newIntent(context);
        //if the intent does no already exist, return null instead of recreating it
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent
                .FLAG_NO_CREATE);
        return pendingIntent != null;
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
                ((GeoResponse) geoResponses.get(0));

        //now use the astronomy data to determine when the sun will set
        AstroResponse astroResponse = (AstroResponse) astroResponses.get(0);
        String goldenHourBegins = "The golden hour begins at " + astroResponse.getGoldenHour();
        Log.i(TAG, goldenHourBegins);

        //notify the user
        /*
        TODO Change notification timing
        right now a notification gets sent every time the service runs,
        notification should get scheduled for when Golden Hour begins instead
         */
        Resources resources = getResources();
        Intent choiceIntent = ChoiceActivity.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, choiceIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.new_golden_hour_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_golden_hour_title))
                .setContentText(resources.getString(R.string.new_golden_hour_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, notification);


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
