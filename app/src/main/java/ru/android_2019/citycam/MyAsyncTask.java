package ru.android_2019.citycam;

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
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

import static android.content.Context.MODE_PRIVATE;

public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

    private final City city;
    private CityCamActivity cityCamActivity;
    private List<WebCamInfo> webCamInfos;
    private Bitmap bitmap;
    SharedPreferences sharedPreferences;

    public MyAsyncTask(CityCamActivity cityCamActivity, City city) {
        this.cityCamActivity = cityCamActivity;
        this.city = city;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String response = getJSONResponse(Webcams.createNearbyUrl(city.latitude, city.longitude));
            String json = loadJSON(city.toString());
            if (json != null) {
                webCamInfos = JSONReader.getWebcamList(json);
                bitmap = getBitmapFromCache(city.toString());
            } else if (response != null) {
                webCamInfos = JSONReader.getWebcamList(response);
                if (webCamInfos != null) {
                    bitmap = getBitmap(new URL(webCamInfos.get(0).getImageURL()));
                    saveJSON(city.toString(), response);
                    saveBitmapToCache(city.toString(), bitmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJSONResponse(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader(Webcams.getHeaderApiKey(), Webcams.getDevId())
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String result = response.body().string();
            response.close();
            return result;
        } else {
            response.close();
            return null;
        }
    }

    private String loadJSON(String key) {
        sharedPreferences = cityCamActivity.getPreferences(MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    private Bitmap getBitmapFromCache(String name) {
        File cacheDir = cityCamActivity.getCacheDir();
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

    private Bitmap getBitmap(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        response.close();
        return bitmap;
    }

    private void saveJSON(String key, String json) {
        sharedPreferences = cityCamActivity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(key, json);
        ed.commit();
    }

    private void saveBitmapToCache(String name, Bitmap bitmap) {
        File cacheDir = cityCamActivity.getCacheDir();
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
        cityCamActivity.updateInfo(webCamInfos, bitmap);
    }

    @Override
    protected void onProgressUpdate(Void... Void) {
        super.onProgressUpdate(Void);
        cityCamActivity.updateInfo(webCamInfos, bitmap);
    }

    public void attachActivity(CityCamActivity cityCamActivity) {
        this.cityCamActivity = cityCamActivity;
        this.cityCamActivity.updateInfo(webCamInfos, bitmap);
    }
}
