# Explanation: Dashboard Activities in the Student CV Android App

This document explains the roles and logic of the three main dashboard activities in the Student CV Android application: **AdminDashboardActivity, StudentDashboardActivity,** and **RecruiterDashboardActivity**.

---

## 1. AdminDashboardActivity

**Purpose:**  
Serves as the main dashboard screen for admin users, allowing navigation to manage users, jobs, and provides a logout feature.

**Key Features:**
- **Edge-to-Edge UI:**  
  Uses `EdgeToEdge.enable(this)` and adjusts window insets for a modern, immersive UI.

- **Navigation Buttons:**  
  - **Users Card:** Navigates to the `UsersActivity` for managing registered users.
  - **Jobs Card:** Navigates to the `JobsActivity` for managing posted jobs.
  - **Logout Card:** Logs the admin out by clearing the activity stack and returning to the login screen (`LoginActivity`). Shows a Toast message confirming logout.

**Summary of Code Logic:**
- On activity creation, the layout is set and window insets are handled.
- Three main views are referenced for navigation.
- Each card (users, jobs, logout) has a click listener:
  - **Users:** Launches user management.
  - **Jobs:** Launches job management.
  - **Logout:** Starts the login screen, clears the back stack, shows a toast, and finishes the current activity.

---

## 2. StudentDashboardActivity

**Purpose:**  
Acts as the dashboard for student users, offering quick access to CV management, job search, application tracking, and a button to return to the home screen.

**Key Features:**
- **Card Navigation:**
  - **CV Card:** Navigates to `MyCVActivity` for viewing/editing the student's CV.
  - **Jobs Card:** Navigates to `AvailableJobsActivity` to browse job listings.
  - **Applications Card:** Navigates to `StudentApplicationsActivity` to track applications.

- **Continue Button:**  
  Returns the user to the home screen (`HomeActivity`) and clears the activity stack to prevent navigating back to the dashboard.

**Summary of Code Logic:**
- On activity creation, all card views and the continue button are initialized.
- Each card view and the button has a click listener:
  - **CV:** Opens CV management.
  - **Jobs:** Opens job search.
  - **Applications:** Opens application tracker.
  - **Continue:** Returns to home, clears the stack, and finishes the dashboard activity.

---

## 3. RecruiterDashboardActivity

**Purpose:**  
Provides recruiters with tools to manage jobs, view applications, see analytics, and return to the home screen.

**Key Features:**
- **Edge-to-Edge UI:**  
  Like the admin dashboard, it uses edge-to-edge design and handles window insets for a modern look.

- **Card Navigation:**
  - **Manage Jobs Card:** Navigates to `ManageJobsActivity` for job posting and management.
  - **Applications Card:** Navigates to `ApplicationsActivity` to view applicants.
  - **Analytics Card:** Navigates to `AnalyticsActivity` for insights and statistics.

- **Continue Button:**  
  Navigates back to the home screen (`HomeActivity`).

**Summary of Code Logic:**
- On activity creation, sets up edge-to-edge UI.
- Each card is assigned a click listener:
  - **Manage Jobs:** Opens job management.
  - **Applications:** Opens applications viewer.
  - **Analytics:** Opens analytics dashboard.
- **Continue Button:** Takes the user to the home screen.

---

## General Notes

- **Intent Flags:**  
  In both admin and student dashboards, the "continue" or "logout" actions use the `Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK` flags to clear the activity stack, ensuring that the user cannot navigate back to the dashboard after leaving (useful for security and flow control).

- **Session Handling:**  
  The admin logout logic mentions a placeholder for clearing session data (e.g., `SharedPreferences`). If session management is implemented, the logout action should clear user credentials before returning to the login screen.

- **User Experience:**  
  Each dashboard provides role-based quick navigation, keeps the UI clean, and ensures users can always return to a safe starting point (home or login).

---

**In summary:**  
Each dashboard activity is tailored for its user type (admin, student, recruiter), offering relevant navigation and actions. They use standard Android practices for UI, navigation, and user session control.
