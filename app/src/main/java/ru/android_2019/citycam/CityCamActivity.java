package ru.android_2019.citycam;

import android.app.Activity;
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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.webcams.WebCamParser;
import ru.android_2019.citycam.webcams.Webcams;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends Activity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";
    private static final String TAG = "CityCam";
    private static final String LOG_TAG = "CityCamActivityTag";

    private City city;
    private PictureDownloadTask downloadTask;
    private ImageView camImageView;
    private TextView textViewTitle;
    private TextView textViewTime;
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
        textViewTime = findViewById(R.id.activity_city_cam__time);
        textViewTitle = findViewById(R.id.activity_city_cam__title);


        if (getActionBar() != null) {
            getActionBar().setTitle(city.getName());
        }

        progressView.setVisibility(View.VISIBLE);
        if (savedInstanceState != null) {
            // Пытаемся получить ранее запущенный таск
            downloadTask = (PictureDownloadTask) getLastNonConfigurationInstance();
        }
        if (downloadTask == null) {
            // Создаем новый таск, только если не было ранее запущенного таска
            downloadTask = new PictureDownloadTask(this);
            downloadTask.execute(city);
        } else {
            // Передаем в ранее запущенный таск текущий объект Activity
            downloadTask.attachActivity(this);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        // Этот метод вызывается при смене конфигурации, когда текущий объект
        // Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
        // и его можно будет использовать в новом объекте Activity
        downloadTask.attachActivity(null);
        return downloadTask;
    }

    static class PictureDownloadTask extends AsyncTask<City, Void, Void> {
        private Bitmap bitmap;
        private Webcam webcam;
        private long count = -1;

        private WeakReference<CityCamActivity> weakReference;

        PictureDownloadTask(CityCamActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        /**
         * Этот метод вызывается, когда новый объект Activity подключается к
         * данному таску после смены конфигурации.
         *
         * @param activity новый объект Activity
         */
        void attachActivity(CityCamActivity activity) {
            this.weakReference = new WeakReference<>(activity);
            updateView();
        }

        /**
         * Вызываем на UI потоке для обновления отображения прогресса и
         * состояния в текущей активности.
         */
        void updateView() {
            CityCamActivity activity = weakReference.get();
            if (activity != null && webcam != null) {

                activity.progressView.setVisibility(View.INVISIBLE);
                activity.textViewTitle.setText(webcam.getTitle());
                activity.textViewTime.setText(webcam.getTime() + " " + count);
                if (bitmap != null) {
                    activity.camImageView.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(City... cities) {
            City city = cities[0];
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) Webcams.createNearbyUrl(city.getLatitude(), city.getLongitude()).openConnection();
                httpURLConnection.setRequestProperty("X-RapidAPI-Key", "bb43131250mshf0d6ed9887777a2p1fada8jsnb76edde66d91");
                httpURLConnection.connect();
                Log.d(LOG_TAG, "Begin connect  " + httpURLConnection.getURL());
                Log.d(LOG_TAG, httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());

                JsonReader reader = new JsonReader(new InputStreamReader(httpURLConnection.getInputStream()));
                List<Webcam> webcams = new WebCamParser().parse(reader);

                count = webcams.size();
                if (count > 0) {
                    InputStream in = new URL(webcams.get(0).getUrl()).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    webcam = webcams.get(0);
                    in.close();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateView();
        }

    }

}