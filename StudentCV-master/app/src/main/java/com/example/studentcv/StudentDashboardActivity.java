package com.example.studentcv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudentDashboardActivity extends AppCompatActivity {

    private CardView cvCard, jobsCard, applicationsCard;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize the views
        cvCard = findViewById(R.id.cvCard);
        jobsCard = findViewById(R.id.jobsCard);
        applicationsCard = findViewById(R.id.applicationsCard);
        continueButton = findViewById(R.id.continueButton);

        // Set click listener for the CV card
        cvCard.setOnClickListener(v -> {
            // Navigate to My CV Activity
            Intent intent = new Intent(StudentDashboardActivity.this, MyCVActivity.class);
            startActivity(intent);
        });

        // Set click listener for the Jobs card
        jobsCard.setOnClickListener(v -> {
            // Navigate to Available Jobs Activity
            Intent intent = new Intent(StudentDashboardActivity.this, AvailableJobsActivity.class);
            startActivity(intent);
        });

        // Set click listener for the Applications card
        applicationsCard.setOnClickListener(v -> {
            // Navigate to Student Applications Activity
            Intent intent = new Intent(StudentDashboardActivity.this, StudentApplicationsActivity.class);
            startActivity(intent);
        });

        // Set click listener for the continue button
        continueButton.setOnClickListener(v -> {
            // Navigate back to Home Activity
            Intent intent = new Intent(StudentDashboardActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
            startActivity(intent);
            finish(); // Finish this activity to prevent returning to it
        });
    }
}