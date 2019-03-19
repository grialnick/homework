package ru.android_2019.citycam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.android_2019.citycam.model.City;
import ru.android_2019.citycam.webcams.Webcam;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    public static final int HANDLER_CODE_SUCCESS = 1;
    public static final int HANDLER_CODE_BAD = 2;

    private City city;
    private Webcam webcam;

    private ImageView camImageView;
    private ProgressBar progressView;
    private TextView titleView;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == HANDLER_CODE_SUCCESS) {
                    if (msg.obj == null) {
                        Toast.makeText(getApplicationContext(), "Видеокамер не найдено", Toast.LENGTH_SHORT).show();
                    } else {
                        webcam = (Webcam) msg.obj;
                        updateWebcam();
                    }
                    return true;
                } else if (msg.what == HANDLER_CODE_BAD) {
                    Toast.makeText(getApplicationContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = findViewById(R.id.cam_image);
        progressView = findViewById(R.id.progress);
        titleView = findViewById(R.id.title_text);
        statusView = findViewById(R.id.status_text);

        getSupportActionBar().setTitle(city.name);

        progressView.setVisibility(View.VISIBLE);

        new WebcamThread(handler, city).start();
    }

    private void updateWebcam() {
        if (webcam != null) {
            camImageView.setImageBitmap(webcam.getPreview());
            progressView.setVisibility(View.GONE);
            titleView.setText(webcam.getTitle());
            statusView.setText(webcam.getStatus());
        }
    }

    private static final String TAG = "CityCam";
}
