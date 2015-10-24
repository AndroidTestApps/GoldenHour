package com.hebertwilliams.goldenhour;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kylehebert on 10/23/15.
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

    private RecyclerView mRecyclerView;

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
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        return view;
    }

    private class getPhotosTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void...params) {
            new FlickrApiUtility().fetchPhotos();
            return null;
        }
    }
}
