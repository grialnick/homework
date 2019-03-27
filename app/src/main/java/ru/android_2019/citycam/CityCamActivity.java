package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
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
    private TextView idView;
    private TextView statusView;
    private TextView titleView;
    private TextView imageNotFoundView;
    private TextView cityView;
    private TextView regionView;
    private TextView countryView;
    private TextView continentView;
    private TextView wikiView;
    private TextView viewsView;
    private DownloadWebcamInfoTask downloadTask;

    public void setImageNotFound() {
        imageNotFoundView.setVisibility(View.VISIBLE);
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            imageNotFoundView.setVisibility(View.VISIBLE);
        } else {
            camImageView.setImageBitmap(bitmap);
        }
    }

    public void setInvisible() {
        progressView.setVisibility(View.INVISIBLE);
    }

    public void setId(String id) {
        idView.append(id);
    }

    public void setStatus(String status) {
        statusView.append(status);
    }

    public void setCity(String city) {
        cityView.append(city);
    }

    public void setRegion(String region) {
        regionView.append(region);
    }

    public void setCountry(String country) {
        countryView.append(country);
    }

    public void setContinent(String continent) {
        continentView.append(continent);
    }

    public void setWiki(String wiki) {
        wikiView.append(wiki);
    }

    public void setViews(String views) {
        viewsView.append(views);
    }

    public void setTitle(String title) {
        titleView.append(title);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return downloadTask;
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

        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);
        idView = (TextView) findViewById(R.id.id);
        statusView = (TextView) findViewById(R.id.status);
        titleView = (TextView) findViewById(R.id.title);
        imageNotFoundView = (TextView) findViewById(R.id.image_not_found);
        cityView = (TextView) findViewById(R.id.city);
        regionView = (TextView) findViewById(R.id.region);
        countryView = (TextView) findViewById(R.id.country);
        continentView = (TextView) findViewById(R.id.continent);
        wikiView = (TextView) findViewById(R.id.wikipedia);
        viewsView = (TextView) findViewById(R.id.views);

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);
        imageNotFoundView.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null) {
            downloadTask = (DownloadWebcamInfoTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            downloadTask = new DownloadWebcamInfoTask(this);
            downloadTask.execute(city);
        } else {
            downloadTask.attachActivity(this);
        }
    }

    public static final String TAG = "CityCam";
}

