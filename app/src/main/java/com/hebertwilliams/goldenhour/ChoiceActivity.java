package com.hebertwilliams.goldenhour;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

/**
 * Created by kylehebert on 10/23/15.
 */
public class  ChoiceActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, ChoiceActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return new ChoiceFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_single_fragment;
    }
}
