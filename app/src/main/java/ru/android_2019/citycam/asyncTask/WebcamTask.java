package ru.android_2019.citycam.asyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.android_2019.citycam.CityCamActivity;
import ru.android_2019.citycam.asyncTask.webcamJSONObject.WebcamInfo;
import ru.android_2019.citycam.asyncTask.webcamJSONObject.WebcamJSONReader;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

public class WebcamTask extends AsyncTask<WebcamTask.UpdateCacheImage, Void, Void> {

    private City city;
    private ArrayList<WebcamInfo> webcamInfos;
    private CityCamActivity activity;
    private Bitmap bitmap = null;

    public WebcamTask(CityCamActivity activity, City city) {
        this.activity = activity;
        this.city = city;
    }

    @Override
    protected Void doInBackground(WebcamTask.UpdateCacheImage... updateCacheImages) {
        String response = null;
        try {
            response = DownloadUtils.getJSONResponse(Webcams.createNearbyUrl(city.latitude, city.longitude));
            webcamInfos = WebcamJSONReader.getWebcamList(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress();
        if (webcamInfos != null) {
            bitmap = getBitmapFromCache(webcamInfos.get(0).getId());
            if (bitmap == null || updateCacheImages[0] == UpdateCacheImage.YES) {
                try {
                    bitmap = DownloadUtils.getBitmap(new URL(webcamInfos.get(0).getURLPreviewImage()));
                    saveBitmapFromCache(bitmap, webcamInfos.get(0).getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, webcamInfos.toString());
        }
        return null;
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

    public static final String TAG = "WebcanTask";

    public void attachActivity(CityCamActivity cityCamActivity) {
        this.activity = cityCamActivity;
        activity.updateView(webcamInfos, bitmap);
    }

    public enum UpdateCacheImage {
        YES,
        NO
    }
}
