package com.hebertwilliams.goldenhour;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hebertwilliams.goldenhour.api.WundergroundApiUtility;
import com.hebertwilliams.goldenhour.model.AstroResponse;
import com.hebertwilliams.goldenhour.model.GeoResponse;

import java.util.List;

/**
 * Created by kylehebert on 10/23/15.
 */
public class ChoiceFragment extends Fragment {

    private static final String TAG = "ChoiceFragment";

    private Button mCameraActivityButton;
    private Button mGalleryActivityButton;
    private Button mNotifyMeButton;
    private TextView mSunsetTextView;
    private TextView mGoldenHourTextView;

    private GeoResponse mGeoResponse;
    private int mSunsetHour;
    private int mSunsetMinute;




    public static ChoiceFragment newInstance() {

        return new ChoiceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchLocationTask().execute();

        //start background service to check for sunset
        GoldenHourService.setServiceAlarm(getActivity(),true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choice, container, false);



        mCameraActivityButton = (Button) view.findViewById(R.id.camera_fragment_button);
        mCameraActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CameraActivity.class);
                startActivity(i);
            }
        });
        mGalleryActivityButton = (Button) view.findViewById(R.id.gallery_fragment_button);
        mGalleryActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GalleryActivity.class);

                startActivity(i);
            }
        });

        mSunsetTextView = (TextView) view.findViewById(R.id.sunset_textview);
        mGoldenHourTextView = (TextView) view.findViewById(R.id.golden_hour_textview);

//        mNotifyMeButton = (Button) view.findViewById(R.id.notify_me_button);
//        //this button should only be visible if the async tasks are successful
//        mNotifyMeButton.setVisibility(View.INVISIBLE);
//        mNotifyMeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), DebugActivity.class);
//                startActivity(intent);
//            }
//        });


        return view;
    }

    private class FetchLocationTask extends AsyncTask<Void, Void, GeoResponse> {
        @Override
        protected GeoResponse doInBackground(Void... params) {
            List<Object> responses = new WundergroundApiUtility().fetchGeoResponse();
            GeoResponse geoResponse = (GeoResponse) responses.get(0);
            return geoResponse;
        }

        @Override
        protected void onPostExecute(GeoResponse geoResponse) {
            mGeoResponse = geoResponse;
            new FetchAstronomyTask().execute(mGeoResponse);

        }
    }

    private class FetchAstronomyTask extends AsyncTask<GeoResponse, Void, AstroResponse> {
        @Override
        protected AstroResponse doInBackground(GeoResponse... params) {
            List<Object> responses = new WundergroundApiUtility().fetchAstroResponse(mGeoResponse);
            AstroResponse astroResponse = (AstroResponse) responses.get(0);
            return astroResponse;
        }

        @Override
        protected void onPostExecute(AstroResponse astroResponse) {
            mSunsetHour = astroResponse.getSunsetHour();
            mSunsetMinute = astroResponse.getSunsetMinute();
            String sunsetString = getString(R.string.sunset_today,astroResponse.getSunset());
            String goldenHourString = getString(R.string.golden_hour_today, astroResponse
                    .getGoldenHour());
            mSunsetTextView.setText(sunsetString);
            mGoldenHourTextView.setText(goldenHourString);
//            mNotifyMeButton.setVisibility(View.VISIBLE);
            Log.i(TAG, "Date converted:" + astroResponse.getGoldenHourTime());

        }
    }

}
