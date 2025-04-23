package com.cardoso.shareyourtime;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SettingsManager settingsManager;
    private NavController navController;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private ImageView profileImageView;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingsManager = new SettingsManager(this);
        settingsManager.applySettings();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.imageView);
        
        setupCameraLauncher();
        checkAndRequestPermissions();
        loadProfileImage();
        
        if (profileImageView != null) {
            profileImageView.setOnClickListener(v -> checkCameraPermission());
        }
        
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
        setupLogoutButton();
    }

    private void setupLogoutButton() {
        NavigationView navigationView = binding.navView;
        MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);
        if (logoutItem != null) {
            logoutItem.setOnMenuItemClickListener(item -> {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            });
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void loadProfileImage() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference imageRef = storageRef.child("profile_images").child(userId + ".jpg");
            
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Usar Glide para cargar la imagen
                com.bumptech.glide.Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(profileImageView);
            }).addOnFailureListener(e -> {
                // Si no hay imagen, cargar una por defecto
                profileImageView.setImageResource(R.drawable.default_profile);
            });
        }
    }

    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (profileImageView != null && imageBitmap != null) {
                        profileImageView.setImageBitmap(imageBitmap);
                        uploadImageToFirebase(imageBitmap);
                    }
                }
            });
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference imageRef = storageRef.child("profile_images").child(userId + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            imageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(MainActivity.this, R.string.image_upload_success, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, getString(R.string.image_upload_failed, e.getMessage()), Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
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