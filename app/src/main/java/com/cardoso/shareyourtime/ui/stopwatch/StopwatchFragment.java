package com.cardoso.shareyourtime.ui.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;

public class StopwatchFragment extends Fragment {
    private TextView timeView;
    private Button startButton;
    private Button resetButton;
    private Button lapButton;
    private RecyclerView lapsRecyclerView;
    private StopwatchAdapter adapter;
    private Handler handler;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long lastLapTime = 0L;
    private boolean isRunning = false;

    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTimer(timeInMilliseconds);
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        
        timeView = root.findViewById(R.id.time_view);
        startButton = root.findViewById(R.id.start_button);
        resetButton = root.findViewById(R.id.reset_button);
        lapButton = root.findViewById(R.id.lap_button);
        lapsRecyclerView = root.findViewById(R.id.laps_recycler_view);

        handler = new Handler(Looper.getMainLooper());
        adapter = new StopwatchAdapter();

        lapsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lapsRecyclerView.setAdapter(adapter);

        setupButtons();
        
        return root;
    }

    private void setupButtons() {
        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis() - timeInMilliseconds;
                handler.postDelayed(updateTimerThread, 0);
                isRunning = true;
                startButton.setText(R.string.stop);
                lapButton.setEnabled(true);
            } else {
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
                startButton.setText(R.string.start);
                lapButton.setEnabled(false);
            }
        });

        resetButton.setOnClickListener(v -> {
            handler.removeCallbacks(updateTimerThread);
            isRunning = false;
            timeInMilliseconds = 0L;
            lastLapTime = 0L;
            startButton.setText(R.string.start);
            updateTimer(0);
            adapter.clearLaps();
            lapButton.setEnabled(false);
        });

        lapButton.setOnClickListener(v -> {
            if (isRunning) {
                String lapTime = formatTime(timeInMilliseconds - lastLapTime);
                adapter.addLap(lapTime);
                lastLapTime = timeInMilliseconds;
            }
        });

        lapButton.setEnabled(false);
    }

    private void updateTimer(long timeInMillis) {
        timeView.setText(formatTime(timeInMillis));
    }

    private String formatTime(long timeInMillis) {
        int minutes = (int) (timeInMillis / 60000);
        int seconds = (int) ((timeInMillis % 60000) / 1000);
        int milliseconds = (int) (timeInMillis % 1000);
        return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimerThread);
    }
}