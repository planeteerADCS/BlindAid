package com.planeteers.blindaid.gallery;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.planeteers.blindaid.R;
import com.planeteers.blindaid.base.TalkActivity;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.services.ClarifaiService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GalleryActivity extends TalkActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    GalleryPagerAdapter mGalleryPagerAdapter;

    @Bind(R.id.gallery_view_pager)
    ViewPager viewPager;

    private boolean mVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        mVisible = true;

        viewPager.setOffscreenPageLimit(2);

        setTagsRetrievedListener(new TagsRetrievedListener() {
            @Override
            public void onTagsRetrieved(ArrayList<String> tagNames, ArrayList<Double> tagProbs) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < tagNames.size(); i++) {

                    if (i < 7){
                        if(i == 0){
                            builder.append("There is ");
                        }else{
                            builder.append(" there's also ");
                        }
                        builder.append(tagNames.get(i));
                    }
                }

                talkBack(builder.toString());
            }
        });

        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(getApplication());
        imageAsyncTask.execute();

    }

    private class GalleryPagerAdapter extends FragmentStatePagerAdapter{

        ArrayList<String> imageArray;

        public GalleryPagerAdapter(FragmentManager fm, ArrayList<String> images) {
            super(fm);

            this.imageArray = images;
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {

            return GalleryFragment.newInstance(imageArray.get(position));
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return imageArray.size();
        }
    }

    public static ArrayList<String> getImagesPath(Context appContext) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        Cursor cursor;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = appContext.getContentResolver().query(uri, projection, null,
                null, null);

        if(cursor != null) {
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(columnIndexData);

                listOfAllImages.add(imagePath);
            }

            cursor.close();
        }
        return listOfAllImages;
    }

    private void setImageArray(final ArrayList<String> imageArray){
        if(viewPager != null){
            mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), imageArray);
            viewPager.setAdapter(mGalleryPagerAdapter);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    String imageUrl = imageArray.get(position);

                    startService(TalkActivity.getServiceIntent(GalleryActivity.this,
                            Constants.ACTION.START_CLARIFAI_ACTION).setData(Uri.parse(imageUrl)
                    ));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            if(!imageArray.isEmpty()) {
                viewPager.setCurrentItem(0, false);
            }
        }
    }

    public class ImageAsyncTask extends AsyncTask<Void, Void, ArrayList<String>>{

        private Context context;

        public ImageAsyncTask(Context appContext){
            this.context = appContext;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            return getImagesPath(context);
        }

        @Override
        protected void onPostExecute(ArrayList<String> imageArray) {
            super.onPostExecute(imageArray);

            setImageArray(imageArray);
        }
    }


}
