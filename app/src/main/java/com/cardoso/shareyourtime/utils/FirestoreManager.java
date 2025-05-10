package com.cardoso.shareyourtime.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class FirestoreManager {
    private static final String COLLECTION_USERS = "users";
    private static final String FIELD_TIMEZONES = "timezones";
    private static final String FIELD_DEFAULT_TZ = "defaultTimezone";
    private static final String FIELD_ALARMS = "alarms";
    private static final String FIELD_THEME = "theme";
    private static final String FIELD_LANGUAGE = "language";

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public FirestoreManager() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    // Interfaces para callbacks
    public interface FirestoreCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface TimeZonesCallback {
        void onTimeZonesLoaded(List<String> timeZones, String defaultZoneId);
        void onError(String error);
    }

    public interface AlarmsCallback {
        void onAlarmsLoaded(List<String> alarms);
        void onError(String error);
    }

    // ==========================
    // ZONAS HORARIAS
    // ==========================

    public void saveTimeZones(List<String> timeZones, String defaultZoneId, FirestoreCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_TIMEZONES, timeZones);
        data.put(FIELD_DEFAULT_TZ, defaultZoneId);

        db.collection(COLLECTION_USERS)
                .document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void loadTimeZones(TimeZonesCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        db.collection(COLLECTION_USERS).document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> timeZones = new ArrayList<>();
                    String defaultZone = TimeZone.getDefault().getID(); // fallback local

                    if (doc.exists()) {
                        if (doc.contains(FIELD_TIMEZONES)) {
                            timeZones = (List<String>) doc.get(FIELD_TIMEZONES);
                        }
                        if (doc.contains(FIELD_DEFAULT_TZ)) {
                            defaultZone = doc.getString(FIELD_DEFAULT_TZ);
                        }
                    }

                    // Asegurar que esté la zona por defecto
                    if (!timeZones.contains(defaultZone)) {
                        timeZones.add(0, defaultZone);
                    }

                    callback.onTimeZonesLoaded(timeZones, defaultZone);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ==========================
    // ALARMAS
    // ==========================

    public void saveAlarms(List<String> alarms, FirestoreCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_ALARMS, alarms);

        db.collection(COLLECTION_USERS).document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void loadAlarms(AlarmsCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        db.collection(COLLECTION_USERS).document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> alarms = new ArrayList<>();
                    if (doc.exists() && doc.contains(FIELD_ALARMS)) {
                        alarms = (List<String>) doc.get(FIELD_ALARMS);
                    }
                    callback.onAlarmsLoaded(alarms);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ==========================
    // TEMA
    // ==========================

    public void saveTheme(boolean isDarkTheme, FirestoreCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_THEME, isDarkTheme);

        db.collection(COLLECTION_USERS).document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ==========================
    // IDIOMA
    // ==========================

    public void saveLanguage(String language, FirestoreCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_LANGUAGE, language);

        db.collection(COLLECTION_USERS).document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
