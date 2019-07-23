package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

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
    private TextView textView;
    private MyAsyncTask asyncTask;

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
        textView = (TextView) findViewById(R.id.cam_text);

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            asyncTask = (MyAsyncTask) getLastCustomNonConfigurationInstance();
        }
        if (asyncTask == null) {
            asyncTask = new MyAsyncTask(this, city);
            asyncTask.execute();
        } else {
            asyncTask.attachActivity(this);
        }
    }

    private static final String TAG = "CityCam";

    public void updateInfo(List<WebCamInfo> webCamInfos, Bitmap bitmap) {
        if (webCamInfos != null) {
            textView.setText(webCamInfos.get(0).toString());
            if (bitmap != null) {
                camImageView.setImageBitmap(bitmap);
                progressView.setVisibility(View.GONE);
            }
        } else {
            textView.setText(getString(R.string.findCamErr));
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return asyncTask;
    }
}
