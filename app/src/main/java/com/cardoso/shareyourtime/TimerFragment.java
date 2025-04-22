package com.cardoso.shareyourtime;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timerTextView = view.findViewById(R.id.timerTextView);
        hoursPicker = view.findViewById(R.id.hoursPicker);
        minutesPicker = view.findViewById(R.id.minutesPicker);
        secondsPicker = view.findViewById(R.id.secondsPicker);
        startButton = view.findViewById(R.id.startButton);
        resetButton = view.findViewById(R.id.resetButton);

        setupNumberPickers();
        setupButtons();
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
        isTimerRunning = false;
        startButton.setText(R.string.start);
        disableNumberPickers(false);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
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
    }
} 