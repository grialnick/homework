package ru.android_2019.citycam.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.android_2019.citycam.webcams.Webcams;

class Utils {
    static String getJSONResponse(URL url) throws IOException {
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

    static Bitmap getBitmap(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        response.close();
        return bitmap;
    }
}
