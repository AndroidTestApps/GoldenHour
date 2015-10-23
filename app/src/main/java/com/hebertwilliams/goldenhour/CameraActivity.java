package com.hebertwilliams.goldenhour;

import android.support.v4.app.Fragment;

/**
 * Created by kylehebert on 10/23/15.
 */
public class CameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CameraFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_single_fragment;
    }
}
