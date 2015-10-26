package com.hebertwilliams.goldenhour;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylehebert on 10/23/15.
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

    private RecyclerView mRecyclerView;
    private List<FlickrPhoto> mFlickrPhotos = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new getPhotosTask().execute();

        //start the background thread for downloading thumbnails
        Handler responseHandler = new Handler(); //handles the response from the background task
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(),thumbnail);
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
        View view = inflater.inflate(R.layout.fragment_gallery, container,false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //make sure to kill the background thread
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        //confirm that the fragment has been attached to an activity and that getActivity will
        // not be null
        if(isAdded()) {
            mRecyclerView.setAdapter(new PhotoAdapter(mFlickrPhotos));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoImageView;

        public PhotoHolder(View photoView) {
            super(photoView);
            mPhotoImageView = (ImageView) photoView
                    .findViewById(R.id.fragment_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mPhotoImageView.setImageDrawable(drawable);
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
            //the ImageView will display a placeholder until images are downloaded
            Drawable placeholder = getResources().getDrawable(R.drawable.imageplaceholder);
            photoHolder.bindDrawable(placeholder);
            //replace the image with a thumbnail using a background thread
            mThumbnailDownloader.queueThumbnail(photoHolder,flickrPhoto.getUrl());
        }

        @Override
        public int getItemCount() {
            return mFlickrPhotos.size();
        }
    }

    private class getPhotosTask extends AsyncTask<Void,Void,List<FlickrPhoto>> {
        @Override
        protected List <FlickrPhoto> doInBackground(Void...params) {
            return new FlickrApiUtility().fetchPhotos();
        }

        @Override
        protected void onPostExecute(List<FlickrPhoto> flickrPhotos) {
            mFlickrPhotos = flickrPhotos;
            setupAdapter();
        }
    }
}
