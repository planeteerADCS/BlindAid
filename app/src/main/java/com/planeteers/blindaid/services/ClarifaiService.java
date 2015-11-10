package com.planeteers.blindaid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.planeteers.blindaid.helpers.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClarifaiService extends IntentService {

    private static final String APP_ID = "nOVRTkdFjshLLibO-vu5IxHi-vb0NU-u9jVxQLZ7";
    private static final String APP_SECRET = "eJeVpqilheUoEkow61tyZoW1HOihVbw1TjhXJlFa";

    public ClarifaiService() {
        super("ClarifaiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            ClarifaiClient clarifai = new ClarifaiClient(APP_ID, APP_SECRET);
            List<RecognitionResult> results =
                    clarifai.recognize(new RecognitionRequest(new File(intent.getData().toString())));

            ArrayList<String> tags = new ArrayList<>();
            for (Tag tag : results.get(0).getTags()) {
                tags.add(tag.getName() + ":" + tag.getProbability());
            }

            sendDataToReceivers(tags);
        }
    }


    private void sendDataToReceivers(ArrayList<String> tags) {
        Intent intent = new Intent(Constants.FILTER.RECEIVER_INTENT_FILTER);
        intent.putStringArrayListExtra(Constants.KEY.CLARIFAI_TAG_LIST_KEY, tags);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
