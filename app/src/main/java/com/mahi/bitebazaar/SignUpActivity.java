package com.mahi.bitebazaar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText, phoneEditText;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();


        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return;
        }
        String PhoneRegex = "^[2-9]\\d{2}[2-9]\\d{6}$";
        if (!phone.matches(PhoneRegex)) {
            phoneEditText.setError("Enter a valid  phone number (e.g., 1234567890)");
            phoneEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String userId = auth.getCurrentUser().getUid();
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("userId", userId);
                        userMap.put("email", email);
                        userMap.put("phone", phone);
                        userMap.put("name", name);


                        databaseReference.child(userId).setValue(userMap)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    }

                                });
                    } else {

                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration Failed";
                        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
           });
}
}