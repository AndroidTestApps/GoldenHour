package com.hebertwilliams.goldenhour;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hebertwilliams.goldenhour.api.WundergroundApiUtility;
import com.hebertwilliams.goldenhour.model.AstroResponse;
import com.hebertwilliams.goldenhour.model.GeoResponse;

import java.util.List;

/**
 * Created by kylehebert on 10/23/15. This is the main activity, shows the current
 * day's sunset time as well as golden hour start. Users can launch the camera
 * or gallery fragments, as well as enable and disable notifications.
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        new FetchLocationTask().execute();

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


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_choice, menu);

        MenuItem toggleNotification = menu.findItem(R.id.menu_item_toggle_notification);
        if (GoldenHourService.isServiceAlarmOn(getActivity())) {
            //change menu text to stop notification
            toggleNotification.setTitle(R.string.stop_notification);
        } else {
            toggleNotification.setTitle(R.string.start_notification);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_toggle_notification:
                boolean shouldStartAlarm = !GoldenHourService.isServiceAlarmOn(getActivity());
                GoldenHourService.setServiceAlarm(getActivity(), shouldStartAlarm);
                //refresh the menu to display the text change
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
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
            String sunsetString = getString(R.string.sunset_today, astroResponse.getSunset());
            String goldenHourString = getString(R.string.golden_hour_today, astroResponse
                    .getGoldenHour());
            mSunsetTextView.setText(sunsetString);
            mGoldenHourTextView.setText(goldenHourString);
            Log.i(TAG, "Date converted:" + astroResponse.getGoldenHourTime());

        }
    }


}
