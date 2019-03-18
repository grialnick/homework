package ru.android_2019.citycam.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import ru.android_2019.citycam.model.Webcam;

public class WebcamsRepository {

    public static Webcam getRandomWebcamfromRepositpry(List<Webcam> webcams) {
        if(webcams.isEmpty()) {
            throw new NoSuchElementException();
        }
        int randomIndex = new Random().nextInt(webcams.size());
        return webcams.get(randomIndex);
    }

}
