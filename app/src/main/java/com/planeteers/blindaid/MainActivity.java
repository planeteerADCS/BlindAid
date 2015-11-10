package com.planeteers.blindaid;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import com.planeteers.blindaid.camera.CameraActivity;
import com.planeteers.blindaid.camera.CameraFragment;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.services.ClarifaiService;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE_CAMERA = 134;

    @Bind(R.id.cameraFeedButton)
    Button cameraFeedButton;
    @Bind(R.id.faceDetectButton)
    Button faceDetectButton;
    @Bind(R.id.previewImage)
    ImageView mPreviewImage;
    private TextToSpeech mTts;
    private BroadcastReceiver mTrackDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<String> tags = intent.getStringArrayListExtra(Constants.KEY.TAG_LIST_KEY);
            ArrayList<String> tagNames = new ArrayList<>();
            ArrayList<Double> tagProbs = new ArrayList<>();

            for (String tag : tags) {
                String[] tagParts = tag.split(":");
                tagNames.add(tagParts[0]);
                tagProbs.add(Double.parseDouble(tagParts[1]));
            }

            talkBack(tagNames, tagProbs);
        }
    };

    @OnClick(R.id.cameraFeedButton)
    public void onCameraButtonClicked(View v) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, REQUEST_CODE_CAMERA);
    }

    @OnClick(R.id.faceDetectButton)
    public void onFaceDetectButtonClicked(View v) {
//        Intent i = new Intent(this, FaceDetectActivity.class);
//        startService(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mTrackDataReceiver,
                new IntentFilter(Constants.FILTER.RECEIVER_INTENT_FILTER));

        ButterKnife.bind(this);
    }

    @Override
    public void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTrackDataReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        // Reregister since the activity is visible
        LocalBroadcastManager.getInstance(this).registerReceiver(mTrackDataReceiver,
                new IntentFilter(Constants.FILTER.RECEIVER_INTENT_FILTER));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; L<this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME);
                String fullPath = getFilesDir() + "/" + path;
                Log.d("Camera", "wrote file to: " + path);

                File image = new File(fullPath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                mPreviewImage.setImageDrawable(bitmapDrawable);

                this.startService(getServiceIntent(Constants.ACTION.START_CLARIFAI_ACTION).setData(
                        Uri.parse(fullPath)
                ));
            }
        }
    }

    private void talkBack(final List<String> tagNames, final List<Double> tagProbs) {
        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.ERROR) {
                    Timber.e("error", TextToSpeech.ERROR);
                } else {
                    String speakString = "";
                    for (int i = 0; i < tagNames.size(); i++) {
                        speakString += "In view " + tagNames.get(i) + " Certainty " + tagProbs.get(i) + ". ";
                    }
                    mTts.speak(speakString, 0, null, null);
                }
            }
        });
    }

    private Intent getServiceIntent(String action) {
        Intent serviceIntent = new Intent(this, ClarifaiService.class);
        serviceIntent.setAction(action);
        return serviceIntent;
    }
}