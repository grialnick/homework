package ru.android_2019.citycam;

import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JSONReader {

    public static ArrayList<WebCam> getWebcamList(String stringJSONResponse) throws IOException {
        return readJson(stringJSONResponse);
    }

    private static ArrayList<WebCam> readJson(String inputString) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String name;
        ArrayList<WebCam> webCams = null;
        try (JsonReader reader = new JsonReader(inputStreamReader)) {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("result")) {
                    webCams = readResult(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return webCams;
    }

    private static ArrayList<WebCam> readResult(JsonReader reader) throws IOException {
        String name;
        ArrayList<WebCam> webCams = null;
        int total = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("total")) {
                total = Integer.valueOf(reader.nextString());
            } else if (name.equals("webcams") && total != 0) {
                webCams = readWebcamArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return webCams;
    }

    private static ArrayList<WebCam> readWebcamArray(JsonReader reader) throws IOException {
        ArrayList<WebCam> webCams = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            webCams.add(readWebcamElement(reader));
        }
        reader.endArray();
        return webCams;
    }

    private static WebCam readWebcamElement(JsonReader reader) throws IOException {
        String name;
        String id = null;
        String title = null;
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
                imageURL = readWebcamImagePreview(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new WebCam(id, status, title, imageURL);
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

}
