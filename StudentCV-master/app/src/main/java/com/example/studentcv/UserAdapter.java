package com.example.studentcv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users;
    private Context context;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.emailTextView.setText(user.getEmail());
        holder.roleTextView.setText("Role: " + user.getRole());
        holder.profileCompleteTextView.setText(
                "Profile Complete: " + (user.isProfileComplete() ? "Yes" : "No"));

        holder.editButton.setOnClickListener(v -> {
            // Handle edit/delete action
            Toast.makeText(context, "Edit/Delete clicked for " + user.getEmail(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView emailTextView;
        MaterialTextView roleTextView;
        MaterialTextView profileCompleteTextView;
        MaterialButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            profileCompleteTextView = itemView.findViewById(R.id.profileCompleteTextView);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}