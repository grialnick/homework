package ru.android_2019.citycam;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import ru.android_2019.citycam.model.WebCamMessage;

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
        String timeZone = null;
        String image = null;
        String time = null;
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
                    timeZone = readLocation(reader);
                    break;
                case "title":
                    title = reader.nextString();
                    break;
                case "image":
                    String[] imageObject = readImageObject(reader);
                    image = imageObject[0];
                    time = imageObject[1];
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
        return new WebCamMessage(id, title, timeZone, getBitmapFromURL(image), status, views, time);
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

    private String readLocation(JsonReader reader) throws IOException {
        String timezone = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "timezone":
                    timezone = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return timezone;
    }


    private String[] readImageObject(JsonReader reader) throws IOException {
        String[] data = new String[2];
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "current":
                    data[0] = readImage(reader);
                    break;
                case "update":
                    Log.d("", "readImageObject: " + data[1]);
                    data[1] = parseTime(reader.nextString());
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return data;
    }

    private String readImage(JsonReader reader) throws IOException {
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

    private String parseTime(String time) {

        @SuppressLint("SimpleDateFormat")
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(Long.parseLong(time) * 1000));
        return date;
    }
}
