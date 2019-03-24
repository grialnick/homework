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
import android.widget.TextView;
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
import ru.android_2019.citycam.model.inbound.Category;
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
    private TextView cityTextView;
    private TextView regionTextView;
    private TextView countryTextView;
    private TextView continentTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView timezoneTextView;
    private TextView wiki_linkTextView;
    private TextView categoriesTextView;
    private TextView viewsTextView;
    private TextView webcam_linkTextView;


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
        cityTextView = findViewById(R.id.city);
        regionTextView = findViewById(R.id.region);
        countryTextView = findViewById(R.id.country);
        continentTextView = findViewById(R.id.continent);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        timezoneTextView = findViewById(R.id.timezone);
        wiki_linkTextView = findViewById(R.id.wiki_link);
        categoriesTextView = findViewById(R.id.categories);
        viewsTextView = findViewById(R.id.views);
        webcam_linkTextView = findViewById(R.id.webcam_link);

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
                finish();
            }
        } else {
            downloadTask.attachActivity(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.downloadTask;
    }

    private static class WebcamEntity {

        private WebcamResponse response;
        private Bitmap image;
        private int choosenCam;

        public WebcamEntity(WebcamResponse response, Bitmap image, int choosenCam) {
            this.response = response;
            this.image = image;
            this.choosenCam = choosenCam;
        }

        private int getChoosenCam() {
            return choosenCam;
        }

        private void setChoosenCam(int choosenCam) {
            this.choosenCam = choosenCam;
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

    private class DownloadTask extends AsyncTask<URL, Integer, WebcamEntity> {
        private static final String HEADER_KEY = "X-RapidAPI-Key";
        private static final String HEADERUE = "34e3573172mshba6a93651c07a6ap14feedjsn9dd95419b0b0";
        private CityCamActivity activity;
        private Integer progress;
        private Context appContext;
        private WebcamEntity webcamEntity;

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
                if (webcamEntity.getImage() == null) {
                    Toast.makeText(activity, R.string.no_cam, Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else {
                    activity.camImageView.setImageBitmap(webcamEntity.getImage());
                    activity.camImageView.setVisibility(View.VISIBLE);
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getCity().isEmpty()) {
                        activity.cityTextView.append(webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getCity());
                    } else {
                        activity.cityTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getRegion().isEmpty()) {
                        activity.regionTextView.append(webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getRegion());
                    } else {
                        activity.regionTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getCountry().isEmpty()) {
                        activity.countryTextView.append(webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getCountry());
                    } else {
                        activity.countryTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getContinent().isEmpty()) {
                        activity.continentTextView.append(webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getContinent());
                    } else {
                        activity.continentTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getLatitude() != null) {
                        activity.latitudeTextView.append(String.format("%f", webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getLatitude()));
                    } else {
                        activity.latitudeTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getLongitude() != null) {
                        activity.longitudeTextView.append(String.format("%f", webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getLongitude()));
                    } else {
                        activity.longitudeTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getTimezone().isEmpty()) {
                        activity.timezoneTextView.append(webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getTimezone());
                    } else {
                        activity.timezoneTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getWikipedia().isEmpty()) {
                        activity.wiki_linkTextView.append(webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getLocation().getWikipedia());
                    } else {
                        activity.wiki_linkTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getCategory().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Category category : webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getCategory()) {
                            sb.append(category.getName().toLowerCase());
                            sb.append(", ");
                        }
                        sb.replace(sb.length() - 2, sb.length() - 1, "");
                        activity.categoriesTextView.append(sb.toString());
                    } else {
                        activity.categoriesTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getStatistics().getViews() != null) {
                        activity.viewsTextView.append(String.format("%d", webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getStatistics().getViews()));
                    } else {
                        activity.viewsTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam()).getUrl().getCurrent().getDesktop().isEmpty()) {
                        activity.webcam_linkTextView.append(webcamEntity.getResponse().getResult()
                                .getWebcams().get(webcamEntity.getChoosenCam()).getUrl().getCurrent().getDesktop());
                    } else {
                        activity.webcam_linkTextView.append(getResources().getString(R.string.no_info));
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {
            activity.progressView.setVisibility(View.VISIBLE);
        }

        protected WebcamEntity doInBackground(URL... urls) {
            URL url = urls[0];
            InputStream inputStream = null;
            HttpURLConnection imageConnection = null;
            HttpsURLConnection webcamConnection = null;
            try {
                webcamConnection = (HttpsURLConnection) url.openConnection();
                webcamConnection.setRequestMethod("GET");
                webcamConnection.setDoInput(true);
                webcamConnection.setRequestProperty(HEADER_KEY, HEADERUE);
                webcamConnection.connect();
                if (webcamConnection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    throw new IllegalArgumentException(webcamConnection.getResponseMessage());
                }
                webcamEntity = createWebcam(webcamConnection.getInputStream());
                if (webcamEntity.getResponse().getResult().getWebcams().size() == 0) {
                    Log.w(TAG, "No cams there");
                    throw new NoSuchElementException("No cams there");
                }
                webcamEntity.setChoosenCam(new Random().nextInt(webcamEntity.getResponse().getResult().getWebcams().size()));
                URL imageUrl = new URL(webcamEntity.getResponse().getResult().getWebcams()
                        .get(webcamEntity.getChoosenCam())
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
                    webcamEntity.setImage(BitmapFactory.decodeStream(inputStream));
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
            return webcamEntity;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(WebcamEntity webcamEntity) {
            progress = 100;
            updateView();
        }

        private WebcamEntity createWebcam(InputStream inputStream) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();
            try {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            WebcamEntity webcamEntity = new WebcamEntity(null, null, 0);
            webcamEntity.setResponse(Serializer.getInstance().fromJson(response.toString(), WebcamResponse.class));
            return webcamEntity;
        }

    }

    private static final String TAG = "CityCam";
}
