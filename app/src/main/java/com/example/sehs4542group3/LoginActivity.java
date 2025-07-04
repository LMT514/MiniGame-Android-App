package com.example.sehs4542group3;

import android.content.Intent;
import android.widget.CheckBox;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        CheckBox rememberMe = findViewById(R.id.cbRememberMe);
        TextView btnForgotPassword = findViewById(R.id.btnForgotPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnExit = findViewById(R.id.btnExit);
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        btnExit.setOnClickListener(v -> {
            finish();
        });
        btnForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String savedUsername = prefs.getString("username", "");

        if (isLoggedIn && !savedUsername.isEmpty()) {
            if (dbHelper.checkUserExists(savedUsername)) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("username", savedUsername);
                startActivity(intent);
                finish();
                return;
            } else {
                prefs.edit().clear().apply();
            }
        }

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.checkUser(username, password)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Welcome back! " + username, Toast.LENGTH_SHORT).show();
                if (rememberMe.isChecked()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", username);
                    editor.apply();
                }

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
