package com.example.nexby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout categoriesContainer;
    private ImageView logoImage;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Init views
        dbHelper = new DatabaseHelper(this);
        categoriesContainer = findViewById(R.id.categoriesContainer);
        logoImage = findViewById(R.id.logoImage);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Open drawer on logo click
        logoImage.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Load categories and display them
        loadCategories();

        // Setup drawer menu item click handling
        setupDrawerMenu();

        // Load user data if needed
        Intent locationServiceIntent = new Intent(this, LocationNotificationService.class);
        startService(locationServiceIntent);

    }

    private void loadCategories() {
        ArrayList<Category> categories = dbHelper.getAllCategoryObjects();

        for (Category category : categories) {
            MaterialCardView cardView = new MaterialCardView(this);
            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardLayoutParams.setMargins(0, 0, 0, 24);
            cardView.setLayoutParams(cardLayoutParams);

            cardView.setRadius(16f);
            cardView.setStrokeColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            cardView.setStrokeWidth(2);
            cardView.setCardElevation(8f);
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.my_red));

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);

            TextView textView = new TextView(this);
            textView.setText(category.getName());
            textView.setTextSize(22);
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);

            layout.addView(textView);
            cardView.addView(layout);
            categoriesContainer.addView(cardView);

            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminActivity.this, CompaniesByCategoryActivity.class);
                intent.putExtra("category_id", category.getId()); // Pass ID
                intent.putExtra("category_name", category.getName()); // Optional
                startActivity(intent);
            });
        }
    }


    private void setupDrawerMenu() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pocetna) {
                startActivity(new Intent(this, AdminActivity.class));
            } else if (id == R.id.profil) {
                startActivity(new Intent(this, AdminProfileActivity.class));
            } else if (id == R.id.kontakt) {
                startActivity(new Intent(this, ContactAdminActivity.class));
            } else if (id == R.id.addkategorii) {
                // Add category activity (if you have one)
                startActivity(new Intent(this, AddCompanyActivity.class));
            } else if (id == R.id.adduser) {
                startActivity(new Intent(this, AddUserActivity.class));
            } else if (id == R.id.logout) {
                clearUserSession();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void clearUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
