// File: ApplicationsActivity.java
package com.example.studentcv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView applicationsRecyclerView;
    private RecruiterApplicationsAdapter adapter;
    private List<JobApplication> applicationList;
    private String recruiterEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbarApplications);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Job Applications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, RecruiterDashboardActivity.class));
            finish();
        });

        firestore = FirebaseFirestore.getInstance();
        recruiterEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationList = new ArrayList<>();
        adapter = new RecruiterApplicationsAdapter(applicationList);
        applicationsRecyclerView.setAdapter(adapter);

        fetchApplicationsFromFirestore();
    }

    private void fetchApplicationsFromFirestore() {
        firestore.collection("job_applications")
                .get()
                .addOnSuccessListener(qs -> {
                    applicationList.clear();
                    for (QueryDocumentSnapshot doc : qs) {
                        JobApplication app = doc.toObject(JobApplication.class);
                        // ← Capture the Firestore doc‑ID so you can update it later
                        app.setApplicationId(doc.getId());

                        String jobId = app.getJobId();
                        if (jobId == null || jobId.isEmpty()) {
                            app.setJobTitle("Unknown Job");
                            applicationList.add(app);
                            adapter.notifyDataSetChanged();
                            continue;
                        }

                        // Fetch job post to verify recruiter & title
                        firestore.collection("job_posts")
                                .document(jobId)
                                .get()
                                .addOnSuccessListener(jobDoc -> {
                                    if (!jobDoc.exists()) {
                                        app.setJobTitle("Untitled Job");
                                        applicationList.add(app);
                                        adapter.notifyDataSetChanged();
                                        return;
                                    }
                                    String postRecruiterEmail = jobDoc.getString("recruiterEmail");
                                    if (recruiterEmail != null &&
                                            recruiterEmail.equals(postRecruiterEmail)) {
                                        app.setJobTitle(jobDoc.getString("jobTitle"));
                                        applicationList.add(app);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this,
                                                "Error fetching job details: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error fetching applications: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, RecruiterDashboardActivity.class));
        finish();
    }
}
