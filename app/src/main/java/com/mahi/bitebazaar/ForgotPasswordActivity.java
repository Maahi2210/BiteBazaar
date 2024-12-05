package com.mahi.bitebazaar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        firebaseAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        progressBar = findViewById(R.id.progressBar);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });
    }

    private void sendPasswordResetEmail() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
