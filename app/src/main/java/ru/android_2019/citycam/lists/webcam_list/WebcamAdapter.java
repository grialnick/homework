package ru.android_2019.citycam.lists.webcam_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import ru.android_2019.citycam.R;
import ru.android_2019.citycam.model.Webcam;

public class WebcamAdapter extends RecyclerView.Adapter<WebcamAdapter.WebcamHolder> {
    List<Webcam> webcamList;
    Context context;

    public WebcamAdapter(Context context, List<Webcam> webcamList) {
        this.context = context;
        this.webcamList = webcamList;
    }

    @NonNull
    @Override
    public WebcamHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city_cam, null);
        return new WebcamHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WebcamHolder webcamHolder, int i) {
        Webcam webcam = webcamList.get(i);
        webcamHolder.bind(webcam);
    }

    @Override
    public int getItemCount() {
        return webcamList.size();
    }

    class WebcamHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView timeView;
        private ImageView image;

        WebcamHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_city_cam__image);
            titleView = itemView.findViewById(R.id.item_city_cam__title);
            timeView = itemView.findViewById(R.id.item_city_cam__time);
        }

        void bind(Webcam webcam) {
            image.setImageBitmap(webcam.getBitmap());
            titleView.setText(webcam.getTitle());
            timeView.setText(new Date(webcam.getTime()).toString());
        }
    }
}
