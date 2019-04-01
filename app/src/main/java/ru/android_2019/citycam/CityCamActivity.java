package ru.android_2019.citycam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    // Task загрузки изображения
    private DownloadFilesTask downloadTask;

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
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);

        getSupportActionBar().setTitle(city.name);

        progressView.setMax(100);
        progressView.setVisibility(View.VISIBLE);

        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.
        if (savedInstanceState != null) {
            // Пытаемся получить ранее запущенный таск
            downloadTask = (DownloadFilesTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            // Создаем новый таск, только если не было ранее запущенного таска
            downloadTask = new DownloadFilesTask(this);
        } else {
            // Передаем в ранее запущенный таск текущий объект Activity
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
        downloadTask.attachActivity(null);
        return downloadTask;
    }

    /**
     * Состояние загрузки в DownloadFileTask
     */
    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error);

        // ID строкового ресурса для заголовка окна прогресса
        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }

    static private class DownloadFilesTask extends AsyncTask<Void, Integer, DownloadState> implements ProgressCallback{

        // Context приложения (Не Activity!) для доступа к файлам
        private Context appContext;
        // Текущий объект Activity, храним для обновления отображения
        private CityCamActivity activity;

        // Текущее состояние загрузки
        private DownloadState state = DownloadState.DOWNLOADING;
        // Прогресс загрузки от 0 до 100
        private int progress;

        DownloadFilesTask(CityCamActivity activity) {
            this.appContext = activity.getApplicationContext();
            this.activity = activity;
        }

        /**
         * Этот метод вызывается, когда новый объект Activity подключается к
         * данному таску после смены конфигурации.
         *
         * @param activity новый объект Activity
         */
        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }

        /**
         * Вызываем на UI потоке для обновления отображения прогресса и
         * состояния в текущей активности.
         */
        void updateView() {
            if (activity != null) {
                activity.progressView.setProgress(progress);
            }
        }

        /**
         * Вызывается в UI потоке из execute() до начала выполнения таска.
         */
        @Override
        protected void onPreExecute() {
            updateView();
        }

        /**
         * Скачивание файла в фоновом потоке. Возвращает результат:
         *      0 -- если файл успешно скачался
         *      1 -- если произошла ошибка
         */
        @Override
        protected DownloadState doInBackground(Void... ignore) {
            try {
                downloadFile(appContext, this);
                state = DownloadState.DONE;

            } catch (Exception e) {
                Log.e(TAG, "Error downloading file: " + e, e);
                state = DownloadState.ERROR;
            }
            return state;
        }

        // Метод ProgressCallback, вызывается в фоновом потоке из downloadFile
        @Override
        public void onProgressChanged(int progress) {
            publishProgress(progress);
        }
        // Метод AsyncTask, вызывается в UI потоке в результате вызова publishProgress

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values.length > 0) {
                int progress = values[values.length - 1];
                this.progress = progress;
                updateView();
            }
        }
        @Override
        protected void onPostExecute(DownloadState state) {
            // Проверяем код, который вернул doInBackground и показываем текст в зависимости
            // от результата
            this.state = state;
            if (state == DownloadState.DONE) {
                progress = 100;
            }
            updateView();
        }

    }

    static void downloadFile(Context context, ProgressCallback progressCallback) throws IOException {
        /*TODO*/
        Connector connector = new Connector();
        URL url = Webcams.createNearbyUrl(city.latitude,city.longitude);
        Connector.Message message = connector.downloadUrl(url);
    }

    private static final String TAG = "CityCam";
}
