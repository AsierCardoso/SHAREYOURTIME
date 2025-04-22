package com.cardoso.shareyourtime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cardoso.shareyourtime.service.TimerService;

public class TimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TimerService timerService = new TimerService(context);
        timerService.showTimerNotification();
    }
} 

 