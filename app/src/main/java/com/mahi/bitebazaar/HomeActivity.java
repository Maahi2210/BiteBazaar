package com.mahi.bitebazaar;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<FoodItem> foodItemList;
    private FoodItemAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Two items per row
        foodItemList = new ArrayList<>();
        adapter = new FoodItemAdapter(foodItemList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("food_items");

        loadFoodItems();

//        loading food items
    }

    private void loadFoodItems() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                foodItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
                    foodItemList.add(foodItem);
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
}