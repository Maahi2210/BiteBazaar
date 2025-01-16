package com.mahi.bitebazaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public void updateOrders(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }
    static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderIdTextView, orderStatusTextView, totalPriceTextView, timestampTextView, scheduleTextView;
        private RecyclerView itemsRecyclerView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            scheduleTextView = itemView.findViewById(R.id.scheduleTextView);
            itemsRecyclerView = itemView.findViewById(R.id.itemsRecyclerView);
            itemsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

        public void bind(Order order) {
            orderIdTextView.setText("Order ID: " + order.getOrderId());
            orderStatusTextView.setText("Status: " + order.getStatus());
            totalPriceTextView.setText("Total: $" + order.getTotalPrice());


            String formattedDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
                    .format(new Date(order.getTimestamp()));
            timestampTextView.setText("Order Date: " + formattedDate);
            scheduleTextView.setText("Schedule: " + (order.getSchedule() != null ? order.getSchedule() : "No schedule"));

        }
    }
}



