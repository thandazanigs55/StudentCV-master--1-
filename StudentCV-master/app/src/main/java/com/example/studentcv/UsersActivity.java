package com.example.studentcv;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private final List<User> usersList = new ArrayList<>();
    private UserAdapter adapter;


    private CircularProgressIndicator progressBar;     // ⬅ uses Material 3 indicator
    private ExtendedFloatingActionButton fabAddUser;   // ⬅ matches XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // ─── views ────────────────────────────────────────────────────────

        progressBar            = findViewById(R.id.progressBar);
        fabAddUser             = findViewById(R.id.fabAddUser);

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.usersRecyclerView);
        adapter = new UserAdapter(this, usersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Top‑app‑bar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Users Management");
        }

        // FAB action
        fabAddUser.setOnClickListener(
                v -> startActivity(new Intent(this, RegisterActivity.class)));

        // Firestore
        db = FirebaseFirestore.getInstance();
        fetchUsers();
    }

    // ─── Fetch & UI helpers ───────────────────────────────────────────────
    private void fetchUsers() {
        showLoading(true);

        db.collection("users")
                .get()
                .addOnSuccessListener(snapshots -> {
                    usersList.clear();
                    int complete = 0, incomplete = 0;

                    for (DocumentSnapshot doc : snapshots) {
                        try {
                            User user = doc.toObject(User.class);
                            if (user != null) {
                                usersList.add(user);
                                if (user.isProfileComplete()) complete++;
                                else                           incomplete++;
                            }
                        } catch (Exception e) {
                            Log.e("Firestore", "User parse error", e);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showErrorDialog("Error fetching users", e.getMessage());
                });
    }



    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        fabAddUser.setEnabled(!loading);
    }

    private void showErrorDialog(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Retry",  (d, w) -> fetchUsers())
                .setNegativeButton("Close",  null)
                .show();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
}
