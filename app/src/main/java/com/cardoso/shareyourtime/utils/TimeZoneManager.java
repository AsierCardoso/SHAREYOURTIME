package com.cardoso.shareyourtime.utils;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeZoneManager {
    private static final String TIMEZONE_API_URL = "https://maps.googleapis.com/maps/api/timezone/json";
    private final String apiKey;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public interface TimeZoneCallback {
        void onTimeZoneReceived(String timeZoneId);
        void onError(String error);
    }

    public TimeZoneManager(String apiKey) {
        this.apiKey = apiKey;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void getTimeZone(double latitude, double longitude, TimeZoneCallback callback) {
        executor.execute(() -> {
            try {
                long timestamp = System.currentTimeMillis() / 1000;
                String urlStr = String.format(Locale.US,
                        "%s?location=%f,%f&timestamp=%d&key=%s",
                        TIMEZONE_API_URL, latitude, longitude, timestamp, apiKey);

                URL url = new URL(urlStr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject json = new JSONObject(result.toString());
                String status = json.getString("status");

                if ("OK".equals(status)) {
                    String timeZoneId = json.getString("timeZoneId");
                    mainHandler.post(() -> callback.onTimeZoneReceived(timeZoneId));
                } else {
                    String errorMsg = json.optString("errorMessage", "Error desconocido");
                    mainHandler.post(() -> callback.onError("Google Time Zone API: " + errorMsg));
                }

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Exception: " + e.getMessage()));
            }
        });
    }
}
