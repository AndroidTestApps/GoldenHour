package com.hebertwilliams.goldenhour;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by kylehebert on 10/23/15. Host activity for ChoiceFragment
 */
public class ChoiceActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, ChoiceActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return new ChoiceFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_single_fragment;
    }
}
