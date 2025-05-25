package com.example.nexby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView logoImage;
    TextView tvName, tvUsername, tvEmail;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // UI references
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        logoImage = findViewById(R.id.logoImage);
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);

        dbHelper = new DatabaseHelper(this);

        // Toolbar config
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        logoImage.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pocetna) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            } else if (id == R.id.profil) {
                // Already here
            } else if (id == R.id.kontakt) {
                startActivity(new Intent(ProfileActivity.this, ContactAdminActivity.class));
            } else if (id == R.id.kategorii) {
                startActivity(new Intent(ProfileActivity.this, CategoriesActivity.class));
            } else if (id == R.id.logout) {
                clearUserSession();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT name, username, email FROM users WHERE username = ?", new String[]{username});

            if (cursor.moveToFirst()) {
                String name = cursor.getString(0);
                String user = cursor.getString(1);
                String email = cursor.getString(2);

                tvName.setText(name);
                tvUsername.setText("Username: " + user);
                tvEmail.setText("Email: " + email);
            }

            cursor.close();
            db.close();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void clearUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
