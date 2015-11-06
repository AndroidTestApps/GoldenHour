package com.hebertwilliams.goldenhour;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
        super(context);

        mCamera = camera;

        //callback so we're notified when the surface is created or destroyed
        mHolder = getHolder();
        mHolder.addCallback(this);
        //for versions < 3
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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

        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = getOptimalPreviewSize(w, h);
        Camera.Size optimalSize = getOptimalPreviewSize(getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels);
        parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);

        //start preview with new settings
        try {
            //setCameraDisplayOrientation();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera view: " + e.getMessage());
        }
    }

    private Camera.Size getOptimalPreviewSize(int width, int height) {
        Camera.Size optimalSize=null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (optimalSize==null) {
                    optimalSize=size;
                } else {
                    int resultArea=optimalSize.width*optimalSize.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        optimalSize=size;
                    }
                }
            }
        }
        return optimalSize;
    }
}

