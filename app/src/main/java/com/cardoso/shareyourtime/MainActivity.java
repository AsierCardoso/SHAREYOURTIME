package com.cardoso.shareyourtime;

import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.cardoso.shareyourtime.databinding.ActivityMainBinding;
import com.cardoso.shareyourtime.utils.PreferencesManager;
import com.cardoso.shareyourtime.utils.SettingsManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.Navigation;
import java.util.Locale;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SettingsManager settingsManager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingsManager = new SettingsManager(this);
        settingsManager.applySettings();
        
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        
        // Obtener el NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_alarm, R.id.nav_world_clock, R.id.nav_stopwatch,
                    R.id.nav_timer, R.id.nav_tasks)
                .setOpenableLayout(drawer)
                .build();
            
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
            
            // Configurar el BottomNavigationView
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
        
        setupSettingsMenu();
    }

    private void setupSettingsMenu() {
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();

        MenuItem themeItem = menu.findItem(R.id.nav_theme);
        if (themeItem != null) {
            themeItem.setOnMenuItemClickListener(item -> {
                showThemeDialog();
                return true;
            });
        }

        MenuItem languageItem = menu.findItem(R.id.nav_language);
        if (languageItem != null) {
            languageItem.setOnMenuItemClickListener(item -> {
                showLanguageDialog();
                return true;
            });
        }
    }

    private void showThemeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_theme, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_theme);
        
        // Seleccionar el tema actual
        radioGroup.check(settingsManager.isDarkTheme() ? R.id.radio_dark : R.id.radio_light);

        new AlertDialog.Builder(this)
                .setTitle(R.string.theme)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    settingsManager.setTheme(selectedId == R.id.radio_dark);
                    recreate();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showLanguageDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_language, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_language);
        
        // Seleccionar el idioma actual
        radioGroup.check(settingsManager.getLanguage().equals("es") ? R.id.radio_spanish : R.id.radio_english);

        new AlertDialog.Builder(this)
                .setTitle(R.string.language)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    String language = (selectedId == R.id.radio_spanish) ? "es" : "en";
                    settingsManager.setLanguage(language);
                    recreate();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openTimeZoneSettings() {
        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}