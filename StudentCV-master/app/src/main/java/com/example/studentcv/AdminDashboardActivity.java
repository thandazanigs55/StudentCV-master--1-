package com.example.studentcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        // Adjust window insets (padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup navigation buttons
        View btnUsers = findViewById(R.id.cardUsers);
        View btnJobs = findViewById(R.id.cardJobs);
        View btnLogout = findViewById(R.id.cardOther); // Now acts as "Logout"

        btnUsers.setOnClickListener((View v) -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UsersActivity.class);
            startActivity(intent);
        });

        btnJobs.setOnClickListener((View v) -> {
            Intent intent = new Intent(AdminDashboardActivity.this, JobsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener((View v) -> {
            // Clear session/logout logic here if you have any (e.g., SharedPreferences)
            // For now, just return to login screen
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            Toast.makeText(AdminDashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}