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
                if (name.equals("result")) {
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
                } else if (name.equals("status")) {
                    String status = reader.nextString();
                    if (!status.equals("OK")) {
                        throw new IOException(status + " status was returned");
                    }
                } else {
                    reader.skipValue();
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
        Webcam webcam = new Webcam(
                "Not specified",
                "Not specified",
                "Not specified");

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            String value;
            if (name.equals("id")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    webcam.setId(value);
                }
            } else if (name.equals("status")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    webcam.setStatus(value);
                }
            } else if (name.equals("title")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    webcam.setTitle(value);
                }
            } else if (name.equals("image")) {
                webcam.setImageURL(readImageURL(reader));
            } else if (name.equals("location")) {
                ;
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
        String imageURL = "Not specified";
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
        Location location = new Location(
                "Not specified",
                "Not specified",
                "Not specified",
                "Not specified",
                "Not specified");

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            String value;
            if (name.equals("city")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    location.setCity(value);
                }
            } else if (name.equals("region")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    location.setRegion(value);
                }
            } else if (name.equals("country")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    location.setCountry(value);
                }
            } else if (name.equals("continent")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    location.setContinent(value);
                }
            } else if (name.equals("wikipedia")) {
                value = reader.nextString();
                if (!value.equals("")) {
                    location.setWikiURL(value);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return location;
    }

    private String readViews(JsonReader reader) throws IOException {
        String views = "Not specified";
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("views")) {
                views = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return views;
    }
}
