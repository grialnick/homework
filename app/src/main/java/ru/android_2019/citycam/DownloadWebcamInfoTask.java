package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

class DownloadWebcamInfoTask extends AsyncTask<URL, Void, Webcam> {
    private CityCamActivity activity;
    private Webcam webcam = null;

    DownloadWebcamInfoTask(CityCamActivity activity) {
        this.activity = activity;
    }

    private void updateViews() {
        activity.setInvisible();
        if (webcam != null) {
            activity.setBitmap(webcam.getBitmap());
            activity.setId(webcam.getId());
            activity.setStatus(webcam.getStatus());
            activity.setTitle(webcam.getTitle());
            activity.setCity(webcam.getLocation().getCity());
            activity.setRegion(webcam.getLocation().getRegion());
            activity.setCountry(webcam.getLocation().getCountry());
            activity.setContinent(webcam.getLocation().getContinent());
            activity.setWiki(webcam.getLocation().getWikiURL());
            activity.setViews(webcam.getViews());
        } else {
            activity.setBitmap(null);
            activity.setId(null);
            activity.setStatus(null);
            activity.setTitle(null);
            activity.setCity(null);
            activity.setRegion(null);
            activity.setCountry(null);
            activity.setContinent(null);
            activity.setWiki(null);
            activity.setViews(null);
        }
    }

    public void attachActivity(CityCamActivity activity) {
        this.activity = activity;
        updateViews();
    }

    @Override
    protected Webcam doInBackground(URL... urls) {
        try {
            webcam = downloadWebcam(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return webcam;
    }

    @Override
    protected void onPostExecute(Webcam webcam) {
        updateViews();
    }


    private Webcam downloadWebcam(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection urlConnection = null;
        List<Webcam> webcams = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("X-RapidAPI-Key", "df6d6603ddmsh555507aa6abb190p1419f9jsnacbcaa2ba59b");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = urlConnection.getInputStream();
            if (stream != null) {
                JsonParser parser = new JsonParser();
                webcams = parser.readJsonStream(stream);
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        if (webcams.isEmpty()) {
            return null;
        }
        int index = 0;
        if (webcams.size() > 1) {
            index = (new Random()).nextInt(webcams.size() - 1);
        }
        Webcam webcam = webcams.get(index);
        try {
            if (webcam.getImageURL() == null) {
                return webcam;
            }
            URL imageUrl = new URL(webcam.getImageURL());
            urlConnection = (HttpsURLConnection) imageUrl.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = urlConnection.getInputStream();
            if (stream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                webcam.setBitmap(bitmap);
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return webcam;
    }
}
