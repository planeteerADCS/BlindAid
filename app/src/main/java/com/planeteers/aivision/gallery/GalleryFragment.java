package com.planeteers.aivision.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.planeteers.aivision.R;

import butterknife.Bind;
import butterknife.ButterKnife;

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
