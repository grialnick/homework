package ru.android_2019.citycam.asyncTask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.android_2019.citycam.CityCamActivity;
import ru.android_2019.citycam.asyncTask.webcamJSONObject.WebcamInfo;
import ru.android_2019.citycam.asyncTask.webcamJSONObject.WebcamJSONReader;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

public class WebcamTask extends AsyncTask<Void, Void, Void> {

    private City city;
    private ArrayList<WebcamInfo> webcamInfos;
    private CityCamActivity activity;
    private Bitmap bitmap = null;

    public WebcamTask(CityCamActivity activity, City city) {
        this.activity = activity;
        this.city = city;
    }

    @Override
    protected Void doInBackground(Void... Void) {
        String response = null;
        try {
            response = DownloadUtils.getJSONResponse(Webcams.createNearbyUrl(city.latitude, city.longitude));
            webcamInfos = WebcamJSONReader.getWebcamList(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress();
        if (webcamInfos != null) {
            try {
                bitmap = DownloadUtils.getBitmap(new URL(webcamInfos.get(0).getURLPreviewImage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, webcamInfos.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.updateView(webcamInfos, bitmap);
    }

    @Override
    protected void onProgressUpdate(Void... Void) {
        super.onProgressUpdate(Void);
        activity.updateView(webcamInfos, bitmap);
    }

    public static final String TAG = "WebcanTask";

    public void attachActivity(CityCamActivity cityCamActivity) {
        this.activity = cityCamActivity;
        activity.updateView(webcamInfos, bitmap);
    }
}
