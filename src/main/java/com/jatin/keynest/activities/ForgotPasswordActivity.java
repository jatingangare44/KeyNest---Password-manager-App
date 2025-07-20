package com.jatin.keynest.activities;

import android.content.ContentValues;
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

import com.jatin.keynest.R;
import com.jatin.keynest.database.EncryptedDBHelper;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView questionText;
    EditText answerInput, newPasswordInput;
    Button resetButton;
    EncryptedDBHelper dbHelper;
    String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        questionText = findViewById(R.id.security_question_text);
        answerInput = findViewById(R.id.input_answer);
        newPasswordInput = findViewById(R.id.input_new_password);
        resetButton = findViewById(R.id.reset_button);
        dbHelper = new EncryptedDBHelper(this);

        loadSecurityQuestion();

        resetButton.setOnClickListener(v -> {
            String answer = answerInput.getText().toString().trim();
            String newPass = newPasswordInput.getText().toString().trim();

            if (!answer.equals(correctAnswer)) {
                Toast.makeText(this, "Incorrect answer", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("masterPassword", newPass);
            db.update("master", values, null, null);

            Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();
            finish(); // return to login
        });
    }

    private void loadSecurityQuestion() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT securityQuestion, securityAnswer FROM master", null);
        if (cursor.moveToFirst()) {
            questionText.setText(cursor.getString(0));
            correctAnswer = cursor.getString(1);
        }
        cursor.close();
    }
}
