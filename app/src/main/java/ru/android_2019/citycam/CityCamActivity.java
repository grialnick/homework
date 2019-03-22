package ru.android_2019.citycam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.inbound.WebcamResponse;
import ru.android_2019.citycam.serializer.Serializer;
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

        if (savedInstanceState != null) {
            downloadTask = (DownloadTask) getLastNonConfigurationInstance();
        }
        if (downloadTask == null) {
            downloadTask = new DownloadTask(this);
            try {
                downloadTask.execute(Webcams.createNearbyUrl(city.latitude, city.longitude));
            } catch (MalformedURLException e) {
                Log.w(TAG, "Wrong params for creating url");
                this.finish();
            }
        } else {
            downloadTask.attachActivity(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.downloadTask;
    }

    private static class Webcam {

        private WebcamResponse response;
        private Bitmap image;

        private Webcam(WebcamResponse response, Bitmap image) {
            this.response = response;
            this.image = image;
        }

        private WebcamResponse getResponse() {
            return response;
        }

        private void setResponse(WebcamResponse response) {
            this.response = response;
        }

        private Bitmap getImage() {
            return image;
        }

        private void setImage(Bitmap image) {
            this.image = image;
        }
    }

    private class DownloadTask extends AsyncTask<URL, Integer, Webcam> {
        private static final String HEADER_KEY = "X-RapidAPI-Key";
        private static final String HEADER_VALUE = "34e3573172mshba6a93651c07a6ap14feedjsn9dd95419b0b0";
        private CityCamActivity activity;
        private Integer progress;
        private Context appContext;
        private Webcam webcam;

        DownloadTask(CityCamActivity activity) {
            this.appContext = activity.getApplicationContext();
            this.activity = activity;
        }

        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            updateView();
        }

        void updateView() {
            if (activity != null) {
                activity.progressView.setVisibility(progress != 100 ? View.VISIBLE : View.INVISIBLE);
                if (webcam.getImage() == null) {
                    Toast.makeText(activity, R.string.no_cam, Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else {
                    activity.camImageView.setImageBitmap(webcam.getImage());
                    activity.camImageView.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            activity.progressView.setVisibility(View.VISIBLE);
        }

        protected Webcam doInBackground(URL... urls) {
            URL url = urls[0];
            InputStream inputStream = null;
            HttpURLConnection imageConnection = null;
            HttpsURLConnection webcamConnection = null;
            try {
                webcamConnection = (HttpsURLConnection) url.openConnection();
                webcamConnection.setRequestMethod("GET");
                webcamConnection.setDoInput(true);
                webcamConnection.setRequestProperty(HEADER_KEY, HEADER_VALUE);
                webcamConnection.connect();
                if (webcamConnection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    throw new IllegalArgumentException(webcamConnection.getResponseMessage());
                }
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(webcamConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                webcam = new Webcam(null, null);
                webcam.setResponse(Serializer.getInstance().fromJson(response.toString(), WebcamResponse.class));
                if (webcam.getResponse().getResult().getWebcams().size() == 0) {
                    Log.w(TAG, "No cams there");
                    throw new NoSuchElementException("No cams there");
                }
                URL imageUrl = new URL(webcam.getResponse().getResult().getWebcams()
                        .get(new Random().nextInt(webcam.getResponse().getResult().getWebcams().size()))
                        .getImage().getCurrent().getPreview());
                imageConnection = (HttpURLConnection) imageUrl.openConnection();
                imageConnection.setRequestMethod("GET");
                imageConnection.setDoInput(true);
                imageConnection.connect();
                if (imageConnection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    throw new IllegalArgumentException("Download image error: " + imageUrl);
                }
                inputStream = imageConnection.getInputStream();
                if (inputStream != null) {
                    webcam.setImage(BitmapFactory.decodeStream(inputStream));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (imageConnection != null) {
                    imageConnection.disconnect();
                }
                if (webcamConnection != null) {
                    webcamConnection.disconnect();
                }
            }
            return webcam;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Webcam webcam) {
            progress = 100;
            updateView();
        }

    }

    private static final String TAG = "CityCam";
}
