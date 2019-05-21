package ru.android_2019.citycam.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.android_2019.citycam.R;
import ru.android_2019.citycam.model.WebCamForm;

import java.util.List;

public class WebCamRecyclerAdapter extends RecyclerView.Adapter<WebCamRecyclerAdapter.WebCamViewHolder> {

    private final List<WebCamForm> webCamForms;
    private final Context context;

    public WebCamRecyclerAdapter(List<WebCamForm> webCamForms, Context context) {
        this.context = context;
        this.webCamForms = webCamForms;
    }

    @NonNull
    @Override
    public WebCamViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_city_cam_item, viewGroup, false);
        return new WebCamViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull WebCamViewHolder holder, int position) {
        if (webCamForms != null && webCamForms.size() > 0 && position < webCamForms.size()) {
            WebCamForm webCamForm = webCamForms.get(position);
            holder.bind(webCamForm);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return webCamForms.size();
    }

    static class WebCamViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView;
        final ImageView imageView;
        final TextView cityView;
        final TextView timeView;
        final Context context;

        WebCamViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;

            titleView = itemView.findViewById(R.id.activity_city_cam_item__title);
            imageView = itemView.findViewById(R.id.activity_city_cam_item__image);
            cityView = itemView.findViewById(R.id.activity_city_cam_item__city);
            timeView = itemView.findViewById(R.id.activity_city_cam_item__time);
        }

        @SuppressLint("SetTextI18n")
        private void bind(@NonNull WebCamForm webCamForm) {
            titleView.setText(webCamForm.getTitle());
            imageView.setImageBitmap(webCamForm.getImage());
            cityView.setText(context.getString(R.string.city_title) + webCamForm.getCity());
            timeView.setText(context.getString(R.string.time) + webCamForm.getTime());
        }
    }

}
