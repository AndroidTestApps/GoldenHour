package com.hebertwilliams.goldenhour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

/**
 * Created by kylehebert on 10/31/15. Instead of a gallery fragment this fragment displays a single
 * display a single image based on location instead. Searches for location and golden hour weren't returning enough results to fill a gallery
 */
public class InspirationFragment extends Fragment {

    private static final String TAG = "InspirationFragment";

    private ImageView mInspirationImageView;
    private Button mSearchButton;
    private GoogleApiClient mGoogleApiClient;

    public static InspirationFragment newInstance() {
        return  new InspirationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        findPhoto();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inspiration, container, false);

        mInspirationImageView = (ImageView) view.findViewById(R.id.inspiration_image_view);

        mSearchButton = (Button) view.findViewById(R.id.search_button);

        //only enable this button if Google Play Services is available
        //mSearchButton.setEnabled(mGoogleApiClient.isConnected());
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();
            }
        });

        return view;
    }

    /*
used to find a single image based on location for InspirationFragment
 */
    private void findPhoto() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setNumUpdates(1);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Got a location: " + location);
                        new GetPhotoTask().execute(location);
                    }
                });
    }

    private class GetPhotoTask extends AsyncTask<Location, Void, Void> {
        private FlickrPhoto mFlickrPhoto;
        private Bitmap mBitmap;

        @Override
        protected Void doInBackground(Location...params) {
            FlickrApiUtility apiUtility = new FlickrApiUtility();
            List<FlickrPhoto> photos = apiUtility.getGoldenHourPhotos(params[0]);

            if (photos.size() == 0) {
                return null;
            }

            mFlickrPhoto = photos.get(0);

            try {
                byte[] bytes = apiUtility.getUrlBytes(mFlickrPhoto.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            } catch (IOException ioe) {
                Log.i(TAG, "Unable to download bitmap", ioe);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mInspirationImageView.setImageBitmap(mBitmap);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }



}
