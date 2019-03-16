package ru.android_2019.citycam.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import ru.android_2019.citycam.model.Webcam;

public class WebcamsRepository {

    private List <Webcam> webcams;

    private static  WebcamsRepository instance = new WebcamsRepository();

    public static WebcamsRepository getInstance() {
        return instance;
    }

    private WebcamsRepository () {
        webcams = new ArrayList<>();
    }

    public Webcam getWebcamFromRepository() {
        if(webcams.isEmpty()) {
            throw new NoSuchElementException();
        }
        int randomIndex = new Random().nextInt(webcams.size());
        Webcam webcam = webcams.get(randomIndex);
        webcams.remove(randomIndex);
        return webcam;
    }

    public void putWebcamInRepository(Webcam webcam) {
        if(webcam == null) {
            throw new NullPointerException();
        }
        else {
            webcams.add(webcam);
        }
    }

    public void putWebcamsListInRepository(List <Webcam> webcams) {
        webcams.addAll(webcams);
    }
}
