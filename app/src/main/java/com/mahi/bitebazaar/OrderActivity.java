package com.mahi.bitebazaar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private DatabaseReference ordersRef;
    private String currentUserId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Enable the back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show the back arrow
            getSupportActionBar().setDisplayShowHomeEnabled(true); // Enable home button
            getSupportActionBar().setTitle("My Orders"); // Set the title
        }

        // Initialize Views
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        orderAdapter = new OrderAdapter(new ArrayList<>());
        ordersRecyclerView.setAdapter(orderAdapter);

        // Get current user's ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "orders" node in Firebase
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Load orders for the current user
        loadUserOrders();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back arrow click
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserOrders() {
        ordersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Order> orderList = new ArrayList<>();

                // Iterate through the user's orders
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    orderList.add(order);
                }

                // Update the RecyclerView with the fetched orders
                orderAdapter.updateOrders(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderActivity.this, "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
