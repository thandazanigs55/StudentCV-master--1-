package com.example.studentcv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ViewHolder> {

    private List<JobApplication> applications;
    private Context context;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public ApplicationsAdapter(List<JobApplication> applications, Context context) {
        this.applications = applications;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_application_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobApplication app = applications.get(position);

        holder.emailTextView.setText("Email: " + app.getStudentEmail());

        // Fix: Only try to fetch if jobId is not null or empty
        if (app.getJobId() == null || app.getJobId().isEmpty()) {
            holder.jobTitleTextView.setText("Job Title: Unknown Job");
        } else if (app.getJobTitle() == null || app.getJobTitle().isEmpty() ||
                "Unknown Job".equals(app.getJobTitle()) || "Untitled Job".equals(app.getJobTitle())) {
            holder.jobTitleTextView.setText("Job Title: Loading...");
            FirebaseFirestore.getInstance()
                    .collection("job_posts")
                    .document(app.getJobId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        String fetchedTitle = doc.getString("jobTitle");
                        if (fetchedTitle == null || fetchedTitle.isEmpty())
                            fetchedTitle = doc.getString("title");
                        if (fetchedTitle == null || fetchedTitle.isEmpty())
                            fetchedTitle = "Unknown Job";
                        holder.jobTitleTextView.setText("Job Title: " + fetchedTitle);
                        app.setJobTitle(fetchedTitle);
                    })
                    .addOnFailureListener(e -> holder.jobTitleTextView.setText("Job Title: Unknown Job"));
        } else {
            holder.jobTitleTextView.setText("Job Title: " + app.getJobTitle());
        }

        holder.statusTextView.setText("Status: " + app.getStatus());

        Timestamp ts = app.getAppliedDate();
        if (ts != null) {
            Date d = ts.toDate();
            holder.dateTextView.setText("Applied: " + DATE_FORMAT.format(d));
        } else {
            holder.dateTextView.setText("Applied: N/A");
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView jobTitleTextView;
        TextView statusTextView;
        TextView dateTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView    = itemView.findViewById(R.id.txtEmail);
            jobTitleTextView = itemView.findViewById(R.id.txtJobTitle);
            statusTextView   = itemView.findViewById(R.id.txtStatus);
            dateTextView     = itemView.findViewById(R.id.txtAppliedDate);
        }
    }
}