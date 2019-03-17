package ru.android_2019.citycam;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.android_2019.citycam.async_task.DownloadImageTask;
import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.model.City;


public class TaskFragment extends Fragment  {

    private static final String TAG = "Task_Fragment";
    private DownloadCallbacks callbacks;
    private DownloadImageTask downloadImageTask;
    private static City currentCity;

    public static  TaskFragment getInstance(FragmentManager fragmentManager, City city) {

        TaskFragment taskFragment = (TaskFragment) fragmentManager.findFragmentByTag(TaskFragment.TAG);
        if(taskFragment == null) {
            taskFragment = new TaskFragment();
            fragmentManager.beginTransaction().add(taskFragment, TAG).commit();
        }
        currentCity = city;
        return taskFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (DownloadCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onDestroy() {
        cancelDownload();
        super.onDestroy();
    }


    public void startDownload() {
        cancelDownload();
        downloadImageTask = new DownloadImageTask(callbacks);
        downloadImageTask.execute(currentCity);
    }

    public void cancelDownload() {
        if(downloadImageTask != null) {
            downloadImageTask.cancel(true);
        }
    }
}
