package com.second.moneymanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static java.util.Calendar.*;

public class moneyExpanded extends AppCompatActivity {

    Button nextDay, btnPrevious;
    ListView lvItems;
    TextView tvToday;
    LinearLayout layoutToSwipe;

    ArrayList items = new ArrayList();
    ArrayList<Income> incomes = new ArrayList<>();
    ArrayList<Expense> expenses = new ArrayList<>();
    Spinner spinnerCurrency2, spinnerMonthly2;
    private DatabaseReference mDatabase;
    Calendar calendar = Calendar.getInstance();


    MenuItem menuItemCurrency, menuitemYearly, chartsButton,privacyPolicy;


    private static final String[] valuesToShowAccount = {"Yearly", "Monthly", "Daily"};


    SharedPreferences prefs = null;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuItemCurrency = menu.findItem(R.id.changeCurrency);
        menuItemCurrency.setVisible(false);
        menuitemYearly = menu.findItem(R.id.monthlyYearly);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final AlertDialog.Builder b = new AlertDialog.Builder(this);
        final AlertDialog.Builder b1 = new AlertDialog.Builder(this);

        b1.setTitle("Sort");


        b1.setItems(valuesToShowAccount, new DialogInterface.OnClickListener() {

            Intent refresh = getIntent();

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (spinnerMonthly2.getAdapter().getItem(which).toString().trim().equals("Yearly")) {

                    prefs.edit().putString("monthlyOrYearly", "Yearly").apply();
                    calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")));
                    tvToday.setText(String.valueOf(calendar.get(Calendar.YEAR)));


                    startActivity(refresh);
                    overridePendingTransition(0, 0);
                    moneyExpanded.this.finish();
                    overridePendingTransition(0, 0);


                } else if (spinnerMonthly2.getAdapter().getItem(which).toString().trim().equals("Monthly")) {

                    prefs.edit().putString("monthlyOrYearly", "Monthly").apply();
                    calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));
                    calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")));
                    tvToday.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));


                    startActivity(refresh);
                    overridePendingTransition(0, 0);
                    moneyExpanded.this.finish();
                    overridePendingTransition(0, 0);


                } else if (spinnerMonthly2.getAdapter().getItem(which).toString().trim().equals("Daily")) {

                    prefs.edit().putString("monthlyOrYearly", "Daily").apply();


                    calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));
                    calendar.set(DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));

                    tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                            calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault())
                            + " - " + calendar.get(Calendar.YEAR));


                    startActivity(refresh);
                    overridePendingTransition(0, 0);
                    moneyExpanded.this.finish();
                    overridePendingTransition(0, 0);


                }
                dialog.dismiss();
            }
        });

        switch (item.getItemId()) {

            case R.id.monthlyYearly:
                b1.show();
                break;

            case R.id.changeCurrency:
                b.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_expanded);

        final Calendar calendar = Calendar.getInstance();


        btnPrevious = findViewById(R.id.btnPrevious);
        layoutToSwipe = findViewById(R.id.layoutToSwipe);
        nextDay = findViewById(R.id.nextDay);
        lvItems = findViewById(R.id.lvItems);
        tvToday = findViewById(R.id.tvToday);

        spinnerCurrency2 = findViewById(R.id.spinnerCurrency2);
        spinnerMonthly2 = findViewById(R.id.spinnerMonthly2);

        spinnerCurrency2.setVisibility(View.GONE);
        spinnerMonthly2.setVisibility(View.GONE);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        prefs = getSharedPreferences("com.mycompany.MoneyManager", moneyExpanded.MODE_PRIVATE);


        final ArrayAdapter<String> adapterMonthly = new ArrayAdapter<>(moneyExpanded.this,
                android.R.layout.simple_spinner_item, valuesToShowAccount);

        spinnerMonthly2.setAdapter(adapterMonthly);

        calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")));
        calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));
        calendar.set(DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));

        final MyApplication app = (MyApplication) moneyExpanded.this.getApplication();
        final ItemsAdapter[] adapter = new ItemsAdapter[1];

        if (prefs.getString("monthlyOrYearly", "").equals("Yearly")) {
            tvToday.setText(prefs.getString("year", ""));
            calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")));
//            items.clear();
                mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Income> incomeArray = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                            String monthIncome = ds.child("monthIncome").getValue(String.class);
                            Double sum = ds.child("sum").getValue(Double.class);
                            int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                            String id = ds.getKey();
                            String category = ds.child("category").getValue(String.class);
                            String notes = ds.child("notes").getValue(String.class);
                            Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                            incomeArray.add(income);
                        }
                        incomes = incomeArray;
                        mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Expense> expenseArray = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Double price = ds.child("price").getValue(Double.class);
                                    int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                    String monthExpense = ds.child("monthExpense").getValue(String.class);
                                    int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                    String id = ds.getKey();
                                    String category = ds.child("category").getValue(String.class);
                                    String notes = ds.child("notes").getValue(String.class);

                                    Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                    expenseArray.add(expense);
                                }
                                expenses = expenseArray;
                                Log.d("CHKLoop","Income => "+incomes.size()+" , Expense => "+expenses.size());
                                for (int i = 0; i < incomes.size(); i++) {
                                    items.add(incomes.get(i));
                                }
                                for (int i = 0; i < expenses.size(); i++) {
                                    items.add(expenses.get(i));
                                }
                                app.setItems(items);
                                adapter[0] = new ItemsAdapter(moneyExpanded.this, items);
                                lvItems.setAdapter(adapter[0]);

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });



        } else if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {
            calendar.set(MONTH, Integer.valueOf(prefs.getString("month", "")));
            calendar.set(YEAR, Integer.valueOf(prefs.getString("year", "")));

            tvToday.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

            try {
                final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Income> incomeArray = new ArrayList<>();
                        items.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String monthIncome = ds.child("monthIncome").getValue(String.class);
                            if(monthIncome.equals(monthGet)){
                                Double sum = ds.child("sum").getValue(Double.class);
                                int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                                int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                String id = ds.getKey();
                                String category = ds.child("category").getValue(String.class);
                                String notes = ds.child("notes").getValue(String.class);

                                Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                incomeArray.add(income);
                            }
                        }
                        incomes = incomeArray;
                        for (int i = 0; i < incomes.size(); i++) {
                            items.add(incomes.get(i));
                        }

                        mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Expense> expenseArray = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String monthExpense = ds.child("monthExpense").getValue(String.class);
                                    if(monthExpense.equals(monthGet)){
                                        Double price = ds.child("price").getValue(Double.class);
                                        int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                        int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                        String id = ds.getKey();
                                        String category = ds.child("category").getValue(String.class);
                                        String notes = ds.child("notes").getValue(String.class);
                                        Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                        expenseArray.add(expense);
                                    }
                                }
                                expenses = expenseArray;
                                for (int i = 0; i < expenses.size(); i++) {
                                    items.add(expenses.get(i));
                                }

                                app.setItems(items);
                                adapter[0] = new ItemsAdapter(moneyExpanded.this, items);
                                lvItems.setAdapter(adapter[0]);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

            } catch (SQLException e) {
                Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (prefs.getString("monthlyOrYearly", "").equals("Daily")) {

//            calendar.set(MONTH, Integer.valueOf(prefs.getString("month", "")));
//            calendar.set(YEAR, Integer.valueOf(prefs.getString("year", "")));
//            calendar.set(DAY_OF_MONTH, Integer.valueOf(prefs.getString("day", "")));

            tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

            try {
                final int dayGet = calendar.get(Calendar.DAY_OF_MONTH);
                final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Income> incomeArray = new ArrayList<>();
                        items.clear();
                        items.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String monthIncome = ds.child("monthIncome").getValue(String.class);
                            int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                            if(monthIncome.equals(monthGet)&&dayIncome==dayGet){
                                Double sum = ds.child("sum").getValue(Double.class);
                                int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                String id = ds.getKey();
                                String category = ds.child("category").getValue(String.class);
                                String notes = ds.child("notes").getValue(String.class);

                                Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                incomeArray.add(income);
                            }
                        }
                        incomes = incomeArray;
                        for (int i = 0; i < incomes.size(); i++) {
                            items.add(incomes.get(i));
                        }

                        mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Expense> expenseArray = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                    String monthExpense = ds.child("monthExpense").getValue(String.class);
                                    if(monthExpense.equals(monthGet)&&dayExpense==dayGet){
                                        Double price = ds.child("price").getValue(Double.class);
                                        int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                        String id = ds.getKey();
                                        String category = ds.child("category").getValue(String.class);
                                        String notes = ds.child("notes").getValue(String.class);
                                        Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                        expenseArray.add(expense);
                                    }
                                }
                                expenses = expenseArray;
                                for (int i = 0; i < expenses.size(); i++) {
                                    items.add(expenses.get(i));
                                }
                                app.setItems(items);
                                adapter[0] = new ItemsAdapter(moneyExpanded.this, items);
                                lvItems.setAdapter(adapter[0]);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

            } catch (SQLException e) {
                Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }





        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")));
                calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));
                calendar.set(DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));

                if (prefs.getString("monthlyOrYearly", "").equals("Yearly")) {
                    calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")) + 1);
                    Toast.makeText(app, calendar.get(YEAR) + "", Toast.LENGTH_SHORT).show();
                    tvToday.setText(String.valueOf(calendar.get(YEAR)));
                    prefs.edit().putString("year", String.valueOf(calendar.get(YEAR))).apply();

                    try {
                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                                    String monthIncome = ds.child("monthIncome").getValue(String.class);
                                    Double sum = ds.child("sum").getValue(Double.class);
                                    Log.d("CHKRP",""+sum);
                                    int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                    String id = ds.getKey();
                                    String category = ds.child("category").getValue(String.class);
                                    String notes = ds.child("notes").getValue(String.class);
                                    Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                    incomeArray.add(income);
                                }
                                incomes = incomeArray;
                                items.clear();
                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            Double price = ds.child("price").getValue(Double.class);
                                            int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                            String monthExpense = ds.child("monthExpense").getValue(String.class);
                                            int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                            String id = ds.getKey();
                                            String category = ds.child("category").getValue(String.class);
                                            String notes = ds.child("notes").getValue(String.class);

                                            Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                            expenseArray.add(expense);
                                        }
                                        expenses = expenseArray;
                                        adapter[0].clear();

                                        for (int i = 0; i < incomes.size(); i++) {
                                            items.add(incomes.get(i));
                                        }
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                        }

                                        adapter[0].notifyDataSetChanged();

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        throw databaseError.toException();
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });


                    } catch (SQLException e) {
                        Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {

                    calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) + 1);

                    if (calendar.get(MONTH) > 11) {
                        calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")) + 1);
                        calendar.set(MONTH, 0);

                    }
                    tvToday.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    prefs.edit().putString("year", String.valueOf(calendar.get(YEAR))).apply();
                    prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();


                    try {
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                items.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String monthIncome = ds.child("monthIncome").getValue(String.class);
                                    if(monthIncome.equals(monthGet)){
                                        Double sum = ds.child("sum").getValue(Double.class);
                                        int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                                        int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                        String id = ds.getKey();
                                        String category = ds.child("category").getValue(String.class);
                                        String notes = ds.child("notes").getValue(String.class);

                                        Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                        incomeArray.add(income);
                                    }
                                }
                                incomes = incomeArray;
                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            String monthExpense = ds.child("monthExpense").getValue(String.class);
                                            if(monthExpense.equals(monthGet)){
                                                Double price = ds.child("price").getValue(Double.class);
                                                int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                                int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                                String id = ds.getKey();
                                                String category = ds.child("category").getValue(String.class);
                                                String notes = ds.child("notes").getValue(String.class);
                                                Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                                expenseArray.add(expense);
                                            }
                                        }
                                        expenses = expenseArray;
                                        adapter[0].clear();

                                        for (int i = 0; i < incomes.size(); i++) {
                                            items.add(incomes.get(i));
                                        }
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                        }

                                        adapter[0].notifyDataSetChanged();

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        throw databaseError.toException();
                                    }
                                });

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });



                    } catch (SQLException e) {
                        Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else if (prefs.getString("monthlyOrYearly", "").equals("Daily")) {

                    int maxDayFromCurrentMonth = calendar.getActualMaximum(DAY_OF_MONTH);

                    if (calendar.get(DAY_OF_MONTH) + 1 <= maxDayFromCurrentMonth) {
                        calendar.set(DAY_OF_MONTH, calendar.get(DAY_OF_MONTH) + 1);

                        prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();

                        tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    } else {
                        calendar.set(DAY_OF_MONTH, 1);
                        prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();

                        int monthToVerifyForChangingYear = calendar.get(MONTH) + 1;


                        if (monthToVerifyForChangingYear <= 11) {
                            calendar.set(MONTH, calendar.get(MONTH) + 1);
                            prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();
                            tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                        } else {

                            calendar.set(DAY_OF_MONTH, 1);
                            calendar.set(MONTH, 0);
                            calendar.set(YEAR, calendar.get(YEAR) + 1);

                            prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH)));
                            prefs.edit().putString("month", String.valueOf(calendar.get(MONTH)));
                            prefs.edit().putString("year", String.valueOf(calendar.get(YEAR)));

                            tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));
                        }
                    }

                    try {
                        final int dayGet = calendar.get(Calendar.DAY_OF_MONTH);
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                items.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String monthIncome = ds.child("monthIncome").getValue(String.class);
                                    int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                                    if(monthIncome.equals(monthGet)&&dayIncome==dayGet){
                                        Double sum = ds.child("sum").getValue(Double.class);
                                        int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                        String id = ds.getKey();
                                        String category = ds.child("category").getValue(String.class);
                                        String notes = ds.child("notes").getValue(String.class);

                                        Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                        incomeArray.add(income);
                                    }
                                }
                                incomes = incomeArray;

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                            String monthExpense = ds.child("monthExpense").getValue(String.class);
                                            if(monthExpense.equals(monthGet)&&dayExpense==dayGet){
                                                Double price = ds.child("price").getValue(Double.class);
                                                int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                                String id = ds.getKey();
                                                String category = ds.child("category").getValue(String.class);
                                                String notes = ds.child("notes").getValue(String.class);
                                                Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                                expenseArray.add(expense);
                                            }
                                        }
                                        expenses = expenseArray;
                                        adapter[0].clear();

                                        for (int i = 0; i < incomes.size(); i++) {
                                            items.add(incomes.get(i));
                                        }
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                        }

                                        adapter[0].notifyDataSetChanged();

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        throw databaseError.toException();
                                    }
                                });

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });


                    } catch (SQLException e) {
                        Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (prefs.getString("monthlyOrYearly", "").equals("Yearly")) {
                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) - 1);
                    tvToday.setText(String.valueOf(calendar.get(Calendar.YEAR)));
                    prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();

                    try {
                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                                    String monthIncome = ds.child("monthIncome").getValue(String.class);
                                    Double sum = ds.child("sum").getValue(Double.class);
                                    Log.d("CHKRP",""+sum);
                                    int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                    String id = ds.getKey();
                                    String category = ds.child("category").getValue(String.class);
                                    String notes = ds.child("notes").getValue(String.class);
                                    Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                    incomeArray.add(income);
                                }
                                incomes = incomeArray;
                                items.clear();
                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            Double price = ds.child("price").getValue(Double.class);
                                            int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                            String monthExpense = ds.child("monthExpense").getValue(String.class);
                                            int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                            String id = ds.getKey();
                                            String category = ds.child("category").getValue(String.class);
                                            String notes = ds.child("notes").getValue(String.class);

                                            Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                            expenseArray.add(expense);
                                        }
                                        expenses = expenseArray;
                                        adapter[0].clear();

                                        for (int i = 0; i < incomes.size(); i++) {
                                            items.add(incomes.get(i));
                                        }
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                        }

                                        adapter[0].notifyDataSetChanged();

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        throw databaseError.toException();
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    } catch (SQLException e) {
                        Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {
                    calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) - 1);

                    if (calendar.get(MONTH) < 0) {
                        calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")) - 1);
                        calendar.set(MONTH, 11);

                    }
                    tvToday.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    prefs.edit().putString("year", String.valueOf(calendar.get(YEAR))).apply();
                    prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();


                    try {
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                items.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String monthIncome = ds.child("monthIncome").getValue(String.class);
                                    if(monthIncome.equals(monthGet)){
                                        Double sum = ds.child("sum").getValue(Double.class);
                                        int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                                        int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                        String id = ds.getKey();
                                        String category = ds.child("category").getValue(String.class);
                                        String notes = ds.child("notes").getValue(String.class);

                                        Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                        incomeArray.add(income);
                                    }
                                }
                                incomes = incomeArray;
                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            String monthExpense = ds.child("monthExpense").getValue(String.class);
                                            if(monthExpense.equals(monthGet)){
                                                Double price = ds.child("price").getValue(Double.class);
                                                int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                                int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                                String id = ds.getKey();
                                                String category = ds.child("category").getValue(String.class);
                                                String notes = ds.child("notes").getValue(String.class);
                                                Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                                expenseArray.add(expense);
                                            }
                                        }
                                        expenses = expenseArray;
                                        adapter[0].clear();

                                        for (int i = 0; i < incomes.size(); i++) {
                                            items.add(incomes.get(i));
                                        }
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                        }

                                        adapter[0].notifyDataSetChanged();

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        throw databaseError.toException();
                                    }
                                });

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    } catch (SQLException e) {
                        Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (prefs.getString("monthlyOrYearly", "").equals("Daily")) {
                    calendar.set(DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));
                    calendar.set(DAY_OF_MONTH, calendar.get(DAY_OF_MONTH) - 1);
                    prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();

                    if (calendar.get(DAY_OF_MONTH) >= 1) {

                        prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();
                        tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    } else {

                        int monthToVerifyForChangingYear = calendar.get(MONTH) - 1;
                        calendar.set(MONTH, calendar.get(MONTH) - 1);
                        int maxDayFromMonth = calendar.getActualMaximum(DAY_OF_MONTH);

                        if (monthToVerifyForChangingYear >= 0) {
                            tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                        } else {

                            calendar.set(MONTH, 11);
                            calendar.set(DAY_OF_MONTH, maxDayFromMonth);
                            calendar.set(MONTH, calendar.get(MONTH));
                            tvToday.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));
                        }
                    }
                    try {
                        final int dayGet = calendar.get(Calendar.DAY_OF_MONTH);
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                items.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String monthIncome = ds.child("monthIncome").getValue(String.class);
                                    int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                                    if(monthIncome.equals(monthGet)&&dayIncome==dayGet){
                                        Double sum = ds.child("sum").getValue(Double.class);
                                        int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                                        String id = ds.getKey();
                                        String category = ds.child("category").getValue(String.class);
                                        String notes = ds.child("notes").getValue(String.class);

                                        Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                                        incomeArray.add(income);
                                    }
                                }
                                incomes = incomeArray;

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            int dayExpense = ds.child("dayExpense").getValue(Integer.class);
                                            String monthExpense = ds.child("monthExpense").getValue(String.class);
                                            if(monthExpense.equals(monthGet)&&dayExpense==dayGet){
                                                Double price = ds.child("price").getValue(Double.class);
                                                int yearExpense = ds.child("yearExpense").getValue(Integer.class);
                                                String id = ds.getKey();
                                                String category = ds.child("category").getValue(String.class);
                                                String notes = ds.child("notes").getValue(String.class);
                                                Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
                                                expenseArray.add(expense);
                                            }
                                        }
                                        expenses = expenseArray;
                                        adapter[0].clear();

                                        for (int i = 0; i < incomes.size(); i++) {
                                            items.add(incomes.get(i));
                                        }
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                        }

                                        adapter[0].notifyDataSetChanged();

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        throw databaseError.toException();
                                    }
                                });

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });


                    } catch (SQLException e) {
                        Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {
                if (adapter.getItemAtPosition(i) instanceof Income) {
                    Income income = (Income) adapter.getItemAtPosition(i);
                    String notes = income.getNotes();
                    String category = income.getCategory();
                    double sum = income.getSum();
                    Intent intent = new Intent(moneyExpanded.this,
                            ShowIncome.class);
                    intent.putExtra("notes", notes);
                    intent.putExtra("category", category);
                    intent.putExtra("sum", sum);
                    intent.putExtra("day", income.getDayIncome());
                    intent.putExtra("month", income.getMonthIncome());
                    intent.putExtra("year", income.getYearIncome());
                    intent.putExtra("rowId", i);
                    startActivity(intent);
                } else if (adapter.getItemAtPosition(i) instanceof Expense) {
                    Expense expense = (Expense) adapter.getItemAtPosition(i);
                    double amountSpent = expense.getPrice();
                    String category = expense.getCategory();
                    String notes = expense.getNotes();

                    Intent intent = new Intent(moneyExpanded.this,
                            ShowExpense.class);
                    intent.putExtra("amountSpent", amountSpent);
                    intent.putExtra("day", expense.getDayExpense());
                    intent.putExtra("month", expense.getMonthExpense());
                    intent.putExtra("year", expense.getYearExpense());
                    intent.putExtra("category", category);
                    intent.putExtra("notes", notes);
                    startActivity(intent);
                }
            }
        });

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            MyApplication app = (MyApplication) moneyExpanded.this.getApplication();

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i) instanceof Income) {
                    final Income income = (Income) adapterView.getItemAtPosition(i);
                    AlertDialog.Builder builder = new AlertDialog.Builder(moneyExpanded.this);
                    builder.setMessage("Do you want to remove the selected item?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ExpensesDB db = new ExpensesDB(moneyExpanded.this);
                            db.open();
                            db.deleteEntryIncome(income.getId());
                            db.close();
                            adapter[0].remove(income);
                            adapter[0].notifyDataSetChanged();
                            Toast.makeText(moneyExpanded.this, "The item has been removed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();

                } else if (adapterView.getItemAtPosition(i) instanceof Expense) {
                    final Expense expense = (Expense) adapterView.getItemAtPosition(i);
                    AlertDialog.Builder builder = new AlertDialog.Builder(moneyExpanded.this);
                    builder.setMessage("Do you want to remove the selected item?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                ExpensesDB db = new ExpensesDB(moneyExpanded.this);
                                db.open();
                                db.deleteEntryExpense(expense.getId());
                                db.close();
                            } catch (SQLException e) {
                                Toast.makeText(moneyExpanded.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            adapter[0].remove(expense);
                            adapter[0].notifyDataSetChanged();
                            Toast.makeText(moneyExpanded.this, "The item has been removed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });
    }
}

