package com.hebertwilliams.goldenhour;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by kylehebert on 10/23/15.
 */
public class CameraFragment extends Fragment {

    Button takePictureButton;
    ImageView pictureImageView;

    private static final int TAKE_PICTURE_REQUEST = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        takePictureButton = (Button) v.findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                Context context = getActivity();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
                }
            }
        });

        pictureImageView = (ImageView) v.findViewById(R.id.picture_image_view);

        return v;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == -1 && requestCode == TAKE_PICTURE_REQUEST) {
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap) extras.get("data");
            pictureImageView.setImageBitmap(bmp);

        }
}
}

