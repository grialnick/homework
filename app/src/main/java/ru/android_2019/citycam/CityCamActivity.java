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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе
        if (savedInstanceState != null) {
            // Пытаемся получить ранее запущенный таск
            downloadTask = (DownloadImageTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            try{
                downloadTask = new DownloadImageTask(this);
                downloadTask.execute(Webcams.createNearbyUrl(city.latitude,city.longitude));
            } catch (MalformedURLException e){
                Log.w(TAG, "Cannot create URL with lat : "+city.latitude+" and lng : "+city.longitude);
                this.finish();
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

    private class DownloadImageTask extends AsyncTask<URL, Integer, DownloadImageTask.WebcamsMessage> {

        private CityCamActivity activity;
        private Integer progress;
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
                    String idText = getResources().getString(R.string.id_cam)+mess.getId();
                    activity.idTextView.setText(idText);
                    String titleText = getResources().getString(R.string.title_cam)+mess.getTitle();
                    activity.titleTextView.setText(titleText);
                    String timezoneText = getResources().getString(R.string.timezone_cam)+mess.getTimezone();
                    activity.timezoneTextView.setText(timezoneText);
                    String countViewsText = getResources().getString(R.string.views_cam)+mess.getViews();
                    activity.countViewsTextView.setText(countViewsText);
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
                    List<WebcamsMessage> messagesList = readJsonStream(stream);
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

        public List<WebcamsMessage> readJsonStream(InputStream in) throws IOException {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            try {
                return readResult(reader);
            } finally {
                reader.close();
            }
        }

        public List<WebcamsMessage> readResult(JsonReader reader) throws IOException {
            List<WebcamsMessage> messages = new ArrayList<>();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("result")) {
                    messages = readWebcams(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return messages;
        }

        public List<WebcamsMessage> readWebcams(JsonReader reader) throws IOException {
            List<WebcamsMessage> messages = new ArrayList<>();
            Integer total = -1;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("total")) {
                    total = reader.nextInt();
                }
                else if (name.equals("webcams") && total > 0) {
                    messages = readArrayCams(reader);
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            //Если не найдено ни одной камеры
            if (total == 0) {
                Log.w(TAG, "There is no camera available");
                throw  new IOException("There is no camera available");
            }
            return messages;
        }

        public List<WebcamsMessage> readArrayCams(JsonReader reader) throws IOException {
            List<WebcamsMessage> messages = new ArrayList<>();

            reader.beginArray();
            while (reader.hasNext()) {
                messages.add(readWebcamsMessage(reader));
            }
            reader.endArray();
            return messages;
        }

        public WebcamsMessage readWebcamsMessage(JsonReader reader) throws IOException {
            String id = null;
            String status = null;
            String title = null;
            String preview = null;
            String timezone = null;
            Integer views = -1;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    id = reader.nextString();
                }
                else if (name.equals("status")) {
                    status = reader.nextString();
                }
                else if (name.equals("title")) {
                    title = reader.nextString();
                }
                else if (name.equals("image")) {
                    preview = readCurrent(reader);
                }
                else if (name.equals("location")) {
                    timezone = readTimeZone(reader);
                }
                else if (name.equals("statistics")) {
                    reader.beginObject();
                    reader.nextName();
                    views = reader.nextInt();
                    reader.endObject();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return new WebcamsMessage(id, status, title, preview, timezone, views);
        }


        public String readCurrent(JsonReader reader) throws IOException {
            String preview = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("current")) {
                    preview = readPreview(reader);
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            return preview;
        }
        public String readPreview(JsonReader reader) throws IOException {
            String preview = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("preview")) {
                    preview = reader.nextString();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            return preview;
        }

        public String readTimeZone(JsonReader reader) throws IOException {
            String timezone = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("timezone")) {
                    timezone = reader.nextString();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            return timezone;
        }

        //["result"]["webcams"][0]["id"]    ("1350096618")
        //["result"]["webcams"][0]["status"] if "active"
        //["result"]["webcams"][0]["title"]   ("St Petersburg: Saint Petersburg − Intermodal Terminal")
        //["result"]["webcams"][0]["image"]["current"]["preview"] ("https://images.webcams.travel/preview/1240664025.jpg")
        //["result"]["webcams"][0]["location"]["timezone"]  ("Europe/Moscow")
        //["result"]["webcams"][0]["statistics"]["views"]   (28718)
        private class WebcamsMessage {
            private String id;
            private String status;
            private String title;
            private String preview;
            private String timezone;
            private Integer views;
            private Bitmap bitmap = null;

            public WebcamsMessage(String id, String status, String title, String preview, String timezone, Integer views) {
                this.id = id;
                this.status = status;
                this.title = title;
                this.preview = preview;
                this.timezone = timezone;
                this.views = views;
            }

            public String getId() {
                return id;
            }

            public String getStatus() {
                return status;
            }

            public String getTitle() {
                return title;
            }

            public String getPreview() {
                return preview;
            }

            public String getTimezone() {
                return timezone;
            }

            public Integer getViews() {
                return views;
            }

            public Bitmap getBitmap() {
                return bitmap;
            }

            public void setBitmap(Bitmap bitmap) {
                this.bitmap = bitmap;
            }
        }
    }

    private static final String TAG = "CityCam";
}
