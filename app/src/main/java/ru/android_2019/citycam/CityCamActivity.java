package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ru.android_2019.citycam.asyncTask.WebcamTask;
import ru.android_2019.citycam.asyncTask.webcamJSONObject.WebcamInfo;
import ru.android_2019.citycam.model.City;

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
    private TextView textViewId;
    private TextView textViewTitle;
    private TextView textViewCategories;
    private TextView textViewLocation;
    private WebcamTask webcamTask;
    private Button buttonUpdate;

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
        textViewId = (TextView) findViewById(R.id.textView_id);
        textViewTitle = (TextView) findViewById(R.id.textView_title);
        textViewCategories = (TextView) findViewById(R.id.textView_categories);
        textViewLocation = (TextView) findViewById(R.id.textView_location);
        buttonUpdate = (Button) findViewById(R.id.button_update_image);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonUpdate.setEnabled(false);
                progressView.setVisibility(View.VISIBLE);
                webcamTask = new WebcamTask((CityCamActivity) v.getContext(), city);
                webcamTask.execute(WebcamTask.UpdateCacheImage.YES);
            }
        });

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            webcamTask = (WebcamTask) getLastCustomNonConfigurationInstance();
        }
        if (webcamTask == null) {
            webcamTask = new WebcamTask(this, city);
            webcamTask.execute(WebcamTask.UpdateCacheImage.NO);
        } else {
            webcamTask.attachActivity(this);
        }
    }

    public void updateView(ArrayList<WebcamInfo> webcamInfo, Bitmap bitmap) {
        if (webcamInfo != null) {
            textViewId.setText(getString(R.string.webcam_id) + " : " + webcamInfo.get(0).getId());
            textViewTitle.setText(getString(R.string.webcam_title) + " : " + webcamInfo.get(0).getTitle());
            textViewCategories.setText(getString(R.string.webcam_catigories) + " : " + webcamInfo.get(0).getGategoriesAsString());
            textViewLocation.setText(getString(R.string.webcam_location) + " : " + webcamInfo.get(0).getLocation().toString());
            if (bitmap != null) {
                camImageView.setImageBitmap(bitmap);
                progressView.setVisibility(View.GONE);
            }
            buttonUpdate.setEnabled(true);
        } else {
            textViewTitle.setText(city.name + " : " + getString(R.string.not_found_webcam));
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return webcamTask;
    }

    private static final String TAG = "CityCam";

}
