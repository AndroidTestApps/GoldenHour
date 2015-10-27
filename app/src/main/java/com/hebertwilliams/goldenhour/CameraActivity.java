package com.hebertwilliams.goldenhour;

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

//
//    Button takePictureButton;
//    ImageView pictureImageView;
//
//    private static final int TAKE_PICTURE_REQUEST = 0;
//
//    final String filename = "temp_photo.jpg";
//    Uri imageFileUri;
//
//    private static final String PICTURE_TO_DISPLAY = "picture has been taken";
//    boolean pictureToDisplay = false;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera);
//
//
//        pictureImageView = (ImageView) findViewById(R.id.picture_image_view);
//
//        takePictureButton = (Button) findViewById(R.id.take_picture_button);
//        takePictureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                File file = new File(Environment.getExternalStorageDirectory(), filename);
//                imageFileUri = Uri.fromFile(file);
//
//                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
//
//                if (pictureIntent.resolveActivity(CameraActivity.this.getPackageManager()) != null) {
//                   startActivityForResult(pictureIntent, TAKE_PICTURE_REQUEST);
//                } else {
//                    Toast.makeText(CameraActivity.this, "No camera available", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_REQUEST) {
//            pictureToDisplay = true;
//
//            //Request new picture is added to device's media store
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            File file = new File(Environment.getExternalStorageDirectory(), filename);
//            imageFileUri = Uri.fromFile(file);
//            mediaScanIntent.setData(imageFileUri);
//            sendBroadcast(mediaScanIntent);
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outBundle) {
//        outBundle.putBoolean(PICTURE_TO_DISPLAY, pictureToDisplay);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && pictureToDisplay) {
//            Bitmap image = scaleBitmap();
//            pictureImageView.setImageBitmap(image);
//        }
//    }
//
//
//    Bitmap scaleBitmap () {
//        // * Scale picture taken to fit into the ImageView */
//
//        //gather height and width of imageview
//        int imageViewHeight = pictureImageView.getHeight();
//        int imageViewWidth = pictureImageView.getWidth();
//
//        //decode file to find out how large the image is.
//        //Set the inJustDecodeBounds flag to true,
//        //the picture is decoded and stored in bOptions
//        BitmapFactory.Options bOptions = new BitmapFactory.Options();
//        bOptions.inJustDecodeBounds = true;
//        File file = new File(Environment.getExternalStorageDirectory(), filename);
//        Uri imageFileUri = Uri.fromFile(file);
//        String photoFilePath = imageFileUri.getPath();
//        BitmapFactory.decodeFile(photoFilePath, bOptions);
//
//        //What size is the picture?
//        int pictureHeight = bOptions.outHeight;
//        int pictureWidth = bOptions.outWidth;
//
//        //calculate resize
//        int scaleFactor = Math.min(pictureHeight / imageViewHeight, pictureWidth / imageViewWidth);
//
//        //Decode the image file into a new bitmap, scaled to fit the ImageView
//        bOptions.inJustDecodeBounds = false;
//        bOptions.inSampleSize = scaleFactor;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath, bOptions);
//        return bitmap;
//   }

    @Override
    protected Fragment createFragment() {
        return new CameraFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_single_fragment;
    }
}

