package ru.android_2019.citycam;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.android_2019.citycam.model.WebCamForm;

class JSonParser {
    private static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    List<WebCamForm> readJsonStream(InputStream in) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return readFormsArray(reader);
        }
    }

    private List<WebCamForm> readFormsArray(JsonReader reader) throws IOException {
        List<WebCamForm> forms = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("result")) {
                forms = readWebCamObject(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return forms;
    }

    private List<WebCamForm> readWebCamObject(JsonReader reader) throws IOException {
        List<WebCamForm> forms = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("webcams")) {
                forms = readArrayWebCams(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return forms;
    }

    private List<WebCamForm> readArrayWebCams(JsonReader reader) throws IOException {
        List<WebCamForm> forms = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            forms.add(readWebCamForm(reader));
        }
        reader.endArray();
        return forms;
    }

    private WebCamForm readWebCamForm(JsonReader reader) throws IOException {
        int id = -1;
        String image = null;
        String time = null;
        String title = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    id = reader.nextInt();
                    break;
                case "title":
                    title = reader.nextString();
                    break;
                case "image":
                    String[] imageObject = readImageObject(reader);
                    image = imageObject[0];
                    time = imageObject[1];
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new WebCamForm(id, title, getBitmapFromURL(image), time);
    }

    @SuppressLint("SimpleDateFormat")
    private String[] readImageObject(JsonReader reader) throws IOException {
        String[] data = new String[2];
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "current":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        if (reader.nextName().equals("preview")) {
                            data[0] = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "update":
                    data[1] = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").
                            format(new java.util.Date(Long.parseLong(reader.nextString()) * 1000));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return data;
    }
}
