package com.second.moneymanager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.second.moneymanager.R.id.barchart;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SHORT;

public class BarChartActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    Button btnPreviousBarChart, btnNextBarChart;
    TextView tvTodayForBarChartActivity;

    float valueExpenses = 0;

    ArrayList<Expense> expenses = new ArrayList<>();
    ArrayList<Double> expensesValues = new ArrayList<>();
    double totalValuesFromExpenseValues;

    ArrayList expensesForBarChart = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    BarChart chart;

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        chart = findViewById(barchart);
        btnPreviousBarChart = findViewById(R.id.btnPreviousBarChart);
        btnNextBarChart = findViewById(R.id.btnNextBarChart);
        tvTodayForBarChartActivity = findViewById(R.id.tvTodayForBarChartActivity);

        prefs = getSharedPreferences("com.mycompany.MoneyManager", MainActivity.MODE_PRIVATE);


        final Calendar calendar = Calendar.getInstance();
        calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));
        calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")));
        tvTodayForBarChartActivity.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));



        if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {

            try {
                boolean categoryExisted;
                ExpensesDB db = new ExpensesDB(BarChartActivity.this);
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
                        expensesForBarChart.add(new BarEntry(((float) (totalValuesFromExpenseValues * 100) / valueExpenses), i));
                    }
                }

                db.close();
            } catch (SQLException e) {
                Toast.makeText(BarChartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        BarDataSet bardataset = new BarDataSet(expensesForBarChart, "");
        chart.animateY(3000);
        BarData data = new BarData(categories, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);

        btnPreviousBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) - 1);
                if (calendar.get(MONTH) < 0) {
                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) - 1);
                    calendar.set(MONTH, 11);
                }
                tvTodayForBarChartActivity.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();

                if ((expensesForBarChart.isEmpty() && categories.isEmpty())) {

                } else {

                    expensesForBarChart.clear();
                    categories.clear();
                }


                boolean categoryExisted ;
                try {

                    ExpensesDB db = new ExpensesDB(BarChartActivity.this);
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
                            expensesForBarChart.add(new BarEntry(((float) (totalValuesFromExpenseValues * 100) / valueExpenses), i));
                        }
                    }

                    db.close();
                } catch (SQLException e) {
                    Toast.makeText(BarChartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                BarDataSet bardataset = new BarDataSet(expensesForBarChart, "");
                chart.animateY(3000);
                BarData data1 = new BarData(categories, bardataset);
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                chart.setData(data1);
            }
        });


        btnNextBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) + 1);
                if (calendar.get(MONTH) >= 11) {
                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) + 1);
                    calendar.set(MONTH, 0);
                }
                tvTodayForBarChartActivity.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();

                if ((expensesForBarChart.isEmpty() && categories.isEmpty())) {

                } else {

                    expensesForBarChart.clear();
                    categories.clear();
                }


                try {
                    ExpensesDB db = new ExpensesDB(BarChartActivity.this);
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

                        if (totalValuesFromExpenseValues > 0 && !categoryExisted) {
                            expensesForBarChart.add(new BarEntry(((float) (totalValuesFromExpenseValues * 100) / valueExpenses), i));
                        }
                    }

                    db.close();
                } catch (SQLException e) {
                    Toast.makeText(BarChartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                BarDataSet bardataset = new BarDataSet(expensesForBarChart, "");
                chart.animateY(2000);
                BarData data1 = new BarData(categories, bardataset);
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                chart.setData(data1);
            }
        });


    }
}
