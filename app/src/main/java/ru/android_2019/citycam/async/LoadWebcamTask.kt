package ru.android_2019.citycam.async

import android.content.ContentValues.TAG
import ru.android_2019.citycam.webcams.Webcams
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import android.view.View
import ru.android_2019.citycam.Cache
import ru.android_2019.citycam.CityCamActivity
import ru.android_2019.citycam.Webcam
import java.net.URL


internal class LoadWebcamTask(
        private var activity: CityCamActivity?) : AsyncTask<Void, Void, TaskState>() {

    private var image: Bitmap? = null
    private var webcam: Webcam? = null

    private var state = TaskState.DOWNLOADING

    fun attachActivity(activity: CityCamActivity) {
        this.activity = activity
        updateView()
    }

    fun updateView() {
        if (activity != null) {
            if (webcam != null) {
                activity!!.titleText!!.text = webcam!!.title
                activity!!.cityText!!.text = webcam!!.city
            }
            if (image != null) {
                activity!!.camImageView!!.setImageBitmap(image)
            }
            if (state === TaskState.DONE) {
                activity!!.progressView!!.visibility = View.INVISIBLE
            } else {
                activity!!.progressView!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onPreExecute() {
        updateView()
    }

    override fun doInBackground(vararg ignore: Void): TaskState {
        if (Cache.getInstance().get(activity!!.city!!.name) == null) {
            try {
                val webcams = Download.loadResponse(Webcams.createNearbyUrl(activity!!.city!!.latitude, activity!!.city!!.longitude))
                if (!webcams.isEmpty()) {
                    webcam = webcams[0]
                    image = Download.downloadImage(URL(webcam!!.previewUrl))
                    webcam!!.image = image
                    Cache.getInstance().put(activity!!.city!!.name, webcam)
                    Log.d(TAG, "Data downloaded")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading file: $e", e)
                state = TaskState.ERROR
            }

        } else {
            webcam = Cache.getInstance().get(activity!!.city!!.name)
            image = webcam!!.image
            Log.d(TAG, "Data loaded from cache")
        }
        state = TaskState.DONE
        return state
    }

    override fun onPostExecute(state: TaskState) {
        this.state = state
        if (state === TaskState.DONE) {
            updateView()
        }
        if (image != null) {
            activity!!.camImageView!!.setImageBitmap(image)
        }
        if (webcam != null) {
            activity!!.cityText!!.text = webcam!!.city
            activity!!.titleText!!.text = webcam!!.title
        }
    }
}