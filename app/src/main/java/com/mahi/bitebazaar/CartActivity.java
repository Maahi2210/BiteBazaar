package com.mahi.bitebazaar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView totalLabelTextView, totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalLabelTextView = findViewById(R.id.totalLabel);
        totalPriceTextView = findViewById(R.id.totalPrice);

        Button cancelButton = findViewById(R.id.cancelButton);
        Button confirmButton = findViewById(R.id.confirmButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, this::loadCartItems);
        cartRecyclerView.setAdapter(cartAdapter);

        loadCartItems();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrder();
            }
        });
    }

    private void loadCartItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("cart_items")
                .child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cartItemList.clear();
                double totalPrice = 0.0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartItemList.add(cartItem);
                        try {
                            double price = Double.parseDouble(cartItem.getFoodPrice());
                            totalPrice += price * cartItem.getQuantity();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(CartActivity.this, "Invalid price format", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                cartAdapter.notifyDataSetChanged();

                if (cartItemList.isEmpty()) {
                    findViewById(R.id.emptyCartMessage).setVisibility(View.VISIBLE);
                    cartRecyclerView.setVisibility(View.GONE);
                    totalPriceTextView.setVisibility(View.GONE);
                    totalLabelTextView.setVisibility(View.GONE);
                } else {
                    findViewById(R.id.emptyCartMessage).setVisibility(View.GONE);
                    cartRecyclerView.setVisibility(View.VISIBLE);
                    totalPriceTextView.setVisibility(View.VISIBLE);
                    totalPriceTextView.setText(String.format("$%.2f", totalPrice));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void confirmOrder() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(userId);

        if (cartItemList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }


        double totalPrice = 0.0;
        for (CartItem item : cartItemList) {
            try {
                totalPrice += Double.parseDouble(item.getFoodPrice()) * item.getQuantity();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        String orderId = orderRef.push().getKey();


        Order order = new Order(orderId, userId, "Confirmed", cartItemList, totalPrice, System.currentTimeMillis(), "No Schedule");


        assert orderId != null;

        String finalTotalPrice = String.valueOf(totalPrice);
        orderRef.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CartActivity.this, "Order Confirmed!", Toast.LENGTH_SHORT).show();
                    clearCart(userId);
                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("totalPrice", finalTotalPrice);

                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(CartActivity.this, "Failed to confirm order", Toast.LENGTH_SHORT).show());
    }


    private void clearCart(String userId) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("cart_items")
                .child(userId);

        cartRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(CartActivity.this, "Cart Cleared", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(CartActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show());
    }
}

