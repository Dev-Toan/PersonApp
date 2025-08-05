package com.example.personapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class viewAllEvent extends AppCompatActivity implements PopupMenuHelper.PopupMenuListener {

    private ArrayList<Event> events = new ArrayList<>();
    private EventAdapter adapter; // 1. Khai báo adapter

    private ArrayList<Event> filteredEvents = new ArrayList<>();


    TextView txtTatCaSuKien;
    TextView txtSapDienRa;
    TextView txtDaHoanThanh;

    private String currentFilter = "all";



//    EventAdapter eventAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_all_event);



        loadEvents();


// Xoá những sự kiện đã trôi qua 7 ngày
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.set(Calendar.HOUR_OF_DAY, 0);
        calendarNow.set(Calendar.MINUTE, 0);
        calendarNow.set(Calendar.SECOND, 0);
        calendarNow.set(Calendar.MILLISECOND, 0);
        Date now = calendarNow.getTime();

        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            Date eventDate = event.getNgay();

            Calendar calendarEvent = Calendar.getInstance();
            calendarEvent.setTime(eventDate);
            calendarEvent.set(Calendar.HOUR_OF_DAY, 0);
            calendarEvent.set(Calendar.MINUTE, 0);
            calendarEvent.set(Calendar.SECOND, 0);
            calendarEvent.set(Calendar.MILLISECOND, 0);

            long diffMillis = now.getTime() - calendarEvent.getTimeInMillis();
            if (diffMillis >= 7L * 24 * 60 * 60 * 1000) {  // 7 ngày
                iterator.remove();
            }

        }


        updateEventStatusTheoThoiGian();
        saveEvents();



        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnAddEvent = findViewById(R.id.btnAddEvent);


        txtTatCaSuKien = findViewById(R.id.txtTatCaSuKien);
        txtSapDienRa = findViewById(R.id.txtSapDienRa);
        txtDaHoanThanh = findViewById(R.id.txtDaHoanThanh);







        txtTatCaSuKien.setTextColor(getResources().getColor(R.color.white));
        txtTatCaSuKien.setShadowLayer(6, 3, 3, Color.parseColor("#222222"));
        txtTatCaSuKien.setTypeface(null, Typeface.BOLD);



        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
        adapter = new EventAdapter(events);
        adapter.setContext(this);



        adapter.setHideSwitch(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateFilteredEvents();





        btnBack.setOnClickListener(v-> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(viewAllEvent.this, AddEvent.class);
            startActivityForResult(intent, 1); // requestCode = 1
        });


        txtTatCaSuKien.setOnClickListener(v -> {

            currentFilter = "all";
            adapter.setHideSwitch(true);


            filteredEvents.clear();
            filteredEvents.addAll(events); // hiện tất cả

            adapter.setEvents(filteredEvents); // cập nhật adapter
            txtTatCaSuKien.setTextColor(getResources().getColor(R.color.white));
            txtTatCaSuKien.setShadowLayer(6, 3, 3, Color.parseColor("#222222"));
            txtTatCaSuKien.setTypeface(null, Typeface.BOLD);

            txtDaHoanThanh.setTextColor(Color.parseColor("#AAAAAA"));
            txtDaHoanThanh.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            txtDaHoanThanh.setTypeface(null, Typeface.NORMAL);

            txtSapDienRa.setTextColor(Color.parseColor("#AAAAAA"));
            txtSapDienRa.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            txtSapDienRa.setTypeface(null, Typeface.NORMAL);




//            for (Event event : events) {
//                Date eventDate = event.getNgay();
//                Time endTime = event.getEndTime();
//
//                java.util.Calendar cal = java.util.Calendar.getInstance();
//                cal.setTime(eventDate);
//                cal.set(java.util.Calendar.HOUR_OF_DAY, endTime.getHours());
//                cal.set(java.util.Calendar.MINUTE, endTime.getMinutes());
//                cal.set(java.util.Calendar.SECOND, endTime.getSeconds());
//                cal.set(java.util.Calendar.MILLISECOND, 0);
//
//                Date eventEndDateTime = cal.getTime();
//                if (eventEndDateTime.before(new Date())) {
//                    event.setActivity(false);
//                } else {
//                    event.setActivity(true);
//                }
//            }



            filteredEvents.sort((e1, e2) -> {
                int dateCompare = e2.getNgay().compareTo(e1.getNgay());
                if (dateCompare == 0) {
                    return e2.getStartTime().compareTo(e1.getStartTime());
                } else {
                    return dateCompare;
                }
            });
            adapter.setEvents(filteredEvents);
        });




        txtSapDienRa.setOnClickListener(v -> {

            currentFilter = "upcoming";
            filteredEvents.clear();

            updateEventStatusTheoThoiGian();


            adapter.setHideSwitch(false);


//            for (Event event : events) {
//                Date eventDate = event.getNgay();
//                Time endTime = event.getEndTime();
//
//                java.util.Calendar cal = java.util.Calendar.getInstance();
//                cal.setTime(eventDate);
//                cal.set(java.util.Calendar.HOUR_OF_DAY, endTime.getHours());
//                cal.set(java.util.Calendar.MINUTE, endTime.getMinutes());
//                cal.set(java.util.Calendar.SECOND, endTime.getSeconds());
//                cal.set(java.util.Calendar.MILLISECOND, 0);
//
//                Date eventEndDateTime = cal.getTime();
//
//                if (eventEndDateTime.before(new Date())) {
//                   // event.setStatus(true); // Đã hoàn thành
//                    event.setActivity(false);
//                } else {
//                   // event.setStatus(false); // Sắp diễn ra
//                    event.setActivity(true);
//                }
//            }


            txtSapDienRa.setTextColor(getResources().getColor(R.color.white));
            txtSapDienRa.setShadowLayer(6, 3, 3, Color.parseColor("#222222"));
            txtSapDienRa.setTypeface(null, Typeface.BOLD);

            txtTatCaSuKien.setTextColor(Color.parseColor("#AAAAAA")); // hoặc màu mặc định bạn chọn
            txtTatCaSuKien.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            txtTatCaSuKien.setTypeface(null, Typeface.NORMAL);

            txtDaHoanThanh.setTextColor(Color.parseColor("#AAAAAA"));
            txtDaHoanThanh.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            txtDaHoanThanh.setTypeface(null, Typeface.NORMAL);

            for (Event event : events) {
                if (!event.isStatus()) {
                    filteredEvents.add(event);
                }
            }
//            adapter.setEvents(filteredEvents);



            if(!filteredEvents.isEmpty()){
                filteredEvents.sort((e1, e2) -> {
                    int dateCompare = e1.getNgay().compareTo(e2.getNgay());
                    if (dateCompare == 0) {
                        return e1.getStartTime().compareTo(e2.getStartTime());
                    } else {
                        return dateCompare;
                    }
                });
            }

            adapter.setEvents(filteredEvents);


        });

        txtDaHoanThanh.setOnClickListener(v -> {
            currentFilter = "completed";
            filteredEvents.clear();


            adapter.setHideSwitch(true);


//            Date now = new Date();
//
//            for (Event event : events) {
//                // Nếu ngày kết thúc trước hiện tại, đánh dấu là đã hoàn thành
//                if (event.getNgay().before(now) ||
//                        (event.getNgay().equals(now) && event.getEndTime().before(new Time(now.getTime())))) {
//                    event.setStatus(true);
//                    event.setActivity(false);
//                } else {
//                    event.setStatus(false);
//                    event.setActivity(true);
//                }
//            }

            txtDaHoanThanh.setTextColor(getResources().getColor(R.color.white));
            txtDaHoanThanh.setShadowLayer(6, 3, 3, Color.parseColor("#222222"));
            txtDaHoanThanh.setTypeface(null, Typeface.BOLD);

            txtTatCaSuKien.setTextColor(Color.parseColor("#AAAAAA")); // hoặc màu mặc định bạn chọn
            txtTatCaSuKien.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            txtTatCaSuKien.setTypeface(null, Typeface.NORMAL);

            txtSapDienRa.setTextColor(Color.parseColor("#AAAAAA"));
            txtSapDienRa.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            txtSapDienRa.setTypeface(null, Typeface.NORMAL);

            for (Event event : events) {
                if (event.isStatus()) {
                    filteredEvents.add(event);
                }
            }

            filteredEvents.sort((e1, e2) -> {
                        int dateCompare = e2.getNgay().compareTo(e1.getNgay());
                        if (dateCompare == 0) {
                            return e2.getStartTime().compareTo(e1.getStartTime());
                        } else {
                            return dateCompare;
                        }
                    });
            adapter.setEvents(filteredEvents);
        });




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) { // Add new event
                Event event = (Event) data.getSerializableExtra("event");
                if (event != null) {
                    events.add(event);
                    sortEventsByDateTime();
                    if (event.isNotification()) {
                        schedeleEventNotification(this, event, event.hashCode());
                    }
                    updateFilteredEvents();
                    saveEvents();
                    Toast.makeText(this, "Đã thêm sự kiện!", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 2) { // Edit event
                Event updatedEvent = (Event) data.getSerializableExtra("event");
                int position = data.getIntExtra("position", -1);
                
                if (position != -1 && updatedEvent != null) {
                    // Cập nhật sự kiện trong danh sách
                    events.set(position, updatedEvent);
                    sortEventsByDateTime();
                    
                    // Cập nhật notification nếu cần
                    if (updatedEvent.isNotification()) {
                        schedeleEventNotification(this, updatedEvent, updatedEvent.hashCode());
                    } else {
                        cancelEventNotification(this, updatedEvent.hashCode());
                    }
                    
                    updateFilteredEvents();
                    saveEvents();
                    Toast.makeText(this, "Đã cập nhật sự kiện!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void saveEvents() {
        SharedPreferences prefs = getSharedPreferences("events_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(events);
        editor.putString("events_list", json);
        editor.apply();
    }


    public void loadEvents() {
        SharedPreferences prefs = getSharedPreferences("events_prefs", MODE_PRIVATE);
        String json = prefs.getString("events_list", null);
        if (json != null) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<Event>>(){}.getType();
            ArrayList<Event> loadedEvents = gson.fromJson(json, type);
            events.clear();
            events.addAll(loadedEvents);
            sortEventsByDateTime();
        }

    }

    public void sortEventsByDateTime() {
        events.sort((e1, e2) -> {
            int compareDate = e2.getNgay().compareTo(e1.getNgay());
            if (compareDate == 0) {
                return e2.getStartTime().compareTo(e1.getStartTime());
            } else {
                return compareDate;
            }
        });
    }

    private void updateFilteredEvents() {
        filteredEvents.clear();

        switch (currentFilter) {
            case "all":
                // hien thi tat ca su kien sap xep theo thoi gian gan nhat
                filteredEvents.addAll(events);
                filteredEvents.sort((e1,e2) ->{
                    int dateCompare = e2.getNgay().compareTo(e1.getNgay());
                    if(dateCompare == 0){
                        return e2.getStartTime().compareTo(e1.getStartTime());
                    }else {
                        return dateCompare;
                    }
                });
                adapter.setHideSwitch(true);
                break;

            case "upcoming":
                //hien thi su kien sap xay ra
                for(Event event : events) {
                    if (!event.isStatus()) {
                        filteredEvents.add(event);
                    }
                }
                adapter.setHideSwitch(false);
                    break;


            case "completed":
                //hien thi su kien da hoan thanh
                for(Event event : events) {
                    if (event.isStatus()) {
                        filteredEvents.add(event);
                    }
                }
                adapter.setHideSwitch(true);
                break;

        }
        adapter.setEvents(filteredEvents);
    }


    private void updateEventStatusTheoThoiGian() {
        Date now = new Date();
        for (Event event : events) {
            Date eventDate = event.getNgay();
            Time endTime = event.getEndTime();

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(eventDate);
            cal.set(java.util.Calendar.HOUR_OF_DAY, endTime.getHours());
            cal.set(java.util.Calendar.MINUTE, endTime.getMinutes());
            cal.set(java.util.Calendar.SECOND, endTime.getSeconds());
            cal.set(java.util.Calendar.MILLISECOND, 0);

            Date eventEndDateTime = cal.getTime();

            if (eventEndDateTime.before(now)) {
                event.setStatus(true);     // Đã hoàn thành
                event.setActivity(false); // Không còn hoạt động
            } else {
                event.setStatus(false);    // Sắp diễn ra hoặc đang diễn ra
                event.setActivity(true);
            }
        }
    }


    public void schedeleEventNotification(Context context, Event event, int requestCode) {
        if (!event.isNotification()) return;

        Intent intent = new Intent(context, EventNotification.class);
        intent.putExtra("title", event.getTieude());
        intent.putExtra("content", "Sự kiện '" + event.getTieude() + "' sẽ bắt đầu lúc " +
                String.format("%02d:%02d", event.getStartTime().getHours(), event.getStartTime().getMinutes()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getNgay());
        calendar.set(Calendar.HOUR_OF_DAY, event.getStartTime().getHours());
        calendar.set(Calendar.MINUTE, event.getStartTime().getMinutes());
        calendar.set(Calendar.SECOND, event.getStartTime().getSeconds());

        long triggerTime = calendar.getTimeInMillis();

        if(triggerTime > System.currentTimeMillis()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Kiểm tra quyền SCHEDULE_EXACT_ALARM trên Android 12+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    // Fallback cho trường hợp không có quyền
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else {
                // Android 11 trở xuống
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }

        // Thêm debug log
        Toast.makeText(context, "Đã lên lịch thông báo cho: " + event.getTieude() +
                        " lúc " + String.format("%02d:%02d", event.getStartTime().getHours(), event.getStartTime().getMinutes()),
                Toast.LENGTH_SHORT).show();
    }

    public void cancelEventNotification(Context context, int requestCode) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    private boolean checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true; // Android 11 trở xuống không cần kiểm tra
    }


    // Implement PopupMenuListener methods
    @Override
    public void onEditClick(Event event, int position) {
        // Mở AddEvent với dữ liệu sự kiện cần sửa
        Intent intent = new Intent(this, AddEvent.class);
        intent.putExtra("event", event);
        intent.putExtra("position", position);
        intent.putExtra("isEdit", true);
        startActivityForResult(intent, 2); // requestCode = 2 cho edit
    }

    @Override
    public void onDeleteClick(Event event, int position) {
        // Remove event from both lists
        events.remove(event);
        filteredEvents.remove(event);

        // Update adapter
        adapter.setEvents(filteredEvents);

        // Save changes
        saveEvents();

        // Show confirmation
        Toast.makeText(this, "Đã xóa sự kiện: " + event.getTieude(), Toast.LENGTH_SHORT).show();
    }


    // hehehe

}
