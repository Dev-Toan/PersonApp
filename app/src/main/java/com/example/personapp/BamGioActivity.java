package com.example.personapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class BamGioActivity extends AppCompatActivity {

    TextView txtBaoThuc, txtDemGio, txtBamGio, txtTime;
    Button btnPause, btnStart, btnPick;
    ImageButton btnBack;
    ListView listView;

    Handler handler = new Handler();

    double startTime = 0;
    double timeBuff = 0;
    double updateTime =0;
    double milliSeconds =0;

    boolean isRunning = false;
    int tempPause=1;
    int tempDel =0;
    private ArrayList<String> lapList = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bam_gio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        txtTime = findViewById(R.id.txtTime);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnPick = findViewById(R.id.btnPick);
        btnBack = findViewById(R.id.btnBack);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lapList);
        listView.setAdapter(adapter);


        btnPick.setVisibility(View.INVISIBLE);
        btnPause.setVisibility(View.INVISIBLE);


        // Start
        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = System.currentTimeMillis();
                handler.postDelayed(runnable, 0);
                isRunning = true;
            }

            hideViewScaleFade(btnStart);
            showSlideCenterToLeft(btnPause);
            showSlideCenterToRight(btnPick);

            btnPause.setText("Tạm dừng");
            btnPick.setText("Bấm");
        });

        // Pause
        btnPause.setOnClickListener(v -> {
            if (isRunning) {
                timeBuff += milliSeconds;
                handler.removeCallbacks(runnable);
                isRunning = false;

                btnPause.setText("Tiếp tục");
                btnPause.setBackgroundColor(Color.parseColor("#FF0000"));
                btnPick.setText("Xóa");
            } else { // Đang dừng → tiếp tục
                startTime = System.currentTimeMillis();
                handler.postDelayed(runnable, 0);
                isRunning = true;

                btnPause.setText("Tạm dừng");
                btnPause.setBackgroundColor(Color.parseColor("#9966CC"));
                btnPick.setText("Bấm");
            }

        });

        // Pick (Lap)

        btnPick.setOnClickListener(v -> {
            if (isRunning) { // Khi chạy thì Bấm/Lap
                lapList.add(0, txtTime.getText().toString());
                adapter.notifyDataSetChanged();
            } else { // Khi dừng thì Xóa
                lapList.clear();
                adapter.notifyDataSetChanged();
                txtTime.setText("00:00.00");
                timeBuff = 0;
                updateTime = 0;
                milliSeconds = 0;

                // Reset lại nút
                btnPick.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                showViewScaleFade(btnStart);
            }
        });

        // Back
        btnBack.setOnClickListener(v -> finish()); }



    private void hideViewScaleFade(View view) {   // Ẩn vào giữa (scale nhỏ lại + fade out)
        view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(400)
                .withEndAction(() -> view.setVisibility(View.INVISIBLE))
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


    private void CheckPause(int temp){
        if(temp==0){
            btnPause.setText("Tạm dừng");
            btnPause.setBackgroundColor(Color.parseColor("#9966CC"));
        }else {
            btnPause.setText("Tiếp tục");
            btnPause.setBackgroundColor(Color.parseColor("#FF0000"));
        }

    }


    private Runnable runnable = new Runnable() {
        @Override public void run() {
            milliSeconds = System.currentTimeMillis() - startTime;
            updateTime = timeBuff + milliSeconds;
            int seconds = (int) (updateTime / 1000);
            int minutes = seconds / 60; seconds = seconds % 60;
            int milli = (int) (updateTime % 1000) / 10;
            txtTime.setText(String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, milli));
            handler.postDelayed(this, 10);
        }
    };
}