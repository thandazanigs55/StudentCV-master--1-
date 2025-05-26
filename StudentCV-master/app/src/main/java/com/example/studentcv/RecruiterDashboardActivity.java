package com.example.studentcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecruiterDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recruiter_dashboard);

        // Handle edge-to-edge UI adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to navigate to Manage Jobs
        findViewById(R.id.manageJobsCard).setOnClickListener(v -> {
            Intent intent = new Intent(RecruiterDashboardActivity.this, ManageJobsActivity.class);
            startActivity(intent);
        });

        // Button to navigate to Applications
        findViewById(R.id.applicationsCard).setOnClickListener(v -> {
            Intent intent = new Intent(RecruiterDashboardActivity.this, ApplicationsActivity.class);
            startActivity(intent);
        });

        // Button to navigate to Analytics
        findViewById(R.id.analyticsCard).setOnClickListener(v -> {
            Intent intent = new Intent(RecruiterDashboardActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        // Continue to home button
        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecruiterDashboardActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}