package com.example.personapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "event_notification_channel";
    private static final String ALARM_CHANNEL_ID = "alarm_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Kiểm tra xem đây có phải là chuông báo hay thông báo sự kiện
        String message = intent.getStringExtra("message");
        
        if (message != null) {
            // Đây là chuông báo từ ActivityAddClock
            handleAlarmNotification(context, message);
        } else {
            // Đây là thông báo sự kiện
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            handleEventNotification(context, title, content);
        }
    }

    // Xử lý thông báo chuông báo
    private void handleAlarmNotification(Context context, String message) {
        // Tạo notification channel cho chuông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chuông Báo Thức";
            String description = "Thông báo chuông báo thức";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(ALARM_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Tạo notification cho chuông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Chuông Báo Thức")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500, 200, 500, 200, 500, 200, 500});

        showNotification(context, builder.build());
    }

    // Xử lý thông báo sự kiện
    private void handleEventNotification(Context context, String title, String content) {
        // Tạo notification channel cho sự kiện
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Reminder";
            String description = "Nhắc nhở sự kiện";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Tạo notification cho sự kiện
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        showNotification(context, builder.build());
    }

    // Hiển thị notification với kiểm tra quyền
    private void showNotification(Context context, android.app.Notification notification) {
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Kiểm tra quyền POST_NOTIFICATIONS trên Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                }
            } else {
                // Android 12 trở xuống không cần kiểm tra quyền POST_NOTIFICATIONS
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            }
        } catch (SecurityException e) {
            // Xử lý khi không có quyền thông báo
            Toast.makeText(context, "Không có quyền hiển thị thông báo", Toast.LENGTH_SHORT).show();
        }
    }
}
