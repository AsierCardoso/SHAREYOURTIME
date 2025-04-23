package com.cardoso.shareyourtime.utils;

import android.content.Context;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsManager {
    private final Context context;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private static final String COLLECTION_USERS = "users";
    private static final String FIELD_THEME = "theme";
    private static final String FIELD_LANGUAGE = "language";

    public SettingsManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void applySettings() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection(COLLECTION_USERS).document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Aplicar tema
                        if (documentSnapshot.contains(FIELD_THEME)) {
                            boolean isDarkTheme = documentSnapshot.getBoolean(FIELD_THEME);
                            AppCompatDelegate.setDefaultNightMode(
                                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                            );
                        }

                        // Aplicar idioma
                        if (documentSnapshot.contains(FIELD_LANGUAGE)) {
                            String language = documentSnapshot.getString(FIELD_LANGUAGE);
                            if (language != null) {
                                Locale locale = new Locale(language);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
                            }
                        }
                    }
                });
        }
    }

    public void setTheme(boolean isDarkTheme) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Map<String, Object> userData = new HashMap<>();
            userData.put(FIELD_THEME, isDarkTheme);
            
            db.collection(COLLECTION_USERS).document(userId)
                .set(userData, com.google.firebase.firestore.SetOptions.merge());
        }
    }

    public void setLanguage(String language) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Map<String, Object> userData = new HashMap<>();
            userData.put(FIELD_LANGUAGE, language);
            
            db.collection(COLLECTION_USERS).document(userId)
                .set(userData, com.google.firebase.firestore.SetOptions.merge());
        }
    }

    public boolean isDarkTheme() {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
} 
