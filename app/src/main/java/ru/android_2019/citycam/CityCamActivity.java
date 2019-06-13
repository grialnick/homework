package ru.android_2019.citycam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.LruCacheBitmap;


/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    private City city;

    public ImageView camImageView;
    public ProgressBar progressView;
    public TextView id;
    public TextView title;
    public TextView status;
    public LruCacheBitmap lruCacheBitmap = LruCacheBitmap.getInstance();
    public String id_cam;

    private CamGetDataTask camGetDataTask  = null;

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

        id = (TextView) findViewById(R.id.cam_id);
        title = (TextView) findViewById(R.id.cam_title);
        status = (TextView) findViewById(R.id.cam_status);
        lruCacheBitmap = LruCacheBitmap.getInstance();


        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.
        if (savedInstanceState != null) {
            camGetDataTask = (CamGetDataTask) getLastCustomNonConfigurationInstance();
        }
        if(camGetDataTask == null){
            Log.w(TAG, "Get from Cam: " + EXTRA_CITY);
            camGetDataTask = new CamGetDataTask(this);
            camGetDataTask.execute(city);
        } else {
            Log.w(TAG, "Attach and Get from Cam: " + EXTRA_CITY);
            camGetDataTask.onAttach(this);
        }
    }
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.camGetDataTask;
    }
    private static final String TAG = "CityCam";
}