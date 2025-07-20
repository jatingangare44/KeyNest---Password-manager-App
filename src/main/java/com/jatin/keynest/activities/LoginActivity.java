package com.jatin.keynest.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.jatin.keynest.R;
import com.jatin.keynest.database.EncryptedDBHelper;
import com.jatin.keynest.utils.SessionManager;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 2 * 60 * 1000; // 2 minutes

    EditText masterPasswordInput;
    Button loginButton, biometricButton;
    EncryptedDBHelper dbHelper;
    String storedMasterPassword;

    SharedPreferences prefs;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_ATTEMPTS = "login_attempts";
    private static final String KEY_LOCKOUT_TIME = "lockout_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        masterPasswordInput = findViewById(R.id.master_password);
        loginButton = findViewById(R.id.login_button);
        biometricButton = findViewById(R.id.biometric_button);
        dbHelper = new EncryptedDBHelper(this);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        storedMasterPassword = getStoredMasterPassword();

        loginButton.setOnClickListener(v -> {
            if (isLockedOut()) return;

            String input = masterPasswordInput.getText().toString().trim();
            if (input.equals(storedMasterPassword)) {
                prefs.edit().putInt(KEY_ATTEMPTS, 0).apply(); // reset on success
                goToMain(input);
            } else {
                int attempts = prefs.getInt(KEY_ATTEMPTS, 0) + 1;
                prefs.edit().putInt(KEY_ATTEMPTS, attempts).apply();

                if (attempts >= MAX_ATTEMPTS) {
                    long lockUntil = System.currentTimeMillis() + LOCKOUT_DURATION;
                    prefs.edit()
                            .putLong(KEY_LOCKOUT_TIME, lockUntil)
                            .putInt(KEY_ATTEMPTS, 0)
                            .apply();
                    Toast.makeText(this, "Too many failed attempts. App locked for 2 minutes.", Toast.LENGTH_LONG).show();
                    finishAffinity(); // optional: force close the app
                } else {
                    int remaining = MAX_ATTEMPTS - attempts;
                    Toast.makeText(this, "Invalid Master Password. " + remaining + " attempts left.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        biometricButton.setOnClickListener(v -> {
            if (!isLockedOut()) {
                showBiometricPrompt();
            }
        });

        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                != BiometricManager.BIOMETRIC_SUCCESS) {
            biometricButton.setEnabled(false);
        }

        TextView forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private boolean isLockedOut() {
        long lockoutTime = prefs.getLong(KEY_LOCKOUT_TIME, 0);
        if (System.currentTimeMillis() < lockoutTime) {
            long remaining = (lockoutTime - System.currentTimeMillis()) / 1000;
            Toast.makeText(this, "Too many attempts. Try again in " + remaining + " seconds.", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private String getStoredMasterPassword() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT masterPassword FROM master", null);
        String password = "";
        if (cursor.moveToFirst()) {
            password = cursor.getString(0);
        }
        cursor.close();
        return password;
    }

    private void goToMain(String key) {
        new SessionManager(this).updateLastActiveTime();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
        finish();
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(LoginActivity.this, "Biometric authentication successful", Toast.LENGTH_SHORT).show();
                        goToMain(storedMasterPassword);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Unlock KeyNest using Fingerprint or Face")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
