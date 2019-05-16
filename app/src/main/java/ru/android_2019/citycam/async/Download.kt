package ru.android_2019.citycam.async

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import ru.android_2019.citycam.Parser
import ru.android_2019.citycam.Webcam

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Методы для скачивания файлов.
 */
object Download {
    private const val TAG = "Download"

    fun loadResponse(downloadUrl: URL): List<Webcam> {
        Log.d(TAG, "Start downloading url: $downloadUrl")

        val conn = downloadUrl.openConnection() as HttpURLConnection
        var `in`: InputStream? = null
        val webcams: List<Webcam>

        try {
            val responseCode = conn.responseCode
            Log.d(TAG, "Received HTTP response code: $responseCode")
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw FileNotFoundException("Unexpected HTTP response: " + responseCode
                        + ", " + conn.responseMessage)
            }

            val contentLength = conn.contentLength
            Log.d(TAG, "Content Length: $contentLength")

            `in` = conn.inputStream
            webcams = Parser.readJsonStream(`in`)!!

        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Failed to close HTTP input stream: $e", e)
                }

            }
            conn.disconnect()
        }
        return webcams
    }

    fun downloadImage(downloadUrl: URL): Bitmap {
        Log.d(TAG, "Start downloading url: $downloadUrl")
        val conn = downloadUrl.openConnection() as HttpURLConnection
        var `in`: InputStream? = null
        val image: Bitmap

        try {
            val responseCode = conn.responseCode
            Log.d(TAG, "Received HTTP response code: $responseCode")
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw FileNotFoundException("Unexpected HTTP response: " + responseCode
                        + ", " + conn.responseMessage)
            }

            val contentLength = conn.contentLength
            Log.d(TAG, "Content Length: $contentLength")

            `in` = conn.inputStream
            image = BitmapFactory.decodeStream(`in`)

        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Failed to close HTTP input stream: $e", e)
                }
            }
            conn.disconnect()
        }
        return image
    }
}