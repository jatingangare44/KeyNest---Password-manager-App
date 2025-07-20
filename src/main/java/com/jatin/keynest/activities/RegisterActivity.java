package com.jatin.keynest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jatin.keynest.R;
import com.jatin.keynest.database.EncryptedDBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText passwordInput, confirmPasswordInput, securityAnswerInput;
    Spinner securityQuestionSpinner;
    Button setupButton;
    EncryptedDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        passwordInput = findViewById(R.id.setup_master_password);
        confirmPasswordInput = findViewById(R.id.confirm_master_password);
        securityAnswerInput = findViewById(R.id.setup_security_answer);
        securityQuestionSpinner = findViewById(R.id.security_question_spinner);
        setupButton = findViewById(R.id.setup_button);
        dbHelper = new EncryptedDBHelper(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityQuestionSpinner.setAdapter(adapter);

        setupButton.setOnClickListener(v -> {
            String pass = passwordInput.getText().toString().trim();
            String confirmPass = confirmPasswordInput.getText().toString().trim();
            String question = securityQuestionSpinner.getSelectedItem().toString().trim();
            String answer = securityAnswerInput.getText().toString().trim();

            if (pass.length() < 8) {
                Toast.makeText(this, "Master password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (answer.isEmpty()) {
                Toast.makeText(this, "Please enter an answer to the security question", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("masterPassword", pass);
            values.put("securityQuestion", question);
            values.put("securityAnswer", answer);

            db.insert("master", null, values);

            Toast.makeText(this, "Account setup successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
