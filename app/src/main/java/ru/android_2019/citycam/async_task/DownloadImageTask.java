package ru.android_2019.citycam.async_task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.connection_api.ConnectionApi;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.parsers.ResponseWebcamParser;
import ru.android_2019.citycam.repository.WebcamsRepository;
import ru.android_2019.citycam.webcams.Webcams;

public class DownloadImageTask extends AsyncTask <City, Integer, Bitmap> {


    private WebcamsRepository webcamsRepository = WebcamsRepository.getInstance();
    private DownloadCallbacks callbacks;

    public DownloadImageTask(DownloadCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null && callbacks != null) {
            callbacks.updateFromDownload(bitmap);
            callbacks.finishDownloading();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Bitmap doInBackground(City... cities) {
        City city = cities[0];
        InputStream in = null;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            URL url = Webcams.createNearbyUrl(city.getLatitude(), city.getLongitude());
            connection = ConnectionApi.getConnection(url);
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException();
            }
            in = connection.getInputStream();
            List <Webcam> webcams = ResponseWebcamParser.listResponseWebcam(in, "UTF-8");
            webcamsRepository.putWebcamsListInRepository(webcams);
            bitmap = webcamsRepository.getWebcamFromRepository().getImage();
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
        return bitmap;
    }
}
