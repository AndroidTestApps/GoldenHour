package com.hebertwilliams.goldenhour.model;

/**
 * Created by kylehebert on 11/4/15. Model object for capturing the json response
 * from a wunderground api astronomy query
 */
public class AstroResponse {
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


}
