package com.planeteers.aivision.base;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Jose on 11/10/15.
 */
public class TalkActivity extends AppCompatActivity{

    private TextToSpeech mTts;

    private boolean textToSpeechReady = false;

    private TagsRetrievedListener tagsRetrievedListener;

    /**
     * Callback interface to be used when retrieving tags from an image
     */
    public interface TagsRetrievedListener {
        void onTagsRetrieved(ArrayList<String> tagNames, ArrayList<Double> tagProbs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeechReady = status == TextToSpeech.SUCCESS;

                mTts.setSpeechRate(0.85f);
            }
        });




    }

    @Override
    public void onPause() {
        // Unregister since the activity is not visible
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mTts != null){
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        // Reregister since the activity is visible
        super.onResume();
    }


    // say it out loud
    public void talkBack(String message) {
        if(textToSpeechReady) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                mTts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        }else{
            Timber.e("Text To Speech engine is not ready. Make sure it is being initialized first!!!");
        }
    }

    public void setTagsRetrievedListener(TagsRetrievedListener tagsRetrievedListener) {
        this.tagsRetrievedListener = tagsRetrievedListener;
    }
}
