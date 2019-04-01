package ru.android_2019.citycam;

import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.android_2019.citycam.model.Webcam;

class JsonParser {
    private JsonReader reader;
    private String LOG_TAG = "JsonParser";
    private List<Webcam> webcams;

    JsonParser(JsonReader reader) {
        this.reader = reader;
        webcams = new ArrayList<>();
    }

    void mainJsonParser() throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "status":
                    String resultCode = reader.nextString();
                    Log.d(LOG_TAG, name + " " + resultCode);
                    if (!resultCode.equals("OK")) {
                        throw new IOException();
                    }
                    break;
                case "result":
                    Log.d(LOG_TAG, "result reader");
                    readResult(reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();

    }


    private void readResult(JsonReader reader) throws IOException {
        reader.beginObject();

        while (reader.hasNext()) {
            String jsonName = reader.nextName();

            if (jsonName.equals("webcams")) {
                readWebCamArray(reader);
            } else reader.skipValue();
        }
        reader.endObject();
    }

    private void readWebCamArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            webcams.add(readWebCamObject(reader));
        }
        reader.endArray();
    }

    private Webcam readWebCamObject(JsonReader reader) throws IOException {
        Webcam webcam = new Webcam();
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "image":
                    readImage(reader, webcam);
                    break;
                case "title":
                    String tmp = reader.nextString();
                    Log.d(LOG_TAG, "title of webcam" + tmp);
                    webcam.setTitle(tmp);
                    break;
                default:
                    reader.skipValue();
            }

        }
        reader.endObject();
        InputStream in = new URL(webcam.getImgUrl()).openStream();
        webcam.setImage(BitmapFactory.decodeStream(in));
        return webcam;
    }

    private void readImage(JsonReader reader, Webcam webcam) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("current")) {
                readCurrent(reader, webcam);
            } else reader.skipValue();
        }
        reader.endObject();
    }

    private void readCurrent(JsonReader reader, Webcam webcam) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("preview")) {
                webcam.setImgUrl(reader.nextString());
                Log.d(LOG_TAG, "url of image: " + webcam.getImgUrl());
            } else reader.skipValue();
        }
        reader.endObject();
    }

    List<Webcam> getWebcams() {
        return webcams;
    }

}
