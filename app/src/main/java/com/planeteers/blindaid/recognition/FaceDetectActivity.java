package com.planeteers.blindaid.recognition;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.planeteers.blindaid.R;
import com.planeteers.blindaid.util.ContactsUtil;

import java.util.ArrayList;

import timber.log.Timber;

public class FaceDetectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        ArrayList<Uri> contactImageUris = ContactsUtil.getContactsImageUri(this);

        int uriArraySize = contactImageUris.size();

        for(int i = 0; i < uriArraySize; i++){
            Timber.d("Uri %d/%d: %s", i, uriArraySize, contactImageUris.get(i).toString());
        }
    }

}
