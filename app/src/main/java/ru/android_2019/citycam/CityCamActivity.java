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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.BitmapCache;
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
    private DownloadTask downloadTask;

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

        progressView.setVisibility(View.VISIBLE);

        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.
        Log.i(EXTRA_CITY, city.toString());

        if (savedInstanceState != null) {
            downloadTask = (DownloadTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            downloadTask = new DownloadTask(this);
            try {
                downloadTask.execute(city);
            } catch (Exception e) {
                Log.e(EXTRA_CITY, "Запрос валится", e);
            }
        } else {
            downloadTask.attachActivity(this);
            Log.i(TAG, "Картинка взята из другого активити");
        }

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        downloadTask.attachActivity(null);
        return downloadTask;
    }

    private static final String TAG = "CityCam";

    private interface GetResult<T> {
        T getResult(JsonReader jsonReader) throws IOException;
    }

    public class DownloadTask extends AsyncTask<City, Void, Bitmap> {
        private static final String TAG = "DownloadTask";

        private CityCamActivity activity;
        private Bitmap bitmap;

        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }

        void updateView() {
            if (activity != null) {
                activity.camImageView.setImageBitmap(bitmap);
                activity.progressView.setVisibility(View.INVISIBLE);
            }
        }

        public DownloadTask(CityCamActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Bitmap doInBackground(City... city) {
            try {
                Bitmap bitmap = BitmapCache.bitmapLruCache.get(city[0].name);
                if (bitmap == null) {
                    for (int radius = 50; (bitmap = download(city[0], radius)) == null; radius *= 2);
                    Log.i(TAG, "Картинка скачана " + city[0].name);
                } else {
                    Log.i(TAG, "Картинка взята из кеша " + city[0].name);
                }
                BitmapCache.bitmapLruCache.put(city[0].name, bitmap);
                return bitmap;
            } catch (Exception e) {
                Log.e(TAG, "Проблемы со скачкой", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            this.bitmap = bitmap;
//            activity.camImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            updateView();
        }

        private Bitmap download(City city, int radius) throws IOException {
            URL url = Webcams.createNearbyUrl(city.latitude, city.longitude, radius);
            HttpURLConnection urlConnection = null;
            InputStream in = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Key",
                        "6b0f0c636emshbd6f1c732d3dafep15e2d6jsn21866776fb55");
                urlConnection.connect();

                int code = urlConnection.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Код ответа: " + code);
                }

                in = urlConnection.getInputStream();
                JsonReader jsonReader = new JsonReader(new InputStreamReader(in));

                // Получение урла картинки
                URL webcamUrl = new URL(readWebcamUrl(jsonReader));
                Log.d(TAG, "Url: " + webcamUrl + " Radius: " + radius);
                jsonReader.close();
                in.close();

                urlConnection.disconnect();
                urlConnection = (HttpURLConnection) webcamUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                in = urlConnection.getInputStream();
                code = urlConnection.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Код ответа: " + code);
                }
                return BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                Log.e(TAG, "Не получилось скачать", e);
                return null;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Не удалось завершить InputStream", e);
                    }
                }

                urlConnection.disconnect();
            }
        }

        private <T> T readJson(JsonReader jsonReader, String[] path, int i, GetResult<T> getResult) throws IOException {
            T result = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals(path[i])) {
                    if (path.length - 1 == i) {
                        result = getResult.getResult(jsonReader);
                    } else {
                        result = readJson(jsonReader, path, ++i, getResult);
                    }
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            return result;
        }

        private String readWebcamUrl(final JsonReader jsonReader) throws IOException {
            final String[] path = {"result", "webcams"};

            try {
                final String webcam = readJson(jsonReader, path, 0, new GetResult<String>() {
                    @Override
                    public String getResult(JsonReader jsonReader) throws IOException {
                        List<String> webcamsUrl = new ArrayList<>();

                        jsonReader.beginArray();

                        while (jsonReader.hasNext()) {
                            String[] imgPath = {"image", "current", "preview"};
                            webcamsUrl.add(readJson(jsonReader, imgPath, 0, new GetResult<String>() {
                                @Override
                                public String getResult(JsonReader jsonReader) throws IOException {
                                    return jsonReader.nextString();
                                }
                            }));
                        }

                        jsonReader.endArray();
                        return webcamsUrl.size() > 0 ? webcamsUrl.get(0) : null;
                    }
                });
                return webcam;
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }
}
