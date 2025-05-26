package com.example.studentcv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobViewHolder> {
    private Context context;
    private List<Job> recruiterJobList;
    private List<JobModel> studentJobList;
    private String recruiterId;
    private Mode mode;

    // For student apply
    public interface OnApplyClickListener {
        void onApplyClick(JobModel job);
    }
    private OnApplyClickListener onApplyClickListener;

    public enum Mode { RECRUITER, STUDENT }

    // Recruiter constructor
    public JobsAdapter(Context context, List<Job> jobList, String recruiterId) {
        this.context = context;
        this.recruiterJobList = jobList;
        this.recruiterId = recruiterId;
        this.mode = Mode.RECRUITER;
    }

    // Student constructor (must pass listener for Apply)
    public JobsAdapter(Context context, List<JobModel> jobList, OnApplyClickListener onApplyClickListener) {
        this.context = context;
        this.studentJobList = jobList;
        this.onApplyClickListener = onApplyClickListener;
        this.mode = Mode.STUDENT;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context != null ? context : parent.getContext())
                .inflate(R.layout.item_job_post, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        if (mode == Mode.RECRUITER) {
            Job job = recruiterJobList.get(position);
            holder.tvJobTitle.setText(job.getTitle());
            holder.tvCompanyName.setText("Company: " + job.getCompany());
            holder.tvJobType.setText("Type: " + job.getType());
            holder.tvJobLocation.setText("Location: " + job.getLocation());
            holder.tvJobDescription.setText(job.getDescription());

            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnApply.setVisibility(View.GONE);

            holder.btnDelete.setOnClickListener(v -> {
                String docId = job.getId();
                FirebaseFirestore.getInstance().collection("job_posts")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(holder.itemView.getContext(), "Job deleted", Toast.LENGTH_SHORT).show();
                            recruiterJobList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, recruiterJobList.size());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                        });
            });

        } else if (mode == Mode.STUDENT) {
            JobModel job = studentJobList.get(position);
            holder.tvJobTitle.setText(job.getJobTitle());
            holder.tvCompanyName.setText("Company: " + job.getCompanyName());
            holder.tvJobType.setText("Type: " + job.getJobType());
            holder.tvJobLocation.setText("Location: " + job.getJobLocation());
            holder.tvJobDescription.setText(job.getJobDescription());

            holder.btnDelete.setVisibility(View.GONE);
            holder.btnApply.setVisibility(View.VISIBLE);

            holder.btnApply.setOnClickListener(v -> {
                if (onApplyClickListener != null) {
                    onApplyClickListener.onApplyClick(job);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mode == Mode.RECRUITER && recruiterJobList != null) {
            return recruiterJobList.size();
        } else if (studentJobList != null) {
            return studentJobList.size();
        }
        return 0;
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompanyName, tvJobType, tvJobLocation, tvJobDescription;
        Button btnDelete, btnApply;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvJobType = itemView.findViewById(R.id.tvJobType);
            tvJobLocation = itemView.findViewById(R.id.tvJobLocation);
            tvJobDescription = itemView.findViewById(R.id.tvJobDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnApply = itemView.findViewById(R.id.btnApply);
        }
    }
}