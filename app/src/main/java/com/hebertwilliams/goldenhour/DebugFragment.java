package com.hebertwilliams.goldenhour;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hebertwilliams.goldenhour.model.AstroResponse;
import com.hebertwilliams.goldenhour.model.GeoResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by kylehebert on 11/3/15.
 */
public class DebugFragment extends Fragment {

    private static final String TAG = "DebugFragment";

    private GeoResponse mGeoResponse;
    private String mSunsetHour;
    private String mSunsetMinute;

    private TextView mSunsetTextView;


    public static DebugFragment newInstance() {
        return new DebugFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchLocationTask().execute();
        new FetchAstronomyTask().execute(mGeoResponse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debug, container, false);

        mSunsetTextView = (TextView) view.findViewById(R.id.sunset_textview);
        mSunsetTextView.setText(mSunsetHour + mSunsetMinute);

        return view;

    }

    private class FetchLocationTask extends AsyncTask<Void, Void, GeoResponse> {
        @Override
        protected GeoResponse doInBackground(Void...params) {
            List<Object> responses = new WundergroundApiUtility().fetchGeoRepsonse();
            GeoResponse geoResponse = (GeoResponse) responses.get(0);
            return geoResponse;
        }

        @Override
        protected void onPostExecute(GeoResponse geoResponse) {
            mGeoResponse = geoResponse;
        }
    }

    private class FetchAstronomyTask extends AsyncTask<GeoResponse,Void,AstroResponse> {
        @Override
        protected AstroResponse doInBackground(GeoResponse...params) {
            List<Object> responses = new WundergroundApiUtility().fetchAstroResponse(mGeoResponse);
            AstroResponse astroResponse = (AstroResponse) responses.get(0);
            return astroResponse;
        }

        @Override
        protected void onPostExecute(AstroResponse astroResponse) {
            mSunsetHour = astroResponse.getSunsetHour();
            mSunsetMinute = astroResponse.getSunsetMinute();

        }
    }


}
