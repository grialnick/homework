package ru.android_2019.citycam;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class JSonParser {
    List<WebCamMessage> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    private List<WebCamMessage> readMessagesArray(JsonReader reader) throws IOException {
        List<WebCamMessage> messages = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("result")) {
                messages = readWebCamObject(reader);
            } else {
                reader.skipValue();
            }

        }
        reader.endObject();
        return messages;
    }

    private List<WebCamMessage> readWebCamObject(JsonReader reader) throws IOException {
        List<WebCamMessage> messages = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("webcams")) {
                messages = readArrayWebCams(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return messages;

    }

    private List<WebCamMessage> readArrayWebCams(JsonReader reader) throws IOException {
        List<WebCamMessage> messages = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readWebCamMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    private WebCamMessage readWebCamMessage(JsonReader reader) throws IOException {
        long id = -1;
        WebCamLocation location = null;
        String image = null;
        String title = null;
        String status = null;
        String views = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            Log.d("City", "readWebCamMessage: " + name);
            switch (name) {
                case "id":
                    id = reader.nextLong();
                    break;
                case "location":
                    location = readLocation(reader);
                    break;
                case "title":
                    title = reader.nextString();
                    break;
                case "image":
                    image = readImage(reader);
                    break;
                case "status":
                    status = reader.nextString();
                    break;
                case "statistics":
                    views = getViews(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new WebCamMessage(id, title, location, image, status, views);
    }

    private String getViews(JsonReader reader) throws IOException {
        String views = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("views")) {
                views = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return views;
    }

    private WebCamLocation readLocation(JsonReader reader) throws IOException {
        String timezone = null;
        String city = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "timezone":
                    timezone = reader.nextString();
                    break;
                case "city":
                    city = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new WebCamLocation(timezone, city);
    }


    private String readImage(JsonReader reader) throws IOException {
        String image = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("current")) {
                image = readImageObject(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return image;
    }

    private String readImageObject(JsonReader reader) throws IOException {
        String preview = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("preview")) {
                preview = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return preview;
    }
}
