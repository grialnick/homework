package ru.android_2019.citycam;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ru.android_2019.citycam.async_task.DownloadImageTask;
import ru.android_2019.citycam.async_task.TaskCallbacks;
import ru.android_2019.citycam.model.City;


public class TaskFragment extends Fragment {

    public static final String CURRENT_CITY = "CURRENT_CITY";
    private TaskCallbacks callbacks;
    private City city;
    private DownloadImageTask downloadImageTask;

    @Override
    public void onDetach() {
        super.onDetach();
        downloadImageTask = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (TaskCallbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        downloadImageTask = new DownloadImageTask(callbacks);
        downloadImageTask.execute(city);
    }



}
