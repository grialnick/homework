package ru.android_2019.citycam;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Objects;

import ru.android_2019.citycam.async_tasks.DownloadImageTask;
import ru.android_2019.citycam.cache.CityCache;
import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;


public class CityCamFragment extends Fragment implements DownloadCallbacks {

    private static final String CITY = "extra_city";
    private DownloadCallbacks callbacks;
    private static final Object NO_WEBCAM = new Object();
    private boolean isActivityCreated = false;
    private Object webcamToPublish = null;
    private CityCache cityCache = CityCache.getInstance();
    private City city;

    @NonNull
    public static Fragment newInstance(City city) {
        final Fragment result = new CityCamFragment();
        final Bundle args = new Bundle();
        args.putParcelable(CITY, city);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DownloadCallbacks) {
            callbacks = (DownloadCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
        isActivityCreated = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        city = Objects.requireNonNull(getArguments()).getParcelable(CITY);
        City cacheCity = cityCache.getDataFromCache(city.getName());
        if (cacheCity == null) {
            DownloadImageTask downloadImageTask = new DownloadImageTask(this);
            downloadImageTask.execute(city);
        }
        else {
            city = cacheCity;
            isActivityCreated = true;
            webcamToPublish = city.getWebcam();
            this.onProgressUpdate(100);
            this.onPostExecute((Webcam) webcamToPublish);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isActivityCreated = true;
        if (callbacks != null && webcamToPublish != null) {
            callbacks.onProgressUpdate(100);
            callbacks.onPostExecute(webcamToPublish != NO_WEBCAM? (Webcam) webcamToPublish : null);
        }
    }



    @Override
    public void onPreExecute() {
        if (isActivityCreated && callbacks != null) {
            callbacks.onPreExecute();
        }
    }

    @Override
    public void onProgressUpdate(final int percent) {
        if (isActivityCreated && callbacks != null) {
            callbacks.onProgressUpdate(percent);
        }
    }

    @Override
    public void onCancelled() {
        if (isActivityCreated && callbacks != null) {
            callbacks.onCancelled();
        }
    }

    @Override
    public void onPostExecute(final Webcam webcam) {
        webcamToPublish = webcam != null ? webcam : NO_WEBCAM;
        if (isActivityCreated && callbacks != null) {
            city.setWebcam(webcam);
            cityCache.putDataInCache(city);
            callbacks.onProgressUpdate(100);
            callbacks.onPostExecute(webcam);
        }
    }
}
