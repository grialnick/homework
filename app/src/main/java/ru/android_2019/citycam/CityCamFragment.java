package ru.android_2019.citycam;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Objects;

import ru.android_2019.citycam.async_tasks.DownloadImageTask;
import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;


public class CityCamFragment extends Fragment implements DownloadCallbacks {

    private static final String CITY = "extra_city";
    private DownloadCallbacks callbacks;
    private boolean isActivityCreated = false;
    private List <Webcam> webcamList;

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
        City city = Objects.requireNonNull(getArguments()).getParcelable(CITY);
        DownloadImageTask downloadImageTask = new DownloadImageTask(this);
        downloadImageTask.execute(city);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isActivityCreated = true;
        if (callbacks != null && webcamList != null) {
            callbacks.onProgressUpdate(100);
            callbacks.onPostExecute(webcamList);
        }
    }



    @Override
    public void onProgressUpdate(final int percent) {
        if (isActivityCreated && callbacks != null) {
            callbacks.onProgressUpdate(percent);
        }
    }


    @Override
    public void onPostExecute(final List<Webcam> webcams) {
        webcamList = webcams;
        if (isActivityCreated && callbacks != null) {
            callbacks.onProgressUpdate(100);
            callbacks.onPostExecute(webcams);
        }
    }
}
