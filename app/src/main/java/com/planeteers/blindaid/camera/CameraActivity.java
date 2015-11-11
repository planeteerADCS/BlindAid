package com.planeteers.blindaid.camera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

import com.planeteers.blindaid.R;
import com.planeteers.blindaid.base.TalkActivity;

public class CameraActivity extends TalkActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //equestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_fragment);

		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction()
					.add(R.id.fragmentContainer, fragment)
					.commit();
		}

	}

	protected Fragment createFragment() {
		return new CameraFragment();
	}

}
