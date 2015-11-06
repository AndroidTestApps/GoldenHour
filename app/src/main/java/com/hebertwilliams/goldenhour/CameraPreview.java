package com.hebertwilliams.goldenhour;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by Josiah Williams on 11/2/2015.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super (context);

        mCamera = camera;

        //callback so we're notified when the surface is created or destroyed
        mHolder = getHolder();
        mHolder.addCallback(this);
        //for versions < 3
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId,
                                                   android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; //compensates mirror
        } else { //backfacing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //tells the camera where to draw the view
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException ioe) {
            Log.d(TAG, "Error setting camera preview: " + ioe.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //Camera view released in activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        //TODO camera orientation
        if (mHolder.getSurface() == null) {
            //view doesn't exist
            return;
        }
        //stop the view before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            //nothing to stop
        }

        //TODO view size, rotate, reformat here
        //start preview with new settings
        try {
            //setCameraDisplayOrientation();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera view: " + e.getMessage());
        }
    }
}

