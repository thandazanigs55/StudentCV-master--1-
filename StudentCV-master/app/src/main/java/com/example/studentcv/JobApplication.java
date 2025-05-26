// File: JobApplication.java
package com.example.studentcv;

import com.google.firebase.Timestamp;

/**
 * Model representing a single job application.
 */
public class JobApplication {

    // Firestore document ID (unique per application)
    private String applicationId;

    // ID of the job post the student applied for
    private String jobId;

    // Student info
    private String studentId;
    private String studentEmail;
    private String cvBase64;

    // Metadata
    private Timestamp appliedDate;
    private String    status;
    private String    jobTitle;

    /** Firestore needs an empty constructor */
    public JobApplication() {}

    public JobApplication(String jobId,
                          String studentId,
                          String studentEmail,
                          String cvBase64,
                          String jobTitle) {
        this.jobId        = jobId;
        this.studentId    = studentId;
        this.studentEmail = studentEmail;
        this.cvBase64     = cvBase64;
        this.appliedDate  = Timestamp.now();
        this.status       = "Pending";
        this.jobTitle     = jobTitle;
    }

    // ─── getters & setters ─────────────────────────────────

    public String getApplicationId() { return applicationId; }
    public void   setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getJobId() { return jobId; }
    public void   setJobId(String jobId) { this.jobId = jobId; }

    public String getStudentId() { return studentId; }
    public void   setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentEmail() { return studentEmail; }
    public void   setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCvBase64() { return cvBase64; }
    public void   setCvBase64(String cvBase64) { this.cvBase64 = cvBase64; }

    public Timestamp getAppliedDate() { return appliedDate; }
    public void      setAppliedDate(Timestamp appliedDate) { this.appliedDate = appliedDate; }

    public String getStatus() { return status; }
    public void   setStatus(String status) { this.status = status; }

    public String getJobTitle() { return jobTitle; }
    public void   setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}
