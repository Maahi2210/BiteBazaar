package com.mahi.bitebazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView, categoryRecyclerView;
    private ProgressBar progressBar;
    private List<FoodItem> foodItemList;
    private List<Category> categoryList;
    private FoodItemAdapter adapter;
    private CategoryAdapter categoryAdapter;
    private DatabaseReference databaseReference;
    private TextView cartItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        cartItemCount = findViewById(R.id.cartItemCount);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        foodItemList = new ArrayList<>();
        adapter = new FoodItemAdapter(foodItemList);
        recyclerView.setAdapter(adapter);

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, this::onCategorySelected);
        categoryRecyclerView.setAdapter(categoryAdapter);



        ImageView cartIcon = findViewById(R.id.cartIcon);
        ImageView ordersIcon = findViewById(R.id.ordersIcon);
        ImageView profileIcon = findViewById(R.id.profileIcon);
        databaseReference = FirebaseDatabase.getInstance().getReference("food_items");
        loadCategories();
        loadFoodItems(null);
        updateCartItemCount();

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            }
        });

        ordersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, OrderActivity.class));
            }
        });
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.add(new Category("All", true)); // Default category
        categoryList.add(new Category("Pizza", false));
        categoryList.add(new Category("Burgers", false));
        categoryList.add(new Category("Drinks", false));
        categoryList.add(new Category("Desserts", false));

        categoryAdapter.notifyDataSetChanged();
    }

    private void loadFoodItems(String category) {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                foodItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
                    if (category == null || category.equals("All") || foodItem.getFoodCategory().equals(category)) {
                        foodItemList.add(foodItem);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onCategorySelected(String category) {
        loadFoodItems(category);
    }

    private void updateCartItemCount() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart_items").child(userId);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int itemCount = (int) snapshot.getChildrenCount();

                if (itemCount > 0) {
                    cartItemCount.setVisibility(View.VISIBLE);
                    cartItemCount.setText(String.valueOf(itemCount));
                } else {
                    cartItemCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to update cart count", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

