package ru.android_2019.citycam.asynctask.camjson;

import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CamJSONReader {

    public static ArrayList<CamInfo> getWebcamList(String stringJSONResponse) throws IOException {
        return readJson(stringJSONResponse);
    }

    private static ArrayList<CamInfo> readJson(String inputString) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String name;
        ArrayList<CamInfo> camInfos = null;
        try (JsonReader reader = new JsonReader(inputStreamReader)) {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("result")) {
                    camInfos = readResult(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return camInfos;
    }

    private static ArrayList<CamInfo> readResult(JsonReader reader) throws IOException {
        String name;
        ArrayList<CamInfo> camInfos = null;
        int total = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("total")) {
                total = Integer.valueOf(reader.nextString());
            } else if (name.equals("webcams") && total != 0) {
                camInfos = readCamArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return camInfos;
    }

    private static ArrayList<CamInfo> readCamArray(JsonReader reader) throws IOException {
        ArrayList<CamInfo> camInfos = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            camInfos.add(readCamElement(reader));
        }
        reader.endArray();
        return camInfos;
    }

    private static CamInfo readCamElement(JsonReader reader) throws IOException {
        String name;
        String camId = null;
        String camTitle = null;
        ArrayList<Category> camCategory = null;
        String camImageURL = null;
        Location camLocation = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case "id":
                    camId = reader.nextString();
                    break;
                case "title":
                    camTitle = reader.nextString();
                    break;
                case "category":
                    camCategory = readCamCategoryArray(reader);
                    break;
                case "image":
                    camImageURL = readCamImagePreview(reader);
                    break;
                case "location":
                    camLocation = readCamLocation(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new CamInfo(camId, camTitle, camCategory, camLocation, camImageURL);
    }

    private static Location readCamLocation(JsonReader reader) throws IOException {
        String name;
        String locationRegion = null;
        String locationRegionCode = null;
        String locationTimeZone = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case "region":
                    locationRegion = reader.nextString();
                    break;
                case "region_code":
                    locationRegionCode = reader.nextString();
                    break;
                case "timezone":
                    locationTimeZone = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new Location(locationRegion, locationRegionCode, locationTimeZone);
    }

    private static String readCamImagePreview(JsonReader reader) throws IOException {
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

    private static ArrayList<Category> readCamCategoryArray(JsonReader reader) throws IOException {
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
