package ru.android_2019.citycam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import ru.android_2019.citycam.callbacks.CamLoadCallbacks;
import ru.android_2019.citycam.model.Cam;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.tasks.DownloadCamTask;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity implements CamLoadCallbacks {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";
    private static final String TAG = "CityCam";

    private ImageView camImageViewBitmap;
    private ProgressBar progressView;
    private DownloadCamTask downloadCamTask;
    private TextView camTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        City city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camTextView = findViewById(R.id.activity_city__title);
        camImageViewBitmap = findViewById(R.id.cam_image_viewBitmap);
        progressView = findViewById(R.id.progress);
        Objects.requireNonNull(getSupportActionBar()).setTitle(city.name);
        progressView.setVisibility(View.VISIBLE);
        progressView.setMax(100);
        if(savedInstanceState != null) {
            downloadCamTask = (DownloadCamTask) getLastCustomNonConfigurationInstance();
        }
        if(downloadCamTask == null) {
            downloadCamTask = new DownloadCamTask(this);
            downloadCamTask.execute(city);
        }
        else {
            downloadCamTask.bindContext(this);
        }

        Log.d(String.valueOf(this), "CityCamActivity");
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.downloadCamTask;
    }

    @Override
    public void onPostExecute(List<Cam> cams) {
        if(cams.isEmpty()) {
            camTextView.setText(getString(R.string.cant_find_camera_mesasge));
        } else {
            Cam cam = cams.get((int)(Math.random()* cams.size()));
            camTextView.setText(cam.getTitle());
            camImageViewBitmap.setImageBitmap(cam.getBitmap());
        }
    }

    @Override
    public void onProgressUpdate(int percent) {
        if (percent == 100) {
            progressView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
