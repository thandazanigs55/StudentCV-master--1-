package com.example.studentcv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText nameEditText;
    private TextView roleTextView;
    private Button saveButton;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        roleTextView = view.findViewById(R.id.roleTextView);
        saveButton = view.findViewById(R.id.saveButton);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadProfileData();

        saveButton.setOnClickListener(v -> saveProfileData());

        return view;
    }

    private void loadProfileData() {
        String userId = auth.getCurrentUser().getUid();
        DocumentReference userDoc = firestore.collection("users").document(userId);

        userDoc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String role = documentSnapshot.getString("role");

                        nameEditText.setText(name);
                        roleTextView.setText(role);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show());
    }

    private void saveProfileData() {
        String userId = auth.getCurrentUser().getUid();
        String updatedName = nameEditText.getText().toString().trim();

        if (updatedName.isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            return;
        }

        // Save the updated name to Firestore
        DocumentReference userDoc = firestore.collection("users").document(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", updatedName);

        userDoc.update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}