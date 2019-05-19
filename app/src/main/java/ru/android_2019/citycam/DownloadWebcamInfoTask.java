package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

class DownloadWebcamInfoTask extends AsyncTask<City, Void, Webcam> {
    private CityCamActivity activity;
    private Webcam webcam = null;
    private WebcamDao webcamDao = WebcamApp.getInstance().getDatabase().webcamDao();

    public DownloadWebcamInfoTask(CityCamActivity activity) {
        this.activity = activity;
    }

    private void updateViews(Webcam webcam) {
        activity.setInvisible();
        if (webcam != null) {
            activity.setId(webcam.getId());
            activity.setBitmap(webcam.getBitmap());
            activity.setStatus(webcam.getStatus());
            activity.setTitle(webcam.getTitle());
            activity.setCity(webcam.getLocation().getCity());
            activity.setRegion(webcam.getLocation().getRegion());
            activity.setCountry(webcam.getLocation().getCountry());
            activity.setContinent(webcam.getLocation().getContinent());
            activity.setWiki(webcam.getLocation().getWikiURL());
            activity.setViews(webcam.getViews());
        } else {
            activity.setImageNotFound();
        }
    }

    public void attachActivity(CityCamActivity activity) {
        this.activity = activity;
        updateViews(webcam);
    }

    @Override
    protected Webcam doInBackground(City... cities) {
        try {
            webcam = downloadWebcams(cities[0]);
        } catch (IOException e) {
            Log.w(CityCamActivity.TAG, e.getMessage());
        }
        return webcam;
    }

    @Override
    protected void onPostExecute(Webcam webcam) {
        updateViews(webcam);
    }

    private Webcam downloadWebcams(City city) throws IOException {
        InputStream stream = null;
        HttpsURLConnection urlConnection = null;
        List<Webcam> webcams;
        URL url = Webcams.createNearbyUrl(city.latitude, city.longitude);
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("X-RapidAPI-Key", "df6d6603ddmsh555507aa6abb190p1419f9jsnacbcaa2ba59b");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                stream = urlConnection.getInputStream();
                if (stream != null) {
                    JsonParser parser = new JsonParser();
                    webcams = parser.readJsonStream(stream);
                } else {
                    webcams = webcamDao.getByCityName(city.name);
                }
            } else {
                webcams = webcamDao.getByCityName(city.name);
            }
        } catch (IOException e) {
            webcams = webcamDao.getByCityName(city.name);
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        if (webcams != null && !webcams.isEmpty()) {
            int index = 0;
            if (webcams.size() > 1) {
                index = (new Random()).nextInt(webcams.size() - 1);
            }
            webcam = webcams.get(index);
            Bitmap bitmap = webcam.getBitmap();
            if (bitmap == null) {
                bitmap = downloadBitmap(new URL(webcam.getImageURL()));
                webcam.setCityName(city.name);
                webcam.setBitmap(bitmap);
                webcamDao.insert(webcam);
            }
        }
        return webcam;
    }

    private Bitmap downloadBitmap(URL url) throws IOException {
        HttpsURLConnection urlConnection = null;
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = urlConnection.getInputStream();
            if (stream != null) {
                bitmap = BitmapFactory.decodeStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }
}
