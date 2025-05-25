package com.example.nexby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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

public class CategoriesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout categoriesContainer;
    private ImageView logoImage;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Initialize views
        dbHelper = new DatabaseHelper(this);
        categoriesContainer = findViewById(R.id.categoriesContainer);
        logoImage = findViewById(R.id.logoImage);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Remove title
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);  // Disable hamburger icon
        }

        // Open drawer on logo click
        logoImage.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Load categories and display them
        loadCategories();

        // Setup drawer menu item click handling
        setupDrawerMenu();
    }

    // Method to load categories from the database and display them as cards
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
                Intent intent = new Intent(CategoriesActivity.this, CompaniesByCategoryActivity.class);
                intent.putExtra("category_id", category.getId()); // Pass ID
                intent.putExtra("category_name", category.getName()); // Optional
                startActivity(intent);
            });
        }
    }

    // Set up the navigation drawer menu item click handling
    private void setupDrawerMenu() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pocetna) {
                startActivity(new Intent(CategoriesActivity.this, HomeActivity.class));
            } else if (id == R.id.profil) {
                startActivity(new Intent(CategoriesActivity.this, ProfileActivity.class));
            } else if (id == R.id.kontakt) {
                startActivity(new Intent(CategoriesActivity.this, ContactAdminActivity.class));
            } else if (id == R.id.kategorii) {
                // Already in the Categories Activity, do nothing
            } else if (id == R.id.logout) {
                clearUserSession();
                startActivity(new Intent(CategoriesActivity.this, MainActivity.class));
                finish();
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // Clear user session (logout functionality)
    private void clearUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Handle back press to close the drawer if open
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
