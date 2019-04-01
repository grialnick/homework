package ru.android_2019.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Методы для скачивания файлов.
 */
final class DownloadUtils {

    static List<Webcam> loadResponse(URL downloadUrl) throws IOException {
        Log.d(TAG, "Start downloading url: " + downloadUrl);

        HttpURLConnection conn = (HttpURLConnection) downloadUrl.openConnection();
        InputStream in = null;
        List<Webcam> webcams;

        try {

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Received HTTP response code: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new FileNotFoundException("Unexpected HTTP response: " + responseCode
                        + ", " + conn.getResponseMessage());
            }

            int contentLength = conn.getContentLength();
            Log.d(TAG, "Content Length: " + contentLength);

            in = conn.getInputStream();
            webcams = WebcamParser.readJsonStream(in);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close HTTP input stream: " + e, e);
                }
            }
            conn.disconnect();
        }
        return webcams;
    }

    static Bitmap downloadImage(URL downloadUrl) throws IOException {
        Log.d(TAG, "Start downloading url: " + downloadUrl);

        HttpURLConnection conn = (HttpURLConnection) downloadUrl.openConnection();
        InputStream in = null;
        Bitmap image;

        try {

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Received HTTP response code: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new FileNotFoundException("Unexpected HTTP response: " + responseCode
                        + ", " + conn.getResponseMessage());
            }

            int contentLength = conn.getContentLength();
            Log.d(TAG, "Content Length: " + contentLength);

            // Начинаем читать ответ
            in = conn.getInputStream();
            image = BitmapFactory.decodeStream(in);

        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close HTTP input stream: " + e, e);
                }
            }
            conn.disconnect();
        }
        return image;
    }

    private static final String TAG = "Download";

}
