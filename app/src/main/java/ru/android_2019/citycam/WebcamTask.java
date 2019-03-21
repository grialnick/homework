package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcam;
import ru.android_2019.citycam.webcams.Webcams;

public class WebcamTask extends AsyncTask<Void, Void, Webcam> implements Serializable {

    private CityCamActivity activity;
    private final City city;
    private String errorMessage;

    public WebcamTask(CityCamActivity activity, City city) {
        super();
        this.activity = activity;
        this.city = city;
    }

    public void attachActivity(CityCamActivity activity) {
        this.activity = activity;
    }

    public void detachActivity() {
        this.activity = null;
    }

    @Override
    protected Webcam doInBackground(Void... voids) {
        Webcam webcam = null;
        try {
            HttpURLConnection connection = Webcams.createNearbyUrlConnection(city.longitude, city.latitude);
            JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream()));
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("status")) {
                    String status = reader.nextString();
                    if (!status.equals("OK")) {
                        errorMessage = "Ошибка подключения";
                        break;
                    }
                } else if (name.equals("result")) {
                    webcam = parseWebcam(reader);
                    if (webcam == null) {
                        errorMessage = "Видеокамер не найдено";
                    }
                }
            }
            reader.endObject();
            connection.disconnect();
        } catch (Exception e) {
            errorMessage =  "Ошибка подключения";
            e.printStackTrace();
        }
        return webcam;
    }

    @Override
    protected void onPostExecute(Webcam webcam) {
        if (activity != null) {
            if (webcam == null) {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                activity.updateWebcam(webcam);
            }
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
