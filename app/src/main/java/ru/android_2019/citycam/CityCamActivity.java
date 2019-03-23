package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.MalformedURLException;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

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

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            camImageView.setImageBitmap(bitmap);
        } else {
            imageNotFoundView.setVisibility(View.VISIBLE);
        }
    }

    public void setInvisible() {
        progressView.setVisibility(View.INVISIBLE);
    }

    public void setId(String id) {
        if (id != null) {
            idView.append(id);
        } else {
            idView.append(getString(R.string.not_found));
        }
    }

    public void setStatus(String status) {
        if (status != null) {
            statusView.append(status);
        } else {
            statusView.append(getString(R.string.not_found));
        }

    }

    public void setCity(String city) {
        if (city != null) {
            cityView.append(city);
        } else {
            cityView.append(getString(R.string.not_found));
        }
    }

    public void setRegion(String region) {
        if (region != null) {
            regionView.append(region);
        } else {
            regionView.append(getString(R.string.not_found));
        }
    }

    public void setCountry(String country) {
        if (country != null) {
            countryView.append(country);
        } else {
            countryView.append(getString(R.string.not_found));
        }
    }

    public void setContinent(String continent) {
        if (continent != null) {
            continentView.append(continent);
        } else {
            continentView.append(getString(R.string.not_found));
        }
    }

    public void setWiki(String wiki) {
        if (wiki != null) {
            wikiView.append(wiki);
        } else {
            wikiView.append(getString(R.string.not_found));
        }
    }

    public void setViews(String views) {
        if (views != null) {
            viewsView.append(views);
        } else {
            viewsView.append(getString(R.string.not_found));
        }
    }

    public void setTitle(String title) {
        if (title != null) {
            titleView.append(title);
        } else {
            titleView.append(getString(R.string.not_found));
        }
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
            try {
                downloadTask.execute(Webcams.createNearbyUrl(city.latitude, city.longitude));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            downloadTask.attachActivity(this);
        }
    }

    private static final String TAG = "CityCam";
}

