package com.example.nexby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ContactAdminActivity extends AppCompatActivity {

    EditText contactMessage;
    Button sendMessageButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_admin);

        contactMessage = findViewById(R.id.contactMessage);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        logoImage = findViewById(R.id.logoImage);

        // Toolbar setup
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        logoImage.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        sendMessageButton.setOnClickListener(v -> sendMessage());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pocetna) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.profil) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.kontakt) {
                // already here
            } else if (id == R.id.kategorii) {
                startActivity(new Intent(this, CategoriesActivity.class));
            } else if (id == R.id.logout) {
                getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                        .edit().clear().apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void sendMessage() {
        String message = contactMessage.getText().toString();

        if (!message.isEmpty()) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@example.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Message from User");
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client"));
        } else {
            contactMessage.setError("Please type a message");
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
}
