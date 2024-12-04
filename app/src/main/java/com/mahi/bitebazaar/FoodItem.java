package com.mahi.bitebazaar;

public class FoodItem {
    private String foodId;
    private String foodName;
    private String foodPrice;
    private String foodImageUrl;

    public FoodItem() {
        // Default constructor for Firebase
    }

    public FoodItem(String foodId, String foodName, String foodPrice, String foodImageUrl) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImageUrl = foodImageUrl;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public String getFoodImageUrl() {
        return foodImageUrl;
}
}