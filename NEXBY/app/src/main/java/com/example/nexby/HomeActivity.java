package com.example.nexby;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView logoImage;
    private RecyclerView recyclerView;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat = 0.0, userLng = 0.0;
    private static final int REQUEST_LOCATION_PERMISSION = 101;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("userRole", "user"); // Default to "user"

        // Redirect admin users
        if (userRole.equals("admin")) {
            startActivity(new Intent(HomeActivity.this, AdminActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupNavigationDrawer();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        logoImage = findViewById(R.id.logoImage);
        recyclerView = findViewById(R.id.recyclerView);

        logoImage.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pocetna) {
                // Stay on home
            } else if (id == R.id.profil) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            } else if (id == R.id.kontakt) {
                startActivity(new Intent(HomeActivity.this, ContactAdminActivity.class));
            } else if (id == R.id.kategorii) {
                startActivity(new Intent(HomeActivity.this, CategoriesActivity.class));
            } else if (id == R.id.logout) {
                clearUserSession();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
                return true;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLat = location.getLatitude();
                        userLng = location.getLongitude();
                        loadNearbyCompanies(); // Call this after location is available
                    }
                });
    }

    private void loadNearbyCompanies() {
        List<Company> allCompanies = dbHelper.getAllCompanies(); // Retrieve all companies from DB
        List<Company> nearbyCompanies = new ArrayList<>();

        for (Company company : allCompanies) {
            float[] results = new float[1];
            Location.distanceBetween(userLat, userLng, company.getLatitude(), company.getLongitude(), results);
            float distanceInMeters = results[0];

            if (distanceInMeters <= 50) {  // Only include companies within 50 meters
                company.setDistance(distanceInMeters);  // Add distance to the company object
                nearbyCompanies.add(company);
            }
        }

        displayCompanies(nearbyCompanies);
    }


    private void displayCompanies(List<Company> companies) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CompanyAdapter adapter = new CompanyAdapter(this, companies);
        recyclerView.setAdapter(adapter);
    }

    private void clearUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation(); // Retry fetching location
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
