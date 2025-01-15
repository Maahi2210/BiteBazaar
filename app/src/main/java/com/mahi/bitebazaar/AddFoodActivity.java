package com.mahi.bitebazaar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodActivity extends AppCompatActivity {

    private EditText etFoodName, etFoodPrice, etFoodQuantity, etFoodDescription, etFoodImageUrl;
    private Button btnAddFood;
    private Spinner spinnerFoodCategory;

    private DatabaseReference foodRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        etFoodName = findViewById(R.id.etFoodName);
        etFoodPrice = findViewById(R.id.etFoodPrice);
        etFoodQuantity = findViewById(R.id.etFoodQuantity);
        etFoodDescription = findViewById(R.id.etFoodDescription);
        etFoodImageUrl = findViewById(R.id.etFoodImageUrl);
        spinnerFoodCategory = findViewById(R.id.spinnerFoodCategory);
        btnAddFood = findViewById(R.id.btnAddFood);

        foodRef = FirebaseDatabase.getInstance().getReference("food_items");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.food_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodCategory.setAdapter(adapter);

        btnAddFood.setOnClickListener(v -> addFoodItem());
    }

    private void addFoodItem() {

        String foodName = etFoodName.getText().toString().trim();
        String foodPrice = etFoodPrice.getText().toString().trim();
        String foodQuantityStr = etFoodQuantity.getText().toString().trim();
        String foodDescription = etFoodDescription.getText().toString().trim();
        String foodImageUrl = etFoodImageUrl.getText().toString().trim();
        String foodCategory = spinnerFoodCategory.getSelectedItem().toString();



        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(foodPrice) || TextUtils.isEmpty(foodQuantityStr)
                || TextUtils.isEmpty(foodDescription) || TextUtils.isEmpty(foodImageUrl) || foodCategory.equals("Select Category")) {
            Toast.makeText(this, "All fields are required, including category!", Toast.LENGTH_SHORT).show();
            return;
        }

        int foodQuantity;
        try {
            foodQuantity = Integer.parseInt(foodQuantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a valid number!", Toast.LENGTH_SHORT).show();
            return;
        }


        String foodId = foodRef.push().getKey();

        if (foodId != null) {
            FoodItem foodItem = new FoodItem(foodId, foodName, foodPrice, foodImageUrl, foodDescription, foodQuantity, foodCategory);


            foodRef.child(foodId).setValue(foodItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddFoodActivity.this, "Food item added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddFoodActivity.this, "Failed to add food item!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
