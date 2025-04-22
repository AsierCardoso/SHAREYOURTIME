package com.cardoso.shareyourtime.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class SettingsManager {
    private static final String PREFERENCES_NAME = "app_settings";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    
    private final Context context;
    private final SharedPreferences preferences;

    public SettingsManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setTheme(boolean isDark) {
        preferences.edit().putBoolean(KEY_THEME, isDark).apply();
        AppCompatDelegate.setDefaultNightMode(
            isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public boolean isDarkTheme() {
        return preferences.getBoolean(KEY_THEME, false);
    }

    public void setLanguage(String language) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
    }

    public void applySettings() {
        // Aplicar tema
        AppCompatDelegate.setDefaultNightMode(
            isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Aplicar idioma
        String language = getLanguage();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
} 
