package ru.android_2019.citycam

import android.util.JsonReader

import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

object Parser {
    fun readJsonStream(`in`: InputStream): List<Webcam>? {
        val reader = JsonReader(InputStreamReader(`in`, "UTF-8"))
        try {
            return readResponse(reader)
        } finally {
            reader.close()
        }
    }

    private fun readResponse(reader: JsonReader): List<Webcam>? {
        var webcams: List<Webcam>? = null
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "webcams") {
                webcams = readWebcams(reader)
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
        return webcams
    }

    private fun readWebcams(reader: JsonReader): List<Webcam> {
        val webcams = ArrayList<Webcam>()
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "webcam") {
                reader.beginArray()
                while (reader.hasNext()) {
                    webcams.add(readWebcam(reader))
                }
                reader.endArray()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
        return webcams
    }

    private fun readWebcam(reader: JsonReader): Webcam {
        var title: String? = null
        var city: String? = null
        var previewUrl: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "preview_url" -> {
                    previewUrl = reader.nextString()
                }
                "title" -> {
                    title = reader.nextString()
                }
                "city" -> {
                    city = reader.nextString()
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        return Webcam(title!!, city!!, previewUrl!!)
    }
}