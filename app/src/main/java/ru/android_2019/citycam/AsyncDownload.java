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

public class AsyncDownload extends AsyncTask<Void, Void, Void> {

    private final City city;
    private CityCamActivity activity;
    private List<WebCam> webCamList = null;
    private Bitmap image = null;
    SharedPreferences sPref;

    public AsyncDownload(CityCamActivity activity, City city) {
        this.activity = activity;
        this.city = city;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String response = getJSONResponse(Webcams.createNearbyUrl(city.latitude, city.longitude));
            String info = loadInfo(city.toString());
            if (info != null) {
                webCamList = JSONReader.getWebcamList(info);
                image = loadImage(city.toString());
            } else if (response != null) {
                webCamList = JSONReader.getWebcamList(response);
                if (webCamList != null) {
                    image = getBitmap(new URL(webCamList.get(0).getImageURL()));
                    saveInfo(city.toString(), response);
                    saveImage(image, city.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.updateInfo(webCamList, image);

    }

    @Override
    protected void onProgressUpdate(Void... Void) {
        super.onProgressUpdate(Void);
        activity.updateInfo(webCamList, image);
    }

    private String getJSONResponse(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader(Webcams.getHeaderApiKey(), Webcams.getHeaderApiValue())
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

    public void attachActivity(CityCamActivity cityCamActivity) {
        activity = cityCamActivity;
        activity.updateInfo(webCamList, image);

    }

    private void saveInfo(String key, String json) {
        sPref = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(key, json);
        ed.commit();
    }

    private String loadInfo(String key) {
        sPref = activity.getPreferences(MODE_PRIVATE);
        return sPref.getString(key, null);
    }

    private Bitmap loadImage(String name) {
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

    private void saveImage(Bitmap bitmap, String name) {
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
}
