package ru.android_2019.citycam;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;

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
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private ImageView camImageView;
    private ProgressBar progressView;
    private TextView webcamTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        City city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }
        setContentView(R.layout.activity_city_cam);
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);
        webcamTitle = findViewById(R.id.activity_city__title);
        Objects.requireNonNull(getSupportActionBar()).setTitle(city.name);
        progressView.setVisibility(View.VISIBLE);
        progressView.setMax(100);

        if(getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(CityCamFragment.newInstance(city), TAG_TASK_FRAGMENT)
                    .commit();
        }
        Log.d(String.valueOf(this), "CityCamActivity");
    }

    private static final String TAG = "CityCam";


    @SuppressLint("SetTextI18n")
    @Override
    public void onPostExecute(Webcam webcam) {
        if(webcam == null) {
            camImageView.setImageResource(R.drawable.image);
            webcamTitle.setText("Sorry, we not found any Webcam in this City:(");
        } else {
            camImageView.setImageBitmap(webcam.getImage());
            webcamTitle.setText(webcam.getTitle());
        }
    }


    @Override
    public void onProgressUpdate(int percent) {
        if (percent == 100) {
            progressView.setVisibility(View.GONE);
        }
    }
}