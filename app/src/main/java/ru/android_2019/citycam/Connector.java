package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

class Connector {

    Connector() {
    }

    Message downloadUrl(URL url) throws IOException {
        List<Message> messageList = new ArrayList<>();
        InputStream stream = null;
        HttpsURLConnection connection = null;
        Message result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("X-RapidAPI-Key","17ca55dc03mshdd5146ee8cf5aadp1406f0jsn60a12a5afd36");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = connection.getInputStream();
            if (stream != null) {
                // Здесь нужно прочитать данные
                messageList = readJsonStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }


    public List<Message> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    private List<Message> readMessagesArray(JsonReader reader) throws IOException {
        List<Message> messages = new ArrayList<Message>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    private Message readMessage(JsonReader reader) throws IOException {
        long id = -1;
        String title = null;
        Bitmap image = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextLong();
            } else if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("image")) {
                image = readImage(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Message(id, title, image);
    }

    private Bitmap readImage(JsonReader reader) {
        /*TODO*/
        return null;
    }


    class Message {

        private long id;
        private String title;
        private Bitmap bitmap;

        private Message(long id, String title, Bitmap bitmap){
            this.id = id;
            this.title = title;
            this.bitmap = bitmap;
        }
    }
}
