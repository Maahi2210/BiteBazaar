package com.mahi.bitebazaar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeActivity extends AppCompatActivity {

    private TextView usersCountTextView, ordersCountTextView, productsCountTextView;
    private DatabaseReference databaseReference;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        usersCountTextView = findViewById(R.id.usersCountTextView);
        ordersCountTextView = findViewById(R.id.ordersCountTextView);
        productsCountTextView = findViewById(R.id.productsCountTextView);
        CardView productsCard = findViewById(R.id.products);
        CardView ordersCard = findViewById(R.id.orders);
        CardView usersCard = findViewById(R.id.users);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fetchUserCount();
        fetchOrderCount();
        fetchProductCount();
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, ProfileActivity.class));
            }
        });

        ordersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, OrderActivity.class));
            }
        });
    }

    private void fetchProductCount() {
        DatabaseReference ordersRef = databaseReference.child("food_items");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long orderCount = snapshot.getChildrenCount();
                productsCountTextView.setText("Products:" + String.valueOf(orderCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to fetch products count: " + error.getMessage());
            }
        });
    }

    private void fetchUserCount() {
        DatabaseReference usersRef = databaseReference.child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long userCount = snapshot.getChildrenCount();
                usersCountTextView.setText("Users: "+ String.valueOf(userCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to fetch user count: " + error.getMessage());
            }
        });
    }

    private void fetchOrderCount() {
        DatabaseReference ordersRef = databaseReference.child("orders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long orderCount = snapshot.getChildrenCount();
                ordersCountTextView.setText("Orders:" + String.valueOf(orderCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to fetch order count: " + error.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(AdminHomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
