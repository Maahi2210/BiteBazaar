package com.mahi.bitebazaar;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
public class FoodItemAdminAdapter extends RecyclerView.Adapter<FoodItemAdminAdapter.FoodItemViewHolder> {
    private final List<FoodItem> foodItems;

    public FoodItemAdminAdapter(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_admin, parent, false);
        return new FoodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.getFoodName());
        holder.foodPrice.setText("$" + foodItem.getFoodPrice());
        if (foodItem.getFoodQuantity() == 0) {
            holder.foodQuantity.setText("Out of Stock");
            holder.foodQuantity.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.foodQuantity.setText("Quantity: " + foodItem.getFoodQuantity());
            holder.foodQuantity.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        }

        Picasso.get()
                .load(foodItem.getFoodImageUrl())
                .into(holder.foodImage);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), FoodDetailActivity.class);
            intent.putExtra("foodId", foodItem.getFoodId());
            intent.putExtra("foodName", foodItem.getFoodName());
            intent.putExtra("foodPrice", foodItem.getFoodPrice());
            intent.putExtra("foodDescription", foodItem.getFoodDescription());
            intent.putExtra("foodImageUrl", foodItem.getFoodImageUrl());
            holder.itemView.getContext().startActivity(intent);
        });


        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditFoodActivity.class);
            intent.putExtra("foodId", foodItem.getFoodId());
            intent.putExtra("foodName", foodItem.getFoodName());
            intent.putExtra("foodPrice", foodItem.getFoodPrice());
            intent.putExtra("foodDescription", foodItem.getFoodDescription());
            intent.putExtra("foodImageUrl", foodItem.getFoodImageUrl());
            intent.putExtra("foodQuantity", String.valueOf(foodItem.getFoodQuantity()));
            holder.itemView.getContext().startActivity(intent);
        });



        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Food Item")
                    .setMessage("Are you sure you want to delete this food item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods_items");
                        foodRef.child(foodItem.getFoodId()).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                foodItems.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, foodItems.size());
                                Toast.makeText(holder.itemView.getContext(), "Food item deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage, editButton, deleteButton;
        TextView foodName, foodPrice, foodQuantity;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}


