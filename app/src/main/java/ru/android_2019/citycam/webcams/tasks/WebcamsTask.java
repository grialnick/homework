package ru.android_2019.citycam.webcams.tasks;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.android_2019.citycam.CityCamActivity;
import ru.android_2019.citycam.webcams.api.WebcamsAPI;
import ru.android_2019.citycam.webcams.exceptions.BadResponseException;
import ru.android_2019.citycam.webcams.model.Webcam;

import static ru.android_2019.citycam.webcams.parser.WebcamsResponseParser.parseWebcamsResponse;

public class WebcamsTask extends AsyncTask<Void, Void, Webcam> {

    private static final double DEFAULT_RADIUS = 100.0;
    private boolean isTaskFinished = false;
    private CityCamActivity activity;
    private Webcam webcam;

    public void attachActivity(CityCamActivity activity){
        this.activity = activity;
    }

    @Override
    protected Webcam doInBackground(Void... params) {
        HttpURLConnection connection = null;
        Webcam webcam = null;

        try {
            double latitude = activity.getCity().latitude;
            double longitude = activity.getCity().longitude;
            connection = WebcamsAPI.createNearbyUrl(latitude, longitude, DEFAULT_RADIUS);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                List<Webcam> webcams = parseWebcamsResponse(inputStream, "UTF-8");
                webcam = pickWebcamRandom(webcams);

                if (webcam != null){
                    URL imageUrl = new URL(webcam.getImageUrl());
                    Log.i(TAG, "URL of image: " + imageUrl);
                    webcam.setBitmap(BitmapFactory.decodeStream(imageUrl.openStream()));
                } else {
                    Log.i(TAG, "No images found");
                }
            } else {
                throw new BadResponseException("HTTP: " + connection.getResponseCode()
                        + ", " + connection.getResponseMessage());
            }

        } catch (IOException e) {
            Log.e(TAG, "Unable to create channel between application and network resource");
        } catch (BadResponseException e) {
            Log.e(TAG,"Failed to get webcams:", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return webcam;
    }

    @Override
    protected void onPostExecute(Webcam webcam) {
        this.webcam = webcam;
        isTaskFinished = true;
        updateView();
    }

    public void updateView() {
        if (isTaskFinished) {
            activity.getProgressView().setVisibility(View.INVISIBLE);
            if (webcam != null) {
                activity.getCamImageView()
                        .setImageBitmap(webcam.getBitmap());
                activity.getTitleImageView()
                        .setText(webcam.getTitle());
            } else {
                activity.getTitleImageView()
                        .setText(ru.android_2019.citycam.R.string.image_not_found);
            }
        }
    }

    private static Webcam pickWebcamRandom(List<Webcam> webcams) {
        if (webcams.size() == 0) {
            return null;
        }

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < webcams.size(); i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);

        int i = 0;
        Webcam webcam;
        do {
            webcam = webcams.get(indexes.get(i++));
        } while (i < indexes.size() && webcam.getImageUrl() == null);

        return i == indexes.size() ? null : webcam;
    }

    private static final String TAG = "WebcamsTask";
}
