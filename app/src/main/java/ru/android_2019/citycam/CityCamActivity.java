package ru.android_2019.citycam;

import android.annotation.SuppressLint;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private static final String TAG = "CityCam";
    private static final String LOG_TAG = "CityCamActivity";

    private City city;

    private ImageView camImageView;
    private ProgressBar progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = findViewById(R.id.cam_image);
        progressView = findViewById(R.id.progress);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(city.getName());
        }
        progressView.setVisibility(View.VISIBLE);

        new PictureDownloadTask(camImageView, progressView).execute(city);
        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.
    }


    static class PictureDownloadTask extends AsyncTask<City, Void, Void> {

        String title, stringUrl;
        long time;
        private Bitmap bitmap;
        @SuppressLint("StaticFieldLeak")
        private ImageView camImageView;
        @SuppressLint("StaticFieldLeak")
        private ProgressBar progressView;

        public PictureDownloadTask(ImageView camImageView, ProgressBar progressView) {
            this.camImageView = camImageView;
            this.progressView = progressView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            title = "WebCam";
            stringUrl = "";
            time = 0;
        }

        @Override
        protected Void doInBackground(City... cities) {
            City city = cities[0];
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) Webcams.createNearbyUrl(city.getLatitude(), city.getLongitude()).openConnection();
                httpURLConnection.connect();
                JsonReader reader = new JsonReader(new InputStreamReader(httpURLConnection.getInputStream()));

                Log.d(LOG_TAG, "Begin connect" + httpURLConnection.getURL());
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    //TODO
                    Log.d(LOG_TAG, name);
                    switch (name) {
                        case "status":
                            String resultCode = reader.nextString();
                            Log.d(LOG_TAG, "---" + resultCode + " " + httpURLConnection.getResponseCode());
                            if (!resultCode.equals("OK")) {
                                return null;
                            }
                            break;
                        case "result":
                            readResult(reader);
                    }
                }
                reader.endObject();
                Log.d(LOG_TAG, "Begin connect");

                InputStream in = new URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }

        private void readResult(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "webcams":
                        reader.beginArray();
                        readWebCamObject(reader);
                        reader.endArray();
                        break;
                }
            }
            reader.endObject();
        }


        private void readWebCamObject(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "image":
                        readImage(reader);
                        break;
                    case "title":
                        title = reader.nextString();
                        break;
                }

            }
            reader.endObject();
        }

        private void readImage(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "current":
                        reader.beginObject();
                        readCurrent(reader);
                        reader.endObject();
                        break;
                    case "update":
                        time = reader.nextLong();
                }
            }
            reader.endObject();
        }

        private void readCurrent(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "preview":
                        stringUrl = reader.nextString();
                        break;
                }
            }
            reader.endObject();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            camImageView.setImageBitmap(bitmap);
        }

    }

}