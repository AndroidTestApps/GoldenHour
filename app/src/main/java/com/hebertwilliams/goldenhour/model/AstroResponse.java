package com.hebertwilliams.goldenhour.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kylehebert on 11/4/15. Model object for capturing the json response
 * from a wunderground api astronomy query
 */
public class AstroResponse {

    private static final String TAG = "AstroResponse";

    private int mSunsetHour;
    private int mSunsetMinute;
    private String mSunset;
    private String mGoldenHour;


    public int getSunsetHour() {
        return mSunsetHour;
    }

    public void setSunsetHour(int sunsetHour) {
        mSunsetHour = sunsetHour;
    }

    public int getSunsetMinute() {
        return mSunsetMinute;
    }

    public void setSunsetMinute(int sunsetMinute) {
        mSunsetMinute = sunsetMinute;
    }

    public String getSunset() {
        if (mSunsetHour <= 12) {
            mSunset = String.valueOf(mSunsetHour) + ":" + String.valueOf(mSunsetMinute);
        } else {
            mSunset = String.valueOf(mSunsetHour - 12) + ":" + String.valueOf(mSunsetMinute);
        }
        return mSunset;
    }

    public String getGoldenHour() {
        if (mSunsetHour <= 12) {
            mGoldenHour = String.valueOf(mSunsetHour - 1) + ":" + String.valueOf(mSunsetMinute);
        } else {
            mGoldenHour = String.valueOf(mSunsetHour - 13) + ":" + String.valueOf(mSunsetMinute);
        }
        return mGoldenHour;
    }

    public Date getGoldenHourTime() {
        Date date = null;
        String formatPattern = "HH:mm";
        String goldenHourString = getGoldenHour();
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatPattern, Locale.US);
        try {
            date = dateFormat.parse(goldenHourString);
        } catch (ParseException pe) {
            Log.e(TAG, "Failed to convert date", pe);
        }

        return date;
    }


}
