package ru.android_2019.citycam.async_task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ru.android_2019.citycam.connection_api.ConnectionApi;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.parsers.ResponseWebcamParser;
import ru.android_2019.citycam.webcams.Webcams;

public class DownloadImageTask extends AsyncTask <City, Integer, Bitmap> {

    private TaskCallbacks callbacks;

    public DownloadImageTask (TaskCallbacks callbacks) {
        this.callbacks = callbacks;
    }


    @Override
    protected void onPreExecute() {
        callbacks.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        callbacks.onPostExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... percent) {
        callbacks.onProgressUpdate(percent[0]);
    }


    @Override
    protected void onCancelled() {
        callbacks.onCancelled();
    }

    @Override
    protected Bitmap doInBackground(City... cities) {
        City city = cities[0];
        InputStream in;
        HttpURLConnection connection = null;
        try {
            URL url = Webcams.createNearbyUrl(city.getLatitude(), city.getLongitude());
            connection = ConnectionApi.getConnection(url);
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException();
            }
            in = connection.getInputStream();
            List <Webcam> webcams = ResponseWebcamParser.listResponseWebcam(in, "UTF-8");
            String i = null;

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
