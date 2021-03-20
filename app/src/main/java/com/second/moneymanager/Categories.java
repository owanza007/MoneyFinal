package com.second.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {
    ListView lvCategories;
    SharedPreferences prefs = null;
    Button btnAddCategory;
    ArrayList<String> incomeCategories = new ArrayList<>();
    ArrayList<String> expenseCategories = new ArrayList<>();
    private AdView mBannerAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

//        mBannerAd = (AdView) findViewById(R.id.banner_adViewCategories);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mBannerAd.loadAd(adRequest);

        lvCategories = findViewById(R.id.lvCategories);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        prefs = getSharedPreferences("com.mycompany.MoneyManager", MainActivity.MODE_PRIVATE);

        try {
            ExpensesDB db = new ExpensesDB(Categories.this);
            db.open();
            incomeCategories = db.getAllIncomeCategories();
            expenseCategories = db.getAllExpenseCategories();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        if (prefs.getBoolean("isExpense", true)) {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.newrow_layout, R.id.textView2, expenseCategories);

            lvCategories.setAdapter(adapter);

        } else if (prefs.getBoolean("isIncome", true)) {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.newrow_layout, R.id.textView2, incomeCategories);

            lvCategories.setAdapter(adapter);
        }

        lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                String itemValue = (String) lvCategories.getItemAtPosition(position);

                Intent intent = new Intent();
                if (prefs.getBoolean("isIncome", true)) {
                    intent.putExtra("categoryIncome", itemValue);
                    prefs.edit().putBoolean("addedNoNewIncomeCategory", true).apply();

                } else if (prefs.getBoolean("isExpense", true)) {
                    intent.putExtra("categoryExpense", itemValue);
                    prefs.edit().putBoolean("addedNoNewExpenseCategory", true).apply();
                }

                setResult(RESULT_OK, intent);
                Categories.this.finish();

            }
        });

        lvCategories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view,
                                           final int position, long id) {


                final String itemValue = (String) lvCategories.getItemAtPosition(position);

                if (prefs.getBoolean("isExpense", true)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Categories.this);
                    builder.setMessage("Do you want to remove the selected category?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ExpensesDB db = new ExpensesDB(Categories.this);
                            db.open();
                            db.deleteCategoryExpense(itemValue);
                            db.close();
                            Toast.makeText(Categories.this, "The category has been removed", Toast.LENGTH_SHORT).show();

                            Categories.this.finish();
                            startActivity(getIntent());
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();

                } else if (prefs.getBoolean("isIncome", true)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Categories.this);
                    builder.setMessage("Do you want to remove the selected category?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ExpensesDB db = new ExpensesDB(Categories.this);
                            db.open();
                            db.deleteCategoryIncome(itemValue);
                            db.close();
                            Toast.makeText(Categories.this, "The category has been removed", Toast.LENGTH_SHORT).show();

                            Categories.this.finish();
                            startActivity(getIntent());
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


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


        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                if (prefs.getBoolean("isIncome", true)) {
                    builder.setTitle("Add Category - Income");

                } else {
                    builder.setTitle("Add Category - Expense");
                }

                builder.setView(R.layout.alertdialogforcategory);

                //create Dialog and show
                final AlertDialog dialog = builder.create();
                dialog.show();
                builder.setCancelable(false);

                //Get AlertDialog from dialog
                final AlertDialog dialogView = dialog;
                Button ok = dialogView.findViewById(R.id.btnAddNewCategory);


                //custom lisenter for Positive button
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText tvNewCategory = dialogView.findViewById(R.id.tvNewCategory);

                        Intent intent = new Intent();
                        String EnteredText = "";
                        if (tvNewCategory.getText() == null || tvNewCategory.getText().toString().isEmpty()) {
                            tvNewCategory.setError("Must not be empty");

                        } else {
                            EnteredText = tvNewCategory.getText().toString().trim();
                            try {
                                ExpensesDB db = new ExpensesDB(Categories.this);
                                db.open();
                                if (prefs.getBoolean("isIncome", true)) {
                                    incomeCategories = db.getAllIncomeCategories();
                                    if (incomeCategories.contains(EnteredText)) {
                                        tvNewCategory.setError("Already exists");
                                    } else {

                                        db.createEntryIncomeCategory(EnteredText);
                                        prefs.edit().putBoolean("addedNoNewIncomeCategory", false).apply();
                                        intent.putExtra("IncomeOrExpense", EnteredText);
                                        setResult(RESULT_OK, intent);
                                        Categories.this.finish();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        dialog.dismiss();
                                    }

                                } else if (prefs.getBoolean("isExpense", true)) {
                                    expenseCategories = db.getAllExpenseCategories();
                                    if (expenseCategories.contains(EnteredText)) {
                                        tvNewCategory.setError("Already exists");

                                    } else {

                                        db.createEntryExpenseCategory(EnteredText);
                                        prefs.edit().putBoolean("addedNoNewExpenseCategory", false).apply();
                                        intent.putExtra("IncomeOrExpense", EnteredText);
                                        setResult(RESULT_OK, intent);
                                        Categories.this.finish();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        dialog.dismiss();
                                    }
                                }
                                db.close();
                            } catch (SQLException e) {
                                Toast.makeText(Categories.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        });
    }

}
