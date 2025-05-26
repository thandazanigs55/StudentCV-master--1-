// File: RecruiterApplicationsAdapter.java
package com.example.studentcv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecruiterApplicationsAdapter
        extends RecyclerView.Adapter<RecruiterApplicationsAdapter.ApplicationViewHolder> {

    private final List<JobApplication> applicationList;
    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public RecruiterApplicationsAdapter(List<JobApplication> applicationList) {
        this.applicationList = applicationList;
    }

    @NonNull @Override
    public ApplicationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application_recruiter, parent, false);
        return new ApplicationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ApplicationViewHolder h, int pos) {

        JobApplication app = applicationList.get(pos);
        Context ctx = h.itemView.getContext();

        // bind fields
        h.txtStudentEmail.setText(app.getStudentEmail());
        h.txtJobTitle.setText(app.getJobTitle());
        h.txtAppliedDate.setText(
                app.getAppliedDate() != null
                        ? DATE_FMT.format(app.getAppliedDate().toDate())
                        : "N/A"
        );
        h.txtStatus.setText(app.getStatus());  // just the word

        // CV preview
        if (app.getCvBase64() != null && !app.getCvBase64().isEmpty()) {
            byte[] bytes = Base64.decode(app.getCvBase64(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            h.cvPreview.setImageBitmap(bmp);
        } else {
            h.cvPreview.setImageResource(android.R.color.transparent);
        }

        // Accept button
        h.btnApprove.setOnClickListener(v ->
                handleDecision(ctx, app, true, pos)
        );

        // Reject button
        h.btnDisapprove.setOnClickListener(v ->
                handleDecision(ctx, app, false, pos)
        );
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudentEmail, txtJobTitle, txtAppliedDate, txtStatus;
        ImageView cvPreview;
        Button btnApprove, btnDisapprove;

        ApplicationViewHolder(@NonNull View item) {
            super(item);
            txtStudentEmail = item.findViewById(R.id.txtStudentEmail);
            txtJobTitle     = item.findViewById(R.id.txtJobTitle);
            txtAppliedDate  = item.findViewById(R.id.txtAppliedDate);
            txtStatus       = item.findViewById(R.id.txtStatus);
            cvPreview       = item.findViewById(R.id.cvPreviewImageView);
            btnApprove      = item.findViewById(R.id.btnApprove);
            btnDisapprove   = item.findViewById(R.id.btnDisapprove);
        }
    }

    /**
     * Update Firestore status if possible (no checks), update UI,
     * then send email based solely on the button clicked.
     */
    private void handleDecision(Context ctx,
                                JobApplication app,
                                boolean isAccepted,
                                int adapterPos) {
        String newStatus = isAccepted ? "Accepted" : "Rejected";

        // 1) Update Firestore (attempt, even if ID might be missing)
        String docId = app.getApplicationId() != null
                ? app.getApplicationId()
                : app.getJobId();
        if (docId != null && !docId.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("job_applications")
                    .document(docId)
                    .set(new StatusHolder(newStatus), SetOptions.merge());
        }

        // 2) Update local model & UI
        app.setStatus(newStatus);
        notifyItemChanged(adapterPos);

        // 3) Send the appropriate email
        if (isAccepted) {
            sendAcceptanceEmail(ctx, app.getStudentEmail(), app.getJobTitle());
        } else {
            sendRejectionEmail(ctx, app.getStudentEmail(), app.getJobTitle());
        }
    }

    private void sendAcceptanceEmail(Context ctx, String to, String jobTitle) {
        String subject = "Congratulations! Your application was accepted";
        String body =
                "Hello,\n\n" +
                        "We are pleased to inform you that your application for \"" +
                        jobTitle + "\" has been accepted.\n\n" +
                        "We will be in touch with next steps shortly.\n\n" +
                        "Best regards,\nRecruitment Team";
        sendEmail(ctx, to, subject, body);
    }

    private void sendRejectionEmail(Context ctx, String to, String jobTitle) {
        String subject = "Update on your application";
        String body =
                "Hello,\n\n" +
                        "Thank you for applying for \"" + jobTitle + "\".\n" +
                        "After careful consideration, we have decided to move forward with other candidates.\n\n" +
                        "We appreciate your interest and wish you every success.\n\n" +
                        "Best regards,\nRecruitment Team";
        sendEmail(ctx, to, subject, body);
    }
    private static void sendEmail(Context ctx, String to, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));         // only email apps will see this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ to });
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            ctx.startActivity(Intent.createChooser(intent, "Send email"));
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(ctx, "No email app installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /** Helper for Firestore updates */
    private static class StatusHolder {
        String status;
        StatusHolder(String s) { this.status = s; }
        @SuppressWarnings("unused")
        public String getStatus() { return status; }
    }
}
