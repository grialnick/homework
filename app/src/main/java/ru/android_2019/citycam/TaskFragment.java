package ru.android_2019.citycam;



import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ru.android_2019.citycam.async_task.DownloadImageTask;
import ru.android_2019.citycam.async_task.TaskCallbacks;
import ru.android_2019.citycam.model.City;


@SuppressLint("ValidFragment")
public class TaskFragment extends Fragment {

    private TaskCallbacks callbacks;
    private City city;
    private DownloadImageTask downloadImageTask;


    public TaskFragment (City city) {
        this.city = city;
    }

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
