package ru.android_2019.citycam;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.android_2019.citycam.database.WebcamDAO;
import ru.android_2019.citycam.lists.RecylcerDividersDecorator;
import ru.android_2019.citycam.lists.webcam_list.WebcamAdapter;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.Webcam;
import ru.android_2019.citycam.webcams.WebCamParser;
import ru.android_2019.citycam.webcams.Webcams;

public class CityCamActivity extends AppCompatActivity {
    public static final String EXTRA_CITY = "city";
    private static final String TAG = "CityCam";
    private static final String LOG_TAG = "CityCamActivityTag";

    private City city;
    private PictureDownloadTask downloadTask;

    private WebcamAdapter adapter;
    private List<Webcam> webcamList;
    private RecyclerView recyclerView;
    private WebcamDAO webcamDAO;
    private ImageView imageViewNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }
        setContentView(R.layout.activity_city_cam);
        recyclerView = findViewById(R.id.activity_city_cam__list);
        imageViewNotFound = findViewById(R.id.activity_city_cam__image);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecylcerDividersDecorator(Color.DKGRAY));

        webcamList = new ArrayList<>();
        adapter = new WebcamAdapter(this, webcamList);
        recyclerView.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(city.getName());
        }

        webcamDAO = App.getInstance().getDataBase().webcamDAO();

        if (savedInstanceState != null) {
            downloadTask = (PictureDownloadTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            downloadTask = new PictureDownloadTask(this);
            downloadTask.execute(city);
        } else {
            downloadTask.attachActivity(this);
        }

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        downloadTask.attachActivity(null);
        return downloadTask;
    }

    static class PictureDownloadTask extends AsyncTask<City, Void, Void> {
        boolean haveResultFromNetWork;
        List<Webcam> list;
        private WeakReference<CityCamActivity> weakReference;

        PictureDownloadTask(CityCamActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        void attachActivity(CityCamActivity activity) {
            this.weakReference = new WeakReference<>(activity);
            updateView();
        }

        void updateView() {
            CityCamActivity activity = weakReference.get();
            if (activity != null) {
                if (!list.isEmpty()) {
                    activity.imageViewNotFound.setVisibility(View.GONE);
                    activity.recyclerView.setVisibility(View.VISIBLE);
                    activity.webcamList.clear();
                    activity.webcamList.addAll(list);
                    activity.adapter.notifyDataSetChanged();
                } else {
                    activity.recyclerView.setVisibility(View.GONE);
                    activity.imageViewNotFound.setVisibility(View.VISIBLE);
                    activity.imageViewNotFound.setImageResource(haveResultFromNetWork ? R.drawable.nothing : R.drawable.notfound);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
            haveResultFromNetWork = false;
        }

        @Override
        protected Void doInBackground(City... cities) {
            City city = cities[0];
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) Webcams.createNearbyUrl(city.getLatitude(), city.getLongitude()).openConnection();
                httpURLConnection.setRequestProperty("X-RapidAPI-Key", "bb43131250mshf0d6ed9887777a2p1fada8jsnb76edde66d91");
                httpURLConnection.connect();
                Log.d(LOG_TAG, "Begin connect  " + httpURLConnection.getURL());
                Log.d(LOG_TAG, httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());

                JsonReader reader = new JsonReader(new InputStreamReader(httpURLConnection.getInputStream()));
                list = new WebCamParser(city.name).parse(reader);

                Iterator<Webcam> iterator = list.iterator();

                while (iterator.hasNext()) {
                    Webcam webcam = iterator.next();
                    webcam.setCity(city.name);
                    insertToDatabase(webcam);
                }

                reader.close();
                haveResultFromNetWork = true;
            } catch (IOException e) {
                e.printStackTrace();
                getDataFromDatabase(city.name);
            }
            return null;
        }

        void getDataFromDatabase(String cityName) {
            CityCamActivity activity = weakReference.get();
            list = activity.webcamDAO.getWebcamsByCity(cityName);
        }

        void insertToDatabase(Webcam webcam) {
            CityCamActivity cityCamActivity = weakReference.get();
            if (cityCamActivity != null) {
                cityCamActivity.webcamDAO.insert(webcam);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateView();
        }

    }

}