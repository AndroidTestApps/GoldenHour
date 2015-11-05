package com.hebertwilliams.goldenhour.model;

/**
 * Created by kylehebert on 11/4/15. Model object for capturing the json response
 * from a wunderground api astronomy query
 */
public class AstroResponse {
    private String mSunsetHour;
    private String mSunsetMinute;

    public String getSunsetHour() {
        return mSunsetHour;
    }

    public void setSunsetHour(String sunsetHour) {
        mSunsetHour = sunsetHour;
    }

    public String getSunsetMinute() {
        return mSunsetMinute;
    }

    public void setSunsetMinute(String sunsetMinute) {
        mSunsetMinute = sunsetMinute;
    }
}
