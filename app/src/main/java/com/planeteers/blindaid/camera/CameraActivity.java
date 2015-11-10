package com.planeteers.blindaid.camera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class CameraActivity extends SingleFragmentActivity {

	// adb shell am start -n com.bignerdranch.android.criminalintent/.CameraActivity
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected Fragment createFragment() {
		return new CameraFragment();
	}
}