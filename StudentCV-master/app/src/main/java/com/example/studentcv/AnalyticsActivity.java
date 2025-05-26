package com.example.studentcv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private RecyclerView recyclerJobs;
    private JobsAdapter jobsAdapter;
    private List<Job> jobList = new ArrayList<>();
    private String userId;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analytics);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        recyclerJobs = findViewById(R.id.recyclerJobs);
        recyclerJobs.setLayoutManager(new LinearLayoutManager(this));

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        jobsAdapter = new JobsAdapter(this, jobList, userId);
        recyclerJobs.setAdapter(jobsAdapter);

        fetchRecruiterJobs();
    }

    private void fetchRecruiterJobs() {
        db.collection("job_posts").whereEqualTo("recruiterId", userId)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Job> jobs = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Job job = doc.toObject(Job.class);
                        if (job != null) {
                            job.setId(doc.getId()); // Set Firestore document ID
                            jobs.add(job);
                        }
                    }
                    jobList.clear();
                    jobList.addAll(jobs);
                    jobsAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch jobs.", Toast.LENGTH_SHORT).show();
                });
    }
}