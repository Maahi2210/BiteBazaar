package com.mahi.bitebazaar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class EditFoodActivity extends AppCompatActivity {

    private ImageView foodImagePreview;
    private EditText editFoodName, editFoodPrice, editFoodDescription, editFoodImageUrl, editFoodQuantity;
    private Button btnSaveChanges;

    private String foodId;
    private String foodImageUrl;

    private DatabaseReference foodRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        foodImagePreview = findViewById(R.id.foodImagePreview);
        editFoodName = findViewById(R.id.editFoodName);
        editFoodPrice = findViewById(R.id.editFoodPrice);
        editFoodDescription = findViewById(R.id.editFoodDescription);
        editFoodImageUrl = findViewById(R.id.editFoodImageUrl);
        editFoodQuantity = findViewById(R.id.editFoodQuantity);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        foodId = getIntent().getStringExtra("foodId");
        String foodName = getIntent().getStringExtra("foodName");
        String foodPrice = getIntent().getStringExtra("foodPrice");
        String foodDescription = getIntent().getStringExtra("foodDescription");
        String foodQuantity = getIntent().getStringExtra("foodQuantity");
        foodImageUrl = getIntent().getStringExtra("foodImageUrl");

        Picasso.get().load(foodImageUrl).into(foodImagePreview);
        editFoodName.setText(foodName);
        editFoodPrice.setText(foodPrice);
        editFoodDescription.setText(foodDescription);
        editFoodImageUrl.setText(foodImageUrl);
        editFoodQuantity.setText(foodQuantity);

        foodRef = FirebaseDatabase.getInstance().getReference("food_items").child(foodId);


        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String updatedName = editFoodName.getText().toString().trim();
        String updatedPrice = editFoodPrice.getText().toString().trim();
        String updatedDescription = editFoodDescription.getText().toString().trim();
        String updatedImage = editFoodImageUrl.getText().toString().trim();
        String updatedQuantity = editFoodQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedPrice) ||
                TextUtils.isEmpty(updatedDescription) || TextUtils.isEmpty(updatedQuantity)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating food item...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        foodRef.child("foodName").setValue(updatedName);
        foodRef.child("foodPrice").setValue(updatedPrice);
        foodRef.child("foodDescription").setValue(updatedDescription);
        foodRef.child("foodImageUrl").setValue(updatedImage);
        foodRef.child("foodQuantity").setValue(Integer.parseInt(updatedQuantity))
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(EditFoodActivity.this, "Food item updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditFoodActivity.this, "Failed to update food item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
