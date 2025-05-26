package com.example.studentcv;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Job {
    private String id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String requirements;
    private String salary;
    private String type; // Full-time, Part-time, Contract
    private Timestamp postedDate;
    private Timestamp deadline;
    private String recruiterId;
    private String status; // Active, Closed
    private String contactEmail;
    private int numberOfPositions;

    // Empty constructor needed for Firestore
    public Job() {}

    public Job(String title, String company, String location, String description,
               String requirements, String salary, String type, Timestamp deadline,
               String recruiterId, String contactEmail, int numberOfPositions) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.requirements = requirements;
        this.salary = salary;
        this.type = type;
        this.postedDate = Timestamp.now();
        this.deadline = deadline;
        this.recruiterId = recruiterId;
        this.status = "Active";
        this.contactEmail = contactEmail;
        this.numberOfPositions = numberOfPositions;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getRequirements() { return requirements; }
    public String getSalary() { return salary; }
    public String getType() { return type; }
    public Timestamp getPostedDate() { return postedDate; }
    public Timestamp getDeadline() { return deadline; }
    public String getRecruiterId() { return recruiterId; }
    public String getStatus() { return status; }
    public String getContactEmail() { return contactEmail; }
    public int getNumberOfPositions() { return numberOfPositions; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setType(String type) { this.type = type; }
    public void setPostedDate(Timestamp postedDate) { this.postedDate = postedDate; }
    public void setDeadline(Timestamp deadline) { this.deadline = deadline; }
    public void setRecruiterId(String recruiterId) { this.recruiterId = recruiterId; }
    public void setStatus(String status) { this.status = status; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setNumberOfPositions(int numberOfPositions) { this.numberOfPositions = numberOfPositions; }
}