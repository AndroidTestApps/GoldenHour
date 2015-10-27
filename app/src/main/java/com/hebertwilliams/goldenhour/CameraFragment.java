package com.hebertwilliams.goldenhour;

import android.content.Context;
import android.content.Intent;
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
import java.io.IOException;


/**
 * Created by kylehebert on 10/23/15.
 */
public class CameraFragment extends Fragment {

    Button takePictureButton;
    ImageView pictureImageView;

    private static final int TAKE_PICTURE_REQUEST = 0;

    final String filename = "temp_photo.jpg";
    Uri imageFileUri;

    private static final String PICTURE_TO_DISPLAY = "picture has been taken";
    boolean pictureToDisplay = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_choice, container, false);

        takePictureButton = (Button) v.findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                Context context = getActivity();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager() != null) {
                    startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
                }

                //takePhoto();
            }
        });

        pictureImageView = (ImageView) v.findViewById(R.id.picture_image_view);

        return v;
    }


    public void takePhoto() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            imageFileUri = Uri.fromFile(file);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
            startActivityForResult(pictureIntent, TAKE_PICTURE_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1 && requestCode == TAKE_PICTURE_REQUEST) {
            pictureToDisplay = true;

            //request new picture is added to device's media store
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            imageFileUri = Uri.fromFile(file);
            mediaScanIntent.setData(imageFileUri);


        }
}
}

