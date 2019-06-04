package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import java.net.MalformedURLException;
import android.util.Log;
import android.view.View;

import ru.android_2019.citycam.webcams.Webcams;
import ru.android_2019.citycam.webcams.HttpConnect;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.ParserJson;

public final class CamGetDataTask extends AsyncTask<City, Void, CamDataScreen>{
    private CityCamActivity cityCamActivity;

    private CamDataScreen dataScreen = null;
    CamGetDataTask (CityCamActivity city){
        this.cityCamActivity = city;
    }

    void RefreshSreen() {
        if (cityCamActivity !=null && dataScreen != null) {
            cityCamActivity.status.setText("Status: "+dataScreen.getCamStatus());
            cityCamActivity.title.setText("Title: "+dataScreen.getCamTitle());
            cityCamActivity.id.setText("ID Cam: "+dataScreen.getCamId());
            cityCamActivity.progressView.setVisibility(View.INVISIBLE);
            cityCamActivity.camImageView.setImageBitmap(dataScreen.getImage());
            cityCamActivity.id_cam = dataScreen.getCamId();
            Log.w(TAG, "UpdateView Ok");
        } else {
            cityCamActivity.status.setText("Status: Empty");
            cityCamActivity.title.setText("Title: Empty");
            cityCamActivity.id.setText("ID Cam: Empty");
            cityCamActivity.progressView.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onPreExecute() {
        cityCamActivity.progressView.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPostExecute(CamDataScreen resul) {
        RefreshSreen();
    }

    void onAttach(CityCamActivity act) {
        this.cityCamActivity = act;
        RefreshSreen();
    }

    @Override
    protected CamDataScreen doInBackground(City... cities){
        City city = cities[0];
        URL url = null;

        try{
            url = Webcams.createNearbyUrl(city.latitude,city.longitude);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            InputStream stream = HttpConnect.httpUrlConnection(url);
            if (stream != null) {
                List<CamDataScreen> dataSreenList = ParserJson.getJsonStream(stream);
                if (!dataSreenList.isEmpty()) {
                    dataScreen = dataSreenList.get(0);
                    if (dataScreen.getCamId()  != cityCamActivity.id_cam ){
                        Log.w(TAG, "Is not to cache:"+dataScreen.getCamId()+" "+cityCamActivity.id_cam);
                        Bitmap img = CamGetImage(dataScreen.getUrl());
                        cityCamActivity.lruCacheBitmap.putLruCache(dataScreen.getCamId(),img);
                        dataScreen.putImage(cityCamActivity.lruCacheBitmap.getLruCache(dataScreen.getCamId()));
                    }else {
                        dataScreen.putImage(cityCamActivity.lruCacheBitmap.getLruCache(dataScreen.getCamId()));
                    }
                    stream.close();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.w(TAG, "End doInBackground:"+dataScreen.getCamId()+", "+dataScreen.getCamTitle()+
                    ", "+dataScreen.getCamStatus()+", "+dataScreen.getUrl());
        }
        return dataScreen;
    }
    private Bitmap CamGetImage(URL url) throws IOException {
        Bitmap img = null;
        try {
            InputStream stream = HttpConnect.httpsUrlConnection(url);
            if (stream != null) {
                img = BitmapFactory.decodeStream(stream);
                stream.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return img;
    }
    private static final String TAG = "CityCam";
}
