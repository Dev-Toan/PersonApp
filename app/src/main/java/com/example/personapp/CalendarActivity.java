package com.example.personapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    ImageButton btnBack;
    ImageButton iconweather;
    TextView nhietdo;
    TextView textweather;
    Button btnseeall;
    TextView txttieude1;
    TextView txtnoidung1;
    TextView txttieude2;
    TextView txtnoidung2;
    TextView txttieude3;
    TextView txtnoidung3;
    TextView txttieude4;
    TextView txtnoidung4;
    TextView txtdayofweek;
    TextView txtdate;
    TextView txtmonthofyear;
    CalendarView calendarView;



    private ArrayList<Event> allEvents = new ArrayList<>();

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btnBack);
        iconweather = findViewById(R.id.iconweather);
        nhietdo = findViewById(R.id.nhietdo);
        textweather = findViewById(R.id.textweather);
        btnseeall = findViewById(R.id.btnseeall);
        txttieude1 = findViewById(R.id.txttieude1);
        txtnoidung1 = findViewById(R.id.txtnoidung1);
        txttieude2 = findViewById(R.id.txttieude2);
        txtnoidung2 = findViewById(R.id.txtnoidung2);
        txttieude3 = findViewById(R.id.txttieude3);
        txtnoidung3 = findViewById(R.id.txtnoidung3);
        txttieude4 = findViewById(R.id.txttieude4);
        txtnoidung4 = findViewById(R.id.txtnoidung4);
        txtdayofweek = findViewById(R.id.txtdayofweek);
        txtdate = findViewById(R.id.txtdate);
        txtmonthofyear = findViewById(R.id.txtmonthofyear);
        calendarView = findViewById(R.id.calendar);



            btnBack.setOnClickListener(v ->{
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });

        btnseeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, viewAllEvent.class);
                startActivity(intent);
                }
        });




        // yeu cau quyen gps
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
            return;
        }

        // Khởi tạo LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Toast.makeText(this, "Không tìm thấy GPS!", Toast.LENGTH_SHORT).show();
            return;
        }

        // lay vi tri
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1, new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double currentLat = location.getLatitude();
                double currentLon = location.getLongitude();

                //getAddressFromLatLng(currentLat, currentLon);
                //txtGlobal.setText(currentLat + ", " + currentLon);

                fetchWeather(currentLat, currentLon);




            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        });








        String[] thuVN = {"Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};
        Calendar calendar = java.util.Calendar.getInstance();
        int thu = calendar.get(Calendar.DAY_OF_WEEK); // Chủ nhật = 1
        txtdayofweek.setText(thuVN[thu - 1]);

        txtdate.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        txtmonthofyear.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + ", " + calendar.get(Calendar.YEAR));

        // Load và hiển thị 4 sự kiện đầu tiên
        loadEvents();
        sortEventsByDateTime();

        // Debug: In ra tất cả sự kiện để kiểm tra
        System.out.println("=== DEBUG: All Events ===");
        for (Event event : allEvents) {
            System.out.println("Event: " + event.getTieude() + " - Date: " + event.getNgay());
        }

        List<Event> todayEvents = getTodayEvents();
        displayFourEvents(todayEvents);




    }


    public void fetchWeather(double currentLat, double currentLon) {
        //String city = "Hanoi";
        String key = "fd9e8f768d512169fd4e64dafcc20a12";
        //String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + currentLat + "&lon=" + currentLon + "&appid=" + key + "&units=metric&lang=vi";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + currentLat + "&lon=" + currentLon + "&appid=" + key + "&units=metric&lang=vi";
        //String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key+ "&units=metric";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            double temp = main.getDouble("temp");
                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weather = weatherArray.getJSONObject(0);
                            String description = weather.getString("description");

                            nhietdo.setText(String.format("%.0f °C", temp));
                            //textweather.setText(translateWeather(description));
                            textweather.setText(description);


                            setIconweather();

                        } catch (Exception e) {
                            e.printStackTrace();
                            nhietdo.setText("--- °C");
                            textweather.setText("Lỗi dữ liệu!");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        nhietdo.setText("---");
                        textweather.setText("Lỗi kết nối!");
                    }
                });

        requestQueue.add(jsonObjectRequest);

    }

    public String translateWeather(String en){
        switch (en.toLowerCase()){
            case "clear sky":
                return "Trời quang";
            case "few clouds":
                return "Ít mây";
            case "scattered clouds":
                return "Mây bão";
            case "broken clouds":
                return "Mây nhiễu";
            case "shower rain":
                return "Mưa nhỏ";
            case "rain":
                return "Mưa";
            case "thunderstorm":
                return "Mưa dông";
            case "snow":
                return "Tuyết";
            case "mist":
                return "Sương mù";
            case "overcast clouds":
                return "Mây u ám";
            case "light rain":
                return "Mưa nhẹ";
            case "moderate rain":
                return "Mưa vừa";
            case "heavy intensity rain":
                return "Mưa to";
            case "thunderstorm with light rain":
                return "Dông kèm mưa nhỏ";
            case "thunderstorm with heavy rain":
                return "Dông kèm mưa to";
            case "drizzle":
                return "Mưa phùn";
            case "haze":
                return "Sương mù nhẹ";
            case "fog":
                return "Sương mù đặc";
            case "squalls":
                return "Gió Giật";
            case "tornado":
                return "Lốc xoáy";

            default:return "Thời tiết phức tạp";
        }
    }


    public void setIconweather(){
        String temp = textweather.getText().toString();
        switch (temp){
            case "Trời quang":
                iconweather.setImageResource(R.drawable.sun);
                break;
            case "Ít mây":
                iconweather.setImageResource(R.drawable.itmay);
                break;
            case "Mây bão":
                iconweather.setImageResource(R.drawable.bao1);
                break;
            case "Mây nhiễu":
                iconweather.setImageResource(R.drawable.nhieumay);
                break;
            case "Mưa nhỏ":
                iconweather.setImageResource(R.drawable.muanho);
                break;
            case "Mưa":
                iconweather.setImageResource(R.drawable.mua);
                break;
            case "Mưa dông":
                iconweather.setImageResource(R.drawable.muadong);
                break;
            case "Tuyết":
                iconweather.setImageResource(R.drawable.tuyet);
                break;
            case "Sương mù":
                iconweather.setImageResource(R.drawable.suongmu);
                break;
            case "Mây u ám":
                iconweather.setImageResource(R.drawable.mayuam);
                break;
            case "Mưa nhẹ":
                iconweather.setImageResource(R.drawable.muanho);
                break;
            case "Mưa vừa":
                iconweather.setImageResource(R.drawable.mua);
                break;
            case "Mưa to":
                iconweather.setImageResource(R.drawable.mua);
                break;
            case "Dông kèm mưa nhỏ":
                iconweather.setImageResource(R.drawable.mua);
                break;
            case "Dông kèm mưa to":
                iconweather.setImageResource(R.drawable.mua);
                break;
            case "Mưa phùn":
                iconweather.setImageResource(R.drawable.muanho);
                break;
            case "Sương mù nhẹ":
                iconweather.setImageResource(R.drawable.suongmu);
                break;
            case "Sương mù đặc":
                iconweather.setImageResource(R.drawable.suongmu);
                break;
            case "Gió Giật":
                iconweather.setImageResource(R.drawable.giogiat);
                break;
            case "Lốc xoáy":
                iconweather.setImageResource(R.drawable.bao1);

            default: iconweather.setImageResource(R.drawable.sun);
        }
    }

    private void loadEvents(){
        SharedPreferences prefs = getSharedPreferences("events_prefs", MODE_PRIVATE);
        String json = prefs.getString("events_list", null);
        if(json != null) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<Event>>() {
            }.getType();
            ArrayList<Event> loadedEvents = gson.fromJson(json, type);
            allEvents.clear();
            allEvents.addAll(loadedEvents);
        }
    }

    private void sortEventsByDateTime() {
        allEvents.sort((e1, e2) -> {
            // So sánh ngày trước
            int compareDate = e1.getNgay().compareTo(e2.getNgay());
            if (compareDate == 0) {
                // Nếu cùng ngày thì so sánh thời gian
                return e1.getStartTime().compareTo(e2.getStartTime());
            } else {
                return compareDate;
            }
        });
    }

    private List<Event> getTodayEvents(){
        List<Event> todayEvents = new ArrayList<>();

        // Lấy ngày hôm nay
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        System.out.println("Today's date: " + today.getTime());

        for(Event event : allEvents){
            // So sánh ngày bằng cách so sánh Date objects
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(event.getNgay());
            eventDate.set(Calendar.HOUR_OF_DAY, 0);
            eventDate.set(Calendar.MINUTE, 0);
            eventDate.set(Calendar.SECOND, 0);
            eventDate.set(Calendar.MILLISECOND, 0);

            System.out.println("Event date: " + eventDate.getTime() + " - Title: " + event.getTieude());

            // So sánh bằng cách so sánh ngày, tháng, năm
            if(eventDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
               eventDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
               eventDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)){
                todayEvents.add(event);
                System.out.println("Found today's event: " + event.getTieude());
            }
        }

        System.out.println("Total today's events found: " + todayEvents.size());
        return todayEvents;
    }

    private void displayFourEvents(List<Event> eventsToShow) {
        List<Event> selectFourEvents = eventsToShow.subList(0,Math.min(4, eventsToShow.size()));
        int maxLength =50;

        if (selectFourEvents.size() >0){
            Event event1 = selectFourEvents.get(0);
            txttieude1.setText(event1.getTieude());

            String text1 = event1.getNoidung();
            if (text1.length() > maxLength) {
                text1 = text1.substring(0, maxLength) + "...";
            }
            txtnoidung1.setText(text1);
        }else {
            txttieude1.setText("Chưa có sự kiện");
            txtnoidung1.setText("-----");

        }

        if(selectFourEvents.size() >1){
            Event event2 = selectFourEvents.get(1);
            txttieude2.setText(event2.getTieude());

            String text2 = event2.getNoidung();
            if (text2.length() > maxLength) {
                text2 = text2.substring(0, maxLength) + "...";
            }
            txtnoidung2.setText(text2);
        }else {
            txttieude2.setText("Chưa có sự kiện");
            txtnoidung2.setText("-----");
        }

        if(selectFourEvents.size() >2){
            Event event3 = selectFourEvents.get(2);
            txttieude3.setText(event3.getTieude());

            String text3 = event3.getNoidung();
            if (text3.length() > maxLength) {
                text3 = text3.substring(0, maxLength) + "...";
            }
            txtnoidung3.setText(text3);
            }else {
            txttieude3.setText("Chưa có sự kiện");
            txtnoidung3.setText("-----");
        }

        if(selectFourEvents.size() >3) {
            Event event4 = selectFourEvents.get(3);
            txttieude4.setText(event4.getTieude());

            String text4 = event4.getNoidung();
            if (text4.length() > 20) {
                text4 = text4.substring(0, 20) + "...";
            }
            txtnoidung4.setText(text4);
            }else {
            txttieude4.setText("Chưa có sự kiện");
            txtnoidung4.setText("-----");
        }


    }



    @Override
    protected void onResume() {
        super.onResume();
        // Reload dữ liệu sự kiện khi quay lại từ màn hình khác
        loadEvents();
        sortEventsByDateTime();
        List<Event> todayEvents = getTodayEvents();
        displayFourEvents(todayEvents);
    }

}