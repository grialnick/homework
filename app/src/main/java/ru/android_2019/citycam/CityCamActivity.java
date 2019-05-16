package ru.android_2019.citycam;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.store.WebcamDao;
import ru.android_2019.citycam.webcams.store.WebcamDatabase;
import ru.android_2019.citycam.webcams.tasks.WebcamsTask;

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
    private ImageView camImageView;
    private ProgressBar progressView;
    private TextView titleImageView;
    private WebcamsTask task;
    private WebcamDao webcamDao;

    public City getCity() {
        return city;
    }

    public ImageView getCamImageView() {
        return camImageView;
    }

    public ProgressBar getProgressView() {
        return progressView;
    }

    public TextView getTitleImageView() {
        return titleImageView;
    }

    public WebcamDao getWebcamDao() {
        return webcamDao;
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (task != null) {
            task.attachActivity(null);
        }
        return task;
    }

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
        titleImageView = findViewById(R.id.cam_image_title);

        WebcamDatabase db =  Room.databaseBuilder(getApplicationContext(),
                WebcamDatabase.class, "webcam-database").build();
        this.webcamDao = db.webcamDao();

        getSupportActionBar().setTitle(city.name);

        task = (WebcamsTask) getLastCustomNonConfigurationInstance();
        if (task == null) {
            progressView.setVisibility(View.VISIBLE);
            task = new WebcamsTask();
            task.execute();
        }
        task.attachActivity(this);
        task.updateView();
    }

    private static final String TAG = "CityCam";
}
