package ru.android_2019.citycam;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import javax.net.ssl.HttpsURLConnection;

import ru.android_2019.citycam.dataBase.App;
import ru.android_2019.citycam.dataBase.WebCamMessageDAO;
import ru.android_2019.citycam.list.RecylcerDividersDecorator;
import ru.android_2019.citycam.list.WebCamRecyclerAdapter;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.model.WebCamMessage;
import ru.android_2019.citycam.webcams.Webcams;

public class CityCamActivity extends AppCompatActivity {

    public static final String EXTRA_CITY = "city";

    private City city;


    private DownLoadWebCamTask downloadTask = null;

    RecyclerView recyclerView;
    WebCamRecyclerAdapter adapter;
    List<WebCamMessage> webCamMessages;
    WebCamMessageDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }
        setContentView(R.layout.activity_city_cam);
        getSupportActionBar().setTitle(city.name);
        webCamMessages = new ArrayList<>();
        adapter = new WebCamRecyclerAdapter(webCamMessages, this);
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
        dao = App.getInstance().getDatabase().webCamMessageDAO();


    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.downloadTask;
    }


    @SuppressLint("StaticFieldLeak")
    class DownLoadWebCamTask extends AsyncTask<URL, Integer, List<WebCamMessage>> {

        @SuppressLint("StaticFieldLeak")
        private CityCamActivity activity;

        private int status;
        private List<WebCamMessage> messages = new ArrayList<>();

        DownLoadWebCamTask(CityCamActivity activity) {
            this.activity = activity;
        }

        void attachActivity(CityCamActivity activity) {
            this.activity = activity;
            this.activity.findViewById(R.id.progress).setEnabled(false);
            updateView();
        }

        void updateView() {
            if (activity != null) {
                TextView errorView = activity.findViewById(R.id.activity_city_cam__error);
                if (status == 2 && messages.size() == 0) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(getResources().getText(R.string.conn_error));
                } else if (messages.size() == 0) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(getResources().getText(R.string.cam_error));
                } else {
                    errorView.setVisibility(View.GONE);
                    activity.webCamMessages.clear();
                    activity.webCamMessages.addAll(messages);
                    activity.adapter.notifyDataSetChanged();
                }
                activity.findViewById(R.id.progress).setVisibility(View.GONE);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<WebCamMessage> doInBackground(URL... list) {
            messages = null;
            try {
                messages = downloadFile(list);
                status = messages != null ? 1 : 0;
            } catch (Exception e) {
                status = 2;
            }
            if (status != 2) {
                boolean isCreated = activity.dao.getByCity(activity.city.name).size() > 0;
                if (isCreated) {
                    activity.dao.deleteByCity(activity.city.name);
                }
                Log.d(TAG, "doInBackground: " + isCreated);
                for (int i = 0; i < messages.size(); i++) {
                    messages.get(i).setCity(activity.city.name);
                    activity.dao.insert(messages.get(i));
                }
            }
            return messages;
        }


        private List<WebCamMessage> downloadFile(URL[] url) throws Exception {
            List<WebCamMessage> messages = new ArrayList<>();
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
                List<WebCamMessage> camList = parser.readJsonStream(in);
                if (camList.size() > 0) {
                    messages = camList;
                    messages.get(messages.size() - 1).setStatus("active");
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close HTTP input stream: " + e, e);
                    }
                } else {
                    loadFromCache();
                }
                connection.disconnect();
            }


            return messages;
        }

        private void loadFromCache() {
            messages = new ArrayList<>();
            messages.addAll(activity.dao.getByCity(activity.city.name));
            updateView();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(List<WebCamMessage> messages) {
            updateView();
            if (recyclerView.getAdapter().getItemCount() > 0) {
                recyclerView.smoothScrollToPosition(0);
            }
        }

    }

    private static final String TAG = "CityCam";

}
