package com.example.nexby;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class CompaniesByCategoryActivity extends AppCompatActivity {

    private LinearLayout companiesContainer;
    private DatabaseHelper dbHelper;
    private Toolbar toolbar;
    private Button btnBack;
    private String userRole;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_by_category);

        toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.btnBack);
        companiesContainer = findViewById(R.id.companiesContainer);
        dbHelper = new DatabaseHelper(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        userRole = dbHelper.getUserRoleById(userId);

        btnBack.setOnClickListener(v -> onBackPressed());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            loadCompaniesWithProximityFilter();
        }
    }

    private void loadCompaniesWithProximityFilter() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                showCompaniesNearby(location);
            } else {
                requestNewLocation();
            }
        });
    }

    private void requestNewLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setNumUpdates(1);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    showCompaniesNearby(location);
                } else {
                    Toast.makeText(CompaniesByCategoryActivity.this, "Дојде до грешка при добивање на твојата локација", Toast.LENGTH_SHORT).show();
                }
            }
        };

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
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    private void showCompaniesNearby(Location location) {
        int categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId != -1) {
            List<Company> companies = dbHelper.getCompaniesByCategoryId2(categoryId);
            companiesContainer.removeAllViews();

            boolean found = false;
            for (Company company : companies) {
                float[] results = new float[1];
                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        company.getLatitude(), company.getLongitude(), results);

                if (results[0] <= 50) {
                    found = true;

                    // Inflate the card layout
                    View cardView = getLayoutInflater().inflate(R.layout.item_company_card, companiesContainer, false);

                    TextView name = cardView.findViewById(R.id.companyName);
                    TextView loc = cardView.findViewById(R.id.companyLocation);
                    TextView dist = cardView.findViewById(R.id.companyDistance);

                    name.setText(company.getName());
                    loc.setText("Локација: " + company.getLatitude() + ", " + company.getLongitude());
                    dist.setText("На " + (int) results[0] + " метри од тебе");

                    companiesContainer.addView(cardView);
                }
            }

            if (!found) {
                TextView noNearby = new TextView(this);
                noNearby.setText("Нема достапни компании на 50 метри од тебе.");
                noNearby.setTextSize(18);
                noNearby.setPadding(24, 24, 24, 24);
                companiesContainer.addView(noNearby);

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCompaniesWithProximityFilter();
            } else {
                Toast.makeText(this, "Одбиена дозвола за пристап до твојата локација", Toast.LENGTH_LONG).show();
            }
        }
    }
}
