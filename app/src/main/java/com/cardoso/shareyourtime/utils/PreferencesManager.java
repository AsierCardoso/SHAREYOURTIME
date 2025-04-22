package com.cardoso.shareyourtime.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import java.util.Locale;

public class PreferencesManager {
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    
    private final SharedPreferences preferences;
    private final Context context;

    public PreferencesManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setTheme(boolean isDarkTheme) {
        preferences.edit().putBoolean(KEY_THEME, isDarkTheme).apply();
    }

    public boolean isDarkTheme() {
        return preferences.getBoolean(KEY_THEME, false);
    }

    public void setLanguage(String languageCode) {
        preferences.edit().putString(KEY_LANGUAGE, languageCode).apply();
        updateLocale(languageCode);
    }

    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, "en");
    }

    private void updateLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            context.getResources().updateConfiguration(config, 
                context.getResources().getDisplayMetrics());
        }
    }
} 