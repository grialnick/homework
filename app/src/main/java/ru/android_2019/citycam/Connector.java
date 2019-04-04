package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

class Connector {

    Connector() {
        Log.d(TAG, "Connector()");
    }

    Webcam downloadUrl(URL url) throws IOException {
        List<Webcam> webcamList;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        Webcam result = null;
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
                webcamList = readJsonStream(stream);
                Log.d(TAG,"webcamList.size() = " + webcamList.size());
                if (webcamList.size() > 0) {
                    int numOfCam;
                    Random random = new Random();
                    numOfCam = random.nextInt(webcamList.size());
                    result = webcamList.get(numOfCam);
                }
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


    public List<Webcam> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.beginObject();
        while (reader.hasNext()) {
            String resultName = reader.nextName();
            Log.d(TAG,resultName);
            if (resultName.equals("result")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String webcamsName = reader.nextName();
                    if (webcamsName.equals("webcams")){
                        try {
                            return readMessagesArray(reader);
                        } finally {
                            reader.close();
                        }
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return null;
    }

    private List<Webcam> readMessagesArray(JsonReader reader) throws IOException {
        List<Webcam> webcams = new ArrayList<Webcam>();

        reader.beginArray();
        while (reader.hasNext()) {
            webcams.add(readMessage(reader));
        }
        reader.endArray();
        return webcams;
    }

    private Webcam readMessage(JsonReader reader) throws IOException {
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
        return new Webcam(id, title, image);
    }

    private Bitmap readImage(JsonReader reader) throws IOException {

        Bitmap result = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("current")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String imageName = reader.nextName();
                    if (imageName.equals("preview")) {
                        URL newurl = new URL(reader.nextString());
                        try {
                            result = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
                        } catch (Exception e) {
                            Log.e(TAG, "Ошибка передачи изображения" + e.getMessage());
                        }
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return result;
    }

    private static final String TAG = "CityCam";
}
