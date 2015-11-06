package com.hebertwilliams.goldenhour.api;

import android.net.Uri;
import android.util.Log;

import com.hebertwilliams.goldenhour.model.AstroResponse;
import com.hebertwilliams.goldenhour.model.GeoResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylehebert on 11/2/15. Polls the weather underground astronomy API for daily sunrise
 * and sunset
 */

/*
sample autocomplete query: http://api.wunderground.com/api/2b50a44d525a8cd3/astronomy/q/zmw:94125.1.99999.json
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

    private final static String ASTRONOMY_QUERY = "astronomy";
    private final static String GEO_QUERY = "geolookup";
    private final static String QUERY = "q";
    private final static String AUTO_IP_QUERY ="autoip.json";
    private final static String JSON_QUERY = ".json";

    //creates the default query string
    private static final Uri ENDPOINT = Uri
            .parse("http://api.wunderground.com/api")
            .buildUpon()
            .appendPath(WundergroundAPIKey)
            .build();

    /*
    create the remaining part of the query URL based on
    parameters passed
    */
    private String buildGeoQueryUrl(String queryType) {

        Uri.Builder builder = ENDPOINT.buildUpon();

        if (queryType.equals(GEO_QUERY)) { //just a double check
            builder.appendPath(GEO_QUERY);
            builder.appendPath(QUERY);
            builder.appendPath(AUTO_IP_QUERY);
        }

        return builder.build().toString();
    }

    private String buildAstroQueryUrl(String queryType, GeoResponse geoResponse) {

        Uri.Builder builder = ENDPOINT.buildUpon();

        String zmw = "zmw:"+ geoResponse.getZip() + "." + geoResponse.getMagic() + "." + geoResponse.getWmo() + "." + JSON_QUERY;

        if (queryType.equals(ASTRONOMY_QUERY)) { //just a double check
            builder.appendPath(ASTRONOMY_QUERY);
            builder.appendPath(QUERY);
            builder.appendPath(zmw);
        }

        return builder.build().toString();

    }

    //build a url for fetching location
    public List<Object> fetchGeoResponse() {
        String url = buildGeoQueryUrl(GEO_QUERY);
        return fetchJsonResponse(url);
    }

    //build a url for fecthing astronomical data
    public List<Object> fetchAstroResponse(GeoResponse geoResponse) {
        String url = buildAstroQueryUrl(ASTRONOMY_QUERY, geoResponse);
        return fetchJsonResponse(url);
    }


    public List<Object> fetchJsonResponse(String url) {

        //I'm using a list as a container for the response objects created by parseJsonRes()
        List<Object> responses = new ArrayList();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            parseJsonResponse(responses,jsonObject);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch JSON", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return responses;
    }

    public void parseJsonResponse(List<Object> responses, JSONObject jsonObject) throws
            IOException,
            JSONException {
        //first determine which response we got by checking the features object
        JSONObject responseJsonObject = jsonObject.getJSONObject("response");
        JSONObject featuresJsonObject = responseJsonObject.getJSONObject("features");

        if (featuresJsonObject.has("geolookup")) {
            //create a GeoResponse object and assign the JSON values to it
            JSONObject locationJsonObject = jsonObject.getJSONObject("location");
            GeoResponse geoResponse = new GeoResponse();
            geoResponse.setZip(locationJsonObject.getString("zip"));
            geoResponse.setMagic(locationJsonObject.getString("magic"));
            geoResponse.setWmo(locationJsonObject.getString("wmo"));

            //add the object to the list
            responses.add(geoResponse);
        }

        if (featuresJsonObject.has("astronomy")) {
            //create an AstroResponse object and assign the JSON values to it
            JSONObject moonPhaseJsonObject = jsonObject.getJSONObject("moon_phase");
            JSONObject sunsetJsonObject = moonPhaseJsonObject.getJSONObject("sunset");
            AstroResponse astroResponse = new AstroResponse();
            astroResponse.setSunsetHour(sunsetJsonObject.getInt("hour"));
            astroResponse.setSunsetMinute(sunsetJsonObject.getInt("minute"));

            //add the object to the list
            responses.add(astroResponse);

        }


    }

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




}

