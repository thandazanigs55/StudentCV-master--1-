package com.example.studentcv;

public class User {
    private String email;
    private String editDelete;
    private boolean profileComplete;
    private String role;

    public User() {} // Required for Firestore

    public User(String email, String editDelete, boolean profileComplete, String role) {
        this.email = email;
        this.editDelete = editDelete;
        this.profileComplete = profileComplete;
        this.role = role;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEditDelete() { return editDelete; }
    public void setEditDelete(String editDelete) { this.editDelete = editDelete; }
    public boolean isProfileComplete() { return profileComplete; }
    public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}