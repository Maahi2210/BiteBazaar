package com.mahi.bitebazaar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity {
    private ImageView foodImage;
    private TextView foodName, foodPrice, foodDescription;
    private Button addToCartButton;

    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        foodImage = findViewById(R.id.foodImage);
        foodName = findViewById(R.id.foodName);
        foodPrice = findViewById(R.id.foodPrice);
        foodDescription = findViewById(R.id.foodDescription);
        addToCartButton = findViewById(R.id.addToCartButton);


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("cart_items").child(userId);


        String foodId = getIntent().getStringExtra("foodId");
        String name = getIntent().getStringExtra("foodName");
        String price = getIntent().getStringExtra("foodPrice");
        String description = getIntent().getStringExtra("foodDescription");
        String imageUrl = getIntent().getStringExtra("foodImageUrl");


        foodName.setText(name);
        foodPrice.setText("$" + price);
        foodDescription.setText(description);
        Picasso.get().load(imageUrl).into(foodImage);
        addToCartButton.setOnClickListener(v -> addToCart(foodId, name, price, imageUrl));
    }

    private void addToCart(String foodId, String name, String price, String imageUrl) {
        databaseReference.child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int currentQuantity = snapshot.child("quantity").getValue(Integer.class);
                    databaseReference.child(foodId).child("quantity").setValue(currentQuantity + 1);
                    Toast.makeText(FoodDetailActivity.this, name + " quantity updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> cartItem = new HashMap<>();
                    cartItem.put("foodId", foodId);
                    cartItem.put("foodName", name);
                    cartItem.put("foodPrice", price);
                    cartItem.put("foodImageUrl", imageUrl);
                    cartItem.put("quantity", 1);

                    databaseReference.child(foodId).setValue(cartItem)
                            .addOnSuccessListener(aVoid -> Toast.makeText(FoodDetailActivity.this, name + " added to cart!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(FoodDetailActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FoodDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
