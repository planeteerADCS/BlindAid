package com.planeteers.blindaid.gallery;

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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.planeteers.blindaid.R;
import com.planeteers.blindaid.helpers.Constants;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Jose on 11/9/15.
 */
public class GalleryFragment extends Fragment{
    public static String EXTRA_IMAGE_URI = "image_uri";

    @Bind(R.id.gallery_imageview)
    ImageView imageView;

    public static GalleryFragment newInstance(String imageUri){
        GalleryFragment fragment = new GalleryFragment();

        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_IMAGE_URI, imageUri);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);

        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String imageUrl = getArguments().getString(EXTRA_IMAGE_URI);

        if(imageUrl != null && !imageUrl.isEmpty()){
            Glide.with(getActivity())
                    .load(imageUrl)
                    .asBitmap()
                    .into(imageView);
        }
    }

}
