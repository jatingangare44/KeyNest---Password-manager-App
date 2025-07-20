package com.jatin.keynest.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jatin.keynest.R;
import com.jatin.keynest.database.EncryptedDBHelper;
import com.jatin.keynest.utils.EncryptionUtils;

public class AddPasswordActivity extends AppCompatActivity {

    private EditText titleInput, usernameInput, passwordInput;
    private TextView passwordStrength;
    private Button saveButton;
    private String masterKey;
    private EncryptedDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);


        titleInput = findViewById(R.id.input_title);
        usernameInput = findViewById(R.id.input_username);
        passwordInput = findViewById(R.id.input_password);
        passwordStrength = findViewById(R.id.password_strength); // Add to layout
        saveButton = findViewById(R.id.save_button);

        masterKey = getIntent().getStringExtra("key");
        dbHelper = new EncryptedDBHelper(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        passwordInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strength = getPasswordStrength(s.toString());
                passwordStrength.setText("Strength: " + strength);
                switch (strength) {
                    case "Weak": passwordStrength.setTextColor(Color.RED); break;
                    case "Medium": passwordStrength.setTextColor(Color.parseColor("#FFA500")); break;
                    case "Strong": passwordStrength.setTextColor(Color.GREEN); break;
                }
            }
        });

        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (title.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String encryptedPassword = EncryptionUtils.encrypt(password, masterKey);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("username", username);
                values.put("encryptedPassword", encryptedPassword);

                db.insert("passwords", null, values);
                Toast.makeText(this, "Password saved!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("key", masterKey);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Encryption error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getPasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[@#$%^&+=!].*")) score++;

        if (score <= 2) return "Weak";
        else if (score <= 4) return "Medium";
        else return "Strong";
    }
}
