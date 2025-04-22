package com.cardoso.shareyourtime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.cardoso.shareyourtime.service.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ShareYourTime::AlarmWakeLock"
        );
        wakeLock.acquire(60000); // 1 minuto

        int alarmId = intent.getIntExtra("ALARM_ID", 0);
        String label = intent.getStringExtra("ALARM_LABEL");

        AlarmService alarmService = new AlarmService(context);
        alarmService.showAlarmNotification(alarmId, label);

        wakeLock.release();
    }
} 
