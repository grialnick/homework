package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcam;
import ru.android_2019.citycam.webcams.Webcams;

import static ru.android_2019.citycam.CityCamActivity.HANDLER_CODE_BAD;
import static ru.android_2019.citycam.CityCamActivity.HANDLER_CODE_SUCCESS;

public class WebcamThread extends Thread {

    private final Handler handler;
    private final City city;

    public WebcamThread(Handler handler, City city) {
        super();
        this.handler = handler;
        this.city = city;
    }

    @Override
    public void run() {
        try {
            Webcam webcam = null;
            HttpURLConnection connection = Webcams.createNearbyUrlConnection(city.longitude, city.latitude);
            JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream()));
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("status")) {
                    String status = reader.nextString();
                    if (!status.equals("OK")) {
                        handler.obtainMessage(HANDLER_CODE_BAD).sendToTarget();
                        return;
                    }
                } else if (name.equals("result")) {
                    webcam = parseWebcam(reader);
                }
            }
            reader.endObject();
            connection.disconnect();
            handler.obtainMessage(HANDLER_CODE_SUCCESS, webcam).sendToTarget();
        } catch (Exception e) {
            e.printStackTrace();
            handler.obtainMessage(HANDLER_CODE_BAD).sendToTarget();
        }
    }

    static private Webcam parseWebcam(JsonReader reader) throws IOException {
        Webcam result = null;
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("webcams")) {
                reader.beginArray();
                if (reader.hasNext()) {
                    reader.beginObject();
                    int id = 0;
                    String status = null;
                    String title = null;
                    Bitmap preview = null;

                    while (reader.hasNext()) {
                        String webcamName = reader.nextName();
                        if (webcamName.equals("id")) {
                            id = Integer.parseInt(reader.nextString());
                        } else if (webcamName.equals("status")) {
                            status = reader.nextString();
                        } else if (webcamName.equals("title")) {
                            title = reader.nextString();
                        } else if (webcamName.equals("image")) {
                            preview = parseImage(reader);
                        } else {
                            reader.skipValue();
                        }
                    }
                    result = new Webcam(id, status, title, preview);
                    reader.endObject();
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return result;
    }

    static private Bitmap parseImage(JsonReader reader) throws IOException {
        Bitmap result = null;
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("current")) {
                reader.beginObject();

                while (reader.hasNext()) {
                    String currentName = reader.nextName();
                    if (currentName.equals("preview")) {
                        HttpURLConnection connection = (HttpURLConnection) new URL(reader.nextString()).openConnection();
                        result = BitmapFactory.decodeStream(connection.getInputStream());
                        connection.disconnect();
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
}
