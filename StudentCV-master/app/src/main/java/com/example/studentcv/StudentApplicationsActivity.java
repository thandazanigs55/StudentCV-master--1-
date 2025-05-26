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

public class StudentApplicationsActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private RecyclerView applicationsRecyclerView;
    private ApplicationsAdapter applicationsAdapter;
    private List<JobApplication> applicationList;
    private String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_applications);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarApplications);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Applications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, StudentDashboardActivity.class));
            finish();
        });

        // RecyclerView + Adapter
        firestore = FirebaseFirestore.getInstance();
        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationList = new ArrayList<>();
        applicationsAdapter = new ApplicationsAdapter(applicationList, this);
        applicationsRecyclerView.setAdapter(applicationsAdapter);

        fetchApplicationsFromFirestore();
    }

    private void fetchApplicationsFromFirestore() {
        firestore.collection("job_applications")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        JobApplication app = doc.toObject(JobApplication.class);
                        applicationList.add(app);
                    }
                    applicationsAdapter.notifyDataSetChanged();
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
        startActivity(new Intent(this, StudentDashboardActivity.class));
        finish();
    }
}
