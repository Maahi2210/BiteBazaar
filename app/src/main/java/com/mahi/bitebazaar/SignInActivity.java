package com.mahi.bitebazaar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignInActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button signInButton = findViewById(R.id.signInButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        signInButton.setOnClickListener(v -> signIn());
        signUpButton.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        forgotPasswordButton.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class)));
    }

    private void signIn() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }
        if (password.length() < 5) {
            passwordEditText.setError("Password must be at least 5 characters");
            passwordEditText.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(email.equals("maahishah2210@gmail.com")){
                            startActivity(new Intent(SignInActivity.this, AdminHomeActivity.class));
                            finish();
                        }else {
                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            finish();
                        }

                    } else {
                        String errorMessage;
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            errorMessage = "No account found with this email.";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            errorMessage = "Invalid password or email.";
                        } catch (FirebaseAuthUserCollisionException e) {
                            errorMessage = "An account already exists with this email.";
                        } catch (Exception e) {
                            errorMessage = "Authentication failed: " + e.getMessage();
                        }
                        Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
