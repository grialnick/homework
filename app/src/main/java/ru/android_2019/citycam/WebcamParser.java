package ru.android_2019.citycam;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WebcamParser {

    public static List<Webcam> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readResponse(reader);
        } finally {
            reader.close();
        }
    }

    private static List<Webcam> readResponse(JsonReader reader) throws IOException {
        List<Webcam> webcams = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("webcams")) {
                webcams = readWebcams(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return webcams;
    }

    private static List<Webcam> readWebcams(JsonReader reader) throws IOException {
        List<Webcam> webcams = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("webcam")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    webcams.add(readWebcam(reader));
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return webcams;
    }

    private static Webcam readWebcam(JsonReader reader) throws IOException {
        String title = null;
        String city = null;
        String previewUrl = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case "preview_url": {
                    previewUrl = reader.nextString();
                    break;
                }
                case "title": {
                    title = reader.nextString();
                    break;
                }case "city": {
                    city = reader.nextString();
                    break;
                }
                default: {
                    reader.skipValue();
                }
            }
        }
        reader.endObject();
        return new Webcam(title, city, previewUrl);
    }

}
