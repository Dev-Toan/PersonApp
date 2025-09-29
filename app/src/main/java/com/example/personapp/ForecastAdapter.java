package com.example.personapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<ForecastItem> forecastList;

    public ForecastAdapter(List<ForecastItem> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastItem item = forecastList.get(position);
        holder.tvDayOfWeek.setText(item.getDayOfWeek());
        holder.imgDayIcon.setImageResource(item.getIconResource());
        holder.tvMinMaxTemp.setText(item.getMinTemp() + "° / " + item.getMaxTemp() + "°");
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public void updateData(List<ForecastItem> newList) {
        this.forecastList = newList;
        notifyDataSetChanged();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek;
        ImageView imgDayIcon;
        TextView tvMinMaxTemp;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            imgDayIcon = itemView.findViewById(R.id.imgDayIcon);
            tvMinMaxTemp = itemView.findViewById(R.id.tvMinMaxTemp);
        }
    }
}