package ru.android_2019.citycam.webcams;

import android.util.JsonReader;
import android.util.Log;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import ru.android_2019.citycam.CamDataScreen;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ParserJson {
    public CamDataScreen camDataSreen = null;

    public static List<CamDataScreen> getJsonStream (InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return  readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }
    public static List<CamDataScreen> readMessagesArray(JsonReader reader) throws IOException {
        List<CamDataScreen> messages = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            Log.w(TAG, "String name: " + name);
            if (name.equals("status")) {
                String status = reader.nextString();
                if (!status.equals("OK")) {
                    throw new IOException("Bad status");
                }
            }else if (name.equals("result")) {
                messages = readWebCams(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return messages;
    }
    private static List<CamDataScreen> readWebCams(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        List <CamDataScreen> camDataSreens = new ArrayList<>();
        int total=0;
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("webcams") && total > 0) {
                camDataSreens = readWebCamsList(jsonReader);
            }else if(name.equals("total")){
                total = jsonReader.nextInt();
            }else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return camDataSreens;
    }
    private static List<CamDataScreen> readWebCamsList(JsonReader jsonReader) throws IOException {
        List <CamDataScreen> webcams = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            webcams.add(parserWebCam(jsonReader));
        }
        jsonReader.endArray();
        return webcams;
    }
    public static CamDataScreen parserWebCam(JsonReader reader) throws IOException {
        String camId = null;
        String camTitle = null;
        String camStatus = null;
        URL url =null ;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                camId = reader.nextString();
                Log.w(TAG, "String name: " + camId);
            } else if (name.equals("status")) {
                camStatus = reader.nextString();
                Log.w(TAG, "String name: " + camStatus);
            } else if (name.equals("title")){
                camTitle = reader.nextString();
                Log.w(TAG, "String name: " + camTitle);
            } else if (name.equals("image")) {
                url = readUrl(reader);
                Log.w(TAG, "String name: " + url.toString());
            } else {
                reader.skipValue();
            }
        }
        Log.w(TAG, "End parserWebCam");
        reader.endObject();
        return new CamDataScreen(camId,camTitle,camStatus,url);
    }
    private static URL readUrl(JsonReader jsonReader) throws IOException {
        URL imageUrl = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("current")){
                imageUrl = getUrl(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return imageUrl;
    }
    private static URL getUrl(JsonReader jsonReader) throws IOException {
        URL url = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("preview")) {
                String imageUrl = jsonReader.nextString();
                url = new URL(imageUrl);
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return url;
    }
    private static final String TAG = "CityCam";
}

