package com.hebertwilliams.goldenhour;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by kylehebert on 10/23/15.
 */
public class GalleryActivity extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return GalleryFragment.newInstance();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_single_fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //verify that Google Play Services is available
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                    REQUEST_ERROR, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //leave if services are unavailable
                            finish();
                        }
                    });

            errorDialog.show();
        }
    }
}
