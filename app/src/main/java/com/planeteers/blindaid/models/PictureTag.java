package com.planeteers.blindaid.models;


import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.planeteers.blindaid.helpers.Constants;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PictureTag {

    public String tagName;
    public Double confidence;

    public PictureTag(String tagName, Double confidence){
        this.tagName = tagName;
        this.confidence = confidence;
    }
    public static class ImaggaDeserializer implements JsonDeserializer<List> {

        @Override
        public List<PictureTag> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            List<PictureTag> imaggaList = new ArrayList<>();

            if (object.has("results")) {
                JsonArray array = object.get("results").getAsJsonArray();

                for (JsonElement element : array) {
                    JsonObject image = element.getAsJsonObject();
                    JsonArray tags = image.get(Constants.IMAGGA.TAGS_KEY).getAsJsonArray();

                    for (JsonElement tagElement : tags) {
                        JsonObject tag = tagElement.getAsJsonObject();

                        if (tag.has(Constants.IMAGGA.CONFIDENCE_KEY) && tag.has(Constants.IMAGGA.TAG_KEY)) {
                            Double confidence = tag.get(Constants.IMAGGA.CONFIDENCE_KEY).getAsDouble();
                            String tagName = tag.get(Constants.IMAGGA.TAG_KEY).getAsString();
                            imaggaList.add(new PictureTag(tagName, confidence));
                        }
                    }
                }
            }
            return imaggaList;
        }
    }
}
