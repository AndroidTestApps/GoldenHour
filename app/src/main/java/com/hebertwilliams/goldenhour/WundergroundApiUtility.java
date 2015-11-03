package com.hebertwilliams.goldenhour;

import android.net.Uri;

/**
 * Created by kylehebert on 11/2/15. Polls the weather underground astronomy API for daily sunrise
 * and sunset
 */
public class WundergroundApiUtility {

    private final static String TAG = "WundergroundApi";

    private final static String WundergroundAPIKey = "2b50a44d525a8cd3";

    private final static String ASTRONOMY_QUERY = "/astronomy/q/";
    private final static String GEO_QUERY = "/geolookup/q/autoip.json";
    private static final Uri ENDPOINT = Uri
            .parse("http://api.wunderground.com/api/")
            .buildUpon()
            .appendPath(WundergroundAPIKey)
            .build();





    /*
    sample astronomy query: http://api.wunderground.com/api/2b50a44d525a8cd3/astronomy/q/Australia/Sydney.json
     */

    /*
    sample autoIP query: http://api.wunderground.com/api/2b50a44d525a8cd3/geolookup/q/autoip.json
     */
}

