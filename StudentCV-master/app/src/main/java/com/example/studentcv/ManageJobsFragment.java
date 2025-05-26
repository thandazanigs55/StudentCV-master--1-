package com.example.studentcv;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.view.animation.AnimationUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.chip.Chip;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ManageJobsFragment extends Fragment {

    private RecyclerView jobsRecyclerView;
    private JobsAdapter jobsAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ExtendedFloatingActionButton fabAddJob;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Job> jobList;
    private View emptyStateView;
    private Timestamp selectedDeadline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_jobs, container, false);

        initializeViews(view);
        setupFirebase();
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
        loadJobs();

        return view;
    }

    private void initializeViews(View view) {
        jobsRecyclerView = view.findViewById(R.id.jobsRecyclerView);
        fabAddJob = view.findViewById(R.id.fabAddJob);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyStateView = view.findViewById(R.id.emptyStateView);

        // Add animation to FAB
        fabAddJob.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_scale_up));
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void setupRecyclerView() {
        jobList = new ArrayList<>();
       // jobsAdapter = new JobsAdapter(jobList, this::showJobOptions);
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        jobsRecyclerView.setAdapter(jobsAdapter);

        // Add animation to RecyclerView items
        jobsRecyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down));
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.primaryColor);
        swipeRefreshLayout.setOnRefreshListener(this::loadJobs);
    }

    private void setupFab() {
        fabAddJob.setOnClickListener(v -> showCreateJobDialog());
    }

    private void showCreateJobDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_job, null);

        // Initialize all input fields
        TextInputEditText titleInput = dialogView.findViewById(R.id.titleInput);
        TextInputEditText companyInput = dialogView.findViewById(R.id.companyInput);
        TextInputEditText locationInput = dialogView.findViewById(R.id.locationInput);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        TextInputEditText requirementsInput = dialogView.findViewById(R.id.requirementsInput);
        TextInputEditText salaryInput = dialogView.findViewById(R.id.salaryInput);
        TextInputEditText emailInput = dialogView.findViewById(R.id.emailInput);
        TextInputEditText positionsInput = dialogView.findViewById(R.id.positionsInput);
        Chip chipFullTime = dialogView.findViewById(R.id.chipFullTime);
        Chip chipPartTime = dialogView.findViewById(R.id.chipPartTime);
        Chip chipContract = dialogView.findViewById(R.id.chipContract);

        // Setup deadline picker
        dialogView.findViewById(R.id.deadlineButton).setOnClickListener(v ->
                showDatePicker(dialogView.findViewById(R.id.deadlineText)));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create New Job")
                .setView(dialogView)
                .setPositiveButton("Post", (dialog, which) -> {
                    // Validate inputs
                    if (validateInputs(titleInput, companyInput, locationInput, descriptionInput)) {
                        String type = getSelectedJobType(chipFullTime, chipPartTime, chipContract);

                        Job newJob = new Job(
                                titleInput.getText().toString(),
                                companyInput.getText().toString(),
                                locationInput.getText().toString(),
                                descriptionInput.getText().toString(),
                                requirementsInput.getText().toString(),
                                salaryInput.getText().toString(),
                                type,
                                selectedDeadline,
                                auth.getCurrentUser().getUid(),
                                emailInput.getText().toString(),
                                Integer.parseInt(positionsInput.getText().toString())
                        );

                        saveNewJob(newJob);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker(TextInputEditText deadlineText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDeadline = new Timestamp(calendar.getTime());
                    deadlineText.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean validateInputs(TextInputEditText... inputs) {
        boolean isValid = true;
        for (TextInputEditText input : inputs) {
            if (input.getText().toString().trim().isEmpty()) {
                input.setError("This field is required");
                isValid = false;
            }
        }
        return isValid;
    }

    private String getSelectedJobType(Chip... chips) {
        for (Chip chip : chips) {
            if (chip.isChecked()) {
                return chip.getText().toString();
            }
        }
        return "Full-time"; // Default value
    }

    private void saveNewJob(Job job) {
        db.collection("jobs")
                .add(job)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Job posted successfully", Toast.LENGTH_SHORT).show();
                    loadJobs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error posting job: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadJobs() {
        swipeRefreshLayout.setRefreshing(true);
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("jobs")
                .whereEqualTo("recruiterId", currentUserId)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    jobList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Job job = document.toObject(Job.class);
                        job.setId(document.getId());
                        jobList.add(job);
                    }
                    jobsAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading jobs: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void updateEmptyState() {
        if (jobList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            jobsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            jobsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showJobOptions(Job job) {
        String[] options = {"Edit", "Delete", job.getStatus().equals("Active") ? "Close" : "Reopen"};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Job Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            showEditJobDialog(job);
                            break;
                        case 1: // Delete
                            confirmDeleteJob(job);
                            break;
                        case 2: // Toggle status
                            toggleJobStatus(job);
                            break;
                    }
                })
                .show();
    }

    private void showEditJobDialog(Job job) {
        // Similar to create dialog but pre-fill the fields
        // Implement the edit functionality
    }

    private void confirmDeleteJob(Job job) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job posting?")
                .setPositiveButton("Delete", (dialog, which) -> deleteJob(job))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteJob(Job job) {
        db.collection("jobs")
                .document(job.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Job deleted successfully",
                            Toast.LENGTH_SHORT).show();
                    loadJobs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error deleting job: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleJobStatus(Job job) {
        String newStatus = job.getStatus().equals("Active") ? "Closed" : "Active";

        db.collection("jobs")
                .document(job.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Job status updated", Toast.LENGTH_SHORT).show();
                    loadJobs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}