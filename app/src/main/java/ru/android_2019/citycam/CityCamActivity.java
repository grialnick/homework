package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.cache.MyCache;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.reader.ResponseJsonReader;
import ru.android_2019.citycam.reader.WebcamsMessage;
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
    private TextView idTextView;
    private TextView titleTextView;
    private TextView timezoneTextView;
    private TextView countViewsTextView;
    private TextView errorTextView;

    private DownloadImageTask downloadTask = null;

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
        idTextView = (TextView) findViewById(R.id.textId);
        titleTextView = (TextView) findViewById(R.id.textTitle);
        timezoneTextView = (TextView) findViewById(R.id.textTimezone);
        countViewsTextView = (TextView) findViewById(R.id.textNumViews);
        errorTextView = (TextView) findViewById(R.id.textError);

        getSupportActionBar().setTitle(city.name);

        if (savedInstanceState != null) {
            // Пытаемся получить ранее запущенный таск
            downloadTask = (DownloadImageTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            downloadTask = new DownloadImageTask(this);
            if (MyCache.getInstance().getLru().get(city.name) == null) {
                try {
                    downloadTask.execute(Webcams.createNearbyUrl(city.latitude, city.longitude));
                } catch (MalformedURLException e) {
                    Log.w(TAG, "Cannot create URL with lat : " + city.latitude + " and lng : " + city.longitude);
                    this.finish();
                }
            } else {
                downloadTask.setMess(MyCache.getInstance().getLru().get(city.name));
                downloadTask.setProgress(100);
                if (downloadTask.getMess().getBitmap() == null) {
                    downloadTask.setErrorOccured(true);
                }
                downloadTask.attachActivity(this);
            }
        } else {
            // Передаем в ранее запущенный таск текущий объект Activity
            downloadTask.attachActivity(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.downloadTask;
    }


    public class DownloadImageTask extends AsyncTask<URL, Integer, WebcamsMessage> {

        private CityCamActivity activity;
        private Integer progress = 0;
        private WebcamsMessage mess;
        private Boolean errorOccured = false;

        DownloadImageTask(CityCamActivity activity) {
            this.activity = activity;
        }

        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }

        void updateView() {
            if (activity != null) {
                activity.progressView.setVisibility(progress != 100 ? View.VISIBLE : View.INVISIBLE);
                if(errorOccured) {
                    activity.errorTextView.setVisibility(View.VISIBLE);
                }
                else {
                    activity.errorTextView.setVisibility(View.INVISIBLE);
                    activity.camImageView.setImageBitmap(mess.getBitmap());
                    activity.idTextView.setText(getString(R.string.id_cam, mess.getId()));
                    activity.titleTextView.setText(getString(R.string.title_cam, mess.getTitle()));
                    activity.timezoneTextView.setText(getString(R.string.timezone_cam, mess.getTimezone()));
                    activity.countViewsTextView.setText(getString(R.string.views_cam, mess.getViews()));
                }
            }
        }

        @Override
        protected void onPreExecute() {
            activity.progressView.setVisibility(View.VISIBLE);
        }

        protected WebcamsMessage doInBackground(URL... urls) {
            int count = urls.length;
            Bitmap bitmap = null;
            WebcamsMessage message = null;
            URL url = null;
            String imageUrl = null;
            for (int i = 0; i < count; i++) {
                try {
                    message = downloadWebcamsMessageByUrl(urls[i]);
                    imageUrl = message.getPreview();
                }
                catch (IOException e) {
                    Log.w(TAG, "IO exception in downloadWebcamsMessageByUrl method");
                    errorOccured = true;
                    break;
                }
                try {
                    url = new URL(imageUrl);
                }
                catch (MalformedURLException e) {
                    Log.w(TAG, "MalformedURL exception");
                    errorOccured = true;
                    break;
                }
                try {
                    bitmap = downloadImageByUrl(url);
                }
                catch (IOException e) {
                    Log.w(TAG, "IO exception in downloadImageByUrl");
                    errorOccured = true;
                    break;
                }
                message.setBitmap(bitmap);
                if (isCancelled()) break;
            }
            mess = message;
            return message;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(WebcamsMessage result) {
            if (MyCache.getInstance().getLru().get(city.name) == null && !errorOccured) {
                MyCache.getInstance().getLru().put(city.name, mess);
            }
            progress = 100;
            updateView();
        }

        private Bitmap downloadImageByUrl(URL url) throws IOException {
            InputStream stream = null;
            HttpsURLConnection connection = null;
            Bitmap result = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                stream = connection.getInputStream();
                if (stream != null) {
                    result = BitmapFactory.decodeStream(stream);
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        private WebcamsMessage downloadWebcamsMessageByUrl(URL url) throws IOException {
            InputStream stream = null;
            HttpsURLConnection connection = null;
            WebcamsMessage result = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                //Установка ключа в header запроса
                connection.setRequestProperty("X-RapidAPI-Key","17ca55dc03mshdd5146ee8cf5aadp1406f0jsn60a12a5afd36");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                stream = connection.getInputStream();
                if (stream != null) {
                    ResponseJsonReader reader = new ResponseJsonReader();
                    List<WebcamsMessage> messagesList = reader.readJsonStream(stream);
                    Iterator iter = messagesList.iterator();
                    WebcamsMessage message = null;
                    while (iter.hasNext()) {
                        message = (WebcamsMessage) iter.next();
                        if (!message.getStatus().equals("active")) {
                            iter.remove();
                        }
                    }
                    Integer maxRand = messagesList.size();
                    Random randomValue = new Random();
                    if (maxRand == 0) {
                        Log.w(TAG, "No one available camera at this place");
                        throw new IOException("No one available camera at this place");
                    } else {
                        result = messagesList.get(randomValue.nextInt(maxRand));
                    }
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        public WebcamsMessage getMess() {
            return mess;
        }

        public void setMess(WebcamsMessage mess) {
            this.mess = mess;
        }

        public void setProgress(Integer progress) {
            this.progress = progress;
        }

        public void setErrorOccured(Boolean errorOccured) {
            this.errorOccured = errorOccured;
        }
    }

    private static final String TAG = "CityCam";
}
