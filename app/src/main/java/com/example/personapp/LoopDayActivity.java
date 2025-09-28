package com.example.personapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoopDayActivity extends AppCompatActivity {
    CheckBox checkBox1lan, checkBoxHangNgay, checkBoxT2, checkBoxT3, checkBoxT4, checkBoxT5, checkBoxT6, checkBoxT7, checkBoxCn;
    Button btnSave;

    boolean check1lan = false, checkHangNgay = false, checkT2 = false, checkT3 = false, checkT4 = false,
            checkT5 = false, checkT6 = false, checkT7 = false, checkCn = false;

    int intCheck2 = 0;
    int intCheck3 = 0;
    int intCheck4 = 0;
    int intCheck5 = 0;
    int intCheck6 = 0;
    int intCheck7 = 0;
    int intCheckCn = 0;

    boolean isUpdating = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loop_day);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView btnBack = findViewById(R.id.imageButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        checkBox1lan = findViewById(R.id.CheckBox1lan);
        checkBoxHangNgay = findViewById(R.id.CheckBoxHangNgay);
        checkBoxT2 = findViewById(R.id.CheckBoxT2);
        checkBoxT3 = findViewById(R.id.CheckBoxT3);
        checkBoxT4 = findViewById(R.id.CheckBoxT4);
        checkBoxT5 = findViewById(R.id.CheckBoxT5);
        checkBoxT6 = findViewById(R.id.CheckBoxT6);
        checkBoxT7 = findViewById(R.id.CheckBoxT7);
        checkBoxCn = findViewById(R.id.CheckBoxCn);

        btnSave = findViewById(R.id.btnSave);





        String current = getIntent().getStringExtra("current_repeat");
        if (current == null || current.isEmpty()) {
            current = "oneday";
        }

        isUpdating = true;

        if ("oneday".equals(current)) {
            checkBox1lan.setChecked(true);
            check1lan = true;
            // đảm bảo các ô khác tắt
            checkBoxHangNgay.setChecked(false);
            unSelectAll();
        } else if ("everyday".equals(current)) {
            checkBox1lan.setChecked(false);
            check1lan = false;
            checkBoxHangNgay.setChecked(true);
            checkHangNgay = true;
            selectAll();
        } else {
            // Chuỗi kiểu "T2 T3 ... CN"
            checkBox1lan.setChecked(false);
            check1lan = false;
            checkBoxHangNgay.setChecked(false);
            checkHangNgay = false;
            unSelectAll();

            // đánh dấu theo từng ngày
            if (current.contains("T2")) { checkBoxT2.setChecked(true); checkT2 = true; intCheck2 = 1; }
            if (current.contains("T3")) { checkBoxT3.setChecked(true); checkT3 = true; intCheck3 = 1; }
            if (current.contains("T4")) { checkBoxT4.setChecked(true); checkT4 = true; intCheck4 = 1; }
            if (current.contains("T5")) { checkBoxT5.setChecked(true); checkT5 = true; intCheck5 = 1; }
            if (current.contains("T6")) { checkBoxT6.setChecked(true); checkT6 = true; intCheck6 = 1; }
            if (current.contains("T7")) { checkBoxT7.setChecked(true); checkT7 = true; intCheck7 = 1; }
            if (current.contains("CN")) { checkBoxCn.setChecked(true); checkCn = true; intCheckCn = 1; }

            // đồng bộ trạng thái "Hàng ngày" nếu đủ 7 ngày
            checkStutus();
        }

        isUpdating = false;





        checkBox1lan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check1lan = isChecked;
                if (isChecked){
                    checkBoxHangNgay.setChecked(false);
                    checkHangNgay = false;
                    unSelectAll();
                }
                
            }
        });

        checkBoxHangNgay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isUpdating) return;

                checkHangNgay = isChecked;
                if (isChecked) {
                    checkBox1lan.setChecked(false);
                    check1lan = false;
                    selectAll();
                }else {
                    unSelectAll();
                }
                
            }
        });




        checkBoxT2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkT2 = isChecked;
            if (isChecked) {
                checkBox1lan.setChecked(false);
                check1lan = false;
                intCheck2 =1;
            }else {
                intCheck2 =0;
            }
            checkStutus();

        });

        checkBoxT3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkT3 = isChecked;
            if (isChecked) {
                checkBox1lan.setChecked(false);
                check1lan = false;
                intCheck3=1;
            }else {
                intCheck3 =0;
            }
            checkStutus();

        });

        checkBoxT4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkT4 = isChecked;
            if (isChecked) {
                checkBox1lan.setChecked(false);
                check1lan = false;
                intCheck4=1;
            }else {
                intCheck4 =0;
            }
            checkStutus();

        });

        checkBoxT5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkT5 = isChecked;
            if (isChecked) {
                checkBox1lan.setChecked(false);
                check1lan = false;
                intCheck5=1;
            }else {
                intCheck5 =0;
            }
            checkStutus();

        });

        checkBoxT6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkT6 = isChecked;
            if (isChecked) {
                checkBox1lan.setChecked(false);
                check1lan = false;
                intCheck6=1;
            }else {
                intCheck6 =0;
            }
            checkStutus();

        });

        checkBoxT7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkT7 = isChecked;
            if (isChecked) {
                checkBox1lan.setChecked(false);
                check1lan = false;
                intCheck7=1;
            }else {
                intCheck7 =0;
            }
            checkStutus();

        });

        checkBoxCn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkCn = isChecked;
            if (isChecked) {
                checkBox1lan.setChecked(false);
                check1lan = false;
                intCheckCn=1;
            }else {
                intCheckCn =0;
            }
            checkStutus();

        });

        btnSave.setOnClickListener( v ->{
            Intent intent = new Intent(LoopDayActivity.this, ActivityAddClock.class);
            intent.putExtra("data", convertData());
            setResult(RESULT_OK, intent);
            finish();
        });



    }



    private void selectAll(){
        checkBoxT2.setChecked(true);
        checkT2 = true;
        checkBoxT3.setChecked(true);
        checkT3 = true;
        checkBoxT4.setChecked(true);
        checkT4 = true;
        checkBoxT5.setChecked(true);
        checkT5 = true;
        checkBoxT6.setChecked(true);
        checkT6 = true;
        checkBoxT7.setChecked(true);
        checkT7 = true;
        checkBoxCn.setChecked(true);
        checkCn = true;
    }

    private void unSelectAll(){
        checkBoxT2.setChecked(false);
        checkT2 = false;
        checkBoxT3.setChecked(false);
        checkT3 = false;
        checkBoxT4.setChecked(false);
        checkT4 = false;
        checkBoxT5.setChecked(false);
        checkT5 = false;
        checkBoxT6.setChecked(false);
        checkT6 = false;
        checkBoxT7.setChecked(false);
        checkT7 = false;
        checkBoxCn.setChecked(false);
        checkCn = false;
    }



    private void checkStutus(){
        int x = intCheck2 + intCheck3 + intCheck4 + intCheck5 + intCheck6 + intCheck7 + intCheckCn;

        isUpdating = true;

        if(x == 7){
            checkBoxHangNgay.setChecked(true);
            checkHangNgay = true;
        }else {
            checkBoxHangNgay.setChecked(false);
            checkHangNgay = false;
        }

        isUpdating = false;
    }

    public String convertData(){
        if(check1lan){
            return "oneday";
        } else if (checkHangNgay) {
            return "everyday";
        } else {
            StringBuilder data = new StringBuilder();
            if (checkT2) data.append("T2 ");
            if (checkT3) data.append("T3 ");
            if (checkT4) data.append("T4 ");
            if (checkT5) data.append("T5 ");
            if (checkT6) data.append("T6 ");
            if (checkT7) data.append("T7 ");
            if (checkCn) data.append("CN");
            return data.toString().trim();
        }

    }


}
