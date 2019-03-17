package ru.android_2019.citycam;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.connection_api.ConnectionApi;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.parsers.ResponseWebcamParser;
import ru.android_2019.citycam.repository.WebcamsRepository;
import ru.android_2019.citycam.webcams.Webcams;


public class TaskFragment extends Fragment  {

    private static final String TAG = "Task_Fragment";
    private DownloadCallbacks callbacks;
    private DownloadImageTask downloadImageTask;
    private City currentCity;
    private static final String cityTag = "CURRENT_CITY_TAG";

    public static  TaskFragment getInstance(FragmentManager fragmentManager, City city) {

        TaskFragment taskFragment = (TaskFragment) fragmentManager.findFragmentByTag(TaskFragment.TAG);
        if(taskFragment == null) {
            taskFragment = new TaskFragment();
            Bundle args = new Bundle();
            args.putParcelable(cityTag,city);
            taskFragment.setArguments(args);
            fragmentManager.beginTransaction().add(taskFragment, TAG).commit();
        }
        return taskFragment;
    }

    //TODO выяснить, почему не работают onCreate и onAttach
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        currentCity = getArguments().getParcelable(cityTag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (DownloadCallbacks) context;
        Log.d(String.valueOf(this), "Fragment");
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
            downloadImageTask = null;
        }
    }

    private static final class DownloadImageTask extends AsyncTask <City, Integer, Bitmap> {

        private WebcamsRepository webcamsRepository = WebcamsRepository.getInstance();
        private DownloadCallbacks callbacks;

        DownloadImageTask (DownloadCallbacks callbacks) {
            this.callbacks = callbacks;
        }


        @Override
        protected Bitmap doInBackground(City... cities) {
            City city = cities[0];
            InputStream in = null;
            HttpURLConnection connection = null;
            Bitmap bitmap = null;
            try {
                URL url = Webcams.createNearbyUrl(city.getLatitude(), city.getLongitude());
                connection = ConnectionApi.getConnection(url);
                connection.connect();
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException();
                }
                in = connection.getInputStream();
                List<Webcam> webcams = ResponseWebcamParser.listResponseWebcam(in, "UTF-8");
                webcamsRepository.putWebcamsListInRepository(webcams);
                bitmap = webcamsRepository.getWebcamFromRepository().getImage();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            finally {
                if(connection != null) {
                    connection.disconnect();
                }
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(String.valueOf(this), "AsyncTask");
            return bitmap;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null && callbacks != null) {
                callbacks.updateFromDownload(bitmap);
                callbacks.finishDownloading();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


    }
}
