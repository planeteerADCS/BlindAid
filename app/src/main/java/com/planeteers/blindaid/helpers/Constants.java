package com.planeteers.blindaid.helpers;


public class Constants {
    public interface FILTER {
        String RECEIVER_INTENT_FILTER = "broadcast_receiver_filter";
    }

    public interface ACTION {
        String START_CLARIFAI_ACTION = "start_clarifai";
    }

    public interface KEY {
        String CLARIFAI_TAG_LIST_KEY = "clarifai_tag_list_key";
        String IMAGGA_TAG_LIST_KEY = "imagga_tag_list_key";
    }
}