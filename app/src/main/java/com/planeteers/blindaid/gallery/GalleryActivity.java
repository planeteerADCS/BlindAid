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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
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
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.planeteers.blindaid.R;
import com.planeteers.blindaid.base.TalkActivity;
import com.planeteers.blindaid.camera.CameraFragment;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.models.PictureTag;
import com.planeteers.blindaid.tasks.ImageTaggingTasks;
import com.planeteers.blindaid.util.ImageUtil;
import com.planeteers.blindaid.util.TagMerger;
import com.planeteers.blindaid.view.BlindViewUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.subscriptions.CompositeSubscription;
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

    public final static String INSTRUCTIONS = "Swipe to the left to go to the next picture. " +
            "Swipe to the right to go the previous picture. Swipe down to go back.";

    GalleryPagerAdapter mGalleryPagerAdapter;

    @Bind(R.id.gallery_view_pager)
    ViewPager viewPager;

    @Bind(R.id.gallery_blind_nav_view)
    FrameLayout blindNavView;

    @Bind(R.id.camera_progressContainer)
    View mProgressContainer;

    @Bind(R.id.tag_textview)
    TextView tagTextView;

    private boolean mVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        mVisible = true;

        viewPager.setOffscreenPageLimit(2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/robotoslab_light.ttf");
        tagTextView.setTypeface(typeface);

        String htmlInstructions = INSTRUCTIONS.replace(".", ".<br/> <br/>");
        tagTextView.setText(Html.fromHtml(htmlInstructions));

        tagTextView.setContentDescription(INSTRUCTIONS);

        mProgressContainer.setVisibility(View.INVISIBLE);

        setTagsRetrievedListener(new TagsRetrievedListener() {
            @Override
            public void onTagsRetrieved(ArrayList<String> tagNames, ArrayList<Double> tagProbs) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < tagNames.size(); i++) {

                    if (i < 7){
                        builder.append(tagNames.get(i)).append(", ");
                    }
                }

                talkBack(builder.toString());
            }
        });

        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(getApplication());
        imageAsyncTask.execute();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        BlindViewUtil blindViewUtil = new BlindViewUtil(new BlindViewUtil.BlindNavGestureListener() {
            @Override
            public boolean onSwipeLeft() {
                Timber.d("OnSwipeLeft Called");

                int currentItem = viewPager.getCurrentItem();
                if (currentItem != 0) {
                    viewPager.setCurrentItem(currentItem - 1);
                }
                return false;
            }

            @Override
            public boolean onSwipeRight() {
                Timber.d("OnSwipeRight Called");
                int currentItem = viewPager.getCurrentItem();
                if (mGalleryPagerAdapter.getCount() > currentItem) {
                    viewPager.setCurrentItem(currentItem + 1);
                }
                return false;
            }

            @Override
            public boolean onSwipeUp() {
                Timber.d("OnSwipeUp Called");
                return false;
            }

            @Override
            public boolean onSwipeDown() {
                Timber.d("OnSwipeDown Called");
                finish();
                return false;
            }

            @Override
            public boolean onClick() {
                Timber.d("OnClick Called");
                return false;
            }
        });

        blindNavView.setOnTouchListener(blindViewUtil.blindTouchListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                talkBack(INSTRUCTIONS);

            }
        }, 1500);
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
            //Make the first fragment an empty one for the instructions
            imageArray.add(0, "");

            mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), imageArray);
            viewPager.setAdapter(mGalleryPagerAdapter);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    String imageUrl = imageArray.get(position);
                    mProgressContainer.setVisibility(View.VISIBLE);
                    processImage(imageUrl);
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

    private void processImage(String imageUrl) {
        File image = new File(imageUrl);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        ExifInterface exif= null;
        try {
            exif = new ExifInterface(image.toString());

            Log.d("EXIF value: %s", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                bitmap = ImageUtil.rotate(bitmap, 90);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                bitmap = ImageUtil.rotate(bitmap, 270);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                bitmap = ImageUtil.rotate(bitmap, 180);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                bitmap = ImageUtil.rotate(bitmap, 90);
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
                                    public List<PictureTag> call(List<PictureTag> clarifai, List<PictureTag> imaggas, List<PictureTag> alyen) {
                                        return TagMerger.mergeTags(clarifai, imaggas, alyen);
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

                                            if (i < Constants.TAG_MERGER.MAX_PICTAG_SIZE - 1) {
                                                builder.append(tagNames.get(i).tagName).append(", ");
                                            } else if (i == Constants.TAG_MERGER.MAX_PICTAG_SIZE - 1) {
                                                builder.append(tagNames.get(i).tagName);
                                            }
                                        }


                                        mProgressContainer.setVisibility(View.GONE);
                                        String newLinedTags = builder.toString().replace(", ", "<br/>");
                                        tagTextView.setText(Html.fromHtml(newLinedTags));
                                        tagTextView.setContentDescription(builder.toString());


                                        talkBack("There is "+builder.toString());

                                    }
                                });

                    }
                });
            }
        });
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
