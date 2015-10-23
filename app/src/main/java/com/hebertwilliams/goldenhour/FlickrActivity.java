package com.hebertwilliams.goldenhour;

import android.support.v4.app.Fragment;

/**
 * Created by kylehebert on 10/23/15.
 */
public class FlickrActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GalleryFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_single_fragment;
    }
}
