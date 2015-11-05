package com.hebertwilliams.goldenhour;

import android.support.v4.app.Fragment;

/**
 * Created by kylehebert on 11/3/15.
 */
public class DebugActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return DebugFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_single_fragment;
    }

}
