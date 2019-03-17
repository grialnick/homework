package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ru.android_2019.citycam.async_task.TaskCallbacks;
import ru.android_2019.citycam.model.City;



/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity implements TaskCallbacks {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private City city;
    private ImageView camImageView;
    private ProgressBar progressView;
    private TaskFragment mTaskFragment;


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
        FragmentManager manager = getSupportFragmentManager();
        if(savedInstanceState == null) {
            mTaskFragment = new TaskFragment(city);
            manager.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        else {
            if(mTaskFragment == null) {
                mTaskFragment = new TaskFragment(city);
                manager.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            }
            else{
                mTaskFragment = (TaskFragment) manager.findFragmentByTag(TAG_TASK_FRAGMENT);
            }
        }
        Log.d(String.valueOf(this), "Activity");


    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(Bitmap bitmap) {
        camImageView.setImageBitmap(bitmap);
    }

    private static final String TAG = "CityCam";
}
