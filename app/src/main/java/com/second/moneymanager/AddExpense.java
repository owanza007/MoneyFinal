package com.second.moneymanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.SHORT;

public class AddExpense extends AppCompatActivity {

    EditText etPrice, etNotesExpense, etCategoryExpense;
    Button btnAdd, btnCancel, btnDate;
    private Calendar myCalendar = Calendar.getInstance();
    private int day, month, year;
    public static final int requestCodeForExpenseCategories = 1;
    private DatabaseReference mDatabase;
    SharedPreferences prefs = null;
    private AdView mBannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

//        mBannerAd = (AdView) findViewById(R.id.banner_adViewExpense);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mBannerAd.loadAd(adRequest);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnDate = findViewById(R.id.btnDate);
        etPrice = findViewById(R.id.etPrice);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        etCategoryExpense = findViewById(R.id.etCategoryExpense);
        etNotesExpense = findViewById(R.id.etNotesExpense);

        day = myCalendar.get(Calendar.DAY_OF_MONTH);
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);

        prefs = getSharedPreferences("com.mycompany.MoneyManager", moneyExpanded.MODE_PRIVATE);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                AddExpense.this.finish();
            }
        });
        btnDate.setText(year + "-" + myCalendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + "-" + day);

        etPrice.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etPrice.setHint("");
                return false;
            }

        });

        etPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    etPrice.setHint("Amount");
                }
            }
        });


        etNotesExpense.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etNotesExpense.setHint("");
                return false;
            }

        });

        etNotesExpense.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    etNotesExpense.setHint("Notes");
                }
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog();
            }

            void DateDialog() {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(MONTH, monthOfYear);
                        btnDate.setText(year + "-" + myCalendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault()) + "-" + dayOfMonth);
                    }
                };

                DatePickerDialog dpDialog = new DatePickerDialog(AddExpense.this, listener, year, month, day);
                dpDialog.show();
            }
        });

        etCategoryExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddExpense.this,Categories.class);
                startActivityForResult(intent, requestCodeForExpenseCategories);

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPrice.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddExpense.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else if (btnDate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddExpense.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else if (etCategoryExpense.getText().toString().isEmpty()) {
                    Toast.makeText(AddExpense.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else if (etNotesExpense.getText().toString().isEmpty()) {
                    Toast.makeText(AddExpense.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {

                    if (etPrice.getText().toString().trim().charAt(0) == '.') {
                        etPrice.setText("0" + etPrice.getText().toString().trim());
                    }
                    String category = etCategoryExpense.getText().toString().trim();
                    String notes = etNotesExpense.getText().toString().trim();
                    double price = Double.parseDouble(etPrice.getText().toString().trim());
                    String dateFromInput = btnDate.getText().toString().trim();

                    StringTokenizer tokens = new StringTokenizer(dateFromInput, "-");
                    int yearFromButton = Integer.parseInt(tokens.nextToken());
                    String monthFromButton = tokens.nextToken();
                    int dayFromButton = Integer.parseInt(tokens.nextToken());

                    try {
                        ExpensesDB db = new ExpensesDB(AddExpense.this);
                        db.open();
                        db.createEntryExpense(price, dayFromButton, myCalendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), yearFromButton, notes, category);
                        MyApplication app = (MyApplication) AddExpense.this.getApplication();
                        app.addExpenseToItems(new Expense(price, dayFromButton, myCalendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), yearFromButton, null, notes, category));
                        mDatabase.child("Expense").push().setValue(new Expens2(price, dayFromButton, myCalendar.getDisplayName(MONTH, SHORT, Locale.getDefault()), yearFromButton, null, notes, category));
                        db.close();
                        Toast.makeText(AddExpense.this, "Succesfully Saved", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Toast.makeText(AddExpense.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    AddExpense.this.finish();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeForExpenseCategories) {
            if (resultCode == RESULT_OK) {
                if (prefs.getBoolean("addedNoNewExpenseCategory", true)) {

                    etCategoryExpense.setText(data.getStringExtra("categoryExpense"));
                } else {

                    etCategoryExpense.setText(data.getStringExtra("IncomeOrExpense"));
                }
            }
        }
    }
}
