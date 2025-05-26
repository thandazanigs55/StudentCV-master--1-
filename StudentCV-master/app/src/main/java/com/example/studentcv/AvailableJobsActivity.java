package com.example.studentcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AvailableJobsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView jobsRecyclerView;
    private JobsAdapter jobsAdapter;
    private List<JobModel> jobList;
    private List<JobModel> filteredJobList;
    private Set<String> appliedJobIds;
    private Spinner filterSpinner;
    private String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_available_jobs);

        firestore = FirebaseFirestore.getInstance();

        filterSpinner = findViewById(R.id.filterSpinner);
        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        filteredJobList = new ArrayList<>();
        appliedJobIds = new HashSet<>();
        jobsAdapter = new JobsAdapter(this, filteredJobList, job -> {
            // On Apply click, launch application activity
            Intent intent = new Intent(this, JobApplicationActivity.class);
            intent.putExtra("jobId", job.getJobId());
            intent.putExtra("jobTitle", job.getJobTitle());
            startActivity(intent);
        });
        jobsRecyclerView.setAdapter(jobsAdapter);

        setupSpinner();
        fetchAppliedJobIds();
        fetchJobsFromFirestore();
    }

    private void setupSpinner() {
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                applyFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                applyFilter(0);
            }
        });
    }

    private void applyFilter(int filterOption) {
        filteredJobList.clear();
        if (filterOption == 0) {
            filteredJobList.addAll(jobList);
        } else if (filterOption == 1) {
            for (JobModel job : jobList) {
                if (appliedJobIds.contains(job.getJobId())) {
                    filteredJobList.add(job);
                }
            }
        } else if (filterOption == 2) {
            for (JobModel job : jobList) {
                if (!appliedJobIds.contains(job.getJobId())) {
                    filteredJobList.add(job);
                }
            }
        }
        jobsAdapter.notifyDataSetChanged();
    }

    private void fetchAppliedJobIds() {
        firestore.collection("job_applications")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appliedJobIds.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String appliedJobId = document.getString("jobId");
                        if (appliedJobId != null) {
                            appliedJobIds.add(appliedJobId);
                        }
                    }
                    applyFilter(filterSpinner.getSelectedItemPosition());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(AvailableJobsActivity.this, "Error fetching applied jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchJobsFromFirestore() {
        firestore.collection("job_posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    jobList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        JobModel job = document.toObject(JobModel.class);
                        job.setJobId(document.getId());
                        jobList.add(job);
                    }
                    applyFilter(filterSpinner.getSelectedItemPosition());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(AvailableJobsActivity.this, "Error fetching jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}