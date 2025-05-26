package com.example.studentcv;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JobsActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private RecyclerView jobsRecyclerView;
    private JobsAdapter jobsAdapter;
    private List<JobModel> jobList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_jobs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Jobs Posted");
        }

        firestore = FirebaseFirestore.getInstance();
        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        // No apply functionality needed, so empty lambda is passed.
        jobsAdapter = new JobsAdapter(this, jobList, job -> {});
        jobsRecyclerView.setAdapter(jobsAdapter);

        fetchJobs();
    }

    private void fetchJobs() {
        firestore.collection("job_posts")
                .get()
                .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                    jobList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        JobModel job = document.toObject(JobModel.class);
                        jobList.add(job);
                    }
                    jobsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(JobsActivity.this, "Error fetching jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back button in the toolbar.
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}