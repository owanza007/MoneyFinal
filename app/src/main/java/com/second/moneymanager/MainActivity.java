package com.second.moneymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SHORT;
import static java.util.Calendar.YEAR;

public class MainActivity extends AppCompatActivity {

    public static final int waitForCancel = 1;

    TextView tvAccount, tvCurrency, tvDailyMonthlyYearly, tvMonthOrYear,
            tvBalanceIncomes, tvBalanceExpense, tvIncomesSum, tvExpenseSum,
            tvCurrencyIncomes, tvCurrencyExpenses;

    Button btnAddExepense, btnAddIncome, btnPreviousDate, btnNextDate;
    Spinner spinnerCurrency, spinnerMonthly;
    LinearLayout llAccount;
    ArrayList<Income> incomes = new ArrayList<>();
    ArrayList<Expense> expenses = new ArrayList<>();
    ArrayList items = new ArrayList();
    SharedPreferences prefs = null;
    MenuItem menuItemCurrency, menuitemYearly, chartsButton, privacyPolicy;
    private DatabaseReference mDatabase;
    double valueIncomes, valueExpenses;
    Calendar calendar = Calendar.getInstance();
    private FireBaseDB fireBaseDB = new FireBaseDB();
    public final int requestCodeActivityAddIncome = 1;
    public final int requestCodeActivityAddExpense = 2;
    private static final String[] paths = {"THA"};

    private static final String[] valuesToShowAccount = {"รายปี", "รายเดือน", "รายวัน"};

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuItemCurrency = menu.findItem(R.id.changeCurrency);
        menuitemYearly = menu.findItem(R.id.monthlyYearly);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final AlertDialog.Builder b = new AlertDialog.Builder(this);
        final AlertDialog.Builder b1 = new AlertDialog.Builder(this);

        final int ANOTHER_RESULT_WAIT_FOR_CANCEL_CODE = 9;

        b.setTitle("Change Currency");
        b1.setTitle("Sort");

        b.setItems(paths, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putString("currency", spinnerCurrency.getAdapter().getItem(which).toString()).apply();
                tvCurrency.setText(spinnerCurrency.getAdapter().getItem(which).toString());
                tvCurrencyExpenses.setText(spinnerCurrency.getAdapter().getItem(which).toString());
                tvCurrencyIncomes.setText(spinnerCurrency.getAdapter().getItem(which).toString());
                dialog.dismiss();
            }
        });

        b1.setItems(valuesToShowAccount, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (spinnerMonthly.getAdapter().getItem(which).toString().trim().equals("รายปี")) {

                    prefs.edit().putString("monthlyOrYearly", "Yearly").apply();
                    prefs.edit().putString("year", prefs.getString("year", "")).apply();

                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")));
                    tvMonthOrYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));



                    try {

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                valueIncomes = 0;
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
                                for (int i = 0; i < incomes.size(); i++) {
                                    items.add(incomes.get(i));
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }


                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }
                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);
                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));
                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }

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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (spinnerMonthly.getAdapter().getItem(which).toString().trim().equals("รายเดือน")) {

                    prefs.edit().putString("monthlyOrYearly", "Monthly").apply();
                    prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                    prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();

                    tvMonthOrYear.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));




                    try {

                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                valueIncomes = 0;
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
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);

                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));
                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));
                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }

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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (spinnerMonthly.getAdapter().getItem(which).toString().trim().equals("รายวัน")) {

                    prefs.edit().putString("monthlyOrYearly", "Daily").apply();
                    prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                    prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();
                    prefs.edit().putString("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))).apply();

                    tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                            calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault())
                            + " - " + calendar.get(Calendar.YEAR));

                    try {
                        final int dayGet = calendar.get(Calendar.DAY_OF_MONTH);
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                valueIncomes = 0;
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
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);

                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));
                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }


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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                dialog.dismiss();
            }
        });

        switch (item.getItemId()) {

            case R.id.monthlyYearly:
                b1.show();
                break;

            case R.id.changeCurrency:

                b.show().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tvAccount = findViewById(R.id.tvAccount);
        tvCurrency = findViewById(R.id.tvCurrency);
        tvBalanceIncomes = findViewById(R.id.tvBalanceIncomes);
        tvBalanceExpense = findViewById(R.id.tvBalanceExpense);
        tvIncomesSum = findViewById(R.id.tvIncomesSum);
        tvExpenseSum = findViewById(R.id.tvExpenseSum);
        tvCurrencyIncomes = findViewById(R.id.tvCurrencyIncomes);
        tvCurrencyExpenses = findViewById(R.id.tvCurrencyExpenses);
        tvDailyMonthlyYearly = findViewById(R.id.tvDailyMonthlyYearly);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        spinnerMonthly = findViewById(R.id.spinnerMonthly);
        llAccount = findViewById(R.id.llAccount);
        tvMonthOrYear = findViewById(R.id.tvMonthOrYear);
        btnAddExepense = findViewById(R.id.btnAddExpense);
        btnAddIncome = findViewById(R.id.btnAddIncome);
        btnPreviousDate = findViewById(R.id.btnPreviousDate);
        btnNextDate = findViewById(R.id.btnNextDate);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvIncomesSum.setTextColor(Color.parseColor("#388e3c"));
        tvExpenseSum.setTextColor(Color.parseColor("#b91400"));

        spinnerCurrency.setVisibility(GONE);
        spinnerMonthly.setVisibility(GONE);

        tvDailyMonthlyYearly.setText("ทั้งหมด");
        tvBalanceIncomes.setText("รายรับ");
        tvBalanceExpense.setText("รายจ่าย");


        prefs = getSharedPreferences("com.mycompany.MoneyManager", MainActivity.MODE_PRIVATE);


        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        if (prefs.getBoolean("firstrun", true)) {
            try {
                ExpensesDB db = new ExpensesDB(MainActivity.this);
                db.open();
                db.createEntryIncomeCategory("เงินเดือน");
                db.createEntryIncomeCategory("สื่อสังคม");
                db.createEntryIncomeCategory("ฝ่ายขาย");
                db.createEntryIncomeCategory("โบนัส");

                db.createEntryExpenseCategory("น้ำมัน");
                db.createEntryExpenseCategory("กาแฟ");
                db.createEntryExpenseCategory("กินข้าวนอกบ้าน");
                db.createEntryExpenseCategory("เสื้อผ้า");
                db.createEntryExpenseCategory("เกม");
                db.createEntryExpenseCategory("ของขวัญ");
                db.createEntryExpenseCategory("วันหยุด");
                db.createEntryExpenseCategory("เด็ก");
                db.createEntryExpenseCategory("กีฬา");
                db.createEntryExpenseCategory("ท่องเที่ยว");
                db.close();
            } catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            Calendar calendar = Calendar.getInstance();
            // Do first run stuff here then set 'firstrun' as false
            prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
            prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();
            prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();

            prefs.edit().putString("idForTheme", String.valueOf(R.style.AppTheme)).apply();

            tvCurrency.setText("THA");
            tvCurrencyExpenses.setText("THA");
            tvCurrencyIncomes.setText("THA");

            prefs.edit().putString("monthlyOrYearly", "Monthly").apply();
            prefs.edit().putString("currency", "EUR").apply();

            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).apply();
        } else {
            tvCurrency.setText(prefs.getString("currency", ""));
            tvCurrencyExpenses.setText(prefs.getString("currency", ""));
            tvCurrencyIncomes.setText(prefs.getString("currency", ""));


        }

        calendar.set(YEAR, Integer.parseInt(prefs.getString("year", "")));
        calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));
        calendar.set(DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));

        btnAddExepense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean("isExpense", true).apply();
                prefs.edit().putBoolean("isIncome", false).apply();

                Intent intent = new Intent(MainActivity.this,
                        AddExpense.class);

                startActivityForResult(intent, requestCodeActivityAddExpense);
            }
        });

        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean("isIncome", true).apply();
                prefs.edit().putBoolean("isExpense", false).apply();

                Intent intent = new Intent(MainActivity.this,
                        AddIncome.class);
                startActivityForResult(intent, requestCodeActivityAddIncome);
            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, paths);

        final ArrayAdapter<String> adapterMonthly = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, valuesToShowAccount);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
        spinnerMonthly.setAdapter(adapterMonthly);
        Arrays.sort(paths);


        if (prefs.getString("monthlyOrYearly", "").equals("Yearly")) {
            tvMonthOrYear.setText(prefs.getString("year", ""));
            calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")));

            try {
                mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Income> incomeArray = new ArrayList<>();
                        valueIncomes = 0;
                        items.clear();
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
                        for (int i = 0; i < incomes.size(); i++) {
                            items.add(incomes.get(i));
                            valueIncomes = valueIncomes + incomes.get(i).getSum();
                        }


                        mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Expense> expenseArray = new ArrayList<>();
                                valueExpenses = 0;
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
                                for (int i = 0; i < expenses.size(); i++) {
                                    items.add(expenses.get(i));
                                    valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                }
                                prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();
                                MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                app.setItems(items);
                                tvIncomesSum.setText(String.valueOf(valueIncomes));
                                tvExpenseSum.setText(String.valueOf(valueExpenses));

                                double amountToSet = valueIncomes - valueExpenses;
                                tvAccount.setText(String.valueOf(amountToSet));


                                if (amountToSet == 0) {
                                    tvAccount.setText("0");
                                } else if (amountToSet > 0) {
                                    tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                } else {
                                    tvAccount.setTextColor(Color.parseColor("#b91400"));
                                }

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
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {
            calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")));
            calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")));

            tvMonthOrYear.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));
            prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();

            valueExpenses = 0;
            valueIncomes = 0;

            try {
                final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Income> incomeArray = new ArrayList<>();
                        valueIncomes = 0;
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
                            valueIncomes = valueIncomes + incomes.get(i).getSum();
                        }

                        mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Expense> expenseArray = new ArrayList<>();
                                valueExpenses = 0;
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
                                    valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                }

                                prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();


                                MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                app.setItems(items);

                                tvIncomesSum.setText(String.valueOf(valueIncomes));
                                tvExpenseSum.setText(String.valueOf(valueExpenses));

                                double amountToSet = valueIncomes - valueExpenses;
                                tvAccount.setText(String.valueOf(amountToSet));


                                if (amountToSet == 0) {
                                    tvAccount.setText("0");
                                } else if (amountToSet > 0) {
                                    tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                } else {
                                    tvAccount.setTextColor(Color.parseColor("#b91400"));
                                }

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
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (prefs.getString("monthlyOrYearly", "").equals("Daily")) {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));
            tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

            prefs.edit().putString("monthlyOrYearly", "Daily").apply();
            prefs.edit().putString("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))).apply();


            try {
                final int dayGet = calendar.get(Calendar.DAY_OF_MONTH);
                final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Income> incomeArray = new ArrayList<>();
                        items.clear();
                        valueIncomes = 0;
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
                            valueIncomes = valueIncomes + incomes.get(i).getSum();
                        }

                        mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Expense> expenseArray = new ArrayList<>();
                                valueExpenses = 0;
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
                                    valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                }

                                prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();

                                MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                app.setItems(items);

                                tvIncomesSum.setText(String.valueOf(valueIncomes));
                                tvExpenseSum.setText(String.valueOf(valueExpenses));
                                double amountToSet = valueIncomes - valueExpenses;
                                tvAccount.setText(String.valueOf(amountToSet));

                                if (amountToSet == 0) {
                                    tvAccount.setText("0");
                                } else if (amountToSet > 0) {
                                    tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                } else {
                                    tvAccount.setTextColor(Color.parseColor("#b91400"));
                                }


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
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        btnPreviousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getString("monthlyOrYearly", "").equals("Yearly")) {
                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) - 1);
                    tvMonthOrYear.setText(calendar.get(Calendar.YEAR) + "");
                    prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();

                    valueExpenses = 0;
                    valueIncomes = 0;

                    try {
                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                valueIncomes = 0;
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
                                for (int i = 0; i < incomes.size(); i++) {
                                    items.add(incomes.get(i));
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }


                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }
                                        prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);
                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));


                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }
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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {

                    calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) - 1);
                    if (calendar.get(MONTH) < 0) {
                        calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) - 1);
                        calendar.set(MONTH, 11);
                    }
                    tvMonthOrYear.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                    prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();


                    valueExpenses = 0;
                    valueIncomes = 0;

                    try {
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                valueIncomes = 0;
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
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }
                                        prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);

                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));

                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }

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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (prefs.getString("monthlyOrYearly", "").equals("Daily")) {


                    calendar.set(DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));
                    calendar.set(DAY_OF_MONTH, calendar.get(DAY_OF_MONTH) - 1);
                    prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();

                    if (calendar.get(DAY_OF_MONTH) >= 1) {

                        prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();
                        tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    } else {

                        int monthToVerifyForChangingYear = calendar.get(MONTH) - 1;
                        calendar.set(MONTH, calendar.get(MONTH) - 1);
                        int maxDayFromMonth = calendar.getActualMaximum(DAY_OF_MONTH);

                        if (monthToVerifyForChangingYear >= 0) {
                            tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                        } else {

                            calendar.set(MONTH, 11);
                            calendar.set(DAY_OF_MONTH, maxDayFromMonth);
                            calendar.set(MONTH, calendar.get(MONTH));
                            tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));
                        }
                    }
                    valueExpenses = 0;
                    valueIncomes = 0;

                    try {
                        final int dayGet = calendar.get(Calendar.DAY_OF_MONTH);
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                items.clear();
                                valueIncomes = 0;
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
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }

                                        prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);

                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));


                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }

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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (prefs.getString("monthlyOrYearly", "").equals("Yearly")) {
                    calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) + 1);
                    tvMonthOrYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
                    prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();

                    valueExpenses = 0;
                    valueIncomes = 0;

                    try {
                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                valueIncomes = 0;
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
                                for (int i = 0; i < incomes.size(); i++) {
                                    items.add(incomes.get(i));
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }


                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(calendar.get(Calendar.YEAR))).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                        for (int i = 0; i < expenses.size(); i++) {
                                            items.add(expenses.get(i));
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }
                                        prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);
                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));


                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }

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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (prefs.getString("monthlyOrYearly", "").equals("Monthly")) {

                    calendar.set(MONTH, Integer.parseInt(prefs.getString("month", "")) + 1);
                    if (calendar.get(MONTH) > 11) {
                        calendar.set(Calendar.YEAR, Integer.parseInt(prefs.getString("year", "")) + 1);
                        calendar.set(MONTH, 0);
                    }
                    tvMonthOrYear.setText(calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    prefs.edit().putString("year", String.valueOf(calendar.get(Calendar.YEAR))).apply();
                    prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();

                    valueExpenses = 0;
                    valueIncomes = 0;

                    try {
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                valueIncomes = 0;
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
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }

                                        prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);

                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));


                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }
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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (prefs.getString("monthlyOrYearly", "").equals("Daily")) {


                    calendar.set(DAY_OF_MONTH, Integer.parseInt(prefs.getString("day", "")));
                    calendar.set(DAY_OF_MONTH, calendar.get(DAY_OF_MONTH) + 1);
                    prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();

                    if (calendar.get(DAY_OF_MONTH) <= calendar.getActualMaximum(DAY_OF_MONTH)) {

                        prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();
                        tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                    } else {

                        int monthToVerifyForChangingYear = calendar.get(MONTH) + 1;
                        calendar.set(MONTH, calendar.get(MONTH) + 1);
                        int maxDayFromMonth = calendar.getActualMaximum(DAY_OF_MONTH);

                        if (monthToVerifyForChangingYear <= 11) {
                            tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));

                        } else {

                            calendar.set(MONTH, 0);
                            calendar.set(DAY_OF_MONTH, maxDayFromMonth);
                            calendar.set(MONTH, calendar.get(MONTH));
                            tvMonthOrYear.setText(calendar.get(Calendar.DAY_OF_MONTH) + " - " +
                                    calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + " - " + calendar.get(Calendar.YEAR));


                            prefs.edit().putString("day", String.valueOf(calendar.get(DAY_OF_MONTH))).apply();
                            prefs.edit().putString("month", String.valueOf(calendar.get(MONTH))).apply();
                            prefs.edit().putString("year", String.valueOf(calendar.get(YEAR))).apply();
                        }


                    }

                    valueExpenses = 0;
                    valueIncomes = 0;

                    try {
                        final int dayGet = calendar.get(Calendar.DAY_OF_MONTH);
                        final String monthGet = calendar.getDisplayName(MONTH, SHORT, Locale.getDefault());
                        final String yearGet = String.valueOf(calendar.get(Calendar.YEAR));

                        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<Income> incomeArray = new ArrayList<>();
                                items.clear();
                                valueIncomes = 0;
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
                                    valueIncomes = valueIncomes + incomes.get(i).getSum();
                                }

                                mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(yearGet)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<Expense> expenseArray = new ArrayList<>();
                                        valueExpenses = 0;
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
                                            valueExpenses = valueExpenses + expenses.get(i).getPrice();
                                        }

                                        prefs.edit().putString("valueExpenses", String.valueOf(valueExpenses)).apply();

                                        MyApplication app = (MyApplication) MainActivity.this.getApplication();
                                        app.setItems(items);

                                        tvIncomesSum.setText(String.valueOf(valueIncomes));
                                        tvExpenseSum.setText(String.valueOf(valueExpenses));

                                        double amountToSet = valueIncomes - valueExpenses;
                                        tvAccount.setText(String.valueOf(amountToSet));


                                        if (amountToSet == 0) {
                                            tvAccount.setText("0");
                                        } else if (amountToSet > 0) {
                                            tvAccount.setTextColor(Color.parseColor("#388e3c"));
                                        } else {
                                            tvAccount.setTextColor(Color.parseColor("#b91400"));
                                        }

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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                tvCurrency.setText(adapterView.getItemAtPosition(position).toString().trim());
                tvCurrencyExpenses.setText(adapterView.getItemAtPosition(position).toString().trim());
                tvCurrencyIncomes.setText(adapterView.getItemAtPosition(position).toString().trim());
                prefs.edit().putString("currency", tvCurrency.getText().toString().trim()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerMonthly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        llAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,moneyExpanded.class);
                startActivityForResult(intent, waitForCancel);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {

            this.finish();
            startActivity(getIntent());
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        }
    }
}

