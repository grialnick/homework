package ru.android_2019.citycam;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class JsonParser {
    public List<Webcam> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<Webcam> webcams = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("status")) {
                    String status = reader.nextString();
                    if (!status.equals("OK")) {
                        throw new IOException("isn't OK");
                    }
                } else if (name.equals("result")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("webcams")) {
                            webcams = readWebcamArray(reader);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
            }
            reader.endObject();
        } finally {
            reader.close();
        }
        return webcams;
    }

    private List<Webcam> readWebcamArray(JsonReader reader) throws IOException {
        List<Webcam> webcams = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            webcams.add(readWebcam(reader));
        }
        reader.endArray();
        return webcams;
    }

    private Webcam readWebcam(JsonReader reader) throws IOException {
        Webcam webcam = new Webcam();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                webcam.setId(reader.nextString());
            } else if (name.equals("status")) {
                webcam.setStatus(reader.nextString());
            } else if (name.equals("title")) {
                webcam.setTitle(reader.nextString());
            } else if (name.equals("image")) {
                webcam.setImageURL(readImageURL(reader));
            } else if (name.equals("location")) {
                webcam.setLocation(readLocation(reader));
            } else if (name.equals("statistics")) {
                webcam.setViews(readViews(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return webcam;
    }

    private String readImageURL(JsonReader reader) throws IOException {
        String imageURL = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("current")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals("preview")) {
                        imageURL = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return imageURL;
    }

    private Location readLocation(JsonReader reader) throws IOException {
        Location location = new Location();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("city")) {
                location.setCity(reader.nextString());
            } else if (name.equals("region")) {
                location.setRegion(reader.nextString());
            } else if (name.equals("country")) {
                location.setCountry(reader.nextString());
            } else if (name.equals("continent")) {
                location.setContinent(reader.nextString());
            } else if (name.equals("wikipedia")) {
                location.setWikiURL(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return location;
    }

    private String readViews(JsonReader reader) throws IOException {
        String views = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("views")) {
                views = reader.nextString();
            }
        }
        reader.endObject();
        return views;
    }

}
