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

import ru.android_2019.citycam.asynctask.CamTask;
import ru.android_2019.citycam.asynctask.camjson.CamInfo;
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

    private ImageView ivCam;
    private ProgressBar progressView;
    private TextView tvId;
    private TextView tvTitle;
    private TextView tvCategories;
    private TextView tvLocation;
    private TextView tvConnection;
    private CamTask camTask;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w("CityCam", "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        ivCam = findViewById(R.id.cam_image);
        progressView = findViewById(R.id.progress);
        tvId = findViewById(R.id.textView_id);
        tvTitle = findViewById(R.id.textView_title);
        tvCategories = findViewById(R.id.textView_categories);
        tvLocation = findViewById(R.id.textView_location);
        tvConnection = findViewById(R.id.no_connection);
        btnUpdate = findViewById(R.id.button_update_image);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate.setEnabled(false);
                progressView.setVisibility(View.VISIBLE);
                camTask = new CamTask((CityCamActivity) v.getContext(), city);
                camTask.execute();
            }
        });

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            camTask = (CamTask) getLastCustomNonConfigurationInstance();
        }

        if (camTask == null) {
            camTask = new CamTask(this, city);
            camTask.execute();
        } else {
            camTask.attachActivity(this);
        }
    }

    public void updateView(ArrayList<CamInfo> camInfo, Bitmap bitmap, boolean connection) {
        if (camInfo != null) {
            tvId.setText(getString(R.string.webcam_id) + " : " + camInfo.get(0).getId());
            tvTitle.setText(getString(R.string.webcam_title) + " : " + camInfo.get(0).getTitle());
            tvCategories.setText(getString(R.string.webcam_catigories) + " : " + camInfo.get(0).getCategoriesAsString());
            tvLocation.setText(getString(R.string.webcam_location) + " : " + camInfo.get(0).getLocationAsString());
            if (bitmap != null) {
                ivCam.setImageBitmap(bitmap);
                progressView.setVisibility(View.GONE);
            }
            btnUpdate.setEnabled(true);
        } else {
            tvTitle.setText(city.name + " : " + getString(R.string.not_found_webcam));
        }
        if (!connection)
            tvConnection.setVisibility(View.VISIBLE);
        else
            tvConnection.setVisibility(View.GONE);
    }

    public void unblockBtn(){
        btnUpdate.setEnabled(true);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return camTask;
    }
}
