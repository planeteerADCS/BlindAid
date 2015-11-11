package com.planeteers.blindaid.util;

import android.util.Log;

import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.models.PictureTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class TagMerger {

    public interface Callback {
        public void onComplete(List<String> tagResults);
    }


    public List<PictureTag> mergeTags(List<String> list1, List<String> list2) {

        List<PictureTag> list1PicTags = convertToPictureTags(list1);
        List<PictureTag> list2PicTags = convertToPictureTags(list2);

        logPictureTagList(list1PicTags);
        logPictureTagList(list1PicTags);

        normalizeList(list1PicTags);
        normalizeList(list2PicTags);

        HashMap<String, Double> pictureMap = new HashMap<>();

        for(int i = 0; i < Constants.TAG_MERGER.MAX_PICTAG_SIZE; i++) {
            PictureTag picTag1 = list1PicTags.get(i);
            evaluateKey(pictureMap, picTag1.tagName, picTag1.confidence);

            PictureTag picTag2 = list2PicTags.get(i);
            evaluateKey(pictureMap, picTag2.tagName, picTag2.confidence);
        }


        List<PictureTag> mergedList = new ArrayList<>();
        for (String tag : pictureMap.keySet()) {
            mergedList.add(new PictureTag(tag, pictureMap.get(tag)));
        }

        Collections.sort(mergedList, new Comparator<PictureTag>() {
            @Override
            public int compare(PictureTag lhs, PictureTag rhs) {
                if (lhs.confidence > rhs.confidence) return 1;
                else return -1;
            }
        });

        logPictureTagList(mergedList);
        return mergedList;
    }


    private void evaluateKey(HashMap<String, Double> map, String tag, Double confidence) {
        if (map.containsKey(tag)) {
            Double oldConfidence = map.get(tag);
            map.put(tag, oldConfidence + confidence);
        } else { map.put(tag, confidence); }
    }

    private void logPictureTagList(List<PictureTag> list) {
        for (PictureTag picTag : list) {
            Log.v("TagMerger", "Logging PicTagList...");
            Log.v("TagMerger", "");
            Log.v("TagMerger", "");

            Log.v("TagMerger", "TagName: " + picTag.tagName + " | " + " Confidence: " + picTag.confidence);
            Log.v("TagMerger", "Finished Logging PicTagList...");
        }
    }


    private void normalizeList(List<PictureTag> pictureTags) {
        double maxConfidence = pictureTags.get(0).confidence;
        for (int i = 0; i < Constants.TAG_MERGER.MAX_PICTAG_SIZE; i++) {
            if (pictureTags.get(i) == null) break;
            pictureTags.get(i).confidence = pictureTags.get(i).confidence / maxConfidence;
        }
    }


    private List<PictureTag> convertToPictureTags(List<String> list) {
        List<PictureTag> picTags = new ArrayList<>();

        for (String tag : list) {
            String[] tagParts = tag.split(":");
            picTags.add(new PictureTag(tagParts[0], Double.parseDouble(tagParts[1])));
        }

        return picTags;
    }
}
