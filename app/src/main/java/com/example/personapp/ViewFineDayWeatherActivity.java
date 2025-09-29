package com.example.personapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewFineDayWeatherActivity extends AppCompatActivity {


    private ImageButton imgBack;

    LocationManager locationManager;
    TextView txtGlobal;

    private RecyclerView recyclerViewForecast;
    private ForecastAdapter forecastAdapter;
    private List<ForecastItem> forecastList;
    private RainfallChartView rainfallChartView;

    private static final String API_KEY = ApiLinhTinh.WeatherKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Đảm bảo rằng bạn đã đổi tên layout thành "activity_fine_day_weather" nếu cần
        setContentView(R.layout.activity_view_fine_day_weather);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initViews();
        setupRecyclerView();



        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        // Khởi tạo LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Toast.makeText(this, "Không tìm thấy GPS!", Toast.LENGTH_SHORT).show();
            return;
        }


        // yeu cau quyen gps
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
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



                getWardNameFromLocation(currentLat, currentLon);
                fetch5DayForecast(currentLat, currentLon);


                getWardNameFromLocation(currentLat,currentLon);

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        });


    }


    private void setupRecyclerView() {
        forecastList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(forecastList);
        recyclerViewForecast.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewForecast.setAdapter(forecastAdapter);
    }
    private void initViews() {
        txtGlobal = findViewById(R.id.txtGlobal);
        imgBack = findViewById(R.id.imgBack);
        recyclerViewForecast = findViewById(R.id.recyclerViewForecast);

        // Thay thế placeholder bằng custom chart view
        View placeholder = findViewById(R.id.chartPlaceholder);
        if (placeholder != null && placeholder.getParent() != null) {
            android.view.ViewGroup parent = (android.view.ViewGroup) placeholder.getParent();
            int index = parent.indexOfChild(placeholder);
            parent.removeView(placeholder);

            rainfallChartView = new RainfallChartView(this);
            rainfallChartView.setLayoutParams(placeholder.getLayoutParams());
            rainfallChartView.setId(R.id.chartPlaceholder);
            parent.addView(rainfallChartView, index);
        }

        imgBack.setOnClickListener(v -> onBackPressed());
    }




    public void getWardNameFromLocation(double lat, double lon) {
        String url = String.format(Locale.US,
                "https://nominatim.openstreetmap.org/reverse?" +
                        "lat=%.9f&lon=%.9f&format=json&accept-language=vi&zoom=18&addressdetails=1",
                lat, lon
        );

        Log.d("GEOCODING", "URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("GEOCODING", "Response: " + response.toString());

                            if (response.has("error")) {
                                txtGlobal.setText("Lỗi: " + response.getString("error"));
                                return;
                            }

                            // Lấy thông tin địa chỉ
                            JSONObject address = response.optJSONObject("address");
                            if (address != null) {
                                // Chỉ lấy phường/quận/huyện
                                String administrativeUnit = getAdministrativeUnit(address);

                                if (!administrativeUnit.isEmpty()) {
                                    txtGlobal.setText(administrativeUnit);
                                    Log.d("GEOCODING", "Administrative unit: " + administrativeUnit);
                                } else {
                                    txtGlobal.setText("Không xác định được vị trí");
                                }
                            } else {
                                txtGlobal.setText("Không tìm thấy thông tin địa chỉ");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("GEOCODING", "Parse error: " + e.getMessage());
                            txtGlobal.setText("Lỗi xử lý dữ liệu");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("GEOCODING", "Network error: " + error.getMessage());
                        txtGlobal.setText("Lỗi kết nối mạng");
                    }
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("User-Agent", "AndroidWeatherApp/1.0 (contact@example.com)");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }


    public String getAdministrativeUnit(JSONObject address) {
        // Ưu tiên 1: Phường/Xã/Thị trấn
        String[] wardKeys = {
                "suburb",           // Phường (thường dùng ở thành phố lớn)
                "ward",             // Phường
                "quarter",          // Khu phố
                "neighbourhood",    // Khu vực lân cận
                "sublocality",      // Địa phương con
                "hamlet",           // Thôn, xóm
                "village"           // Xã (khu vực nông thôn)
        };

        for (String key : wardKeys) {
            if (address.has(key)) {
                String value = address.optString(key, "").trim();
                if (!value.isEmpty()) {
                    Log.d("GEOCODING", "Ward found: " + key + " = " + value);
                    return value;
                }
            }
        }

        // Ưu tiên 2: Quận/Huyện (nếu không tìm thấy phường)
        String[] districtKeys = {
                "city_district",    // Quận thành phố
                "district",         // Quận/Huyện
                "county",           // Huyện
                "state_district"    // Quận bang (ít dùng ở VN)
        };

        for (String key : districtKeys) {
            if (address.has(key)) {
                String value = address.optString(key, "").trim();
                if (!value.isEmpty()) {
                    Log.d("GEOCODING", "District found: " + key + " = " + value);
                    return value;
                }
            }
        }

        // Ưu tiên 3: Tỉnh/Thành phố (nếu không tìm thấy quận/huyện)
        String[] cityKeys = {
                "city",             // Thành phố
                "town",             // Thị xã
                "municipality",     // Đô thị
                "state",            // Tỉnh/Bang
                "province"          // Tỉnh
        };

        for (String key : cityKeys) {
            if (address.has(key)) {
                String value = address.optString(key, "").trim();
                if (!value.isEmpty()) {
                    Log.d("GEOCODING", "City/Province found: " + key + " = " + value);
                    return value;
                }
            }
        }

        return ""; // Không tìm thấy
    }

    private String getDayOfWeek(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String[] days = {"Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};
            return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        } catch (Exception e) {
            return "N/A";
        }
    }


    private int getWeatherIcon(String iconCode) {
        // Map OpenWeatherMap icon codes to available local drawable resources
        switch (iconCode) {
            case "01d": // clear sky day
            case "01n": // clear sky night
                return R.drawable.sun;
            case "02d": // few clouds day
            case "02n": // few clouds night
            case "03d": // scattered clouds
            case "03n":
            case "04d": // broken clouds
            case "04n":
                return R.drawable.ic_cloud;
            case "09d": // shower rain
            case "09n":
            case "10d": // rain
            case "10n":
                return R.drawable.mua;
            case "11d": // thunderstorm
            case "11n":
                return R.drawable.giogiat;
            case "13d": // snow
            case "13n":
                return R.drawable.tuyet;
            case "50d": // mist
            case "50n":
                return R.drawable.suongmu;
            default:
                return R.drawable.icon_water; // default icon
        }
    }


    private void fetch5DayForecast(double lat, double lon) {
        String url = String.format(Locale.US,
                "https://api.openweathermap.org/data/2.5/forecast?lat=%.6f&lon=%.6f&units=metric&appid=%s&lang=vi",
                lat, lon, API_KEY);

        Log.d("WEATHER_API", "URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        parseForecastData(response);
                    } catch (JSONException e) {
                        Log.e("WEATHER_API", "Parse error: " + e.getMessage());
                        Toast.makeText(this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("WEATHER_API", "Error: " + error.toString());
                    Toast.makeText(this, "Không thể tải dữ liệu thời tiết", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void parseForecastData(JSONObject response) throws JSONException {
        JSONArray list = response.getJSONArray("list");
        forecastList.clear();

        // Nhóm dữ liệu theo ngày
        List<DailyData> dailyDataList = new ArrayList<>();
        String currentDate = "";
        DailyData currentDayData = null;

        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            String dateTime = item.getString("dt_txt"); // "2024-01-20 12:00:00"
            String date = dateTime.split(" ")[0];

            // Nếu là ngày mới
            if (!date.equals(currentDate)) {
                if (currentDayData != null) {
                    dailyDataList.add(currentDayData);
                }
                currentDate = date;
                currentDayData = new DailyData(date);
            }

            // Thêm dữ liệu vào ngày hiện tại
            if (currentDayData != null) {
                JSONObject main = item.getJSONObject("main");
                double temp = main.getDouble("temp");
                currentDayData.addTemp(temp);

                // Lấy icon từ thời điểm giữa ngày (12:00)
                if (dateTime.contains("12:00:00")) {
                    JSONArray weather = item.getJSONArray("weather");
                    if (weather.length() > 0) {
                        String icon = weather.getJSONObject(0).getString("icon");
                        currentDayData.setIconCode(icon);
                    }
                }

                // Lấy lượng mưa
                if (item.has("rain")) {
                    JSONObject rain = item.getJSONObject("rain");
                    if (rain.has("3h")) {
                        currentDayData.addRainfall(rain.getDouble("3h"));
                    }
                }
            }
        }

        // Thêm ngày cuối cùng
        if (currentDayData != null) {
            dailyDataList.add(currentDayData);
        }

        // Lấy 5 ngày đầu tiên
        List<Double> rainfallData = new ArrayList<>();
        List<String> dayLabels = new ArrayList<>();

        for (int i = 0; i < Math.min(5, dailyDataList.size()); i++) {
            DailyData day = dailyDataList.get(i);
            String dayOfWeek = getDayOfWeek(day.getDate());
            int icon = getWeatherIcon(day.getIconCode());
            int minTemp = (int) Math.round(day.getMinTemp());
            int maxTemp = (int) Math.round(day.getMaxTemp());
            double rainfall = day.getTotalRainfall();

            forecastList.add(new ForecastItem(dayOfWeek, icon, minTemp, maxTemp, rainfall));
            rainfallData.add(rainfall);

            // Label ngắn cho biểu đồ
            String shortLabel = dayOfWeek.replace("Thứ ", "T").replace("Chủ Nhật", "CN");
            dayLabels.add(shortLabel);
        }

        forecastAdapter.updateData(forecastList);

        // Cập nhật biểu đồ
        if (rainfallChartView != null) {
            rainfallChartView.setData(rainfallData, dayLabels);
        }
    }




    private static class DailyData {
        private String date;
        private List<Double> temps = new ArrayList<>();
        private String iconCode = "01d";
        private double totalRainfall = 0;

        public DailyData(String date) {
            this.date = date;
        }

        public void addTemp(double temp) {
            temps.add(temp);
        }

        public void setIconCode(String icon) {
            this.iconCode = icon;
        }

        public void addRainfall(double rain) {
            this.totalRainfall += rain;
        }

        public String getDate() {
            return date;
        }

        public double getMinTemp() {
            double min = Double.MAX_VALUE;
            for (double t : temps) {
                if (t < min) min = t;
            }
            return min;
        }

        public double getMaxTemp() {
            double max = Double.MIN_VALUE;
            for (double t : temps) {
                if (t > max) max = t;
            }
            return max;
        }

        public String getIconCode() {
            return iconCode;
        }

        public double getTotalRainfall() {
            return totalRainfall;
        }
    }

}


