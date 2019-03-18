package ru.android_2019.citycam.webcams;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.android_2019.citycam.model.Webcam;

public class WebCamParser {

    private static final String LOG_TAG = "WebCamParser";

    public  WebCamParser(){

    }
    public List<Webcam> parse(JsonReader reader) throws IOException {
        List<Webcam> webcamList = new LinkedList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            Log.d(LOG_TAG, name);
            switch (name) {
                case "status":
                    String resultCode = reader.nextString();
                    if (!resultCode.equals("OK")) {
                        Log.d(LOG_TAG, reader.nextName());
                        readError(reader);
                        return null;
                    }
                    break;
                case "result":
                    readResult(webcamList, reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();

        return webcamList;
    }


    private void readError(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            Log.d(LOG_TAG, reader.nextName() + ": " + reader.nextString());
        }
        reader.endObject();
    }

    private void readResult(List<Webcam> webcamList, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "webcams":
                    readWebCams(webcamList, reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }


    private void readWebCams(List<Webcam> webcamList, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            Webcam webcam = new Webcam();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "image":
                        readImage(webcam, reader);
                        break;
                    case "title":
                        webcam.setTitle(reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                }
            }
            webcamList.add(webcam);
            reader.endObject();
        }
        reader.endArray();
    }

    private void readImage(Webcam webcam, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "current":
                    readCurrent(webcam, reader);
                    break;
                case "update":
                    webcam.setTime(reader.nextLong());
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readCurrent(Webcam webcam, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "preview":
                    webcam.setUrl(reader.nextString());
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }

}
