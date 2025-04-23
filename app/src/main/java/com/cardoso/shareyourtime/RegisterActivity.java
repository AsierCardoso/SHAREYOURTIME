package com.cardoso.shareyourtime;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cardoso.shareyourtime.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnRegister.setOnClickListener(v -> registerUser());
        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(username)) {
            binding.etUsername.setError(getString(R.string.username_required));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError(getString(R.string.email_required));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(getString(R.string.invalid_email));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError(getString(R.string.password_required));
            return;
        }

        if (password.length() < 6) {
            binding.etPassword.setError(getString(R.string.password_too_short));
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError(getString(R.string.passwords_dont_match));
            return;
        }

        // Deshabilitar botón durante el registro
        binding.btnRegister.setEnabled(false);

        // Verificar si el nombre de usuario ya existe
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        binding.etUsername.setError(getString(R.string.username_taken));
                        binding.btnRegister.setEnabled(true);
                    } else {
                        // Crear usuario en Firebase Auth
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(authTask -> {
                                if (authTask.isSuccessful()) {
                                    // Guardar información adicional en Firestore
                                    String userId = auth.getCurrentUser().getUid();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("username", username);
                                    user.put("email", email);
                                    user.put("createdAt", System.currentTimeMillis());

                                    db.collection("users").document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            // Actualizar el perfil del usuario
                                            auth.getCurrentUser().updateProfile(
                                                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                                    .setDisplayName(username)
                                                    .build()
                                            );

                                            Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
                                            // Asegurar que la sesión se mantenga iniciada
                                            auth.getCurrentUser().reload().addOnCompleteListener(reloadTask -> {
                                                if (reloadTask.isSuccessful()) {
                                                    startActivity(new Intent(this, MainActivity.class));
                                                    finish();
                                                }
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            binding.btnRegister.setEnabled(true);
                                            Toast.makeText(this, 
                                                getString(R.string.save_data_failed, e.getMessage()), 
                                                Toast.LENGTH_SHORT).show();
                                        });
                                } else {
                                    binding.btnRegister.setEnabled(true);
                                    Toast.makeText(this, 
                                        getString(R.string.registration_failed, authTask.getException().getMessage()),
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                } else {
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(this, 
                        getString(R.string.registration_failed, task.getException().getMessage()),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
} 