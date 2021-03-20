package com.second.moneymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FireBaseDB {
    private DatabaseReference mDatabase;
    public FireBaseDB() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<Income> getIncomesByyear(String year) {
        final ArrayList<Income> incomeArray = new ArrayList<>();
        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(year)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        return incomeArray;
    }

    public ArrayList<Expense> getExpensesByYear(String year) {
        final ArrayList<Expense> expenseArray = new ArrayList<>();
        mDatabase.child("Expense").orderByChild("yearExpense").equalTo(Integer.valueOf(year)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        return expenseArray;
    }


    public ArrayList<Income> getIncomesByMonthAndYear(final String month, String year) {
        final ArrayList<Income> incomeArray = new ArrayList<>();
        mDatabase.child("Income").orderByChild("yearIncome").equalTo(Integer.valueOf(year)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String monthIncome = ds.child("monthIncome").getValue(String.class);
                    if(monthIncome.equals(month)){
                        Double sum = ds.child("price").getValue(Double.class);
                        int dayIncome = ds.child("dayIncome").getValue(Integer.class);
                        int yearIncome = ds.child("yearIncome").getValue(Integer.class);
                        String id = ds.getKey();
                        String category = ds.child("category").getValue(String.class);
                        String notes = ds.child("notes").getValue(String.class);

                        Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
                        incomeArray.add(income);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        return incomeArray;
    }


    public ArrayList<Expense> getExpensesByMonthAndYear(final String month, String year) {

        final ArrayList<Expense> expenseArray = new ArrayList<>();
        mDatabase.child("Expense").orderByChild("yearIncome").equalTo(Integer.valueOf(year)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String monthExpense = ds.child("monthExpense").getValue(String.class);
                    if(monthExpense.equals(month)){
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        return expenseArray;
    }



}
