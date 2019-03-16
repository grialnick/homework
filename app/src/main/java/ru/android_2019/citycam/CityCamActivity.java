package ru.android_2019.citycam;

import android.annotation.SuppressLint;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

public class CityCamActivity extends AppCompatActivity {

    public static final String EXTRA_CITY = "city";

    private City city;

    private TextView titleView;
    private TextView statusView;
    private ImageView imageView;
    private TextView viewsView;
    private TextView cityView;
    private TextView timeZoneView;
    private TextView idView;
    private ProgressBar progressView;

    private DownLoadWebCamTask downloadTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }
        setContentView(R.layout.activity_city_cam);
        initializeUI();
        getSupportActionBar().setTitle(city.name);
        if (savedInstanceState != null) {
            downloadTask = (DownLoadWebCamTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            try {
                downloadTask = new DownLoadWebCamTask(this);
                downloadTask.execute(Webcams.createNearbyUrl(city.latitude, city.longitude));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            downloadTask.attachActivity(this);
        }
    }

    private void initializeUI() {
        titleView = findViewById(R.id.activity_city_cam__title);
        statusView = findViewById(R.id.activity_city_cam__status);
        imageView = findViewById(R.id.cam_image);
        viewsView = findViewById(R.id.activity_city_cam__views);
        cityView = findViewById(R.id.activity_city_cam__city);
        timeZoneView = findViewById(R.id.activity_city_cam__timeZone);
        idView = findViewById(R.id.activity_city_cam__id);
        progressView = findViewById(R.id.progress);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.downloadTask;
    }

    static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DownLoadWebCamTask extends AsyncTask<URL, Integer, WebCamMessage> {

        @SuppressLint("StaticFieldLeak")
        private CityCamActivity activity;

        private Bitmap bitmap;
        private String city;
        private String title;
        private String location;
        private String status;
        private String views;
        private long id;

        DownLoadWebCamTask(CityCamActivity activity) {
            this.activity = activity;
        }

        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }

        void updateView() {
            if (activity != null) {
                if (title != null) {
                    activity.titleView.setText(title);
                    activity.imageView.setImageBitmap(bitmap);
                    activity.cityView.setText(String.valueOf(activity.getResources().getText(R.string.city_title) + city));
                    activity.timeZoneView.setText(String.valueOf(activity.getResources().getText(R.string.time_zone) + location));
                    activity.viewsView.setText(String.valueOf(activity.getResources().getText(R.string.views) + views));
                    activity.idView.setText(String.valueOf(activity.getResources().getText(R.string.id) + "" + id));
                } else {
                    activity.titleView.setText(activity.getResources().getText(R.string.error));
                }
                activity.progressView.setVisibility(View.INVISIBLE);
                activity.statusView.setText(String.valueOf(activity.getResources().getText(R.string.status) + status));
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected WebCamMessage doInBackground(URL... list) {
            WebCamMessage message = null;
            try {
                message = downloadFile(list[0]);
                if (message != null) {
                    bitmap = CityCamActivity.getBitmapFromURL(message.getImage());
                    city = message.getLocation().getCity();
                    location = message.getLocation().getTimezone();
                    title = message.getTitle();
                    status = message.getStatus();
                    views = message.getViews();
                    id = message.getId();
                } else {
                    status = "Not active";
                }
            } catch (Exception e) {
                status = "Connection problems";
            }
            return message;
        }

        private WebCamMessage downloadFile(URL url) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = null;
            WebCamMessage message = null;
            try {
                connection.setRequestProperty("X-RapidAPI-Key", "245da2bce0msh1f4d44a59904349p1f51e2jsn78950678f482");
                connection.connect();
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    throw new UnknownHostException();
                }
                in = connection.getInputStream();
                JSonParser parser = new JSonParser();
                List<WebCamMessage> camList = parser.readJsonStream(in);
                if (camList.size() > 0) {
                    message = camList.get(new Random().nextInt(camList.size()));
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close HTTP input stream: " + e, e);
                    }
                }
                connection.disconnect();
            }
            return message;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(WebCamMessage message) {
            updateView();
        }

    }

    private static final String TAG = "CityCam";

}
