package ru.android_2019.citycam.callbacks;



import java.util.List;

import ru.android_2019.citycam.model.Webcam;

public interface DownloadCallbacks {
    void onPostExecute(List<Webcam> webcamList);
    void onProgressUpdate(int percent);
}
