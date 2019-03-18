package ru.android_2019.citycam;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import ru.android_2019.citycam.async_tasks.DownloadImageTask;
import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;

public class CityCamFragment extends Fragment implements DownloadCallbacks {

    public static final String EXTRA_CITY = "city";
    private DownloadCallbacks callbacks;
    private City city;
    private Webcam onProgressWebcam;
    private DownloadImageTask downloadImageTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        downloadImageTask = new DownloadImageTask(this);
        city = getArguments().getParcelable(EXTRA_CITY);
        downloadImageTask.execute(city);
        Log.d(String.valueOf(this), "Fragment Create!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = context instanceof DownloadCallbacks ? (DownloadCallbacks) context : null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        downloadImageTask.cancel(true);
        downloadImageTask = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(callbacks != null && onProgressWebcam != null) {
            callbacks.onPostExecute(onProgressWebcam);
        }
    }

    @Override
    public void onCancelled() {
        if(callbacks != null) {
            callbacks.onCancelled();
        }
    }

    @Override
    public void onPostExecute(final Webcam webcam) {
        this.onProgressWebcam = webcam;
        if(callbacks != null) {
            callbacks.onPostExecute(onProgressWebcam);
        }
    }

    @Override
    public void onPreExecute() {
        if(callbacks != null) {
            callbacks.onPreExecute();
        }
    }

    @Override
    public void onProgressUpdate(int percent) {
        if(callbacks != null) {
            callbacks.onProgressUpdate(percent);
        }
    }
}
