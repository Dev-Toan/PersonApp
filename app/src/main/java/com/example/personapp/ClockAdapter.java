package com.example.personapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ClockViewHolder> {
    private List<Clock> clockList;
    private Context context;
    private OnClockItemClickListener listener;

    public interface OnClockItemClickListener {
        void onClockToggle(Clock clock, int position, boolean isEnabled);
        void onClockEdit(Clock clock, int position);
        void onClockDelete(Clock clock, int position);
    }

    public ClockAdapter(List<Clock> clockList, Context context) {
        this.clockList = clockList != null ? clockList : new ArrayList<>();
        this.context = context;
    }



    public void setOnClockItemClickListener(OnClockItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clock, parent, false);
        return new ClockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClockViewHolder holder, int position) {
        Clock clock = clockList.get(position);
        
        // Hiển thị thông tin chuông báo
        holder.txtClockName.setText(clock.getName());
        Log.d("ClockAdapter", "Binding clock name: " + clock.getName());
        
        // Format thời gian
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.txtClockTime.setText(timeFormat.format(clock.getTime()));
        
        // Hiển thị tần suất lặp lại
        String repeatText = getRepeatText(clock.getRepeatDays());
        Log.d("RepeatDaysDebug", "Repeat text: " + repeatText);
        holder.txtRepeatDays.setText(repeatText);
        
        // Thiết lập trạng thái switch
        holder.switchEnabled.setChecked(clock.isEnabled());
        
        // Xử lý sự kiện switch
        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onClockToggle(clock, position, isChecked);
            }
        });
        
        // Xử lý sự kiện nút edit
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClockEdit(clock, position);
            }
        });
        
        // Xử lý sự kiện nút delete
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClockDelete(clock, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clockList.size();
    }


    public void updateClockList(List<Clock> newClockList) {
        clockList.clear();
        if (newClockList != null) {
            clockList.addAll(newClockList);
        }
        notifyDataSetChanged();
    }

    public void removeClock(int position) {
        if (position >= 0 && position < clockList.size()) {
            clockList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, clockList.size());
        }
    }

    private String getRepeatText(String repeatDays) {
        if (repeatDays == null || repeatDays.isEmpty()) return "Một lần";
        
        switch (repeatDays) {
            case "oneday":
            case "Một lần":
                return "Một lần";
            case "everyday":
            case "Mỗi ngày":
                return "Mỗi ngày";
            default:
                // Xử lý các ngày cụ thể như "T2 T3 T4 T5 T6 T7 CN"
                return repeatDays;
        }
    }
    


    public static class ClockViewHolder extends RecyclerView.ViewHolder {
        TextView txtClockName, txtClockTime, txtRepeatDays;
        Switch switchEnabled;
        ImageButton btnEdit, btnDelete;

        public ClockViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClockName = itemView.findViewById(R.id.txtClockName);
            txtClockTime = itemView.findViewById(R.id.txtClockTime);
            txtRepeatDays = itemView.findViewById(R.id.txtRepeatDays);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }




}
