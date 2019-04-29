package ru.android_2019.citycam.callbacks;

import java.util.List;

import ru.android_2019.citycam.model.Cam;

public interface CamLoadCallbacks {
    void onPostExecute(List<Cam> cams);
    void onProgressUpdate(int percent);
}
