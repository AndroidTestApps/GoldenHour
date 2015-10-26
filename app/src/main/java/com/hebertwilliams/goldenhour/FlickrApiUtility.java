package com.hebertwilliams.goldenhour;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

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
    private String flickrKey = "45d845c1c326c12baa37283ffb174315";

    public byte[] getUrlBytes(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + "; with " + urlString);

            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlString) throws IOException {
        return  new String(getUrlBytes(urlString));
    }

    public List<FlickrPhoto> fetchPhotos() {

        List<FlickrPhoto> photos = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", flickrKey)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            parsePhotos(photos,jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch photos", ioe);
        }

        return photos;
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
