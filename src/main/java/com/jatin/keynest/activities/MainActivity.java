package com.jatin.keynest.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jatin.keynest.R;
import com.jatin.keynest.adapters.PasswordAdapter;
import com.jatin.keynest.database.EncryptedDBHelper;
import com.jatin.keynest.models.PasswordModel;
import com.jatin.keynest.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PasswordAdapter adapter;
    private List<PasswordModel> passwordList;
    private String masterKey;
    private Button addButton;
    private SessionManager sessionManager;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        masterKey = getIntent().getStringExtra("key");
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_about) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            return false;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }



        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPasswordActivity.class);
            intent.putExtra("key", masterKey);
            startActivity(intent);
        });

        searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPasswords(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordList = loadPasswords();
        adapter = new PasswordAdapter(passwordList, masterKey);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isSessionExpired()) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            sessionManager.updateLastActiveTime();
            passwordList.clear();
            passwordList.addAll(loadPasswords());
            adapter.updateList(passwordList); // refresh on resume
        }
    }

    private List<PasswordModel> loadPasswords() {
        List<PasswordModel> list = new ArrayList<>();
        EncryptedDBHelper dbHelper = new EncryptedDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM passwords", null);
        while (cursor.moveToNext()) {
            list.add(new PasswordModel(
                    cursor.getInt(0),
                    cursor.getString(1), // title
                    cursor.getString(2), // username
                    cursor.getString(3)  // password
            ));
        }
        cursor.close();
        return list;
    }

    private void filterPasswords(String query) {
        List<PasswordModel> filteredList = new ArrayList<>();
        for (PasswordModel model : passwordList) {
            if (model.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(model);
            }
        }
        adapter.updateList(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
