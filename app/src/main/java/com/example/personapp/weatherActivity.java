package com.example.personapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class weatherActivity extends AppCompatActivity {

//    private double currentLat = 21.0285; // Tọa độ mặc định Hà Nội
//    private double currentLon = 105.8542;

    LocationManager locationManager;


    ImageButton btnBack;
    ImageView txtIconWeather;
    TextView txtGlobal,txtNhietDo, txtItmay,txtLuongMua,txtUV,txtNhietDoNow,txtNhietDoNext,txtChiSoUV,txtBinhMinh,txtHoangHon,txtDoAm,txtApSuat,txtDay;
    ImageView imgvIconWeatherNow,imgvIconWeatherNext;
    Button btn5daynext;
//    String cityLocal = "Hanoi";
//    String apiKey = "fd9e8f768d512169fd4e64dafcc20a12";
//    //String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key+ "&units=metric";
//    String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityLocal + "&appid=" + apiKey + "&units=metric&lang=vi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        btnBack = findViewById(R.id.btnBack);
        txtIconWeather=findViewById(R.id.txtIconWeather);
        txtGlobal=findViewById(R.id.txtGlobal);
        txtNhietDo = findViewById(R.id.txtNhietDo);
        txtItmay=findViewById(R.id.txtItmay);
        txtLuongMua=findViewById(R.id.txtLuongMua);
        txtUV=findViewById(R.id.txtUV);
        txtNhietDoNow=findViewById(R.id.txtNhietDoNow);
        txtNhietDoNext=findViewById(R.id.txtNhietDoNext);
        txtChiSoUV=findViewById(R.id.txtChiSoUV);
        txtBinhMinh=findViewById(R.id.txtBinhMinh);
        txtHoangHon=findViewById(R.id.txtHoangHon);
        txtDoAm=findViewById(R.id.txtDoAm);
        txtApSuat=findViewById(R.id.txtApSuat);
        imgvIconWeatherNow=findViewById(R.id.imgvIconWeatherNow);
        imgvIconWeatherNext=findViewById(R.id.imgvIconWeatherNext);
        btn5daynext=findViewById(R.id.btn5daynext);
        txtDay=findViewById(R.id.txtDay);


//        fetchWeather();
//        fetchCurrentWeather();
//        fetchUVIndex();

        txtDay.setText(getTodayVietnamese());
        
        // Set giá trị mặc định cho txtGlobal
        //txtGlobal.setText("---");


        btn5daynext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(weatherActivity.this, ViewFineDayWeatherActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnBack.setOnClickListener(v -> {
            finish();
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
                fetchCurrentWeather(currentLat, currentLon);
                fetchUVIndex(currentLat, currentLon);

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


    /**
     * Lấy tên địa phương theo thứ tự ưu tiên
     * Ưu tiên:
     * 1. Phường/Xã/Thị trấn (suburb, ward, quarter, neighbourhood, sublocality, hamlet, village)
     * 2. Quận/Huyện (city_district, district, county, state_district)
     * 3. Tỉnh/Thành phố (city, town, municipality, state, province)
     */
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

    public void fetchWeather(double currentLat, double currentLon) {
        //String city = "Hanoi";
//        String key = "fd9e8f768d512169fd4e64dafcc20a12";
        String key = ApiLinhTinh.WeatherKey;
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + currentLat + "&lon=" + currentLon + "&appid=" + key + "&units=metric&lang=vi";
        //String url = "https://api.openweathermap.org/data/2.5/uvi?lat=" + currentLat + "&lon=" + currentLon + "&appid=" + key;
        //String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key+ "&units=metric";
        //String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + key + "&units=metric&lang=vi";
//        String city = cityLocal;
//        String key = apiKey;
//        String url = apiUrl;

        //Log.d("UV_REQUEST", "Fetching UV for: " + currentLat + ", " + currentLon);


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray listArray = response.getJSONArray("list");

                            // SỬA: Dùng format phù hợp với API response
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Calendar calendar = Calendar.getInstance();
                            String today = sdf.format(calendar.getTime());

                            calendar.add(Calendar.DATE, 1);
                            String tomorrow = sdf.format(calendar.getTime());

                            double tempToday = -1;
                            double tempTomorrow = -1;
                            String descriptionToday = "";
                            String descriptionTomorrow = "";
                            int humidityToday = -1;
                            int pressureToday = -1;
                            double rainToday = 0.0;

                            for(int i = 0; i < listArray.length(); i++){
                                JSONObject item = listArray.getJSONObject(i);
                                String dt_txt = item.getString("dt_txt");

                                if(dt_txt.startsWith(today) && tempToday == -1){
                                    JSONObject main = item.getJSONObject("main");
                                    tempToday = main.getDouble("temp");
                                    humidityToday = main.getInt("humidity");
                                    pressureToday = main.getInt("pressure");

                                    if(item.has("rain")) {
                                        JSONObject rain = item.getJSONObject("rain");
                                        if(rain.has("3h")) {
                                            rainToday = rain.getDouble("3h");
                                        }
                                    }

                                    JSONArray weatherArray = item.getJSONArray("weather");
                                    descriptionToday = weatherArray.getJSONObject(0).getString("description");
                                }

                                if (dt_txt.startsWith(tomorrow) && tempTomorrow == -1) {
                                    JSONObject main = item.getJSONObject("main");
                                    tempTomorrow = main.getDouble("temp");

                                    JSONArray weatherArray = item.getJSONArray("weather");
                                    descriptionTomorrow = weatherArray.getJSONObject(0).getString("description");
                                }

                                if (tempToday != -1 && tempTomorrow != -1) break;
                            }


                            // Xử lý thời tiết tiếng Việt
                            String processedTodayWeather = processVietnameseWeather(descriptionToday);
                            String processedTomorrowWeather = processVietnameseWeather(descriptionTomorrow);

                            System.out.println("Today weather: " + descriptionToday + " -> " + processedTodayWeather);
                            System.out.println("Tomorrow weather: " + descriptionTomorrow + " -> " + processedTomorrowWeather);

                            txtNhietDo.setText(String.format("%.0f °C", tempToday));
//                            txtItmay.setText(translateWeather(descriptionToday));
//                            txtItmay.setText(descriptionToday);
                            txtItmay.setText(processedTodayWeather);

                            // Set icon dựa trên text đã xử lý
                            String todayIcon = converIconWeather(processedTodayWeather);
                            String tomorrowIcon = converIconWeather(processedTomorrowWeather);



                            txtIconWeather.setImageResource(getResources().getIdentifier(todayIcon, "drawable", getPackageName()));
                            imgvIconWeatherNow.setImageResource(getResources().getIdentifier(todayIcon, "drawable", getPackageName()));
                            imgvIconWeatherNext.setImageResource(getResources().getIdentifier(tomorrowIcon, "drawable", getPackageName()));

//                            txtIconWeather.setImageResource(getResources().getIdentifier(converIconWeather(translateWeather(descriptionToday)), "drawable", getPackageName()));
//                            imgvIconWeatherNow.setImageResource(getResources().getIdentifier(converIconWeather(translateWeather(descriptionToday)), "drawable", getPackageName()));

                            txtDoAm.setText(String.valueOf(humidityToday) + "%");
                            txtApSuat.setText(String.valueOf(pressureToday) + " hPa");
                            txtLuongMua.setText(String.format("Lượng mưa\n%.1f mm", rainToday));

                            txtNhietDoNow.setText(String.format("%.0f°C", tempToday));
                            txtNhietDoNext.setText(String.format("%.0f°C", tempTomorrow));
//                            imgvIconWeatherNext.setImageResource(getResources().getIdentifier(converIconWeather(translateWeather(descriptionTomorrow)), "drawable", getPackageName()));

                        } catch (Exception e) {
                            e.printStackTrace();
                            txtNhietDo.setText("--- °C");
                            txtItmay.setText("Lỗi dữ liệu!");
                            txtLuongMua.setText("--mm");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        txtNhietDo.setText("---");
                        txtItmay.setText("Lỗi kết nối!");
                    }
                });

        requestQueue.add(jsonObjectRequest);

    }



    public String processVietnameseWeather(String description){
        String lowerCase = description.toLowerCase().trim();

        System.out.println("Processing weather: '" + description + "' -> '" + lowerCase + "'");

        if (lowerCase.contains("trời quang") || lowerCase.contains("trời trong")) {
            return "Trời quang";
        } else if (lowerCase.contains("ít mây")) {
            return "Ít mây";
        } else if (lowerCase.contains("mây rải rác") || lowerCase.contains("mây tản mạn")) {
            return "Mây rải rác";
        } else if (lowerCase.contains("mây cụm") || lowerCase.contains("mây nhiều")) {
            return "Mây nhiều";
        } else if (lowerCase.contains("mây u ám") || lowerCase.contains("u ám")) {
            return "Mây u ám";
        } else if (lowerCase.contains("mưa nhẹ") || lowerCase.contains("mưa phùn")) {
            return "Mưa nhẹ";
        } else if (lowerCase.contains("mưa vừa") || lowerCase.equals("mưa")) {
            return "Mưa";
        } else if (lowerCase.contains("mưa to") || lowerCase.contains("mưa nặng") ||
                lowerCase.contains("mưa cường độ nặng") || lowerCase.contains("mưa dữ dội")) {
            return "Mưa to";
        } else if (lowerCase.contains("dông") || lowerCase.contains("sấm")) {
            return "Mưa dông";
        } else if (lowerCase.contains("tuyết")) {
            return "Tuyết";
        } else if (lowerCase.contains("sương mù")) {
            return "Sương mù";
        } else if (lowerCase.contains("sương") || lowerCase.contains("mù")) {
            return "Sương mù nhẹ";
        } else if (lowerCase.contains("gió")) {
            return "Gió giật";
        } else if (lowerCase.contains("lốc")) {
            return "Lốc xoáy";
        }

        System.out.println("Unprocessed weather description: " + description);
        return capitalizeFirst(description);
    }

    // Helper method để viết hoa chữ cái đầu
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public void fetchUVIndex(double currentLat, double currentLon) {
//        String key = "fd9e8f768d512169fd4e64dafcc20a12";
        String key = ApiLinhTinh.WeatherKey;

        //String url = "https://api.openweathermap.org/data/2.5/uvi?lat=" + lat + "&lon=" + lon + "&appid=" + key;
        String url = "https://api.openweathermap.org/data/2.5/uvi?lat=" + currentLat + "&lon=" + currentLon + "&appid=" + key;


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            double uvIndex = response.getDouble("value");
                            txtUV.setText("Chỉ số UV\n" + getUVDescription(uvIndex));
                            txtChiSoUV.setText(String.format("%.1f", uvIndex));

                        } catch (Exception e) {
                            e.printStackTrace();
                            txtUV.setText("Chỉ số UV\n--");
                            txtChiSoUV.setText("--");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        txtUV.setText("Chỉ số UV\n--");
                        txtChiSoUV.setText("--");
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    public void fetchCurrentWeather(double currentLat, double currentLon) {
//        String city = "Hanoi";
//        String key = "fd9e8f768d512169fd4e64dafcc20a12";
        String key = ApiLinhTinh.WeatherKey;
        //String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + currentLat + "&lon=" + currentLon + "&appid=" + key + "&units=metric";
//        String url = apiUrl;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject sys = response.getJSONObject("sys");
                            long sunrise = sys.getLong("sunrise");
                            long sunset = sys.getLong("sunset");



                            txtBinhMinh.setText(convertUnixTime(sunrise));
                            txtHoangHon.setText(convertUnixTime(sunset));

                        } catch (Exception e) {
                            e.printStackTrace();
                            txtBinhMinh.setText("--:--");
                            txtHoangHon.setText("--:--");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        txtBinhMinh.setText("--:--");
                        txtHoangHon.setText("--:--");
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    public String getUVDescription(double uvIndex) {
        if (uvIndex <= 2) {
            return "Thấp";
        } else if (uvIndex <= 5) {
            return "Trung bình";
        } else if (uvIndex <= 7) {
            return "Cao";
        } else if (uvIndex <= 10) {
            return "Rất cao";
        } else {
            return "Cực cao";
        }
    }


    public String converIconWeather(String text){
        String lowerText = text.toLowerCase().trim();

        if (lowerText.contains("trời quang") || lowerText.contains("trong")) {
            return "sun";
        } else if (lowerText.contains("ít mây")) {
            return "itmay";
        } else if (lowerText.contains("lốc") || lowerText.contains("mây bão") || lowerText.contains("mây rải rác")) {
            return "bao1";
        } else if (lowerText.contains("mây nhiều") || lowerText.contains("mây cụm")) {
            return "nhieumay";
        } else if (lowerText.contains("mây u ám") || lowerText.contains("u ám")) {
            return "mayuam";
        } else if (lowerText.contains("mưa nhẹ") || lowerText.contains("mưa phùn")) {
            return "muanho";
        } else if (lowerText.contains("mưa to") || lowerText.contains("mưa nặng") ||
                lowerText.contains("mưa cường độ") || lowerText.contains("mưa dữ dội") ||
                lowerText.contains("mưa vừa") || lowerText.contains("dông kèm")) {
            return "mua";
        } else if (lowerText.contains("mưa") && !lowerText.contains("dông")) {
            return "mua";
        } else if (lowerText.contains("dông")) {
            return "muadong";
        } else if (lowerText.contains("tuyết")) {
            return "tuyet";
        } else if (lowerText.contains("sương mù")) {
            return "suongmu";
        } else if (lowerText.contains("gió")) {
            return "giogiat";
        } else {
            return "sun";
        }
    }



    public String translateWeather(String en) {
        switch (en.toLowerCase()) {
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

            default:
                return "Thời tiết phức tạp";
        }
    }

    private String getTodayVietnamese() {
        Calendar calendar = Calendar.getInstance();
        String[] thu = {"Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 = CN, 2 = Thứ 2, ...
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
        int year = calendar.get(Calendar.YEAR);

        return String.format("%s, %d Tháng %d %d", thu[dayOfWeek - 1], day, month, year);
    }


    private String convertUnixTime(long unixSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixSeconds * 1000L);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d", hour, minute);
    }





}


