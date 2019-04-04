package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

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
    private TextView cityText;
    private TextView titleText;
    private ProgressBar progressView;
    private LoadWebcamTask downloadTask;

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
        cityText = findViewById(R.id.city_text);
        titleText = findViewById(R.id.title_text);
        progressView = findViewById(R.id.progress);

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            downloadTask = (LoadWebcamTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            downloadTask = new LoadWebcamTask(this);
            downloadTask.execute();
        } else {
            downloadTask.attachActivity(this);
        }

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        downloadTask.attachActivity(null);
        return downloadTask;
    }

    enum DownloadState {
        DOWNLOADING,
        DONE,
        ERROR
    }

    static class LoadWebcamTask extends AsyncTask<Void, Void, DownloadState> {

        // Текущий объект Activity, храним для обновления отображения
        private CityCamActivity activity;

        private Bitmap image;
        private Webcam webcam;

        private DownloadState state = DownloadState.DOWNLOADING;

        LoadWebcamTask(CityCamActivity activity) {
            this.activity = activity;
        }

        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }

        void updateView() {
            if (activity != null) {
                if (webcam != null) {
                    activity.titleText.setText(webcam.title);
                    activity.cityText.setText(webcam.city);
                }
                if (image != null) {
                    activity.camImageView.setImageBitmap(image);
                }
                if (state == DownloadState.DONE) {
                    activity.progressView.setVisibility(View.INVISIBLE);
                } else {
                    activity.progressView.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            updateView();
        }

       @Override
        protected DownloadState doInBackground(Void... ignore) {
            if (WebcamCache.getInstance().get(activity.city.name) == null) {
                try {
                    List<Webcam> webcams = DownloadUtils.loadResponse(Webcams.createNearbyUrl(activity.city.latitude, activity.city.longitude));
                    if (!webcams.isEmpty()) {
                        webcam = webcams.get(0);
                        image = DownloadUtils.downloadImage(new URL(webcam.previewUrl));
                        webcam.setImage(image);
                        WebcamCache.getInstance().put(activity.city.name, webcam);
                        Log.d(TAG, "Data downloaded");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error downloading file: " + e, e);
                    state = DownloadState.ERROR;
                }
            } else {
                webcam = WebcamCache.getInstance().get(activity.city.name);
                image = webcam.getImage();
                Log.d(TAG, "Data loaded from cache");
            }
            state = DownloadState.DONE;
            return state;
        }

        @Override
        protected void onPostExecute(DownloadState state) {
            this.state = state;
            if (state == DownloadState.DONE) {
                updateView();
            }
            if (image != null) {
                activity.camImageView.setImageBitmap(image);
            }
            if (webcam != null) {
                activity.cityText.setText(webcam.city);
                activity.titleText.setText(webcam.title);
            }
        }
    }

    private static final String TAG = "CityCam";
}
