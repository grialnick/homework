package ru.android_2019.citycam;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.dataBase.App;
import ru.android_2019.citycam.dataBase.WebCamFormDAO;
import ru.android_2019.citycam.list.RecylcerDividersDecorator;
import ru.android_2019.citycam.list.WebCamRecyclerAdapter;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.WebCamForm;
import ru.android_2019.citycam.webcams.Webcams;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    static final String EXTRA_CITY = "city";

    private City city;


    private DownLoadWebCamTask downloadTask = null;

    private RecyclerView recyclerView;
    private WebCamRecyclerAdapter adapter;
    private List<WebCamForm> webCamForms;
    private WebCamFormDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            finish();
        }
        setContentView(R.layout.activity_city_cam);
        Objects.requireNonNull(getSupportActionBar()).setTitle(city.name);
        webCamForms = new ArrayList<>();
        adapter = new WebCamRecyclerAdapter(webCamForms, this);
        recyclerView = findViewById(R.id.activity_city_cam__list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecylcerDividersDecorator(Color.DKGRAY));
        if (savedInstanceState != null) {
            downloadTask = (DownLoadWebCamTask) getLastCustomNonConfigurationInstance();
        }
        if (downloadTask == null) {
            try {
                downloadTask = new DownLoadWebCamTask(this);
                downloadTask.execute(Webcams.createNearbyUrl(city.latitude, city.longitude));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            downloadTask.attachActivity(this);
        }
        dao = App.getInstance().getDatabase().webCamFormDAO();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.downloadTask;
    }

    @SuppressLint("StaticFieldLeak")
    class DownLoadWebCamTask extends AsyncTask<URL, Integer, List<WebCamForm>> {

        @SuppressLint("StaticFieldLeak")
        private CityCamActivity activity;

        private int status;
        private List<WebCamForm> forms = new ArrayList<>();

        DownLoadWebCamTask(CityCamActivity activity) {
            this.activity = activity;
        }

        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            this.activity.findViewById(R.id.activity_city_cam__progress).setEnabled(false);
            updateView();
        }

        void updateView() {
            if (activity != null) {
                TextView errorView = activity.findViewById(R.id.activity_city_cam__error);
                if (status == 2 && forms.size() == 0) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(getResources().getText(R.string.conn_error));
                } else if (forms.size() == 0) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(getResources().getText(R.string.cam_error));
                } else {
                    errorView.setVisibility(View.GONE);
                    activity.webCamForms.clear();
                    activity.webCamForms.addAll(forms);
                    activity.adapter.notifyDataSetChanged();
                }
                activity.findViewById(R.id.activity_city_cam__progress).setVisibility(View.GONE);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<WebCamForm> doInBackground(URL... list) {
            forms = null;
            try {
                forms = downloadFile(list);
                status = forms != null ? 1 : 0;
            } catch (Exception e) {
                status = 2;
            }
            if (status != 2) {
                boolean isCreated = activity.dao.getByCity(activity.city.name).size() > 0;
                if (isCreated) {
                    activity.dao.deleteByCity(activity.city.name);
                }
                for (WebCamForm form : forms) {
                    form.setCity(activity.city.name);
                    activity.dao.insert(form);
                }
            }
            return forms;
        }

        private List<WebCamForm> downloadFile(URL[] url) throws Exception {
            List<WebCamForm> forms = new ArrayList<>();
            HttpURLConnection connection = (HttpURLConnection) url[0].openConnection();
            InputStream in = null;
            try {
                connection.setRequestProperty("X-RapidAPI-Key", "245da2bce0msh1f4d44a59904349p1f51e2jsn78950678f482");
                connection.connect();
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    throw new UnknownHostException();
                }
                in = connection.getInputStream();
                JSonParser parser = new JSonParser();
                List<WebCamForm> camList = parser.readJsonStream(in);
                if (camList.size() > 0) {
                    forms = camList;
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                } else {
                    loadFromCache();
                }
                connection.disconnect();
            }
            return forms;
        }

        private void loadFromCache() {
            forms = new ArrayList<>();
            forms.addAll(activity.dao.getByCity(activity.city.name));
            updateView();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(List<WebCamForm> forms) {
            updateView();
            if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() > 0) {
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }
}
