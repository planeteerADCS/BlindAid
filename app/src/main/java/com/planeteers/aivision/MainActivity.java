package com.planeteers.aivision;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.planeteers.aivision.base.TalkActivity;
import com.planeteers.aivision.camera.CameraActivity;
import com.planeteers.aivision.gallery.GalleryActivity;
import com.planeteers.aivision.obstacle.ObstacleDetection;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends TalkActivity {
    public final static int REQUEST_CODE_CAMERA = 134;

    @Bind(R.id.cameraFeedButton)
    Button cameraFeedButton;
    @Bind(R.id.faceDetectButton)
    Button faceDetectButton;
    @Bind(R.id.previewImage)
    ImageView mPreviewImage;
    private TextToSpeech mTts;

    Context mContext;

    // launch camera
    @OnClick(R.id.cameraFeedButton)
    public void onCameraButtonClicked(View v) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, REQUEST_CODE_CAMERA);
    }

    //
    @OnClick(R.id.openCvButton)
    public void onOpenCvButtonClicked(View v) {
        Intent i = new Intent(this, ObstacleDetection.class);
        startActivity(i);
    }

    //
    @OnClick(R.id.galleryButton)
    public void onGalleryButtonClicked(View v){
        Intent i = new Intent(this, GalleryActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


}