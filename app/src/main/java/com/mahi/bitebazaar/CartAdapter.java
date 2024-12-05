package com.mahi.bitebazaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private DatabaseReference cartReference;
    private OnQuantityChangeListener quantityChangeListener;

    public CartAdapter(List<CartItem> cartItems, OnQuantityChangeListener quantityChangeListener) {
        this.cartItems = cartItems;
        this.quantityChangeListener = quantityChangeListener;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartReference = FirebaseDatabase.getInstance().getReference("cart_items").child(userId);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        Picasso.get().load(item.getFoodImageUrl()).into(holder.foodImage);
        holder.foodName.setText(item.getFoodName());
        holder.foodPrice.setText("$" + item.getFoodPrice());
        holder.foodQuantity.setText(String.valueOf(item.getQuantity()));

        holder.addButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            updateQuantity(item, newQuantity);
        });

        holder.removeButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity <= 0) {
                removeItem(item);
            } else {
                updateQuantity(item, newQuantity);
            }
        });
    }

    private void updateQuantity(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        cartReference.child(item.getFoodId()).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    notifyDataSetChanged();
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged();
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    private void removeItem(CartItem item) {
        cartReference.child(item.getFoodId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    cartItems.remove(item);
                    notifyDataSetChanged();
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged();
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodPrice, foodQuantity;
        ImageButton addButton, removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            addButton = itemView.findViewById(R.id.addButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }
}
