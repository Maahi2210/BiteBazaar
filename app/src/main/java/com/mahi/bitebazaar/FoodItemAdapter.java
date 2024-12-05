package com.mahi.bitebazaar;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {
    private final List<FoodItem> foodItems;

    public FoodItemAdapter(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }



    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.getFoodName());
        holder.foodPrice.setText("$" + foodItem.getFoodPrice());

        Picasso.get()
                .load(foodItem.getFoodImageUrl())
//                .placeholder(R.drawable.placeholder_image) // Add a placeholder image in your drawable folder
//                .error(R.drawable.error_image)             // Add an error image for failed loads
                .into(holder.foodImage);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), FoodDetailActivity.class);
            intent.putExtra("foodId", foodItem.getFoodId());
            intent.putExtra("foodName", foodItem.getFoodName());
            intent.putExtra("foodPrice", foodItem.getFoodPrice());
            intent.putExtra("foodImageUrl", foodItem.getFoodImageUrl());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodPrice;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
        }
    }
}

