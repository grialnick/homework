package ru.android_2019.citycam

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ru.android_2019.citycam.async.LoadWebcamTask
import ru.android_2019.citycam.model.City


/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
class CityCamActivity : AppCompatActivity() {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    var city: City? = null

    var camImageView: ImageView? = null
    var cityText: TextView? = null
    var titleText: TextView? = null
    var progressView: ProgressBar? = null
    private var downloadTask: LoadWebcamTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_cam)

        city = intent.getParcelableExtra(EXTRA_CITY)
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: $EXTRA_CITY")
            finish()
        }

        camImageView = findViewById(R.id.cam_image)
        cityText = findViewById(R.id.city_text)
        titleText = findViewById(R.id.title_text)
        progressView = findViewById(R.id.progress)

        supportActionBar!!.title = city!!.name

        progressView!!.visibility = View.VISIBLE

        if (savedInstanceState != null) {
            downloadTask = lastCustomNonConfigurationInstance as LoadWebcamTask
        }
        if (downloadTask != null) {
            downloadTask!!.attachActivity(this)
            return
        }
        downloadTask = LoadWebcamTask(this)
        downloadTask!!.execute()
    }

    companion object {
        const val EXTRA_CITY = "city"
        const val TAG = "CityCam"
    }
}