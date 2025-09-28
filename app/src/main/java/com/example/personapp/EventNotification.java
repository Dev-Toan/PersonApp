package com.example.personapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

public class EventNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //String title = intent.getStringExtra("Sự kiện của bạn sắp bắt đầu");
        String title = intent.getStringExtra("title");

        if(title == null){
            title ="su kien sap dien ra";
        }

        String content = intent.getStringExtra("content");
        if(content != null && content.length() > 30){
            content = content.substring(0,30 ) + "...";
        }else {
            content = title;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "event_channel";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Thong bao su kien",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Thong bao su kien sap dien ra");
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.setShowBadge(true);

            notificationManager.createNotificationChannel(channel);

        }

        // am thanh thong bao
        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("📅 " + title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setLights(Color.RED, 3000, 3000);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
