# PersonApp

Ứng dụng Android giúp quản lý thời gian và sự kiện kèm thông tin thời tiết. Dự án sử dụng Java cho mã nguồn ứng dụng và Gradle (Kotlin DSL) để quản lý build.

## Tính năng chính
- Đồng hồ và báo thức cơ bản (`ClockActivity`, `ActivityAddClock`, `AlarmReceiver`).
- Bấm giờ/đếm giờ (`BamGioActivity`, `DemGioActivity`).
- Lịch và quản lý sự kiện (thêm/sửa/xóa, xem tất cả sự kiện) (`CalendarActivity`, `AddEvent`, `viewAllEvent`).
- Xem thời tiết ngày đẹp và dự báo nhiều ngày (`weatherActivity`, `ViewFineDayWeatherActivity`, `ForecastAdapter`).
- Màn hình lặp theo ngày, hiển thị thông tin thời gian (`LoopDayActivity`).

## Yêu cầu môi trường
- Android Studio mới nhất (Giraffe/Iguana hoặc mới hơn).
- JDK 17 (khuyến nghị, tùy theo cấu hình Android Gradle Plugin).
- Android SDK đã cài đặt (đường dẫn trong `local.properties`).

## Cấu trúc dự án (rút gọn)
```
PersonApp/
├─ app/
│  ├─ src/main/java/com/example/personapp/   # Mã nguồn Java
│  ├─ src/main/res/                          # Tài nguyên giao diện
│  ├─ build.gradle.kts                       # Cấu hình module app
├─ build.gradle.kts                          # Cấu hình cấp dự án
├─ settings.gradle.kts
├─ gradle/                                   # Wrapper Gradle
├─ gradlew, gradlew.bat
└─ local.properties                          # SDK path (không commit)
```

## Bắt đầu
1. Mở Android Studio → Open → chọn thư mục `PersonApp`.
2. Chờ Gradle sync hoàn tất. Nếu được hỏi, cài đặt các SDK/Plugin cần thiết.
3. Chọn thiết bị ảo (AVD) hoặc cắm thiết bị thật (bật USB debugging).
4. Nhấn Run để chạy ứng dụng.

## Build bằng dòng lệnh
Trên Windows PowerShell hoặc Command Prompt tại thư mục gốc dự án:

```bash
./gradlew.bat clean assembleDebug
```

Các lệnh hữu ích khác:

```bash
./gradlew.bat :app:installDebug        # Cài APK debug vào thiết bị đang kết nối
./gradlew.bat connectedAndroidTest     # Chạy instrumented tests (yêu cầu thiết bị/emulator)
./gradlew.bat test                     # Chạy unit tests
./gradlew.bat lint                     # Chạy Android Lint
```

APK debug sau khi build thường nằm tại:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Cấu hình API/Secrets (nếu có)
Tính năng thời tiết có thể cần API key. Vui lòng cập nhật theo cách dự án của bạn đang dùng. Gợi ý:

- Lưu vào `local.properties` (không commit):
  ```
  WEATHER_API_KEY=your_key_here
  ```
  Sau đó đọc trong Gradle và truyền vào BuildConfig.

- Hoặc lưu trong `res/values/strings.xml` (ít an toàn hơn) rồi truy xuất qua `getString(R.string.your_key)`.

Kiểm tra các lớp như `ApiLinhTinh.java`, `weatherActivity.java` để xác định nơi sử dụng và cách truyền khóa.

## Khắc phục sự cố
- Gradle sync lỗi SDK: đảm bảo `local.properties` có dòng `sdk.dir=C:\\Android\\Sdk` (đúng đường dẫn máy bạn).
- Lỗi phiên bản JDK/AGP: nâng cấp Android Studio và chọn JDK 17 trong Project Structure.
- Build thất bại không rõ nguyên nhân: thử `./gradlew.bat clean` rồi build lại.

## Giấy phép
Thêm thông tin giấy phép hoặc quyền tác giả của bạn tại đây.


