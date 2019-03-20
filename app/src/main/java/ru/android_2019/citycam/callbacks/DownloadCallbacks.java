package ru.android_2019.citycam.callbacks;



import ru.android_2019.citycam.model.Webcam;

public interface DownloadCallbacks {
    void onPostExecute(Webcam webcam);
    void onProgressUpdate(int percent);
}
