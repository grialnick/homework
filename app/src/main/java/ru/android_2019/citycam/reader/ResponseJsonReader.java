package ru.android_2019.citycam.reader;

import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ResponseJsonReader {
    private List<WebcamsMessage> messages;

    public ResponseJsonReader() {
        messages = new ArrayList<>();
    }

    public List<WebcamsMessage> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readResult(reader);
        } finally {
            reader.close();
        }
        return messages;
    }

    private void readResult(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("result")) {
                readWebcams(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readWebcams(JsonReader reader) throws IOException {
        int total = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("total")) {
                total = reader.nextInt();
            }
            else if (name.equals("webcams") && total > 0) {
                readArrayCams(reader);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        //Если не найдено ни одной камеры
        if (total == 0) {
            throw  new IOException("There is no camera available");
        }
    }

    private void readArrayCams(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readWebcamsMessage(reader));
        }
        reader.endArray();
    }

    private WebcamsMessage readWebcamsMessage(JsonReader reader) throws IOException {
        String id = null;
        String status = null;
        String title = null;
        String preview = null;
        String timezone = null;
        int views = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    id = reader.nextString();
                    break;
                case "status":
                    status = reader.nextString();
                    break;
                case "title":
                    title = reader.nextString();
                    break;
                case "image":
                    preview = readCurrent(reader);
                    break;
                case "location":
                    timezone = readTimeZone(reader);
                    break;
                case "statistics":
                    reader.beginObject();
                    reader.nextName();
                    views = reader.nextInt();
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new WebcamsMessage(id, status, title, preview, timezone, views);
    }


    private String readCurrent(JsonReader reader) throws IOException {
        String preview = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("current")) {
                preview = readPreview(reader);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return preview;
    }

    private String readPreview(JsonReader reader) throws IOException {
        String preview = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("preview")) {
                preview = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return preview;
    }

    private String readTimeZone(JsonReader reader) throws IOException {
        String timezone = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("timezone")) {
                timezone = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return timezone;
    }
}
