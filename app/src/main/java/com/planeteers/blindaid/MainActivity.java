package com.planeteers.blindaid;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.planeteers.blindaid.base.TalkActivity;
import com.planeteers.blindaid.camera.CameraActivity;
import com.planeteers.blindaid.camera.CameraFragment;
import com.planeteers.blindaid.gallery.GalleryActivity;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.models.PictureTag;
import com.planeteers.blindaid.obstacle.ObstacleDetection;
import com.planeteers.blindaid.recognition.FaceDetectActivity;
import com.planeteers.blindaid.tasks.ImageTaggingTasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import timber.log.Timber;

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
    @OnClick(R.id.faceDetectButton)
    public void onFaceDetectButtonClicked(View v) {
        Intent i = new Intent(this, FaceDetectActivity.class);
        startActivity(i);
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