package com.hebertwilliams.goldenhour.model;

import android.net.Uri;

/**
 * Created by kylehebert on 10/25/15. Objects of this class will
 * represent a single Flickr photo. Each FlickrPhoto will be filled
 * with JSON data pulled from the Flickr API.
 */
public class FlickrPhoto {
    private String mCaption;
    private String mID;
    private String mUrl;
    private String mOwner;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    //generates a photo page URL
    public Uri getPhotoPageUri() {
        return Uri.parse("http://www.flickr.com/photos")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mID)
                .build();
    }
}
