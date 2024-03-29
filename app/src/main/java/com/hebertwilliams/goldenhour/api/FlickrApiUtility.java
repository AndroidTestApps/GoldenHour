package com.hebertwilliams.goldenhour.api;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.hebertwilliams.goldenhour.model.FlickrPhoto;

import org.json.JSONArray;
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
 * Created by kylehebert on 10/24/15.
 * Used to get data from the Flickr API
 */
public class FlickrApiUtility {

    private static final String TAG = "FlickrAPIUtility";

    private static String FLICKR_API_KEY = "45d845c1c326c12baa37283ffb174315";


    private static final String GET_RECENT_PHOTOS = "flickr.photos.getRecent";
    private static final String SEARCH_GOLDEN_HOUR = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", FLICKR_API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();


    public byte[] getUrlBytes(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + "; with " + urlString);

            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlString) throws IOException {
        return new String(getUrlBytes(urlString));
    }

    /*
    this method fetches recent photos only, by design should never be called
    it's a relic of my initial test build, but I left it in in case we wanted to
    use it as a fall back if Golden Hour photos can't be found
     */
    public List<FlickrPhoto> getRecentPhotos() {
        String url = buildUrl(GET_RECENT_PHOTOS, null);
        return downloadGalleryPhotos(url);
    }

    /*
    this is the default method the app will use to search
     */
    public List<FlickrPhoto> getGoldenHourPhotos(String query) {
        String url = buildUrl(SEARCH_GOLDEN_HOUR, query);
        return downloadGalleryPhotos(url);
    }

    /*
    search for photos based on location
     */
    public List<FlickrPhoto> getLocalPhotos(Location location) {
        String url = buildUrl(location);
        return downloadGalleryPhotos(url);
    }

    private List<FlickrPhoto> downloadGalleryPhotos(String url) {

        List<FlickrPhoto> photos = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            parsePhotos(photos, jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch photos", ioe);
        }

        return photos;
    }

    private String buildUrl(String method, String query) {
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);

        if (method.equals(SEARCH_GOLDEN_HOUR)) { //this should always be true
            builder.appendQueryParameter("text", query);
        }

        return builder.build().toString();
    }

    /*
    string builder for searching by location
     */
    private String buildUrl(Location location) {
        return ENDPOINT.buildUpon()
                .appendQueryParameter("method", SEARCH_GOLDEN_HOUR)
                .appendQueryParameter("lat", "" + location.getLatitude())
                .appendQueryParameter("lon", "" + location.getLongitude())
                .build().toString();
    }

    public void parsePhotos(List<FlickrPhoto> flickrPhotos, JSONObject jsonObject) throws
            IOException, JSONException {
        //get the JSON object "photos" and the array "photo" from the JSON response
        JSONObject photosJsonObject = jsonObject.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        //create a FlickrPhoto for every item in the array
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            FlickrPhoto photo = new FlickrPhoto();
            photo.setID(photoJsonObject.getString("id"));
            photo.setCaption(photoJsonObject.getString("title"));

            //ignore photos without a url
            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            photo.setUrl(photoJsonObject.getString("url_s"));
            flickrPhotos.add(photo);

        }
    }


}
