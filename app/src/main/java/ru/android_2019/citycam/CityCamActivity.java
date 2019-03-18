package ru.android_2019.citycam;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.android_2019.citycam.callbacks.DownloadCallbacks;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;


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
    private TextView webcamTitle;
    private Fragment cityCamFragment;


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
        webcamTitle = findViewById(R.id.activity_city__title);
        getSupportActionBar().setTitle(city.getName());
        progressView.setVisibility(View.VISIBLE);
        progressView.setMax(100);
        if(savedInstanceState != null) {
            cityCamFragment = (Fragment) getLastNonConfigurationInstance();
        }
        if(cityCamFragment == null) {
            cityCamFragment = new CityCamFragment();
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_CITY, city);
            cityCamFragment.setArguments(args);
        }


        Log.d(String.valueOf(this), "CityCamActivity");
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.cityCamFragment;
    }

    private static final String TAG = "CityCam";

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(Webcam webcam) {
        camImageView.setImageBitmap(webcam.getImage());
        webcamTitle.setText(webcam.getTitle());
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }
}
