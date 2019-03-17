package ru.android_2019.citycam.async_task;

import android.graphics.Bitmap;

public interface TaskCallbacks {
    void onPreExecute();
    void onProgressUpdate(int percent);
    void onCancelled();
    void onPostExecute(Bitmap bitmap);
}
