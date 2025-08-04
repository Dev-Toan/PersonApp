package com.example.personapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEvent extends AppCompatActivity {
    private EditText edtTieuDe, edtNoiDung, edtNgay, edtStartTime, edtEndTime ;
    private CheckBox checkboxStatus;
    private Button btnSave, btnHuy;
    private TextView txtThemSuKien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        edtTieuDe = findViewById(R.id.edtTieuDe);
        edtNoiDung = findViewById(R.id.edtNoiDung);
        edtNgay = findViewById(R.id.edtNgay);
        edtStartTime = findViewById(R.id.edtStartTime);
        edtEndTime = findViewById(R.id.edtEndTime);
        checkboxStatus = findViewById(R.id.checkboxStatus);
        btnSave = findViewById(R.id.btnSave);
        btnHuy = findViewById(R.id.btnHuy);
        txtThemSuKien = findViewById(R.id.txtThemSuKien);



        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        edtNgay.setText(currentDate);

        // Kiểm tra xem có phải edit mode không
        Intent intent = getIntent();
        if (intent.getBooleanExtra("isEdit", false)) {
            Event event = (Event) intent.getSerializableExtra("event");
            int position = intent.getIntExtra("position", -1);
            
            if (event != null) {
                // Load dữ liệu sự kiện vào các field
                edtTieuDe.setText(event.getTieude());
                edtNoiDung.setText(event.getNoidung());
                
                // Format ngày
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                edtNgay.setText(dateFormat.format(event.getNgay()));
                
                // Format thời gian
                edtStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", 
                    event.getStartTime().getHours(), event.getStartTime().getMinutes()));
                edtEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", 
                    event.getEndTime().getHours(), event.getEndTime().getMinutes()));
                
                checkboxStatus.setChecked(event.isStatus());
                
                // Thay đổi text của button và title
                btnSave.setText("Cập nhật");
                txtThemSuKien.setText("Sửa sự kiện");
            }
        }

        edtNgay.setOnClickListener(v -> showDatePicker());
        edtStartTime.setOnClickListener(v -> showTimePicker(edtStartTime));
        edtEndTime.setOnClickListener(v -> showTimePicker(edtEndTime));


        btnHuy.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tieude = edtTieuDe.getText().toString().trim();
                String noidung = edtNoiDung.getText().toString().trim();
                String ngayStr = edtNgay.getText().toString().trim();
                String startTimeStr = edtStartTime.getText().toString().trim();
                String endTimeStr = edtEndTime.getText().toString().trim();
                boolean status = checkboxStatus.isChecked();
                boolean activity = true;


                if (tieude.isEmpty() || noidung.isEmpty() || ngayStr.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                    Toast.makeText(AddEvent.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Parse ngày
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date ngay = dateFormat.parse(ngayStr);
                    // Parse giờ bắt đầu và kết thúc
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Time startTime = new Time(timeFormat.parse(startTimeStr).getTime());
                    Time endTime = new Time(timeFormat.parse(endTimeStr).getTime());

                    boolean isNotification = true;
                    Event event = new Event(endTime, ngay, noidung, startTime, status, tieude, status, activity);
                    
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("event", event);
                    
                    // Nếu là edit mode, thêm position
                    Intent intent = getIntent();
                    if (intent.getBooleanExtra("isEdit", false)) {
                        int position = intent.getIntExtra("position", -1);
                        resultIntent.putExtra("position", position);
                    }
                    
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } catch (ParseException e) {
                    Toast.makeText(AddEvent.this, "Định dạng ngày hoặc giờ không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showDatePicker(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y,m,d) -> {
            String formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
            edtNgay.setText(formatted);

        }, year, month, day);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
        dialog.show();
    }


    private void showTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Dialog_NoActionBar,
                (view, h, m) -> {
                    editText.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));

                },
                hour,
                minute,
                true
        );
        //timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
        timePickerDialog.show();



    }
}
