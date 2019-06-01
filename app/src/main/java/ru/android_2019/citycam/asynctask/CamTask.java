package ru.android_2019.citycam.asynctask;

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
import ru.android_2019.citycam.asynctask.camjson.CamInfo;
import ru.android_2019.citycam.asynctask.camjson.CamJSONReader;
import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcams;

import static android.content.Context.MODE_PRIVATE;

public class CamTask extends AsyncTask<Void, Void, Void> {
    private City city;
    private ArrayList<CamInfo> camInfos;
    private CityCamActivity activity;
    private Bitmap bitmap = null;
    private SharedPreferences sPref;
    private boolean connection;


    public CamTask(CityCamActivity activity, City city) {
        this.activity = activity;
        this.city = city;
        this.connection = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String savedJSON = loadJSON(city.toString()); //Загрузка сохранненого JSONa
        try {
            //Попытка загрузить JSON с сервера
            System.out.println("try");
            String response = Utils.getJSONResponse(Webcams.createNearbyUrl(city.latitude, city.longitude));
            System.out.println("try2");
            //Если не удалось загрузить, то пробуем взять данные из кеша
            if (response == null && savedJSON != null) {
                System.out.println("00001");
                camInfos = CamJSONReader.getWebcamList(savedJSON);
                bitmap = getBitmapFromCache(city.toString());
                connection = false;
                System.out.println("00000");
            } else if (response != null) {
                //Если удалось загрузить, то достаемы данные, а также сохраняем их в кеш
                System.out.println("dada");
                camInfos = CamJSONReader.getWebcamList(response);
                System.out.println("dad2");
                if (camInfos != null) {
                    bitmap = Utils.getBitmap(new URL(camInfos.get(0).getURLPreviewImage()));
                    saveJSON(city.toString(), response);
                    saveBitmapToCache(bitmap, city.toString());
                    System.out.println("agaga");
                }
                connection = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            //Тут также: если траблы с подключением к серверу, то пытаемся взять данные из кеша
            if (savedJSON != null) {
                try {
                    camInfos = CamJSONReader.getWebcamList(savedJSON);
                } catch (IOException e1) {
                    e.printStackTrace();
                    System.out.println("lol");
                }
                bitmap = getBitmapFromCache(city.toString());
                connection = false;
            }
            System.out.println("lol2");
        }
        return null;
    }

    private void saveJSON(String key, String json) {
        sPref = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(key, json);
        ed.apply();
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

    private void saveBitmapToCache(Bitmap bitmap, String name) {
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
        activity.updateView(camInfos, bitmap, connection);
        activity.unblockBtn();
        System.out.println("what");
    }

    @Override
    protected void onProgressUpdate(Void... Void) {
        super.onProgressUpdate(Void);
        activity.updateView(camInfos, bitmap, connection);
    }

    public void attachActivity(CityCamActivity cityCamActivity) {
        this.activity = cityCamActivity;
        activity.updateView(camInfos, bitmap, connection);
    }
}
