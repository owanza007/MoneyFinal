package com.second.moneymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.SHORT;

public class PieChartActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    Button btnPreviousPieChart, btnNextPieChart;
    Calendar calendar = Calendar.getInstance();
    ArrayList<Entry> expensesForChart = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    PieChart pieChart;
    float valueExpenses;
    ArrayList<Expense> expenses = new ArrayList<>();
    ArrayList<Double> expensesValues = new ArrayList<>();
    double totalValuesFromExpenseValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        pieChart = findViewById(R.id.piechart);
        valueExpenses = 0;
        totalValuesFromExpenseValues = 0;

        btnNextPieChart = findViewById(R.id.btnNextPieChart);
        btnPreviousPieChart = findViewById(R.id.btnPreviousPieChart);

        prefs = getSharedPreferences("com.mycompany.MoneyManager", MainActivity.MODE_PRIVATE);
        calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));
        calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")));

        pieChart.setCenterText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));
        pieChart.setCenterTextSizePixels(40);


        if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {
            if ((expensesForChart.isEmpty() && categories.isEmpty())) {

            } else {

                expensesForChart.clear();
                categories.clear();
            }

            pieChart.setCenterText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));
            boolean categoryExisted = false;
            try {
                ExpensesDB db = new ExpensesDB(PieChartActivity.this);
                db.open();
                expenses = db.getExpensesByMonthAndYear(calendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), String.valueOf(calendar.get(Calendar.YEAR)));

                for (int i = 0; i < expenses.size(); i++) {
                    valueExpenses = (float) (valueExpenses + expenses.get(i).getPrice());
                }

                for (int i = 0; i < expenses.size(); i++) {

                    totalValuesFromExpenseValues = 0;

                    expensesValues = db.getExpensesValuesByCategory(expenses.get(i).getCategory());

                    for (int j = 0; j < expensesValues.size(); j++) {
                        totalValuesFromExpenseValues = totalValuesFromExpenseValues + expensesValues.get(j);
                    }

                    if (categories.contains(expenses.get(i).getCategory())) {
                        categoryExisted = true;
                        categories.set(categories.indexOf(expenses.get(i).getCategory()), expenses.get(i).getCategory());
                    } else {
                        categoryExisted = false;
                        categories.add(expenses.get(i).getCategory());

                    }

                    if (totalValuesFromExpenseValues > 0 && !categoryExisted) {
                        expensesForChart.add(new Entry(((float) (totalValuesFromExpenseValues * 100) / valueExpenses), i));
                    }
                }
                db.close();
            } catch (SQLException e) {
                Toast.makeText(PieChartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        PieDataSet dataSet = new PieDataSet(expensesForChart, "");
        PieData data = new PieData(categories, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueFormatter(new PercentFormatter());
        pieChart.animateXY(3000, 3000);


        btnPreviousPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) - 1);
                if (calendar.get(MONTH) < 0) {
                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) - 1);
                    calendar.set(MONTH, 11);
                }

                prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();

                if ((expensesForChart.isEmpty() && categories.isEmpty())) {

                } else {

                    expensesForChart.clear();
                    categories.clear();
                }

                pieChart.setCenterText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));
                boolean categoryExisted = false;
                try {
                    ExpensesDB db = new ExpensesDB(PieChartActivity.this);
                    db.open();
                    expenses = db.getExpensesByMonthAndYear(calendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), String.valueOf(calendar.get(Calendar.YEAR)));

                    for (int i = 0; i < expenses.size(); i++) {
                        valueExpenses = (float) (valueExpenses + expenses.get(i).getPrice());
                    }

                    for (int i = 0; i < expenses.size(); i++) {

                        totalValuesFromExpenseValues = 0;

                        expensesValues = db.getExpensesValuesByCategory(expenses.get(i).getCategory());

                        for (int j = 0; j < expensesValues.size(); j++) {
                            totalValuesFromExpenseValues = totalValuesFromExpenseValues + expensesValues.get(j);
                        }

                        if (categories.contains(expenses.get(i).getCategory())) {
                            categoryExisted = true;
                            categories.set(categories.indexOf(expenses.get(i).getCategory()), expenses.get(i).getCategory());
                        } else {
                            categoryExisted = false;
                            categories.add(expenses.get(i).getCategory());

                        }

                        if (totalValuesFromExpenseValues > 0 && !categoryExisted) {
                            expensesForChart.add(new Entry(((float) (totalValuesFromExpenseValues * 100) / valueExpenses), i));
                        }
                    }
                    db.close();
                } catch (SQLException e) {
                    Toast.makeText(PieChartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                PieDataSet dataSet = new PieDataSet(expensesForChart, "");
                PieData data = new PieData(categories, dataSet);
                pieChart.setData(data);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieChart.animateXY(3000, 3000);
            }
        });


        btnNextPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) + 1);
                if (calendar.get(MONTH) >= 11) {
                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) + 1);
                    calendar.set(MONTH, 0);
                }

                prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();

                if ((expensesForChart.isEmpty() && categories.isEmpty())) {

                } else {

                    expensesForChart.clear();
                    categories.clear();
                }

                pieChart.setCenterText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                try {
                    ExpensesDB db = new ExpensesDB(PieChartActivity.this);
                    db.open();
                    expenses = db.getExpensesByMonthAndYear(calendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), String.valueOf(calendar.get(Calendar.YEAR)));
                    boolean categoryExisted = false;
                    for (int i = 0; i < expenses.size(); i++) {
                        valueExpenses = (float) (valueExpenses + expenses.get(i).getPrice());
                    }

                    for (int i = 0; i < expenses.size(); i++) {

                        totalValuesFromExpenseValues = 0;

                        expensesValues = db.getExpensesValuesByCategory(expenses.get(i).getCategory());

                        for (int j = 0; j < expensesValues.size(); j++) {
                            totalValuesFromExpenseValues = totalValuesFromExpenseValues + expensesValues.get(j);
                        }

                        if (categories.contains(expenses.get(i).getCategory())) {
                            categoryExisted = true;
                            categories.set(categories.indexOf(expenses.get(i).getCategory()), expenses.get(i).getCategory());
                        } else {
                            categoryExisted = false;
                            categories.add(expenses.get(i).getCategory());
                        }

                        if (totalValuesFromExpenseValues > 0 && categoryExisted == false) {
                            expensesForChart.add(new Entry(((float) (totalValuesFromExpenseValues * 100) / valueExpenses), i));
                        }
                    }
                    db.close();
                } catch (SQLException e) {
                    Toast.makeText(PieChartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                PieDataSet dataSet = new PieDataSet(expensesForChart, "");
                PieData data = new PieData(categories, dataSet);
                pieChart.setData(data);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieChart.animateXY(3000, 3000);
            }
        });
    }
}