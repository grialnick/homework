package ru.android_2019.citycam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcam;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";
    public static final String SAVED_WEBCAM = "webcam";

    private City city;
    private Webcam webcam;
    private WebcamTask webcamTask;

    private ImageView camImageView;
    private ProgressBar progressView;
    private TextView titleView;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = findViewById(R.id.cam_image);
        progressView = findViewById(R.id.progress);
        titleView = findViewById(R.id.title_text);
        statusView = findViewById(R.id.status_text);

        getSupportActionBar().setTitle(city.name);

        if (savedInstanceState != null) {
            webcam = (Webcam) savedInstanceState.getSerializable(SAVED_WEBCAM);
            if (webcam != null) {
                updateWebcam(webcam);
            }
        }

        webcamTask = (WebcamTask) getLastCustomNonConfigurationInstance();
        if (webcamTask == null) {
            webcamTask = new WebcamTask(this, city);
            webcamTask.execute();
        } else {
            webcamTask.attachActivity(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        super.onRetainCustomNonConfigurationInstance();
        if (webcamTask != null) {
            webcamTask.detachActivity();
        }
        return webcamTask;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_WEBCAM, webcam);
        super.onSaveInstanceState(outState);
    }

    public void updateWebcam(Webcam webcam) {
        this.webcam = webcam;
        if (webcam != null) {
            camImageView.setImageBitmap(webcam.getPreview());
            progressView.setVisibility(View.GONE);
            titleView.setText(webcam.getTitle());
            statusView.setText(webcam.getStatus());
        }
    }

    private static final String TAG = "CityCam";
}
