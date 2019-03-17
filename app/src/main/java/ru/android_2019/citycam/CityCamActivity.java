package ru.android_2019.citycam;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.model.City;



/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity  implements DownloadCallbacks {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    private City city;
    private ImageView camImageView;
    private ProgressBar progressView;
    private TaskFragment mTaskFragment;
    private boolean downloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }
        setContentView(R.layout.activity_city_cam);
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);
        getSupportActionBar().setTitle(city.getName());
        progressView.setVisibility(View.VISIBLE);
        progressView.setMax(100);
        mTaskFragment = TaskFragment.getInstance(getSupportFragmentManager(), city);

        Log.d(String.valueOf(this), "Activity");
        startDownload();
    }

    private void startDownload() {
        if(!downloading && mTaskFragment != null) {
            mTaskFragment.startDownload();
            downloading = true;
        }
    }

    private static final String TAG = "CityCam";

    @Override
    public void updateFromDownload(Bitmap image) {
        camImageView.setImageBitmap(image);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch (progressCode) {
            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;

            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;

            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
            default:
                break;
        }
    }

    @Override
    public void finishDownloading() {
        downloading = false;
        if(mTaskFragment != null) {
            mTaskFragment.cancelDownload();
        }
    }
}
