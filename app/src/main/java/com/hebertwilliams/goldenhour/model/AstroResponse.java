package com.hebertwilliams.goldenhour.model;

import android.text.method.DateTimeKeyListener;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kylehebert on 11/4/15. Model object for capturing the json response
 * from a wunderground api astronomy query
 */
public class AstroResponse {

    private static final String TAG = "AstroResponse";

    private int mSunsetHour;
    private int mSunsetMinute;
    private String mSunsetTime;
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

    public String getSunsetTime() {
        if (mSunsetHour <= 12) {
            mSunsetTime = String.valueOf(mSunsetHour) + ":" + String.valueOf(mSunsetMinute);
        } else {
            mSunsetTime = String.valueOf(mSunsetHour-12) + ":" + String.valueOf(mSunsetMinute);
        }
        return mSunsetTime;
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
        String goldenHourString = getGoldenHour();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            date = dateFormat.parse(goldenHourString);
        } catch (ParseException pe){
            Log.e(TAG, "Failed to convert date", pe);
        }

        return date;
    }


}
