package com.second.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.Calendar;

public class ShowIncome extends AppCompatActivity {

    TextView tvShowIncome, tvShowType, tvShowDate,tvShowExpensePrice;
    ImageView ivAmountIncome;
    Calendar myCalendar = Calendar.getInstance();

    private int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_income);

        day = myCalendar.get(Calendar.DAY_OF_MONTH);
        month = myCalendar.get(Calendar.MONTH);
        year = myCalendar.get(Calendar.YEAR);

        tvShowIncome = findViewById(R.id.tvShowIncome);
        tvShowType = findViewById(R.id.tvShowType);
        tvShowDate = findViewById(R.id.tvShowDate);
        tvShowExpensePrice = findViewById(R.id.tvShowExpensePrice);

        String notes = getIntent().getStringExtra("notes");
        String category = getIntent().getStringExtra("category");
        double sum = getIntent().getDoubleExtra("sum", 0);
        int day = getIntent().getIntExtra("day", 0);
        String month = getIntent().getStringExtra("month");
        int year = getIntent().getIntExtra("year", 0);

        String date = day + "-" + month + "-" + year;

        tvShowIncome.setText(String.format("" + sum));
        tvShowDate.setText(String.format("" + date));
        tvShowExpensePrice.setText(String.format("" + notes));
        tvShowType.setText(String.format("" + category));

//        ivAmountIncome.setBackgroundResource(R.mipmap.ic_amount);

    }
}
