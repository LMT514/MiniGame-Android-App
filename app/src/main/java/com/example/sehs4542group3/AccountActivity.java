package com.example.sehs4542group3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
    private EditText etNewUsername, etNewPassword, etConfirmPassword;
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private TextView tvCurrentUsername, tvCurrentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        dbHelper = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvCurrentUsername = findViewById(R.id.tvCurrentUsername);
        tvCurrentPassword = findViewById(R.id.tvCurrentPassword);
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        showCurrentUserInfo();
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String newUsername = etNewUsername.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newUsername.isEmpty() && newPassword.isEmpty()) {
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newUsername.isEmpty() && !newUsername.equals(currentUsername)) {
                if (dbHelper.checkUserExists(newUsername)) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String updatedUsername = newUsername.isEmpty() ? currentUsername : newUsername;
            String updatedPassword = newPassword.isEmpty() ? dbHelper.getUserPassword(currentUsername) : newPassword;

            if (dbHelper.updateUser(currentUsername, updatedUsername, updatedPassword)) {
                Toast.makeText(this, "Account updated successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("username", updatedUsername);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Account update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCurrentUserInfo() {
        // Get current password from database
        String currentPassword = dbHelper.getUserPassword(currentUsername);

        // Mask password with asterisks
        String maskedPassword = maskPassword(currentPassword);

        // Set values to TextViews
        tvCurrentUsername.setText("Username: " + currentUsername);
        tvCurrentPassword.setText("Password: " + maskedPassword);
    }

    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) return "";
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            masked.append('*');
        }
        return masked.toString();
    }
}