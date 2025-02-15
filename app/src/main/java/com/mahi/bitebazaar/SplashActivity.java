package com.mahi.bitebazaar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                if(Objects.equals(currentUser.getEmail(), "maahishah2210@gmail.com")){
                    startActivity(new Intent(SplashActivity.this, AdminHomeActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }

            } else {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            }
            finish();
        }, 2000);
    }

}