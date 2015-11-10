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

public class ImaggaService extends IntentService {

    private static final String APP_KEY = "acc_342b660b5ee8703";
    private static final String APP_SECRET = "68d3c33a96913081c869d6e4be8b02f";

    public ImaggaService() {
        super("ImaggaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            ClarifaiClient clarifai = new ClarifaiClient(APP_KEY, APP_SECRET);
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
