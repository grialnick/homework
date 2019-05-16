package ru.android_2019.citycam.webcams.parser;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.android_2019.citycam.webcams.store.Webcam;
import ru.android_2019.citycam.webcams.store.Webcam.TypeImage;
import ru.android_2019.citycam.webcams.exceptions.BadResponseException;

public final class WebcamsResponseParser {

    public static List<Webcam> parseWebcamsResponse(
            String cityName,
            InputStream in,
            String charset) throws IOException, BadResponseException {

        JsonReader reader = new JsonReader(new InputStreamReader(in, charset));
        List<Webcam> webcams = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("result")) {
                webcams = readResult(cityName, reader);
            } else if (name.equals("status")){
                String status = reader.nextString();
                if (!status.equals("OK")) {
                    throw new BadResponseException("Bad response with status: " + status);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return webcams;
    }

    private static List<Webcam> readResult(
            String cityName,
            JsonReader reader) throws IOException {

        List<Webcam> webcams = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("webcams")) {
                webcams = readWebcamsArray(cityName, reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return webcams;
    }

    private static List<Webcam> readWebcamsArray(
            String cityName,
            JsonReader reader) throws IOException {

        List<Webcam> webcams = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            webcams.add(readWebcam(cityName, reader));
        }
        reader.endArray();

        return webcams;
    }

    private static Webcam readWebcam(
            String cityName,
            JsonReader reader) throws IOException {

        String title = null;
        String imageUrl = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("image")) {
                imageUrl = readImages(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        Webcam webcam = new Webcam(cityName);
        webcam.setTitle(title);
        webcam.setImageUrl(imageUrl);
        return webcam;
    }

    private static String readImages(JsonReader reader)
            throws IOException {

        String imageUrl = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("current")) {
                imageUrl = readImageUrl(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return imageUrl;
    }

    private static String readImageUrl(JsonReader reader)
            throws IOException {

        String imageUrl = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(TypeImage.PREVIEW.getName())) {
                imageUrl = reader.nextString();
            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return imageUrl;
    }
}