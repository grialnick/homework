package ru.android_2019.citycam.parsers;


import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.android_2019.citycam.model.Webcam;

public class ResponseWebcamParser  {



    public static List<Webcam> listResponseWebcam(InputStream in, String charset)
            throws IOException {
        JsonReader jsonReader = new JsonReader(new InputStreamReader(in, charset));
        List <Webcam> webcams = new ArrayList<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("result")) {
                webcams = readResponse(jsonReader);
            }
            else if(name.equals("status")) {
                String status = jsonReader.nextString();
                if(!status.equals("OK")){
                    throw  new IOException("Bad status");
                }
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return webcams;
    }

    private static List<Webcam> readResponse(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        List <Webcam> webcams = new ArrayList<>();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("webcams")) {
                webcams = readWebcamsList(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return webcams;
    }

    private static List<Webcam> readWebcamsList(JsonReader jsonReader) throws IOException {
        List <Webcam> webcams = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            webcams.add(readWebcam(jsonReader));
        }
        jsonReader.endArray();
        return webcams;
    }

    private static Webcam readWebcam(JsonReader jsonReader) throws IOException {
        String title = null;
        URL imageUrl = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("title")) {
                title = jsonReader.nextString();
            }
            else if(name.equals("image")) {
                imageUrl = readImage(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        assert imageUrl != null;
        return new Webcam(title, imageUrl);
    }

    private static URL readImage(JsonReader jsonReader) throws IOException {
        URL imageUrl = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("current")){
                imageUrl = readImageUrl(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return imageUrl;
    }

    private static URL readImageUrl(JsonReader jsonReader) throws IOException {
        URL url = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("preview")) {
                String imageUrl = jsonReader.nextString();
                url = new URL(imageUrl);
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return url;
    }


    private ResponseWebcamParser() {

    }
}
