package com.example.studentcv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Check if the user is a student and redirect to StudentDashboardActivity
        if (isStudentUser()) {
            return; // Exit the method to prevent continuing with HomeActivity setup
        }

        // Restore selected fragment on rotation or set default
        if (savedInstanceState == null) {
            loadFragment(new JobsFragment());
        }

        // Handle navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.enter_from_right, R.anim.exit_to_left,
                                R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    private boolean isStudentUser() {
        if (auth.getCurrentUser() == null) {
            return false;
        }

        String userId = auth.getCurrentUser().getUid();

        // Check the user's role in Firestore
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    String role = document.getString("role");
                    if ("Student".equals(role)) {
                        // Redirect to StudentDashboardActivity
                        Intent intent = new Intent(HomeActivity.this, StudentDashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Finish the current activity to ensure it's removed from the stack
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show();
                });

        return false; // Return false temporarily; the actual redirection happens asynchronously
    }

    private void handleLogout() {
        auth.signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}