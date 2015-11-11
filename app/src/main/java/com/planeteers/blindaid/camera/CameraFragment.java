package com.planeteers.blindaid.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.planeteers.blindaid.R;
import com.planeteers.blindaid.base.TalkActivity;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.models.PictureTag;
import com.planeteers.blindaid.tasks.ImageTaggingTasks;
import com.planeteers.blindaid.util.PermissionUtil;
import com.planeteers.blindaid.util.TagMerger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.functions.Func3;
import timber.log.Timber;


public class CameraFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback{

	private Camera mCamera;
	public static final String EXTRA_PHOTO_FILENAME =
			"photo_filename";
	public static final String EXTRA_PHOTO_ORIENTATION =
			"photo_orientation";
	private OrientationEventListener mOrientationEventListener;
	private int mOrientation = 1;
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};

	@Bind(R.id.camera_surfaceView)
	SurfaceView mSurfaceView;

	@Bind(R.id.camera_progressContainer)
	View mProgressContainer;

	@Bind(R.id.camera_takePictureButton)
	Button shutterButton;

	@OnClick(R.id.camera_takePictureButton)
	public void onShutterClicked(View v){
		if (mCamera != null) {
			mCamera.takePicture(mShutterCallback, null, mJpegCallback);
		}
	}

	private Handler autoShutterHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
		}
	};

	private static int cameraId = 0;

	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String filename = UUID.randomUUID().toString() + ".jpg";
			FileOutputStream os = null;
			boolean success = true;
			try {
				os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				os.write(data);
			} catch (Exception e) {
				Timber.e("Error writing to file " + filename);
				success = false;
			} finally {
				try {
					if (os != null)
						os.close();
				} catch (Exception e) {
					Timber.e("Error closing file " + filename);
					success = false;
				}
			}

			if (success) {
//				Timber.i(TAG, "JPEG saved at " + filename);
				processImage(filename);
				mCamera.startPreview();
			}
		}
	};
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);

		ButterKnife.bind(this, v);

		mProgressContainer.setVisibility(View.INVISIBLE);

		initializeSurfaceHolder();
		
		mOrientationEventListener = new OrientationEventListener(getActivity()) {
			@Override
			public void onOrientationChanged(int orientation) {
				mOrientation = orientation;
			}
		};
		
		return v;
	}

	private void initializeSurfaceHolder() {
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		if(mCamera != null && !holder.isCreating()){
			setPreviewDisplay(holder);

			Rect surfaceFrame = holder.getSurfaceFrame();
			int width = surfaceFrame.width();
			int height = surfaceFrame.height();

			Camera.Parameters parameters = mCamera.getParameters();
			Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
			parameters.setPreviewSize(s.width, s.height);
			s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
			parameters.setPictureSize(s.width, s.height);
			mCamera.setParameters(parameters);
			mCamera.setDisplayOrientation(90);

			try {
				mCamera.startPreview();
			} catch (Exception e) {
				Timber.e("Could not start preview");
				mCamera.release();
				mCamera = null;
			}
		}

		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				setPreviewDisplay(holder);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
									   int height) {
				if (mCamera == null) return;
				Camera.Parameters parameters = mCamera.getParameters();
				List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
				Camera.Size optimalSize =
						getOptimalPreviewSize(
								sizes,
								getResources().getDisplayMetrics().widthPixels,
								getResources().getDisplayMetrics().heightPixels
						);

				parameters.setPictureSize(optimalSize.width, optimalSize.height);
				try {
					mCamera.setParameters(parameters);
				}catch (Exception e){
					Timber.e(e, e.getMessage());

					parameters = mCamera.getParameters();
					Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
					parameters.setPreviewSize(s.width, s.height);
					s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
					parameters.setPictureSize(s.width, s.height);
					mCamera.setParameters(parameters);
				}
				mCamera.setDisplayOrientation(90);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Timber.e("Could not start preview");
					mCamera.release();
					mCamera = null;
				}
			}
		});
	}

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w/h;

		if (sizes==null) return null;

		Camera.Size optimalSize = null;

		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Find size
		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	private void setPreviewDisplay(SurfaceHolder holder) {
		try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Timber.e("Error setting up preview display: %s", exception.getMessage());
        }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.unbind(this);
	}

	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance(){
		Camera c = null;
		if(PermissionUtil.Camera.checkPermissionIfNotAsk(this, Manifest.permission.CAMERA)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				int backFacingCameraId = findBackFacingCameraId();
				Timber.d("Camera Id: %d", backFacingCameraId);
				c = Camera.open(backFacingCameraId);
			} else {
				c = Camera.open();
			}
		}

		return c; // returns null if camera is unavailable
	}

	private static int findBackFacingCameraId() {

		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
	
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int bestArea = bestSize.width * bestSize.height;
		for (int i = 1; i < sizes.size(); i++) {
			Size currentSize = sizes.get(i);
			int area = currentSize.height * currentSize.width;
			if (area > bestArea) {
				bestArea = area;
				bestSize = currentSize;
			}
		}
		return bestSize;
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onResume() {
		super.onResume();
		mOrientationEventListener.enable();
		mCamera = getCameraInstance();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		mOrientationEventListener.disable();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
			if(requestCode == PermissionUtil.Camera.PERMISSION_REQUEST_CODE) {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					mCamera = getCameraInstance();
					initializeSurfaceHolder();
				} else {
					// Permission Denied
					Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_SHORT)
							.show();
				}
			}else {
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			}
	}

	public void processImage(String fileName){
		String fullPath = getActivity().getFilesDir() + "/" + fileName;
		Log.d("Camera", "wrote file to: " + fileName);

		File image = new File(fullPath);
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

		ExifInterface exif= null;
		try {
			exif = new ExifInterface(image.toString());

			Log.d("EXIF value: %s", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
			if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
				bitmap = rotate(bitmap, 90);
			} else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
				bitmap = rotate(bitmap, 270);
			} else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
				bitmap = rotate(bitmap, 180);
			} else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
				bitmap = rotate(bitmap, 90);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// display image taken
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
		//mPreviewImage.setImageDrawable(bitmapDrawable);


		// compress image
		final ParseObject imageParseObject = new ParseObject("Image");

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
		byte[] imageData = stream.toByteArray();
		ParseFile imageParseFile = new ParseFile("image.jpeg", imageData);

		// save compressed image for upload
		imageParseObject.put("image", imageParseFile);
		imageParseObject.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				String imageObjectId = imageParseObject.getObjectId();
				Timber.d("imageObjectID: %s", imageObjectId);
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");

				query.getInBackground(imageObjectId, new GetCallback<ParseObject>() {
					@Override
					public void done(ParseObject parseObject, ParseException e) {
						if(e != null){
							Timber.e(e, e.getMessage());
							return;
						}

						String imageUrl = parseObject.getParseFile("image").getUrl();
						Log.v("image url:", imageUrl);


						Observable.zip(
								ImageTaggingTasks.getClarifaiTags(imageUrl),
								ImageTaggingTasks.getImaggaTags(imageUrl),
                                ImageTaggingTasks.getAlyienTags(imageUrl),
								new Func3<List<PictureTag>, List<PictureTag>, List<PictureTag>, List<PictureTag>>() {
									@Override
									public List<PictureTag> call(List<PictureTag> clarifai, List<PictureTag> imaggas, List<PictureTag> alyien) {
										return TagMerger.mergeTags(clarifai, imaggas, alyien);
									}
								}
						)
								.observeOn(AndroidSchedulers.mainThread())
								.subscribe(new Subscriber<List<PictureTag>>() {
									@Override
									public void onCompleted() {

									}

									@Override
									public void onError(Throwable e) {
										Timber.e(e, e.getMessage());
									}

									@Override
									public void onNext(List<PictureTag> tagNames) {
										StringBuilder builder = new StringBuilder();
										for (int i = 0; i < tagNames.size(); i++) {

											if (i < Constants.TAG_MERGER.MAX_PICTAG_SIZE) {
												builder.append(tagNames.get(i).tagName).append(", ");
											}
										}

										if(getActivity() != null) {
											mProgressContainer.setVisibility(View.GONE);

											TalkActivity talkActivity = (TalkActivity) getActivity();
											talkActivity.talkBack(builder.toString());
										}
									}
								});

					}
				});
			}
		});

	}

	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}
}
