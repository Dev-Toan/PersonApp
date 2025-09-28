package com.example.personapp;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Field;

public class DemGioActivity extends AppCompatActivity {

    NumberPicker hourPicker, minutePicker, secondPicker;
    Button btnPause, btnStart, btnReset;
    TextView txtBaoThuc, txtDemGio, txtBamGio;
    ImageButton btnBack;


    int tempPause=1;

    Handler handler = new Handler();
    Runnable countdownRunnable;
    int totalSeconds = 0;
    int remainingSeconds = 0;
    boolean isTimerRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dem_gio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo NumberPicker
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);


        btnPause = findViewById(R.id.btnPause);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);

        txtBaoThuc = findViewById(R.id.txtBaoThuc);
        txtDemGio = findViewById(R.id.txtDemGio);
        txtBamGio = findViewById(R.id.txtBamGio);

        btnBack = findViewById(R.id.btnBack);


        btnPause.setBackgroundColor(Color.parseColor("#FF0000"));



        // Thiết lập giới hạn
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(12);


        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        // Giá trị mặc định
        hourPicker.setValue(0);
        minutePicker.setValue(0);
        secondPicker.setValue(0);


        setNumberPickerTextSizeReflection(hourPicker, 35f);
        setNumberPickerTextSizeReflection(minutePicker, 35f);
        setNumberPickerTextSizeReflection(secondPicker, 35f);


        btnBack.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(DemGioActivity.this, MainActivity.class);
            @Override
            public void onClick(View v) {
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        txtBaoThuc.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(DemGioActivity.this, ClockActivity.class);
             @Override
             public void onClick(View v) {
                 startActivity(intent);
                 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
             }
        });


        btnPause.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.INVISIBLE);


        btnStart.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị từ NumberPicker
                int hours = hourPicker.getValue();
                int minutes = minutePicker.getValue();
                int seconds = secondPicker.getValue();

                totalSeconds = hours * 3600 + minutes * 60 + seconds;

                if(totalSeconds<=0){
                    return;
                }

                remainingSeconds = totalSeconds;

//                btnStart.setVisibility(View.INVISIBLE);
//                btnPause.setVisibility(View.VISIBLE);
//                btnReset.setVisibility(View.VISIBLE);

                hideViewScaleFade(btnStart);
                showSlideCenterToLeft(btnPause);
                showSlideCenterToRight(btnReset);


                // vô hiệu hóa numberPickers
                hourPicker.setEnabled(false);
                minutePicker.setEnabled(false);
                secondPicker.setEnabled(false);

                tempPause =0;
                CheckPause(tempPause);
                isTimerRunning= true;
                startTimer();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tempPause==0){
                    tempPause=1;
                    stopTimer();
                }else {
                    tempPause=0;
                    startTimer();
                }
                CheckPause(tempPause);

            }
        });





        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopTimer();
                isTimerRunning = false;

                tempPause = 0;
                totalSeconds = 0;
                remainingSeconds = 0;

                showViewScaleFade(btnStart);
                btnPause.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.INVISIBLE);

                hourPicker.setEnabled(true);
                minutePicker.setEnabled(true);
                secondPicker.setEnabled(true);

                hourPicker.setValue(0);
                minutePicker.setValue(0);
                secondPicker.setValue(0);
            }
        });


    }


    private void setNumberPickerTextSizeReflection(NumberPicker numberPicker, float textSize) {
        try {
            // Thay đổi paint của selector wheel
            Field selectorWheelPaintField = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            Paint paint = (Paint) selectorWheelPaintField.get(numberPicker);
            if (paint != null) {
                paint.setTextSize(textSize * getResources().getDisplayMetrics().scaledDensity);
            }

            // Thay đổi text size của input text
            Field inputTextField = NumberPicker.class.getDeclaredField("mInputText");
            inputTextField.setAccessible(true);
            TextView inputText = (TextView) inputTextField.get(numberPicker);
            if (inputText != null) {
                inputText.setTextSize(textSize);
            }

            numberPicker.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback về cách cũ
            setNumberPickerTextSizeNormal(numberPicker, textSize);
        }
    }



    private void setNumberPickerTextSizeNormal(NumberPicker numberPicker, float size) {
        int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextSize(size);
            }
        }
        numberPicker.invalidate();
    }

    private void CheckPause(int temp){
        if(temp==0){
            btnPause.setText("Tạm dừng");
            btnPause.setBackgroundColor(Color.parseColor("#9966CC"));
        }else {
            btnPause.setText("Tiếp tục");
            btnPause.setBackgroundColor(Color.parseColor("#FF0000"));
        }

    }






    private void startTimer() {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (remainingSeconds > 0 && isTimerRunning) {
                    remainingSeconds--;

                    // Cập nhật NumberPickers
                    int hours = remainingSeconds / 3600;
                    int minutes = (remainingSeconds % 3600) / 60;
                    int seconds = remainingSeconds % 60;

                    hourPicker.setValue(hours);
                    minutePicker.setValue(minutes);
                    secondPicker.setValue(seconds);

                    handler.postDelayed(this, 1000);
                } else if (remainingSeconds <= 0) {
                    // Hết thời gian
                    timerFinished();
                }
            }
        };
        handler.postDelayed(countdownRunnable, 1000);
    }



    private void stopTimer() {
        if (countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable);
        }
    }

    private void timerFinished() {
        isTimerRunning = false;
        tempPause = 0;

        // Reset UI
        btnStart.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        // Kích hoạt lại NumberPickers
        hourPicker.setEnabled(true);
        minutePicker.setEnabled(true);
        secondPicker.setEnabled(true);

        // Có thể thêm thông báo hoặc âm thanh ở đây
        // Toast.makeText(this, "Hết thời gian!", Toast.LENGTH_LONG).show();
    }





    private void showSlideCenterToRight(View view) {

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        view.setAlpha(0f);
        view.setTranslationX(-screenWidth / 5f); // từ giữa chạy sang phải
        view.setVisibility(View.VISIBLE);

        // Animate về đúng vị trí gốc
        view.animate()
                .alpha(1f)
                .translationX(0) // về vị trí ban đầu trong layout
                .setDuration(400)
                .setStartDelay(400)
                .start();
    }


    private void showSlideCenterToLeft(View view) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        view.setAlpha(0f);
        view.setTranslationX(screenWidth / 5f); // từ giữa chạy sang trái
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .translationX(0)
                .setDuration(400)
                .setStartDelay(400)
                .start();
    }




    private void showViewScaleFade(View view) {
        // Hiện từ giữa (scale lớn lên + fade in)
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(400)
                .start();
    }





    private void hideViewScaleFade(View view) {   // Ẩn vào giữa (scale nhỏ lại + fade out)
        view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(400)
                .withEndAction(() -> view.setVisibility(View.INVISIBLE))
                .start();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    } // dọn dẹp khi activity bị détroy



}



