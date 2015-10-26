package com.hebertwilliams.goldenhour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by kylehebert on 10/23/15.
 */
public class ChoiceFragment extends Fragment {

    private Button cameraActivityButton;
    private Button galleryFragmentButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_choice, container, false);

        cameraActivityButton = (Button) v.findViewById(R.id.camera_fragment_button);
        cameraActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChoiceFragment.this, CameraActivity.class);
                startActivity(i);
            }
        });
        galleryFragmentButton = (Button) v.findViewById(R.id.gallery_fragment_button);


        return v;
    }
}
