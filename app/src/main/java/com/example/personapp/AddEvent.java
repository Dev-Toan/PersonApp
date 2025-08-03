package com.example.personapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEvent extends AppCompatActivity {
    private EditText edtTieuDe, edtNoiDung, edtNgay, edtStartTime, edtEndTime;
    private CheckBox checkboxStatus;
    private Button btnSave;

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

                    Event event = new Event(endTime, ngay, noidung, startTime, status, tieude, activity);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("event", event);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } catch (ParseException e) {
                    Toast.makeText(AddEvent.this, "Định dạng ngày hoặc giờ không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



} 