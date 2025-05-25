package com.example.nexby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button registerButton;
    TextView loginText;
    DatabaseHelper dbHelper; // Add reference to DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Handle register button click
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String type = "admin";

            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert user into the database
            long userId = dbHelper.insertUser(username, email, username, password, type); // Insert user data into database

            if (userId > 0) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                // Redirect to login page
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
            }
        });

        // Redirect to login activity
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }
}
