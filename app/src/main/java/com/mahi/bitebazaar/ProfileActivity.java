package com.mahi.bitebazaar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameTextView, userEmailTextView, userPhoneTextView;
    private ImageView userProfileImageView;
    private Button editProfileButton, signOutButton;

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);


        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userPhoneTextView = findViewById(R.id.userPhoneTextView);
        userProfileImageView = findViewById(R.id.userProfileImageView);
        editProfileButton = findViewById(R.id.editProfileButton);
        signOutButton = findViewById(R.id.signOutButton);

        loadUserProfile();
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Toast.makeText(ProfileActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadUserProfile() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);

                    userNameTextView.setText(name != null ? name : "N/A");
                    userEmailTextView.setText(email != null ? email : "N/A");
                    userPhoneTextView.setText(phone != null ? phone : "N/A");
                } else {
                    Toast.makeText(ProfileActivity.this, "User details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
