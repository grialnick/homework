package ru.android_2019.citycam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
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

    private static City city;

    private DownloadFilesTask downloadTask;

    private ImageView camImageView;
    private ProgressBar progressView;
    private TextView camId;
    private TextView camInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate()");
        super.onCreate(savedInstanceState);

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }
        Log.d(TAG, "City=" + city.name + " " + city.longitude + " " + city.latitude);

        setContentView(R.layout.activity_city_cam);
        camImageView = findViewById(R.id.cam_image);
        progressView = findViewById(R.id.progress);
        camId = findViewById(R.id.cam_id);
        camInfo = findViewById(R.id.cam_info);

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            downloadTask = (DownloadFilesTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            downloadTask = new DownloadFilesTask(this);
            downloadTask.execute();
        } else {
            downloadTask.attachActivity(this);
        }

    }

    /*
     * Этот метод вызывается при смене конфигурации, когда текущий объект
     * Activity уничтожается. Объект, который мы вернем, не будет уничтожен,
     * и его можно будет использовать в новом объекте Activity
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d(TAG, "onRetainCustomNonConfigurationInstance()");
        downloadTask.attachActivity(null);
        return downloadTask;
    }

    /**
     * Состояние загрузки в DownloadFileTask
     */
    enum DownloadState {
        DOWNLOADING,
        DONE,
        ERROR;
    }

    static private class DownloadFilesTask extends AsyncTask<Void, Integer, DownloadState> {

        // Context приложения (Не Activity!) для доступа к файлам
        private Context appContext;
        // Текущий объект Activity, храним для обновления отображения
        private CityCamActivity activity;

        // Текущее состояние загрузки
        private DownloadState state = DownloadState.DOWNLOADING;
        // Прогресс загрузки от 0 до 100
        private int progress;
        private Webcam webcam;

        DownloadFilesTask(CityCamActivity activity) {
            this.appContext = activity.getApplicationContext();
            this.activity = activity;

            Log.d(TAG, "Создаем таску загрузки DownloadFilesTask()");
        }

        /**
         * Этот метод вызывается, когда новый объект Activity подключается к
         * данному таску после смены конфигурации.
         *
         * @param activity новый объект Activity
         */
        void attachActivity(CityCamActivity activity) {

            Log.d(TAG, "attachActivity()");

            this.activity = activity;
            updateView();
        }

        /**
         * Вызываем на UI потоке для обновления отображения прогресса и
         * состояния в текущей активности.
         */
        void updateView() {

            Log.d(TAG, "updateView()");

            if (activity != null && webcam != null) {
                activity.camId.setText(String.valueOf(webcam.getId()));
                activity.camInfo.setText(webcam.getTitle());
                activity.camImageView.setImageBitmap(webcam.getBitmap());
                activity.progressView.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * Вызывается в UI потоке из execute() до начала выполнения таска.
         */
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
            updateView();
        }

        /**
         * Скачивание файла в фоновом потоке.
         */
        @Override
        protected DownloadState doInBackground(Void... ignore) {
            Log.d(TAG, "doInBackground()");

            try {
                boolean b = downloadFile(appContext);
                state = DownloadState.DONE;
                if (b) {
                    Log.d(TAG, "file downloaded: " + webcam.getTitle());
                } else {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle(R.string.alert)
                                    .setMessage(R.string.no_cam)
                                    .setCancelable(false)
                                    .setNeutralButton(R.string.choose_another,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    activity.downloadTask = null;
                                                    Intent cityCam = new Intent(appContext, SelectCityActivity.class);
                                                    cityCam.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    appContext.startActivity(cityCam);
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading file: " + e, e);
                state = DownloadState.ERROR;
            }
            return state;
        }

        /**
         * Проверяем код, который вернул doInBackground и показываем текст в зависимости
         * от результата
         */
        @Override
        protected void onPostExecute(DownloadState state) {
            Log.d(TAG, "onPostExecute()");
            this.state = state;
            if (state == DownloadState.DONE) {
                updateView();
            }
        }

        boolean downloadFile(Context context) throws IOException {
            Log.d(TAG, "downloadFile()");
            Connector connector = new Connector();
            URL url = Webcams.createNearbyUrl(city.latitude, city.longitude);
            webcam = connector.downloadUrl(url);
            if (webcam == null) {
                Log.d(TAG, "Информация о камере: в городе нет камер");
                return false;
            } else {
                Log.d(TAG, "Информация о камере " + webcam.getTitle() + "\nid: " + webcam.getId() + "\nimage: " + webcam.getBitmap());
                return true;
            }
        }

    }

    private static final String TAG = "CityCam";
}
