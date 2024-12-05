package com.mahi.bitebazaar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private Button saveButton;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userNameEditText = findViewById(R.id.userNameEditText);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);
        saveButton.setOnClickListener(v -> {
            String newUserName = userNameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(newUserName)) {
                userNameEditText.setError("Name cannot be empty");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            saveButton.setEnabled(false);
            updateUserName(newUserName);
        });
    }

    private void updateUserName(String newUserName) {
        usersRef.child("name").setValue(newUserName).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            saveButton.setEnabled(true);

            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e("EditProfileActivity", "Error updating name: " + task.getException());
                Toast.makeText(EditProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
