package ru.android_2019.citycam;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

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



    @SuppressLint("ResourceType")
    private ImageView camImageView;
    private ProgressBar progressView;
    private AsyncTask downloadImageTask = new DownloadImageTask();

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

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        // DownloadImageTask.execute(Webcams.createNearbyUrl(city.latitude,city.longitude));
        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.

        downloadImageTask.execute();
    }



    private class DownloadImageTask extends AsyncTask <Void, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL url = Webcams.createNearbyUrl(city.latitude, city.longitude);
                Picasso.get().load(String.valueOf(url)).into(camImageView);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static final String TAG = "CityCam";
}
