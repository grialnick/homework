package ru.android_2019.citycam;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.webcams.Webcams;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String API_KEY = "X-RapidAPI-KEY";
    public static final String EXTRA_CITY = "city";
    private static final String LOG_TAG = "CityCamActivity";
    private static final String DEV_ID = "ce67a1788bmsh6c46b91d9a84025p1ecd95jsn9fd7ca444f62";

    private ImageView camImageView;
    private ProgressBar progressView;
    private TextView titleTextView;
    int countOfWebcam;
    List<Webcam> webcams;

    private DownloadAsyncTask pictureDownloadTask;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        pictureDownloadTask.attachActivity(null);
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return pictureDownloadTask;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        City city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) finish();

        setContentView(R.layout.activity_city_cam);
        camImageView = findViewById(R.id.cam_image);
        progressView = findViewById(R.id.progress);
        titleTextView = findViewById(R.id.activity_city_cam_text_location);

        progressView.setVisibility(View.VISIBLE);
        countOfWebcam = 0;

        if (getSupportActionBar() != null) {
            if (city != null) {
                getSupportActionBar().setTitle(city.getName());
            }
        }

        if (savedInstanceState != null) {
            pictureDownloadTask = (DownloadAsyncTask) getLastCustomNonConfigurationInstance();
        }
        if (pictureDownloadTask == null) {
            Log.d(LOG_TAG, "download");
            pictureDownloadTask = new DownloadAsyncTask(this, progressView);
            pictureDownloadTask.execute(city);
        } else {
            Log.d(LOG_TAG, "download continue");
            pictureDownloadTask.attachActivity(this);
            if (pictureDownloadTask.isDownloaded()) {
                progressView.setVisibility(View.GONE);
            }
        }
    }

    void checkDownLoad(List<Webcam> webcams) {
        if (pictureDownloadTask.isDownloaded()) {
            progressView.setVisibility(View.GONE);
            this.webcams = webcams;
        }
    }


    private static class DownloadAsyncTask extends AsyncTask<City, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private ProgressBar progressView;
        private List<Webcam> webcams;
        private boolean isDownloaded;
        private JsonParser parser;
        int countOfWebcam;
        Webcam tmpWebcam;

        @SuppressLint("StaticFieldLeak")
        private CityCamActivity cityCamActivity;

        DownloadAsyncTask(CityCamActivity cityCamActivity, ProgressBar progressView) {
            countOfWebcam = 0;
            isDownloaded = false;
            this.cityCamActivity = cityCamActivity;
            this.progressView = progressView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            webcams = new ArrayList<>();
            updateView();
        }

        @Override
        @WorkerThread
        protected Void doInBackground(City... cities) {
            City city = cities[0];
            Log.d(LOG_TAG, "execute task");
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) Webcams.createNearbyUrl(city.getLatitude(), city.getLongitude()).openConnection();
                httpURLConnection.setReadTimeout(3000);
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty(API_KEY, DEV_ID);
                httpURLConnection.connect();
                JsonReader reader = new JsonReader(new InputStreamReader(httpURLConnection.getInputStream()));

                parser = new JsonParser(reader);
                parser.mainJsonParser();

                webcams = parser.getWebcams();

                reader.close();
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        boolean isDownloaded() {
            return isDownloaded;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(LOG_TAG, "task executed");
            super.onPostExecute(aVoid);
            isDownloaded = true;
            progressView.setVisibility(View.GONE);
            updateView();
        }

        void attachActivity(CityCamActivity activity) {
            this.cityCamActivity = activity;
            updateView();
        }

        void updateView() {
            if (cityCamActivity != null && webcams != null) {
                initWebCamImage();
                cityCamActivity.checkDownLoad(webcams);
            }
        }

        void initWebCamImage() {
            if (webcams.size() > 0) {
                Log.d(LOG_TAG, countOfWebcam + " in init");
                tmpWebcam = webcams.get(countOfWebcam);
                cityCamActivity.camImageView.setImageBitmap(tmpWebcam.getImage());
                String title = tmpWebcam.getTitle();
                cityCamActivity.titleTextView.setText(String.format("%s %s",
                        cityCamActivity.getString(R.string.about_location), title));
            }
        }
    }
}