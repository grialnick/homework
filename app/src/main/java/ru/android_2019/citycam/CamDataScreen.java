package ru.android_2019.citycam;

import android.graphics.Bitmap;
import java.net.URL;

public class CamDataScreen {
    private String camId;
    private String camTitle;
    private String camStatus;
    private URL url;
    private Bitmap bitmap =null ;

    public CamDataScreen(String camId, String camTitle, String camStatus, URL url){
        this.camId = camId;
        this.camTitle = camTitle;
        this.camStatus = camStatus;
        this.url = url;
    }
    public String getCamId () { return camId;}
    public String getCamTitle() { return camTitle;}
    public String getCamStatus() { return camStatus;}
    public URL getUrl(){ return url;}
    public Bitmap getImage() { return bitmap;}
    public void putImage(Bitmap bitmap) { this.bitmap = bitmap; }
}
