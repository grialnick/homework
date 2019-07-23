package ru.android_2019.citycam;

import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JSONReader {

    public static ArrayList<WebCamInfo> getWebcamList(String stringJSONResponse) throws IOException {
        return readJson(stringJSONResponse);
    }

    private static ArrayList<WebCamInfo> readJson(String inputString) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String name;
        ArrayList<WebCamInfo> webCamInfos = null;
        try (JsonReader reader = new JsonReader(inputStreamReader)) {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("result")) {
                    webCamInfos = readResult(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return webCamInfos;
    }

    private static ArrayList<WebCamInfo> readResult(JsonReader reader) throws IOException {
        String name;
        ArrayList<WebCamInfo> webCamInfos = null;
        int total = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("total")) {
                total = Integer.valueOf(reader.nextString());
            } else if (name.equals("webcams") && total != 0) {
                webCamInfos = readWebcamArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return webCamInfos;
    }

    private static ArrayList<WebCamInfo> readWebcamArray(JsonReader reader) throws IOException {
        ArrayList<WebCamInfo> webCamInfos = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            webCamInfos.add(readWebcamElement(reader));
        }
        reader.endArray();
        return webCamInfos;
    }

    private static WebCamInfo readWebcamElement(JsonReader reader) throws IOException {
        String name;
        String id = null;
        String title = null;
        Location location = null;
        String imageURL = null;
        String status = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextString();
            } else if (name.equals("status")) {
                status = reader.nextString();
            } else if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("image")) {
                imageURL = readWebcamImage(reader);
            } else if (name.equals("location")) {
                location = readWebcamLocation(reader);
            } else  {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new WebCamInfo(id, title, location, imageURL, status);
    }

    private static String readWebcamImage(JsonReader reader) throws IOException {
        String name;
        String imageURL = null;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("current")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String nameCurrent = reader.nextName();
                    if (nameCurrent.equals("preview")) {
                        imageURL = reader.nextString();
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
        return imageURL;
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
}
