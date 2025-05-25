package com.example.nexby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    TextView registerText;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userType = sharedPreferences.getString("userType", "user");
            if (userType.equals("admin")) {
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.validateLogin(username, password)) {
                String userType = dbHelper.getUserType(username);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("username", username);
                editor.putString("userType", userType);
                editor.apply();

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                if (userType.equals("admin")) {
                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }

                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        registerText.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }


}
