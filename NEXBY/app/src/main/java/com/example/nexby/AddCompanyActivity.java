package com.example.nexby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class AddCompanyActivity extends AppCompatActivity {

    private EditText edtCompanyName, edtAddress, edtPhone, edtEmail, edtWebPage, edtLongitude, edtLatitude;
    private Spinner spinnerCategory;
    private Button btnAddCompany;
    private DatabaseHelper dbHelper;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_company_layout);

        // UI elements
        edtCompanyName = findViewById(R.id.edtCompanyName);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtWebPage = findViewById(R.id.edtWebPage);
        edtLongitude = findViewById(R.id.edtLongitude);
        edtLatitude = findViewById(R.id.edtLatitude);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddCompany = findViewById(R.id.btnAddCompany);

        dbHelper = new DatabaseHelper(this);

        // Drawer and toolbar
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup logo image as toggle for drawer
        ImageView logoImage = findViewById(R.id.logoImage);
        logoImage.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        setupDrawerMenu();
        populateCategorySpinner();

        btnAddCompany.setOnClickListener(v -> addCompany());
    }


    private void populateCategorySpinner() {
        ArrayList<String> categories = dbHelper.getAllCategories(); // Fetch categories without sorting
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }


    private void addCompany() {
        String name = edtCompanyName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String webpage = edtWebPage.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Името и адресата се задолжителни!", Toast.LENGTH_SHORT).show();
            return;
        }

        double longitude, latitude;
        try {
            longitude = Double.parseDouble(edtLongitude.getText().toString().trim());
            latitude = Double.parseDouble(edtLatitude.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Невалидни координати!", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = spinnerCategory.getSelectedItemPosition() + 1;

        long result = dbHelper.insertCompany(name, address, longitude, latitude, email, phone, webpage, categoryId);

        if (result != -1) {
            Toast.makeText(this, "Успешно додадена компанија!", Toast.LENGTH_SHORT).show();
            clearFormFields();  // Reset fields after adding the company
            finish();
        } else {
            Toast.makeText(this, "Неуспешно додавање.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFormFields() {
        edtCompanyName.setText("");
        edtAddress.setText("");
        edtPhone.setText("");
        edtEmail.setText("");
        edtWebPage.setText("");
        edtLongitude.setText("");
        edtLatitude.setText("");
        spinnerCategory.setSelection(0);  // Reset to the first category
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
                // startActivity(new Intent(this, AddCategoryActivity.class));
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
