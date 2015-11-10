package com.planeteers.blindaid.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.services.ClarifaiService;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Jose on 11/10/15.
 */
public class TalkActivity extends AppCompatActivity{

    private TextToSpeech mTts;

    private boolean textToSpeechReady = false;

    private TagsRetrievedListener tagsRetrievedListener;

    private BroadcastReceiver mTrackDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<String> tags = intent.getStringArrayListExtra(Constants.KEY.TAG_LIST_KEY);
            ArrayList<String> tagNames = new ArrayList<>();
            ArrayList<Double> tagProbs = new ArrayList<>();

            for (String tag : tags) {
                String[] tagParts = tag.split(":");
                tagNames.add(tagParts[0]);
                tagProbs.add(Double.parseDouble(tagParts[1]));
            }

            if(tagsRetrievedListener != null){
                tagsRetrievedListener.onTagsRetrieved(tagNames, tagProbs);
            }else{
                Timber.e("There is no TagsRetrievedListener set.");
            }
        }
    };

    /**
     * Callback interface to be used when retrieving tags from an image
     */
    public interface TagsRetrievedListener {
        void onTagsRetrieved(ArrayList<String> tagNames, ArrayList<Double> tagProbs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(mTrackDataReceiver,
                new IntentFilter(Constants.FILTER.RECEIVER_INTENT_FILTER));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeechReady = status == TextToSpeech.SUCCESS;
            }
        });

    }

    @Override
    public void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTrackDataReceiver);

        if(mTts != null && mTts.isSpeaking()){
            mTts.stop();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        // Reregister since the activity is visible
        LocalBroadcastManager.getInstance(this).registerReceiver(mTrackDataReceiver,
                new IntentFilter(Constants.FILTER.RECEIVER_INTENT_FILTER));
        super.onResume();
    }

    public static Intent getServiceIntent(Context context, String action) {
        Intent serviceIntent = new Intent(context, ClarifaiService.class);
        serviceIntent.setAction(action);
        return serviceIntent;
    }

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
