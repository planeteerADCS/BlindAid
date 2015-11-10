//package com.planeteers.blindaid.face;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.google.android.gms.vision.CameraSource;
//import com.google.android.gms.vision.MultiDetector;
//import com.google.android.gms.vision.MultiProcessor;
//import com.google.android.gms.vision.barcode.BarcodeDetector;
//
//import java.io.IOException;
//
//import it.jaschke.alexandria.R;
//
///**
// * Created by flavius on 9/26/15.
// */
//public class FaceDetectActivity extends Activity implements BarcodeScannedListener {
//
//    public static final String EXTRA_BARCODE = "extra_barcode";
//    private CameraSourcePreview mPreview;
//    private CameraSource mCameraSource;
//    private GraphicOverlay mGraphicOverlay;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.barcode_scanner);
//
//        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
//        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);
//
//        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).build();
//        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
//        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
//
//        MultiDetector multiDetector = new MultiDetector.Builder()
//                .add(barcodeDetector)
//                .build();
//
//        if (!multiDetector.isOperational()) {
//            // Check for low storage.  If there is low storage, the native library will not be
//            // downloaded, so detection will not become operational.
//            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
//            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
//
//            if (hasLowStorage) {
//                Toast.makeText(this, "There is insufficient storage available on your device to download barcode dependencies.", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Could not set up the barcode detector. Please make sure you are online in order to complete setup", Toast.LENGTH_LONG).show();
//            }
//            finish();
//            return;
//        }
//
//        mCameraSource = new CameraSource.Builder(getApplicationContext(), multiDetector)
//                .setFacing(CameraSource.CAMERA_FACING_BACK)
//                .setRequestedPreviewSize(1600, 1024)
//                .build();
//    }
//
//    private void startCameraSource() {
//        try {
//            mPreview.start(mCameraSource, mGraphicOverlay);
//        } catch (IOException e) {
//            mCameraSource.release();
//            mCameraSource = null;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        startCameraSource();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mPreview.stop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mCameraSource != null) {
//            mCameraSource.release();
//        }
//    }
//
//    @Override
//    public void onBarcodeFound(String barcode) {
//        Intent result = new Intent();
//        result.putExtra(EXTRA_BARCODE, barcode);
//        setResult(Activity.RESULT_OK, result);
//        finish();
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        setResult(Activity.RESULT_CANCELED);
//    }
//}
