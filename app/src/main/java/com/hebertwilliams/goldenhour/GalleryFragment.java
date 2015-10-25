package com.hebertwilliams.goldenhour;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new getPhotosTask().execute();
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

    private void setupAdapter() {
        //confirm that the fragment has been attached to an activity and that getActivity will
        // not be null
        if(isAdded()) {
            mRecyclerView.setAdapter(new PhotoAdapter(mFlickrPhotos));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View photoView) {
            super(photoView);
            mTitleTextView = (TextView) photoView;
        }

        public void bindFlickrPhoto(FlickrPhoto flickrPhoto) {
            mTitleTextView.setText(flickrPhoto.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<FlickrPhoto> mFlickrPhotos;

        public PhotoAdapter(List<FlickrPhoto> flickrPhotos) {
            mFlickrPhotos = flickrPhotos;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            FlickrPhoto flickrPhoto = mFlickrPhotos.get(position);
            photoHolder.bindFlickrPhoto(flickrPhoto);
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
