package com.cardoso.shareyourtime.utils;

import android.content.Context;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.List;
import java.util.Locale;

public class SettingsManager {
    private final Context context;
    private final FirestoreManager firestoreManager;

    public SettingsManager(Context context) {
        this.context = context;
        this.firestoreManager = new FirestoreManager();
    }

    public void applySettings() {
        firestoreManager.loadTimeZones(new FirestoreManager.TimeZonesCallback() {
            @Override
            public void onTimeZonesLoaded(List<String> timeZones, String defaultZoneId) {
                // No necesitamos hacer nada con las zonas horarias aquí en Settings por ahora,
                // pero se cumple con la interfaz correctamente.
            }

            @Override
            public void onError(String error) {
                // Manejar el error si es necesario
            }
        });
    }

    public void setTheme(boolean isDarkTheme) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        firestoreManager.saveTheme(isDarkTheme, new FirestoreManager.FirestoreCallback() {
            @Override
            public void onSuccess() {
                // Tema guardado correctamente
            }

            @Override
            public void onError(String error) {
                // Manejar el error si es necesario
            }
        });
    }

    public void setLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        firestoreManager.saveLanguage(language, new FirestoreManager.FirestoreCallback() {
            @Override
            public void onSuccess() {
                // Idioma guardado correctamente
            }

            @Override
            public void onError(String error) {
                // Manejar el error si es necesario
            }
        });
    }

    public boolean isDarkTheme() {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
