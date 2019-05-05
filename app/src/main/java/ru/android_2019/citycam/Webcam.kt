package ru.android_2019.citycam

import android.graphics.Bitmap

class Webcam(val title: String, val city: String, val previewUrl: String) {
    var image: Bitmap? = null
}