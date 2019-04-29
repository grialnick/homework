package ru.android_2019.citycam.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ru.android_2019.citycam.app.App;
import ru.android_2019.citycam.callbacks.CamLoadCallbacks;
import ru.android_2019.citycam.connection_api.ConnectionApi;
import ru.android_2019.citycam.dao.CamDAO;
import ru.android_2019.citycam.model.Cam;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.parser.ResponseParser;
import ru.android_2019.citycam.webcams.Webcams;

public class DownloadCamTask extends AsyncTask <City, Integer, List<Cam>> {

    private CamLoadCallbacks camLoadCallbacks;
    private List <Cam> cams;

    public DownloadCamTask(CamLoadCallbacks camLoadCallbacks) {
        this.camLoadCallbacks = camLoadCallbacks;
    }

    public void bindContext(CamLoadCallbacks camLoadCallbacks) {
        this.camLoadCallbacks = camLoadCallbacks;
        if(this.camLoadCallbacks != null && this.cams != null) {
            camLoadCallbacks.onPostExecute(this.cams);
            camLoadCallbacks.onProgressUpdate(100);
        }
    }

    @Override
    protected List <Cam> doInBackground(City... cities) {
        City city = cities[0];
        CamDAO dao = App.getInstance().getCamDatabase().getCamDAO();
        List <Cam> cams = null;
        try {
            cams = dao.selectByName(city.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(cams != null && cams.isEmpty()) {
            InputStream in = null;
            HttpURLConnection connection = null;
            try {
                URL url = Webcams.createNearbyUrl(city.latitude, city.longitude);
                connection = ConnectionApi.getConnection(url);
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException();
                }
                in = connection.getInputStream();
                cams = ResponseParser.listResponseWebcam(in, "UTF-8");
                if (cams != null && !cams.isEmpty()) {
                    for(Cam cam : cams) {
                        cam.setCityName(city.name);
                    }
                    dao.insert(cams);
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
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
            Log.d(String.valueOf(this), "AsyncTask");
        }
        this.cams = cams;
        return cams;
    }

    @Override
    protected void onPostExecute(List<Cam> cams) {
        super.onPostExecute(cams);
        camLoadCallbacks.onPostExecute(cams);
        onProgressUpdate(100);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        camLoadCallbacks.onProgressUpdate(values[0]);
    }
}