package com.cardoso.shareyourtime.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.receiver.TimerReceiver;

public class TimerService {
    private static final String CHANNEL_ID = "TIMER_CHANNEL";
    private static final int NOTIFICATION_ID = 2;
    private static final long FIVE_MINUTES = 5 * 60 * 1000; // 5 minutos en milisegundos
    private final Context context;
    private final AlarmManager alarmManager;

    public TimerService(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();
    }

    public void scheduleTimer(long durationMillis) {
        Intent intent = new Intent(context, TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = System.currentTimeMillis() + durationMillis;

        // Usar setAlarmClock para todas las duraciones ya que tiene los privilegios más altos
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
    }

    public void cancelTimer() {
        Intent intent = new Intent(context, TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Canal del Temporizador",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para notificaciones del temporizador");
            
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
            
            channel.setSound(alarmSound, attributes);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void showTimerNotification() {
        // Asegurarse de que el dispositivo no entre en modo de suspensión
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ShareYourTime::TimerWakeLock"
        );
        wakeLock.acquire(60000); // 1 minuto

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle(context.getString(R.string.timer))
            .setContentText(context.getString(R.string.timer_finished))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
            .setAutoCancel(true);

        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        wakeLock.release();
    }
} 