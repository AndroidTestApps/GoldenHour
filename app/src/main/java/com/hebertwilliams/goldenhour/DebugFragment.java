package com.hebertwilliams.goldenhour;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.IOException;

/**
 * Created by kylehebert on 11/3/15.
 */
public class DebugFragment extends Fragment {

    private static final String TAG = "DebugFragment";


    public static DebugFragment newInstance() {
        return new DebugFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchWeatherTask().execute();
    }

    private class FetchWeatherTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void...params) {
            try {
                String result = new WundergroundApiUtility()
                        .getUrlString("https://www.bignerdranch.com");
                Log.i(TAG, "Got URL" + result);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL: " + ioe);
            }
            return null;
        }
    }



}
