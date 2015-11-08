package com.hebertwilliams.goldenhour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hebertwilliams.goldenhour.api.FlickrApiUtility;
import com.hebertwilliams.goldenhour.model.FlickrPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylehebert on 10/23/15. Displays a gallery of photos retrieved
 * from Flickr based on recent nearby photos search or a recent photos
 * tagged"golden hour"
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

    private static final String GOLDEN_HOUR_QUERY = "golden hour";

    private RecyclerView mRecyclerView;
    private List<FlickrPhoto> mFlickrPhotos = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private GoogleApiClient mGoogleApiClient;
    private ProgressBar mProgressBar;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                        getLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        //start the background thread for downloading thumbnails
        Handler responseHandler = new Handler(); //handles the response from the background task
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        //make sure the Looper has been called
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return view;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onPause() {
        super.onPause();
        //make sure to kill the background thread
        mThumbnailDownloader.quit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //make sure to kill the background thread
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_gallery, menu);

        MenuItem goldenSearchItem = menu.findItem(R.id.menu_item_golden);
        MenuItem localSearchItem = menu.findItem(R.id.menu_item_local);

        //only want this enabled if the API client has connected
        goldenSearchItem.setEnabled(mGoogleApiClient.isConnected());
        localSearchItem.setEnabled(mGoogleApiClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_golden:
                new GetGoldenHourPhotosTask().execute();
                return true;
            case R.id.menu_item_local:
                getLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupAdapter() {
        //confirm that the fragment has been attached to an activity and that getActivity will
        // not be null
        if (isAdded()) {
            mRecyclerView.setAdapter(new PhotoAdapter(mFlickrPhotos));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPhotoImageView;
        private FlickrPhoto mFlickrPhoto;

        public PhotoHolder(View photoView) {
            super(photoView);
            mPhotoImageView = (ImageView) photoView
                    .findViewById(R.id.fragment_gallery_image_view);
            photoView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable) {
            mPhotoImageView.setImageDrawable(drawable);
        }

        public void bindFlickrPhoto(FlickrPhoto flickrPhoto) {
            mFlickrPhoto = flickrPhoto;
        }

        @Override
        public void onClick(View view) {
            Intent intent = PhotoPageActivity.newIntent(getActivity(), mFlickrPhoto
                    .getPhotoPageUri());
            startActivity(intent);
        }


    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<FlickrPhoto> mFlickrPhotos;

        public PhotoAdapter(List<FlickrPhoto> flickrPhotos) {
            mFlickrPhotos = flickrPhotos;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new PhotoHolder(view);

        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            FlickrPhoto flickrPhoto = mFlickrPhotos.get(position);
            photoHolder.bindFlickrPhoto(flickrPhoto);
            //the ImageView will display a placeholder until images are downloaded
            Drawable placeholder = getResources().getDrawable(R.drawable.imageplaceholder);
            photoHolder.bindDrawable(placeholder);
            //replace the image with a thumbnail using a background thread
            mThumbnailDownloader.queueThumbnail(photoHolder, flickrPhoto.getUrl());
        }

        @Override
        public int getItemCount() {
            return mFlickrPhotos.size();
        }
    }

    /*
    this method gets the devices location then passes it to GetPhotosTask
     */
    private void getLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setNumUpdates(1);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Got a location: " + location);
                        new GetPhotosTask().execute(location);
                    }
                });

    }

    /*
    AsyncTask for downloading photos based on a query of Golden Hour
     */
    private class GetGoldenHourPhotosTask extends AsyncTask<Void, Void, List<FlickrPhoto>> {

        @Override
        protected List<FlickrPhoto> doInBackground(Void... params) {
            String query = GOLDEN_HOUR_QUERY;
            return new FlickrApiUtility().getGoldenHourPhotos(query);
        }

        @Override
        protected void onPostExecute(List<FlickrPhoto> flickrPhotos) {
            mFlickrPhotos = flickrPhotos;
            setupAdapter();
        }

    }

    /*
    AsyncTask for downloading local photos
     */
    private class GetPhotosTask extends AsyncTask<Location, Void, List<FlickrPhoto>> {
        @Override
        protected List<FlickrPhoto> doInBackground(Location... params) {
            return new FlickrApiUtility().getLocalPhotos(params[0]);

        }

        @Override
        protected void onPostExecute(List<FlickrPhoto> flickrPhotos) {
            mFlickrPhotos = flickrPhotos;
            setupAdapter();
        }
    }
}
