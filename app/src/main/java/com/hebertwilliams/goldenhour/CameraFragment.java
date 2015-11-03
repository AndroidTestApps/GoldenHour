package com.hebertwilliams.goldenhour;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kylehebert on 10/23/15.
 */
public class CameraFragment extends Fragment {

    private CameraExtraction mCameraExtraction;
    Camera mCamera;
    int mNumberOfCameras;
    int cameraId;
    int rotation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraExtraction = new CameraExtraction(
                this.getActivity().getBaseContext(),
                this.getActivity().getWindowManager().getDefaultDisplay().getRotation()
        );

        //get the number of useable cameras
        mNumberOfCameras = Camera.getNumberOfCameras();

        //get camera id for rear facing camera
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
                for (int x = 0; x < mNumberOfCameras; x++) {
                    Camera.getCameraInfo(x, cameraInfo);
                    if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        cameraId = x;
                    }
                }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
                             savedInstanceState) {
        return mCameraExtraction;
    }

    @Override
    public void onResume() {
        super.onResume();

        //use mCurrentCamera to selecet the camera desired to safely restore
        //the fragment after the camera has been changed
        mCamera = Camera.open(cameraId);
        mCameraExtraction.setCamera(mCamera);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    public enum CameraViewMode {
        Inner,
        Outer
    }
}