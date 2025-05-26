package com.example.studentcv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManageJobsActivity extends AppCompatActivity {

    private TextInputLayout jobTitleInput, companyNameInput, jobDescriptionInput;
    private Spinner jobTypeSpinner, jobLocationSpinner;
    private Button postJobButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_manage_jobs);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        jobTitleInput = findViewById(R.id.jobTitleInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        jobDescriptionInput = findViewById(R.id.jobDescriptionInput);
        jobTypeSpinner = findViewById(R.id.jobTypeSpinner);
        jobLocationSpinner = findViewById(R.id.jobLocationSpinner);
        postJobButton = findViewById(R.id.postJobButton);

        // Populate spinners with data
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.job_types,
                android.R.layout.simple_spinner_item
        );
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobTypeSpinner.setAdapter(jobTypeAdapter);

        ArrayAdapter<CharSequence> jobLocationAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.job_locations,
                android.R.layout.simple_spinner_item
        );
        jobLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobLocationSpinner.setAdapter(jobLocationAdapter);

        // Handle post job button click
        postJobButton.setOnClickListener(v -> postJobToFirestore());
    }

    private void postJobToFirestore() {
        String jobTitle = jobTitleInput.getEditText().getText().toString().trim();
        String companyName = companyNameInput.getEditText().getText().toString().trim();
        String jobDescription = jobDescriptionInput.getEditText().getText().toString().trim();
        String jobType = jobTypeSpinner.getSelectedItem().toString();
        String jobLocation = jobLocationSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(jobTitle) || TextUtils.isEmpty(companyName) || TextUtils.isEmpty(jobDescription)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String recruiterId = auth.getCurrentUser().getUid();

        Map<String, Object> jobData = new HashMap<>();
        jobData.put("jobTitle", jobTitle);
        jobData.put("companyName", companyName);
        jobData.put("jobDescription", jobDescription);
        jobData.put("jobType", jobType);
        jobData.put("jobLocation", jobLocation);
        jobData.put("recruiterId", recruiterId);
        jobData.put("timestamp", System.currentTimeMillis());

        firestore.collection("job_posts")
                .add(jobData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Job posted successfully!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to post job: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearInputs() {
        jobTitleInput.getEditText().setText("");
        companyNameInput.getEditText().setText("");
        jobDescriptionInput.getEditText().setText("");
        jobTypeSpinner.setSelection(0);
        jobLocationSpinner.setSelection(0);
    }
}