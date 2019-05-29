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

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.inbound.Category;
import ru.android_2019.citycam.model.inbound.Webcam;
import ru.android_2019.citycam.model.inbound.WebcamResponse;
import ru.android_2019.citycam.serializer.Serializer;
import ru.android_2019.citycam.webcams.Webcams;

import static ru.android_2019.citycam.util.ConnectivityUtils.isConnected;

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
            downloadTask = (DownloadTask) getLastCustomNonConfigurationInstance();
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

        WebcamEntity(WebcamResponse response, Bitmap image, int choosenCam) {
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
        private DiskLruCache diskLruCache;

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
                if (webcamEntity == null) {
                    Toast.makeText(activity, R.string.no_network, Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else if (webcamEntity.getImage() == null) {
                    Toast.makeText(activity, R.string.no_cam, Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else {
                    if (!isConnected(appContext)) {
                        Toast.makeText(activity, R.string.cached, Toast.LENGTH_SHORT).show();
                    }
                    activity.camImageView.setImageBitmap(webcamEntity.getImage());
                    activity.camImageView.setVisibility(View.VISIBLE);
                    Webcam choosenCam = webcamEntity.getResponse().getResult().getWebcams().get(webcamEntity.getChoosenCam());
                    if (!choosenCam.getLocation().getCity().isEmpty()) {
                        activity.cityTextView.append(choosenCam.getLocation().getCity());
                    } else {
                        activity.cityTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!choosenCam.getLocation().getRegion().isEmpty()) {
                        activity.regionTextView.append(choosenCam.getLocation().getRegion());
                    } else {
                        activity.regionTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!choosenCam.getLocation().getCountry().isEmpty()) {
                        activity.countryTextView.append(choosenCam.getLocation().getCountry());
                    } else {
                        activity.countryTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!choosenCam.getLocation().getContinent().isEmpty()) {
                        activity.continentTextView.append(choosenCam.getLocation().getContinent());
                    } else {
                        activity.continentTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (choosenCam.getLocation().getLatitude() != null) {
                        activity.latitudeTextView.append(String.format("%f", choosenCam.getLocation().getLatitude()));
                    } else {
                        activity.latitudeTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (choosenCam.getLocation().getLongitude() != null) {
                        activity.longitudeTextView.append(String.format("%f", choosenCam.getLocation().getLongitude()));
                    } else {
                        activity.longitudeTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!choosenCam.getLocation().getTimezone().isEmpty()) {
                        activity.timezoneTextView.append(choosenCam.getLocation().getTimezone());
                    } else {
                        activity.timezoneTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!choosenCam.getLocation().getWikipedia().isEmpty()) {
                        activity.wiki_linkTextView.append(choosenCam.getLocation().getWikipedia());
                    } else {
                        activity.wiki_linkTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!choosenCam.getCategory().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Category category : choosenCam.getCategory()) {
                            sb.append(category.getName().toLowerCase());
                            sb.append(", ");
                        }
                        sb.replace(sb.length() - 2, sb.length() - 1, "");
                        activity.categoriesTextView.append(sb.toString());
                    } else {
                        activity.categoriesTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (choosenCam.getStatistics().getViews() != null) {
                        activity.viewsTextView.append(String.format("%d", choosenCam.getStatistics().getViews()));
                    } else {
                        activity.viewsTextView.append(getResources().getString(R.string.no_info));
                    }
                    if (!choosenCam.getUrl().getCurrent().getDesktop().isEmpty()) {
                        activity.webcam_linkTextView.append(choosenCam.getUrl().getCurrent().getDesktop());
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

        /**
         * Method checks internet connection at start.
         * If device is connected - gets info from server and update disk cache.
         * Else - tries to get data from disk cache.
         * Cache format:
         * [server response in JSON][num of choosen cam in int][webcam image in Bitmap].
         *
         * @param urls url of city at [0]
         * @return WebcamEntity of the some cam of this city or null
         * if there were problems during the process.
         */

        protected WebcamEntity doInBackground(URL... urls) {
            URL url = urls[0];
            InputStream inputStream = null;
            OutputStream diskOutputStream = null;
            HttpURLConnection imageConnection = null;
            HttpsURLConnection webcamConnection = null;
            String hashedCityName = String.valueOf(UUID.nameUUIDFromBytes(city.name.getBytes()).getMostSignificantBits());
            try {
                diskLruCache = DiskLruCache.open(appContext.getCacheDir(), 2, 3, 52428800);
                if (isConnected(appContext)) {

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
                    DiskLruCache.Editor editor = diskLruCache.edit(hashedCityName);

                    editor.set(0, Serializer.getInstance().toJson(webcamEntity.getResponse()));
                    editor.set(1, String.valueOf(webcamEntity.getChoosenCam()));
                    diskOutputStream = editor.newOutputStream(2);
                    flowStreams(imageConnection.getInputStream(), diskOutputStream);
                    diskOutputStream.flush();
                    diskOutputStream.close();
                    editor.commit();
                    inputStream = diskLruCache.get(hashedCityName).getInputStream(2);

                    if (inputStream != null) {
                        webcamEntity.setImage(BitmapFactory.decodeStream(inputStream));
                    }
                } else {
                    if (diskLruCache.get(hashedCityName) != null) {
                        webcamEntity = createWebcamFromCache(diskLruCache.get(hashedCityName));
                    } else {
                        Log.w(TAG, "Check internet connection");
                        throw new IllegalStateException("Internet connection needed");
                    }
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
                if (diskLruCache != null) {
                    try {
                        diskLruCache.flush();
                        diskLruCache.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (diskOutputStream != null) {
                    try {
                        diskOutputStream.flush();
                        diskOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

        private WebcamEntity createWebcamFromCache(DiskLruCache.Snapshot snapshot) throws IOException {
            WebcamEntity webcamEntity = createWebcam(snapshot.getInputStream(0));
            webcamEntity.setChoosenCam(Integer.parseInt(snapshot.getString(1)));
            webcamEntity.setImage(BitmapFactory.decodeStream(snapshot.getInputStream(2)));
            return webcamEntity;
        }

        void flowStreams(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead == -1)
                    break;
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private static final String TAG = "CityCam";
}
