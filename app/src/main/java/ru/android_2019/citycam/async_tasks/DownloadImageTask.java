package ru.android_2019.citycam.async_tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.android_2019.citycam.appconfig.App;
import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.connection_api.ConnectionApi;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.parsers.ResponseWebcamParser;
import ru.android_2019.citycam.webcams.Webcams;

public final class DownloadImageTask extends AsyncTask<City, Integer, List <Webcam>> {

    private final DownloadCallbacks callbacks;

    public DownloadImageTask(@NonNull final DownloadCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @SuppressLint("WrongThread")
    @Override
    protected List<Webcam> doInBackground(City... cities) {
        final City city = cities[0];
        final List<Webcam> webcamsFromServer = getWebcamsFromServer(city);
        if (!webcamsFromServer.isEmpty()) {
            assert city.name != null;
            putWebcamsToCache(webcamsFromServer, city.name);
            return webcamsFromServer;
        }
        return getWebcamsFromCache(city);
    }

    @NonNull
    private static List<Webcam> getWebcamsFromServer(final City city) {
        InputStream in = null;
        HttpURLConnection connection = null;
        try {
            URL url = Webcams.createNearbyUrl(city.latitude, city.longitude);
            connection = ConnectionApi.getConnection(url);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Bad reply from the server");
            }
            in = connection.getInputStream();
            final List<Webcam> result = ResponseWebcamParser.listResponseWebcam(in, "UTF-8");
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    private static List<Webcam> getWebcamsFromCache(final City city){
        try {
            final List<Webcam> result = App.getInstance().getWebcamDB().webcamDao().selectListWebcamsByName(city.name);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static void putWebcamsToCache(final List<Webcam> webcams, @NonNull String cityName) {
        try {
            for(Webcam webcam : webcams) {
                webcam.setCityName(cityName);
            }
            App.getInstance().getWebcamDB().webcamDao().insertWebcams(webcams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(List <Webcam> webcams) {
        Webcam webcam = webcams != null && !webcams.isEmpty() ? webcams.get(new Random().nextInt(webcams.size())) : null;
        callbacks.onPostExecute(webcam);
    }

    @Override
    protected void onProgressUpdate(Integer... percent) {
        super.onProgressUpdate(percent);
        callbacks.onProgressUpdate(percent[0]);
    }
}
