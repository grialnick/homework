package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    private City city;

    private ImageView camImageView;
    private ProgressBar progressView;
    private TextView cityTextView;
    private TextView regionTextView;
    private TextView latTextView;
    private TextView longTextView;

    private static Webcam currentWebcam;
    private static Bitmap photo;

    private GetDataTask getDataTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);
        cityTextView = (TextView) findViewById(R.id.city_cam__name);
        regionTextView = (TextView) findViewById(R.id.city_cam__region);
        latTextView = (TextView) findViewById(R.id.city_cam__lat);
        longTextView = (TextView) findViewById(R.id.city_cam__lon);

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            getDataTask = (GetDataTask) getLastCustomNonConfigurationInstance();
        }

        if (getDataTask == null) {
            getDataTask = new GetDataTask(this);
            getDataTask.execute();
        } else {
            getDataTask.attachActivity(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        getDataTask.detachActivity();
        return getDataTask;
    }

    private static final String TAG = "CityCam";

    private static class GetDataTask extends AsyncTask<Void, Void, Integer> {

        private CityCamActivity activity;

        GetDataTask(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }


        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }

        void detachActivity() {
            this.activity = null;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                URL nearbyUrl = Webcams.createNearbyUrl(
                        (int) activity.city.latitude,
                        (int) activity.city.longitude);
                HttpsURLConnection connection = (HttpsURLConnection) nearbyUrl.openConnection();
                connection.setRequestProperty("X-RapidAPI-Key", Webcams.RAPID_API_KEY);

                try {
                    JsonReader reader = new JsonReader(new InputStreamReader((InputStream) connection.getContent()));
                    List<Webcam> webcams = readWebcams(reader);
                    if (!webcams.isEmpty()) {
                        currentWebcam = webcams.get(0);
                        try {
                            InputStream in = new URL(currentWebcam.imageUrl).openStream();
                            photo = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                        updateView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
            return 0;
        }

        private List<Webcam> readWebcams(JsonReader reader) throws IOException {
            List<Webcam> webcams = new ArrayList<>();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("result")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("webcams")) {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                Log.i(TAG, "NAME IN ARRAy: " + reader.peek().name());
                                webcams.add(readWebcam(reader));
                            }
                            reader.endArray();
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

            return webcams;
        }

        private Webcam readWebcam(JsonReader reader) throws IOException {
            Webcam webcam = new Webcam();

            try {
                reader.beginObject();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, reader.toString());
            }
            while (reader.hasNext()) {
                String name = reader.nextName();

                switch (name) {
                    case "id":
                        webcam.id = reader.nextLong();
                        break;
                    case "image":
                        webcam.imageUrl = readImageUrl(reader);
                        break;
                    case "location":
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            switch (name) {
                                case "city":
                                    webcam.city = reader.nextString();
                                    break;
                                case "region":
                                    webcam.region = reader.nextString();
                                    break;
                                case "latitude":
                                    webcam.latitude = reader.nextDouble();
                                    break;
                                case "longitude":
                                    webcam.longitude = reader.nextDouble();
                                    break;
                                default:
                                    reader.skipValue();
                                    break;
                            }
                        }
                        reader.endObject();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();

            Log.i(TAG, webcam.toString());
            return webcam;
        }

        private String readImageUrl(JsonReader reader) throws IOException {
            String result = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("current")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("preview")) {
                            result = reader.nextString();
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

        private void updateView() {
            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (currentWebcam != null) {
                                activity.cityTextView.setText(currentWebcam.city);
                                activity.regionTextView.setText(currentWebcam.region);
                                activity.latTextView.setText(String.format("%.2f", currentWebcam.latitude));
                                activity.longTextView.setText(String.format("%.2f", currentWebcam.longitude));

                                if (photo != null) {
                                    activity.camImageView.setImageBitmap(photo);
                                    activity.progressView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
            );

        }

    }

    private static class Webcam {

        long id;

        String city;

        String region;

        String imageUrl;

        double latitude;

        double longitude;

        @Override
        public String toString() {
            return "Webcam{" + "id=" + id +
                    ", city='" + city + '\'' +
                    ", region='" + region + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }
}
