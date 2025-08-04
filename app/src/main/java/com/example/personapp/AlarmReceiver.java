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

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        // Tạo notification channel nếu cần (API 26+)
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

        // Tạo notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        // Kiểm tra quyền thông báo trước khi hiển thị
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Kiểm tra quyền POST_NOTIFICATIONS trên Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                }
            } else {
                // Android 12 trở xuống không cần kiểm tra quyền POST_NOTIFICATIONS
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            }
        } catch (SecurityException e) {
            // Xử lý khi không có quyền thông báo
            Toast.makeText(context, "Không có quyền hiển thị thông báo", Toast.LENGTH_SHORT).show();
        }
    }
}
