package com.planeteers.blindaid.helpers;


public class Constants {
    public interface FILTER {
        String RECEIVER_INTENT_FILTER = "broadcast_receiver_filter";
    }

    public interface PARSE {
        String PARSE_APP_ID = "3reP8WePd3FG2VwFnrxQLZL1zbsNPB3a0oMQMIJ1";
        String PARSE_CLIENT_KEY =  "oMxr6E8aUtB6YeGWC2vxIlaGfk7WarQ82N4066SH";
    }

    public interface TAG_MERGER {
        int MAX_PICTAG_SIZE = 7;
    }

    public interface ACTION {
        String START_CLARIFAI_ACTION = "start_clarifai";
        String START_IMAGGA_ACTION = "start_imagga";
    }

    public interface KEY {
        String CLARIFAI_TAG_LIST_KEY = "clarifai_tag_list_key";
        String IMAGGA_TAG_LIST_KEY = "imagga_tag_list_key";
    }

    public interface CLARIFAI {
        String APP_ID = "nOVRTkdFjshLLibO-vu5IxHi-vb0NU-u9jVxQLZ7";
        String APP_SECRET = "eJeVpqilheUoEkow61tyZoW1HOihVbw1TjhXJlFa";
    }

    public interface IMAGGA {
        String AUTHORIZATION = "Basic YWNjXzM0MmI2NjBiNWVlODcwMzpiNjhkM2MzM2E5NjkxMzA4MWM4NjlkNmU0YmU4YjAyZg==";
        String API_URL = "http://api.imagga.com/";
        String TAGS_KEY = "tags";
        String TAG_KEY = "tag";
        String CONFIDENCE_KEY = "confidence";
    }
}