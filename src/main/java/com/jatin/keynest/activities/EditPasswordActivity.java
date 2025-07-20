package com.jatin.keynest.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jatin.keynest.R;
import com.jatin.keynest.database.EncryptedDBHelper;
import com.jatin.keynest.utils.EncryptionUtils;

public class EditPasswordActivity extends AppCompatActivity {

    EditText titleInput, usernameInput, passwordInput;
    Button updateButton;
    EncryptedDBHelper dbHelper;

    int passwordId;
    String encryptedPassword, masterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        titleInput = findViewById(R.id.edit_input_title);
        usernameInput = findViewById(R.id.edit_input_username);
        passwordInput = findViewById(R.id.edit_input_password);
        updateButton = findViewById(R.id.update_button);

        dbHelper = new EncryptedDBHelper(this);

        // Get data from intent
        Intent intent = getIntent();
        passwordId = intent.getIntExtra("id", -1);
        String title = intent.getStringExtra("title");
        String username = intent.getStringExtra("username");
        encryptedPassword = intent.getStringExtra("encryptedPassword");
        masterKey = intent.getStringExtra("key");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        // Set current values
        titleInput.setText(title);
        usernameInput.setText(username);
        try {
            passwordInput.setText(EncryptionUtils.decrypt(encryptedPassword, masterKey));
        } catch (Exception e) {
            passwordInput.setText("");
            Toast.makeText(this, "Failed to decrypt password", Toast.LENGTH_SHORT).show();
        }

        updateButton.setOnClickListener(v -> {
            String newTitle = titleInput.getText().toString().trim();
            String newUsername = usernameInput.getText().toString().trim();
            String newPassword = passwordInput.getText().toString().trim();

            if (newTitle.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String newEncryptedPassword = EncryptionUtils.encrypt(newPassword, masterKey);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("title", newTitle);
                values.put("username", newUsername);
                values.put("encryptedPassword", newEncryptedPassword);
                db.update("passwords", values, "id=?", new String[]{String.valueOf(passwordId)});
                Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();

                // Navigate back to MainActivity
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("key", masterKey);
                startActivity(i);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
