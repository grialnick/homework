package ru.android_2019.citycam.asyncTask;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import ru.android_2019.citycam.CityCamActivity;
import ru.android_2019.citycam.asyncTask.webcamJSONObject.WebcamInfo;
import ru.android_2019.citycam.asyncTask.webcamJSONObject.WebcamJSONReader;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

import static android.content.Context.MODE_PRIVATE;

public class WebcamTask extends AsyncTask<WebcamTask.UpdateCacheImage, Void, Void> {
    public static final String TAG = "WebcamTask";
    private City city;
    private ArrayList<WebcamInfo> webcamInfos;
    private CityCamActivity activity;
    private Bitmap bitmap = null;
    private SharedPreferences sPref;


    public WebcamTask(CityCamActivity activity, City city) {
        this.activity = activity;
        this.city = city;
    }

    @Override
    protected Void doInBackground(WebcamTask.UpdateCacheImage... updateCacheImages) {
        try {
            background(updateCacheImages);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void background(WebcamTask.UpdateCacheImage... updateCacheImages) throws IOException {
        String savedJSON = loadJSON(city.toString());
        String response = DownloadUtils.getJSONResponse(Webcams.createNearbyUrl(city.latitude, city.longitude));
        if (savedJSON != null && (updateCacheImages[0] != UpdateCacheImage.YES || response == null)) {
            webcamInfos = WebcamJSONReader.getWebcamList(savedJSON);
            bitmap = getBitmapFromCache(city.toString());
        } else if (response != null) {
            webcamInfos = WebcamJSONReader.getWebcamList(response);
            if (webcamInfos != null) {
                bitmap = DownloadUtils.getBitmap(new URL(webcamInfos.get(0).getURLPreviewImage()));
                saveJSON(city.toString(), response);
                saveBitmapFromCache(bitmap, city.toString());
            }
        }
    }

    private void saveJSON(String key, String json) {
        sPref = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(key, json);
        ed.commit();
    }

    private String loadJSON(String key) {
        sPref = activity.getPreferences(MODE_PRIVATE);
        return sPref.getString(key, null);
    }

    private Bitmap getBitmapFromCache(String name) {
        File cacheDir = activity.getCacheDir();
        String imageName = name + ".png";
        File bitmapFile = new File(cacheDir + File.separator + imageName);
        Bitmap result = null;
        if (!bitmapFile.exists()) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(bitmapFile)) {
            result = BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void saveBitmapFromCache(Bitmap bitmap, String name) {
        File cacheDir = activity.getCacheDir();
        String imageName = name + ".png";
        File bitmapFile = new File(cacheDir + File.separator + imageName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(bitmapFile, false)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.updateView(webcamInfos, bitmap);
    }

    @Override
    protected void onProgressUpdate(Void... Void) {
        super.onProgressUpdate(Void);
        activity.updateView(webcamInfos, bitmap);
    }

    public void attachActivity(CityCamActivity cityCamActivity) {
        this.activity = cityCamActivity;
        activity.updateView(webcamInfos, bitmap);
    }

    public enum UpdateCacheImage {
        YES,
        NO
    }
}
