package com.mahi.bitebazaar;

public class FoodItem {
    private String foodId;
    private String foodName;
    private String foodPrice;
    private String foodImageUrl;
    private String foodDescription;
    private int foodQuantity;
    private String foodCategory;

    public FoodItem() {
    }


    public FoodItem(String foodId, String foodName, String foodPrice, String foodImageUrl, String foodDescription, int foodQuantity) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImageUrl = foodImageUrl;
        this.foodDescription = foodDescription;
        this.foodQuantity = foodQuantity;
    }

    public FoodItem(String foodId, String foodName, String foodPrice, String foodImageUrl, String foodDescription, int foodQuantity, String foodCategory) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImageUrl = foodImageUrl;
        this.foodDescription = foodDescription;
        this.foodQuantity = foodQuantity;
        this.foodCategory = foodCategory;
    }


    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodImageUrl() {
        return foodImageUrl;
    }

    public void setFoodImageUrl(String foodImageUrl) {
        this.foodImageUrl = foodImageUrl;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(int foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }
}
