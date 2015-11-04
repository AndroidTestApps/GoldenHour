package com.hebertwilliams.goldenhour;

import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kylehebert on 11/2/15. Polls the weather underground astronomy API for daily sunrise
 * and sunset
 */

/*
sample astronomy query: http://api.wunderground.com/api/2b50a44d525a8cd3/astronomy/q/Australia/Sydney.json
 */

/*
sample autoIP query: http://api.wunderground.com/api/2b50a44d525a8cd3/geolookup/q/autoip.json
 */

public class WundergroundApiUtility {

    private final static String TAG = "WundergroundApi";

    private final static String WundergroundAPIKey = "2b50a44d525a8cd3";

    private final static String ASTRONOMY_QUERY = "astronomy/q/";
    private final static String GEO_QUERY = "geolookup/q/autoip.json";

    //creates the default query string
    private static final Uri ENDPOINT = Uri
            .parse("http://api.wunderground.com/api")
            .buildUpon()
            .appendPath(WundergroundAPIKey)
            .build();

    /*
    here we open a connection to a URL and read in all of the bytes
    from the response until it runs out of data
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return  outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /*
    takes the bytes returned by getUrlBytes and converts them
    into a String
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /*
    create the remanining part of the query URL based on
    parameters passed
     */
    private String buildGeoQueryUrl(String queryType) {

        Uri.Builder builder = ENDPOINT.buildUpon();

        if (queryType.equals("geo")) {
            builder.appendPath(GEO_QUERY);
        }
        if (queryType.equals("astro")) {
            builder.appendPath(ASTRONOMY_QUERY);
        }

        return builder.build().toString();

    }

    public void parseWundergroundJsonRepsonse() {

    }





}

