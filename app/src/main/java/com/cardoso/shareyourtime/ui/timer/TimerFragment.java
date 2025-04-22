package com.cardoso.shareyourtime.ui.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.receiver.TimerReceiver;

public class TimerFragment extends Fragment {
    private TextView timerTextView;
    private NumberPicker hoursPicker;
    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;
    private Button startButton;
    private Button resetButton;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis;
    private AlarmManager alarmManager;
    private PendingIntent timerPendingIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        
        initializeViews(root);
        setupNumberPickers();
        setupButtons();
        
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        
        return root;
    }

    private void initializeViews(View view) {
        timerTextView = view.findViewById(R.id.timerTextView);
        hoursPicker = view.findViewById(R.id.hoursPicker);
        minutesPicker = view.findViewById(R.id.minutesPicker);
        secondsPicker = view.findViewById(R.id.secondsPicker);
        startButton = view.findViewById(R.id.startButton);
        resetButton = view.findViewById(R.id.resetButton);
    }

    private void setupNumberPickers() {
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);
    }

    private void setupButtons() {
        startButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                startTimer();
            } else {
                pauseTimer();
            }
        });

        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        long totalTimeInMillis = (hoursPicker.getValue() * 3600000L) +
                                (minutesPicker.getValue() * 60000L) +
                                (secondsPicker.getValue() * 1000L);
        
        if (totalTimeInMillis == 0) return;

        isTimerRunning = true;
        startButton.setText(R.string.pause);
        disableNumberPickers(true);

        // Programar la notificación
        Intent intent = new Intent(getContext(), TimerReceiver.class);
        timerPendingIntent = PendingIntent.getBroadcast(
            getContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = System.currentTimeMillis() + totalTimeInMillis;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                timerPendingIntent
            );
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                timerPendingIntent
            );
        }

        countDownTimer = new CountDownTimer(totalTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                resetTimer();
            }
        }.start();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (timerPendingIntent != null) {
            alarmManager.cancel(timerPendingIntent);
        }
        isTimerRunning = false;
        startButton.setText(R.string.start);
        disableNumberPickers(false);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (timerPendingIntent != null) {
            alarmManager.cancel(timerPendingIntent);
        }
        isTimerRunning = false;
        startButton.setText(R.string.start);
        disableNumberPickers(false);
        hoursPicker.setValue(0);
        minutesPicker.setValue(0);
        secondsPicker.setValue(0);
        timerTextView.setText("00:00:00");
    }

    private void updateTimerText() {
        int hours = (int) (timeLeftInMillis / 3600000);
        int minutes = (int) ((timeLeftInMillis % 3600000) / 60000);
        int seconds = (int) ((timeLeftInMillis % 60000) / 1000);

        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private void disableNumberPickers(boolean disable) {
        hoursPicker.setEnabled(!disable);
        minutesPicker.setEnabled(!disable);
        secondsPicker.setEnabled(!disable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (timerPendingIntent != null) {
            alarmManager.cancel(timerPendingIntent);
        }
    }
} 
