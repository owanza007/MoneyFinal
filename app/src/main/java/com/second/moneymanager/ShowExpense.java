package com.second.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.time.LocalDate;

public class ShowExpense extends AppCompatActivity {

    TextView tvShowExpenseProduct, tvShowExpensePrice, tvShowExpenseCantity, tvShowExpenseDate, tvShowExpenseTotalSpent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expense);

        tvShowExpenseProduct = findViewById(R.id.tvShowExpenseProduct);
        tvShowExpensePrice = findViewById(R.id.tvShowExpensePrice);
        tvShowExpenseDate = findViewById(R.id.tvShowExpenseDate);
        tvShowExpenseTotalSpent = findViewById(R.id.tvShowExpenseTotalSpent);


        final String category = getIntent().getStringExtra("category");
        final String notes = getIntent().getStringExtra("notes");
        final double amountSpent = getIntent().getDoubleExtra("amountSpent", 0);
        final int day = getIntent().getIntExtra("day", 0);
        final String month = getIntent().getStringExtra("month");
        final int year = getIntent().getIntExtra("year", 0);

        String date = day + "-" + month + "-" + year;

        tvShowExpenseProduct.setText(String.format("" + category));
        tvShowExpensePrice.setText(notes);
        tvShowExpenseDate.setText(String.format("" + date));
        tvShowExpenseTotalSpent.setText("" + amountSpent);
    }
}
