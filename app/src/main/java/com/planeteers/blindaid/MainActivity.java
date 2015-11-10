package com.planeteers.blindaid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.planeteers.blindaid.camera.CameraActivity;
import com.planeteers.blindaid.camera.CameraFragment;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE_CAMERA = 134;

    @Bind(R.id.cameraFeedButton)
    Button cameraFeedButton;
    @Bind(R.id.faceDetectButton)
    Button faceDetectButton;
    @Bind(R.id.previewImage)
    ImageView mPreviewImage;

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
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                Log.d("Camera", "wrote file to: " + path);

                File image = new File(getFilesDir() + "/" + path);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                mPreviewImage.setImageDrawable(bitmapDrawable);

                //todo launch background thread with clarifai request
                /**
                 * sample response
                 * for (Tag tag : results.get(0).getTags()) {
                 *     System.out.println(tag.getName() + ": " + tag.getProbability());
                 * }
                 */
                String[] tagNames = new String[] {
                        "keyboard", "happy", "horatio"
                };
                double[] tagProbs = new double[] {
                      0.8, 0.6, 0.5
                };

                talkBack(tagNames, tagProbs);
            }
        }
    }

    private void talkBack(String[] tagNames, double[] tagProbs) {
        
    }
}
