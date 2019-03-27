package ru.android_2019.citycam.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.android_2019.citycam.R;
import ru.android_2019.citycam.model.WebCamMessage;

import static android.content.ContentValues.TAG;

public class WebCamRecyclerAdapter extends RecyclerView.Adapter<WebCamRecyclerAdapter.WebCamViewHolder> {

    private final List<WebCamMessage> webCamMessages;
    private final Context context;

    public WebCamRecyclerAdapter(List<WebCamMessage> webCamMessages, Context context) {
        this.context = context;
        this.webCamMessages = webCamMessages;
    }


    @NonNull
    @Override
    public WebCamViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: sosososo");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_city_cam_item, viewGroup, false);
        return new WebCamViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull WebCamViewHolder holder, int position) {
        if (webCamMessages != null && webCamMessages.size() > 0 && position < webCamMessages.size()) {
            WebCamMessage webCamMessage = webCamMessages.get(position);
            holder.bind(webCamMessage);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return webCamMessages.size();
    }


    static class WebCamViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView;
        final TextView statusView;
        final ImageView imageView;
        final TextView viewsView;
        final TextView cityView;
        final TextView timeZoneView;
        final TextView idView;
        final TextView timeView;
        final Context context;

        WebCamViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;

            titleView = itemView.findViewById(R.id.activity_city_cam__title);
            statusView = itemView.findViewById(R.id.activity_city_cam__status);
            imageView = itemView.findViewById(R.id.cam_image);
            viewsView = itemView.findViewById(R.id.activity_city_cam__views);
            cityView = itemView.findViewById(R.id.activity_city_cam__city);
            timeZoneView = itemView.findViewById(R.id.activity_city_cam__timeZone);
            idView = itemView.findViewById(R.id.activity_city_cam__id);
            timeView = itemView.findViewById(R.id.activity_city_cam__time);
        }

        private void bind(@NonNull WebCamMessage webCamMessage) {
            titleView.setText(webCamMessage.getTitle());
            imageView.setImageBitmap(webCamMessage.getImage());
            cityView.setText(String.valueOf(context.getString(R.string.city_title) + webCamMessage.getCity()));
            timeZoneView.setText(String.valueOf(context.getString(R.string.time_zone) + webCamMessage.getTimeZone()));
            viewsView.setText(String.valueOf(context.getString(R.string.views) + webCamMessage.getViews()));
            idView.setText(String.valueOf(context.getString(R.string.id) + webCamMessage.getCamId()));
            statusView.setText(String.valueOf(context.getString(R.string.status) + webCamMessage.getStatus()));
            timeView.setText(String.valueOf(context.getString(R.string.time) + webCamMessage.getTime()));
        }
    }

}
