package ru.android_2019.citycam

import android.util.LruCache

class Cache private constructor(maxSize: Int) : LruCache<String, Webcam>(maxSize) {
    companion object {
        private const val MAX_SIZE = 4 * 1024 * 1024
        private var instance: Cache? = null

        fun getInstance(): Cache {
            if (instance == null) {
                instance = Cache(MAX_SIZE)
            }
            return instance as Cache
        }
    }
}