package ru.android_2019.citycam.asyncTask.webcamJSONObject;

import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WebcamJSONReader {

    public static ArrayList<WebcamInfo> getWebcamList(String stringJSONResponse) throws IOException {
        return readJson(stringJSONResponse);
    }

    private static ArrayList<WebcamInfo> readJson(String inputString) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String name;
        ArrayList<WebcamInfo> webcamInfos = null;
        try (JsonReader reader = new JsonReader(inputStreamReader)) {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("result")) {
                    webcamInfos = readResult(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return webcamInfos;
    }

    private static ArrayList<WebcamInfo> readResult(JsonReader reader) throws IOException {
        String name;
        ArrayList<WebcamInfo> webcamInfos = null;
        int total = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("total")) {
                total = Integer.valueOf(reader.nextString());
            } else if (name.equals("webcams") && total != 0) {
                webcamInfos = readWebcamArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return webcamInfos;
    }

    private static ArrayList<WebcamInfo> readWebcamArray(JsonReader reader) throws IOException {
        ArrayList<WebcamInfo> webcamInfos = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            webcamInfos.add(readWebcamElement(reader));
        }
        reader.endArray();
        return webcamInfos;
    }

    private static WebcamInfo readWebcamElement(JsonReader reader) throws IOException {
        String name;
        String webcamId = null;
        String webcamTitle = null;
        ArrayList<Category> webcamCategory = null;
        String webcamImageURL = null;
        Location webcamLocation = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("id")) {
                webcamId = reader.nextString();
            } else if (name.equals("title")) {
                webcamTitle = reader.nextString();
            } else if (name.equals("category")) {
                webcamCategory = readWebcamCategoryArray(reader);
            } else if (name.equals("image")) {
                webcamImageURL = readWebcamImagePreview(reader);
            } else if (name.equals("location")) {
                webcamLocation = readWebcamLocation(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new WebcamInfo(webcamId, webcamTitle, webcamCategory, webcamLocation, webcamImageURL);
    }

    private static Location readWebcamLocation(JsonReader reader) throws IOException {
        String name;
        String locationRegion = null;
        String locationRegionCode = null;
        String locationTimeZone = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("region")) {
                locationRegion = reader.nextString();
            } else if (name.equals("region_code")) {
                locationRegionCode = reader.nextString();
            } else if (name.equals("timezone")) {
                locationTimeZone = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Location(locationRegion, locationRegionCode, locationTimeZone);
    }

    private static String readWebcamImagePreview(JsonReader reader) throws IOException {
        String name;
        String URLPreview = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("current")) {

                reader.beginObject();
                while (reader.hasNext()) {
                    String nameCurrent = reader.nextName();
                    if (nameCurrent.equals("preview")) {
                        URLPreview = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();

            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return URLPreview;
    }

    private static ArrayList<Category> readWebcamCategoryArray(JsonReader reader) throws IOException {
        String name;
        ArrayList<Category> categories = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            categories.add(readCategory(reader));
        }
        reader.endArray();
        return categories;
    }

    private static Category readCategory(JsonReader reader) throws IOException {
        String name;
        String categoryId = null;
        String categoryName = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("id")) {
                categoryId = reader.nextString();
            } else if (name.equals("name")) {
                categoryName = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Category(categoryId, categoryName);
    }

}
