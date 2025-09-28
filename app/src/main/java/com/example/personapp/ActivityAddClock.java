package com.example.personapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ActivityAddClock extends AppCompatActivity {

    Button btnCancel, btnSave;
    TextView txtLaplai, txtBaoThuc;
    TimePicker timePicker;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    PowerManager.WakeLock wakeLock;

    EditText editClockName;
    
    // Biến để chỉnh sửa chuông báo
    private boolean isEditMode = false;
    private int editClockId = 0;
//    private String editClockName = "";
    private String editClockRepeatDays = "2 3 4 5 6 7 cn";
    
    // Biến để lưu trữ danh sách chuông báo
    private List<Clock> clockList = new ArrayList<>();
    public String repeatDays = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_clock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        editClockName = findViewById(R.id.editClockName);


        
        // Khởi tạo các view
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        setTimePickerTextSize(timePicker, 50);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        txtLaplai = findViewById(R.id.txtLaplai);

        txtBaoThuc = findViewById(R.id.txtBaoThuc);

        Intent intent = getIntent();
          Clock clock = (Clock) intent.getSerializableExtra("edit_clock");
          int editPosition = getIntent().getIntExtra("edit_position", -1);

          if(clock != null & editPosition >=0){

              isEditMode = true;
              editClockId = editPosition;


              btnSave.setText("Cập nhật");

              editClockName.setText(clock.getName());
              Log.d("kiem tra editClockName", editClockName.getText().toString());
              timePicker.setHour(clock.getTime().getHours());
              timePicker.setMinute(clock.getTime().getMinutes());
              repeatDays = clock.getRepeatDays();


              if ("oneday".equals(repeatDays)) {
                  txtLaplai.setText("Một lần");
              } else if ("everyday".equals(repeatDays)) {
                  txtLaplai.setText("Mỗi ngày");
              } else {
                  txtLaplai.setText(repeatDays);
              }

          }


        // Xử lý sự kiện click txtLaplai
        txtLaplai.setOnClickListener(v -> {
            Intent loopintent = new Intent(ActivityAddClock.this, LoopDayActivity.class);
            String currentRepeat = (repeatDays != null && !repeatDays.isEmpty()) ? repeatDays : "oneday";
            loopintent.putExtra("current_repeat", currentRepeat);
            startActivityForResult(loopintent, 100);
        });

        // Xử lý sự kiện click btnCancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện click btnSave
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClock();
            }
        });




    }




    private void setTimePickerTextSize(View view, float sizeSp) {
        if (view instanceof NumberPicker) {
            NumberPicker picker = (NumberPicker) view;
            try {
                // Lấy Paint bên trong NumberPicker và set size
                Field field = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
                field.setAccessible(true);
                Paint paint = (Paint) field.get(picker);
                paint.setTextSize(sizeSp * getResources().getDisplayMetrics().scaledDensity);

                // Lấy tất cả TextView con và set size
                for (int i = 0; i < picker.getChildCount(); i++) {
                    View child = picker.getChildAt(i);
                    if (child instanceof TextView) {
                        ((TextView) child).setTextSize(sizeSp);
                        ((TextView) child).setIncludeFontPadding(false);
                    }
                }

                picker.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setTimePickerTextSize(group.getChildAt(i), sizeSp);
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            String loopData = data.getStringExtra("data");

            if(loopData.equals("oneday")){
                txtLaplai.setText("Một lần");
                repeatDays = "oneday";
            }else if(loopData.equals("everyday")){
                txtLaplai.setText("Mỗi ngày");
                repeatDays = "everyday";
            }else {
                txtLaplai.setText("" + loopData);
                repeatDays = loopData;
            }
        }
    }

    // Phương thức lưu chuông báo
    private void saveClock() {
        // Lấy thời gian từ TimePicker
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String clockName = editClockName.getText().toString();
        if(clockName.isEmpty()){
            // Tạo tên chuông báo mặc định
            clockName = "Báo thức lúc " + String.format("%02d:%02d", hour, minute);
        };
        
        // Tạo đối tượng Time
        java.sql.Time clockTime = new java.sql.Time(hour, minute, 0);
        

        
        // Tạo đối tượng Clock
        Clock newClock = new Clock(
            clockName,
            clockTime,
            true, // Mặc định bật
            repeatDays,
            true, // Có rung
            "default" // Âm thanh mặc định
        );

        Log.d("kiem tra repeatDays", repeatDays);
        
        // Load danh sách chuông hiện có
        loadClockList();
//
//        // Thêm chuông mới vào danh sách
//        clockList.add(newClock);


        if (isEditMode && editClockId >= 0 && editClockId < clockList.size()) {
            // Xoá alarm cũ trước
            cancelAlarm(clockList.get(editClockId));
            // Cập nhật vào danh sách
            clockList.set(editClockId, newClock);
        } else {
            clockList.add(newClock);
        }
        
        // Lưu danh sách chuông
        saveClockList();
        
        // Lên lịch chuông báo
        scheduleAlarm(newClock);
        
        // Hiển thị thông báo thành công
        Toast.makeText(this, "Đã đặt báo thức lúc " + hour +" Giờ "+ minute +" phút !", Toast.LENGTH_SHORT).show();
        
        // Trả kết quả về activity gọi
        Intent resultIntent = new Intent();
        resultIntent.putExtra("clock_added", true);
        setResult(Activity.RESULT_OK, resultIntent);
        
        // Đóng activity
        finish();
    }

    private void cancelAlarm(Clock clock) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                clock.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pi);
    }


    // Phương thức load danh sách chuông từ SharedPreferences
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
    
    // Phương thức lưu danh sách chuông vào SharedPreferences
    private void saveClockList() {
        SharedPreferences prefs = getSharedPreferences("clocks_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(clockList);
        editor.putString("clocks_list", json);
        editor.apply();
    }
    
    // Phương thức lên lịch chuông báo
    private void scheduleAlarm(Clock clock) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        // Tạo Intent cho AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("message", "Chuông báo: " + clock.getName());
        intent.putExtra("clock_id", clock.hashCode());
        
        // Tạo PendingIntent
        pendingIntent = PendingIntent.getBroadcast(
            this,
            clock.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Tính toán thời gian kích hoạt
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, clock.getTime().getHours());
        calendar.set(Calendar.MINUTE, clock.getTime().getMinutes());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // Nếu thời gian đã qua trong ngày, đặt cho ngày mai
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        long triggerTime = calendar.getTimeInMillis();
        
        // Lên lịch chuông báo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        
        // Nếu là lặp lại hàng ngày, lên lịch cho các ngày tiếp theo
        if ("everyday".equals(clock.getRepeatDays())) {
            scheduleRepeatingAlarm(clock, calendar);
        }
    }
    
    // Phương thức lên lịch chuông báo lặp lại
    private void scheduleRepeatingAlarm(Clock clock, Calendar calendar) {
        // Lên lịch cho 7 ngày tiếp theo
        for (int i = 1; i <= 7; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("message", "Chuông báo: " + clock.getName());
            intent.putExtra("clock_id", clock.hashCode() + i);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                clock.hashCode() + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
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
    }

}