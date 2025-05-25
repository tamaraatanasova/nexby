package com.example.nexby;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;

public class AddUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private DatabaseHelper databaseHelper;
    private EditText usernameEditText, emailEditText, nameEditText, passwordEditText;
    private Spinner typeSpinner;
    private Button addUserButton;
    private ImageView logoImage;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_user);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);  // Initialize NavigationView
        databaseHelper = new DatabaseHelper(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        typeSpinner = findViewById(R.id.typeSpinner); // Initialize the Spinner
        addUserButton = findViewById(R.id.addUserButton);
        logoImage = findViewById(R.id.logoImage);

        // Set up the Spinner with user types (user/admin)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // Open the drawer when the logo is clicked
        logoImage.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle the "Add User" button click
        addUserButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String type = typeSpinner.getSelectedItem().toString();

            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(AddUserActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert user into the database
            long result = databaseHelper.insertUser(username, email, name, password, type);
            if (result != -1) {
                Toast.makeText(AddUserActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                // Optionally, clear the fields or go back to a different screen
            } else {
                Toast.makeText(AddUserActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the navigation item selected listener
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

    // Method to clear user session (e.g., remove saved login state)
    private void clearUserSession() {
        // Clear session or shared preferences if necessary
    }
}
