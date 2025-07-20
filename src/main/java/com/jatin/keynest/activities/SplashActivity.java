package com.jatin.keynest.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.jatin.keynest.R;
import com.jatin.keynest.database.EncryptedDBHelper;

public class SplashActivity extends AppCompatActivity {

    EncryptedDBHelper dbHelper;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dbHelper = new EncryptedDBHelper(this);
        progressBar = findViewById(R.id.splash_progress_bar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        // Start loading animation
        progressBar.setIndeterminate(true);

        new Handler().postDelayed(() -> {
            if (isMasterPasswordSet()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, RegisterActivity.class));
            }
            finish();
        }, 2000); // 2 seconds splash
    }

    private boolean isMasterPasswordSet() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT masterPassword FROM master", null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
