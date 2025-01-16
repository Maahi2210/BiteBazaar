package com.mahi.bitebazaar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button btn_back, btn_pay, btn_schedule;
    private String orderId;
    private TextView tvTotalPrice, tvDescription, selectedDateTime;
    public String totalPrice;
    public String StripeTotalPrice ;


    String PublishableKey = "pk_test_51QhbeV05xk3YjsYEpnnzNgiUPtz6ogl3ABraQcAh4AQSVugxWDuVlMqkc7llvoI9f8xBMEnL9o22eVx5bVQ5nxOv00LGQ1l3jQ";
    String SecretKey = "sk_test_51QhbeV05xk3YjsYEZA2eRnKYQJATOh8TIBUyBuHvZ81ZVXSKju5d1AYgHyiXHkCFbhFtDGrtA3R9KQ3JNyYGHBCE00gbxNiDvG";
    String CustomerId;
    String ClientSecret;
    String EphemeralKey;
    PaymentSheet paymentSheet;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        orderId = getIntent().getStringExtra("orderId");
        totalPrice = getIntent().getStringExtra("totalPrice");
        String[] parts = totalPrice.split("\\.");
        StripeTotalPrice = parts[0] +  parts[1];



        btn_back = findViewById(R.id.backButton);
        btn_pay = findViewById(R.id.payButton);
        btn_schedule = findViewById(R.id.scheduleButton);
        tvTotalPrice = findViewById(R.id.totalPrice);
        tvDescription = findViewById(R.id.description);
        tvTotalPrice.setText("$"+ totalPrice);
        selectedDateTime = findViewById(R.id.selectedDateTime);


        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentFlow();
            }
        });

        btn_schedule.setOnClickListener(v -> showDateTimePicker());


        PaymentConfiguration.init(this, PublishableKey);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymetResult(paymentSheetResult);
        });
        getCustomerId();

        btn_back.setOnClickListener(view ->  {
            Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });



    }



    private void getCustomerId() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            CustomerId = object.getString("id");
                            getEphemeralKeys();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ SecretKey);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("BiteBasar", new PaymentSheet.CustomerConfiguration(
                CustomerId,
                EphemeralKey
        )));
    }

    private void onPaymetResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){

            btn_pay.setText("Payment Succeeded");
            btn_pay.setEnabled(false);
            btn_schedule.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.GONE);
            Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(userId).child(orderId);


            orderRef.child("status").setValue("Paid")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Order status updated to Paid", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show();
                    });

        }
    }



    private void getEphemeralKeys() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphemeralKey = object.getString("id");
                            // Toast.makeText(SettingsActivity.this, "EphemeralKey:" + EphemeralKey, Toast.LENGTH_SHORT).show();
                            getClientSecret(CustomerId,EphemeralKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ SecretKey);
                header.put("Stripe-Version" , "2024-06-20");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getClientSecret(String customerId, String ephemeralKey) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            // Toast.makeText(SettingsActivity.this, "ClientSecret:" + ClientSecret, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ SecretKey);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);
                params.put("amount",StripeTotalPrice);
                params.put("currency","cad");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }



    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        long currentDateInMillis = calendar.getTimeInMillis();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PaymentActivity.this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    showTimePicker(selectedYear, selectedMonth, selectedDayOfMonth);
                },
                year, month, day);
        datePickerDialog.getDatePicker().setMinDate(currentDateInMillis);

        datePickerDialog.show();
    }

    private void showTimePicker(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                PaymentActivity.this,
                (view, selectedHour, selectedMinute) -> {
                    String selectedDateTimeString = formatDateTime(year, month, dayOfMonth, selectedHour, selectedMinute);
                    selectedDateTime.setVisibility(View.VISIBLE);
                    selectedDateTime.setText("Schedule Date & Time: " + selectedDateTimeString);
                    btn_back.setVisibility(View.VISIBLE);
                    Toast.makeText(PaymentActivity.this, "Date & Time Selected: " + selectedDateTimeString, Toast.LENGTH_SHORT).show();

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(userId).child(orderId);


                    orderRef.child("schedule").setValue(selectedDateTimeString)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Schedule updated", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to Schedule update", Toast.LENGTH_SHORT).show();
                            });
                },
                hour, minute, true);

        timePickerDialog.show();
    }

    private String formatDateTime(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(calendar.getTime());
    }

}
