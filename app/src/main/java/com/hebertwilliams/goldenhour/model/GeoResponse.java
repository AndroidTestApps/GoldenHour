package com.hebertwilliams.goldenhour.model;

/**
 * Created by kylehebert on 11/4/15. A model object for capturing data
 * from the wunderground geolookup json response
 */
public class GeoResponse {
    private String mZip;
    private String mMagic;
    private String mWmo;

    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }

    public String getMagic() {
        return mMagic;
    }

    public void setMagic(String magic) {
        mMagic = magic;
    }

    public String getWmo() {
        return mWmo;
    }

    public void setWmo(String wmo) {
        mWmo = wmo;
    }
}
