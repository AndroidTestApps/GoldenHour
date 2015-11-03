package com.hebertwilliams.goldenhour;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by kylehebert on 10/23/15.
 */
public class CameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ChoiceFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_single_fragment;
    }
}

 