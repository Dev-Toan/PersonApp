package com.example.personapp;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private ArrayList<Event> events;


    public EventAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    private boolean hideSwitch = false;


    public EventAdapter(Context context) {
        this.context = context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;



    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.txtTieuDe.setText(event.getTieude());
        holder.txtNoiDung.setText(event.getNoidung());
        holder.txtStartTime.setText(String.format("%02d:%02d",event.getStartTime().getHours(),event.getStartTime().getMinutes()));
        holder.txtEndTime.setText(String.format("%02d:%02d",event.getEndTime().getHours(),event.getEndTime().getMinutes()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.txtNgay.setText(sdf.format(event.getNgay()));


        //holder.switch1.setChecked(event.isActivity());


        // Hiệu ứng thu nhỏ/mở rộng
        if (event.isExpanded) {
            holder.txtNoiDung.setMaxLines(Integer.MAX_VALUE);
            holder.txtNoiDung.setEllipsize(null);
        } else {
            holder.txtNoiDung.setMaxLines(2);
            holder.txtNoiDung.setEllipsize(TextUtils.TruncateAt.END);
        }

        holder.txtNoiDung.setOnClickListener(v -> {
            if(event != null){
                event.isExpanded = !event.isExpanded;
                notifyItemChanged(position);
            }

        });


        holder.txtTieuDe.setOnClickListener(v -> {
            if(event != null){
                event.isExpanded = !event.isExpanded;
                notifyItemChanged(position);
            }

        });






        holder.switch1.setOnClickListener(null);


        if(hideSwitch){
            holder.switch1.setVisibility(View.GONE);
        }else {
            holder.switch1.setVisibility(View.VISIBLE);
        }




        holder.switch1.setOnCheckedChangeListener(null);
        holder.switch1.setChecked(event.isNotification());

        holder.switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            event.setNotification(isChecked);

            if (context instanceof viewAllEvent){
                viewAllEvent activity = (viewAllEvent) context;

                if(isChecked){
                    activity.schedeleEventNotification(context, event, event.hashCode());
                } else {
                    activity.cancelEventNotification(context, event.hashCode());
                }



            }
        });

        holder.btnMore.setOnClickListener(v -> {
            if (context instanceof PopupMenuHelper.PopupMenuListener) {
                PopupMenuHelper.showPopupMenu(context, v, event, position, (PopupMenuHelper.PopupMenuListener) context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView txtTieuDe, txtNoiDung, txtNgay, txtStartTime, txtEndTime;
        Switch switch1;
        ImageButton btnMore;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTieuDe = itemView.findViewById(R.id.txtTieuDe);
            txtNoiDung = itemView.findViewById(R.id.txtContent);
            txtNgay = itemView.findViewById(R.id.txtNgay);
            txtStartTime = itemView.findViewById(R.id.txtStartTime);
            txtEndTime = itemView.findViewById(R.id.txtEndTime);
            switch1 = itemView.findViewById(R.id.switch1);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }


    public void setEvents(ArrayList<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }


    public void setHideSwitch(boolean hide) {
        this.hideSwitch = hide;
        notifyDataSetChanged();
    }

}
