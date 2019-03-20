package ru.android_2019.citycam.async_tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.connection_api.ConnectionApi;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.parsers.ResponseWebcamParser;
import ru.android_2019.citycam.webcams.Webcams;

public final class DownloadImageTask extends AsyncTask<City, Integer, Webcam> {


    private DownloadCallbacks callbacks;

    public DownloadImageTask(DownloadCallbacks callbacks) {
        this.callbacks = callbacks;
    }


    @SuppressLint("WrongThread")
    @Override
    protected Webcam doInBackground(City... cities) {
        City city = cities[0];
        InputStream in = null;
        HttpURLConnection connection = null;
        Webcam webcam = null;
        try {
            URL url = Webcams.createNearbyUrl(city.latitude, city.longitude);
            connection = ConnectionApi.getConnection(url);
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException();
            }
            in = connection.getInputStream();
            List<Webcam> webcams = ResponseWebcamParser.listResponseWebcam(in, "UTF-8");
            webcam = webcams != null && !webcams.isEmpty() ? webcams.get(new Random().nextInt(webcams.size())) : null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(String.valueOf(this), "AsyncTask");
        return webcam;
    }


    @Override
    protected void onPostExecute(Webcam webcam) {
        callbacks.onPostExecute(webcam);
    }

    @Override
    protected void onProgressUpdate(Integer... percent) {
        super.onProgressUpdate(percent);
        callbacks.onProgressUpdate(percent[0]);
    }
}