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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClockActivity extends AppCompatActivity {
    
    private static final int REQUEST_ADD_CLOCK = 1001;

    FloatingActionButton fabAdd;
    RecyclerView recyclerViewEvents;
    TextView txtTitleMain;
    ImageButton btnBack;
    TextView txtBaoThuc, txtDemGio, txtBamGio;

    private List<Clock> clockList;
    private ClockAdapter clockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các view
        initViews();
        
        // Khởi tạo danh sách clock
        initClockList();
        
        // Thiết lập RecyclerView
        setupRecyclerView();

        // Cập nhật tiêu đề
        updateTitle();


        txtDemGio.setOnClickListener(v ->  {
            Intent intent = new Intent(ClockActivity.this, DemGioActivity.class);
            startActivity(intent);

        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ClockActivity.this, ActivityAddClock.class);
            startActivityForResult(intent, REQUEST_ADD_CLOCK);
        });

        txtBamGio.setOnClickListener(v -> {
            Intent intent = new Intent(ClockActivity.this, BamGioActivity.class);
            startActivity(intent);
        });


    }

    private void initViews() {
        fabAdd = findViewById(R.id.fabAdd);
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        txtTitleMain = findViewById(R.id.txtTitleMain);
        btnBack = findViewById(R.id.btnBack);
        txtBaoThuc = findViewById(R.id.txtBaoThuc);
        txtDemGio = findViewById(R.id.txtDemGio);
        txtBamGio = findViewById(R.id.txtBamGio);
        
        // Thiết lập RecyclerView
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initClockList() {
        loadClockList();
    }
    
    private void loadClockList() {
        SharedPreferences prefs = getSharedPreferences("clocks_prefs", MODE_PRIVATE);
        String json = prefs.getString("clocks_list", null);
        if (json != null) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<List<Clock>>(){}.getType();
            clockList = gson.fromJson(json, type);
        } else {
            clockList = new ArrayList<>();
        }
    }
    
    private void setupRecyclerView() {
        clockAdapter = new ClockAdapter(clockList, this);
        clockAdapter.setOnClockItemClickListener(new ClockAdapter.OnClockItemClickListener() {
            @Override
            public void onClockToggle(Clock clock, int position, boolean isEnabled) {
                clock.setEnabled(isEnabled);
                saveClockList();
                updateTitle();
                Toast.makeText(ClockActivity.this, 
                    isEnabled ? "Đã bật chuông báo" : "Đã tắt chuông báo", 
                    Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClockEdit(Clock clock, int position) {
                Intent intent = new Intent(ClockActivity.this, ActivityAddClock.class);
                intent.putExtra("is_edit_mode", true);
                intent.putExtra("edit_clock", clock);
                intent.putExtra("edit_position", position);
                startActivityForResult(intent, REQUEST_ADD_CLOCK);

//                // TODO: Mở màn hình chỉnh sửa chuông báo
//                Toast.makeText(ClockActivity.this, "Chức năng chỉnh sửa đang phát triển", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClockDelete(Clock clock, int position) {
                showDeleteConfirmDialog(clock, position);
            }
        });
        
        recyclerViewEvents.setAdapter(clockAdapter);
    }
    
    private void saveClockList() {
        SharedPreferences prefs = getSharedPreferences("clocks_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(clockList);
        editor.putString("clocks_list", json);
        editor.apply();
    }
    
    private void showDeleteConfirmDialog(Clock clock, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa chuông báo")
            .setMessage("Bạn có chắc chắn muốn xóa chuông báo \"" + clock.getName() + "\"?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                clockList.remove(position);
                clockAdapter.removeClock(position);
                saveClockList();
                updateTitle();
                Toast.makeText(ClockActivity.this, "Đã xóa chuông báo", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }



    private void updateTitle() {
        if (clockList == null || clockList.isEmpty()) {
            txtTitleMain.setText("Tất cả báo thức đều tắt");
        } else {
            long enabledCount = clockList.stream().filter(Clock::isEnabled).count();
            if (enabledCount == 0) {
                txtTitleMain.setText("Tất cả báo thức đều tắt");
            } else if (enabledCount == clockList.size()) {
                txtTitleMain.setText("Tất cả báo thức đều bật");
            } else {
                txtTitleMain.setText(enabledCount + " báo thức đang bật");
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Load lại dữ liệu khi quay lại activity
        loadClockList();
        if (clockAdapter != null) {
            clockAdapter.updateClockList(clockList);
        }
        updateTitle();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_ADD_CLOCK && resultCode == RESULT_OK) {
            // Load lại dữ liệu khi thêm chuông báo mới
            loadClockList();
            if (clockAdapter != null) {
                clockAdapter.updateClockList(clockList);
            }
            updateTitle();
        }
    }


//    @Override
    public void onClockToggle(Clock clock, int position, boolean isEnabled) {
        clock.setEnabled(isEnabled);

        if (isEnabled) {
            // BẬT chuông báo - Lên lịch thông báo
            scheduleAlarm(clock);
            Toast.makeText(ClockActivity.this, "Đã bật chuông báo", Toast.LENGTH_SHORT).show();
        } else {
            // TẮT chuông báo - Hủy thông báo
            cancelAlarm(clock);
            Toast.makeText(ClockActivity.this, "Đã tắt chuông báo", Toast.LENGTH_SHORT).show();
        }

        saveClockList();
        updateTitle();
    }


    private void scheduleAlarm(Clock clock) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("message", "Chuông báo: " + clock.getName());
        intent.putExtra("clock_id", clock.hashCode());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                clock.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, clock.getTime().getHours());
        calendar.set(Calendar.MINUTE, clock.getTime().getMinutes());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        long triggerTime = calendar.getTimeInMillis();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }



    private void cancelAlarm(Clock clock) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                clock.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}