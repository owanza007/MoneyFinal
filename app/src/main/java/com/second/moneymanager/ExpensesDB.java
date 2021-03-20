package com.second.moneymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class ExpensesDB {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_PRODUCT = "_product";
    public static final String KEY_PRICE = "_price";
    public static final String KEY_DAY_FOR_EXPENSES = "day_for_expenses";
    public static final String KEY_MONTH_FOR_EXPENSES = "month_for_expenses";
    public static final String KEY_YEAR_FOR_EXPENSES = "year_for_expenses";
    public static final String KEY_FOR_NOTES_EXPENSES = "notes_for_expenses";
    public static final String KEY_FOR_CATEGORY = "_category";

    public static final String KEY_SUM = "_sum";
    public static final String KEY_DAY_FOR_INCOMES = "day_for_incomes";
    public static final String KEY_MONTH_FOR_INCOMES = "month_for_incomes";
    public static final String KEY_YEAR_FOR_INCOMES = "year_for_incomes";
    public static final String KEY_FOR_NOTES_INCOMES = "notes_for_incomes";
    public static final String KEY_FOR_CATEGORY_FOR_INCOME = "_category_for_income";

    public static final String KEY_CATEGORY_FOR_INCOME_TABLE = "_category_for_income_table";
    public static final String KEY_CATEGORY_FOR_EXPENSE_TABLE = "_category_for_expense_table";

    private final String DATABASE_TABLE_EXPENSE = "ExpenseTable";
    private final String DATABASE_TABLE_INCOME = "IncomeTable";
    private final String DATABASE_TABLE_CATEGORIES_FOR_INCOME = "CategoriesIncome";
    private final String DATABASE_TABLE_CATEGORIES_FOR_EXPENSE = "CategoriesExpense";
    private final int DATABASE_VERSION = 1;

    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public ExpensesDB(Context context) {
        ourContext = context;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_TABLE_EXPENSE, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {


            String sqlCodeForExpense = "CREATE TABLE " + DATABASE_TABLE_EXPENSE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_FOR_NOTES_EXPENSES + " TEXT NOT NULL, " +
                    KEY_FOR_CATEGORY + " TEXT NOT NULL, " +
                    KEY_PRICE + " REAL, " +
                    KEY_DAY_FOR_EXPENSES + " INTEGER, " +
                    KEY_MONTH_FOR_EXPENSES + " INTEGER, " +
                    KEY_YEAR_FOR_EXPENSES + " INTEGER)";

            String sqlCodeForIncome = "CREATE TABLE " + DATABASE_TABLE_INCOME + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_FOR_CATEGORY_FOR_INCOME + " TEXT NOT NULL, " +
                    KEY_FOR_NOTES_INCOMES + " TEXT NOT NULL, " +
                    KEY_SUM + " REAL, " +
                    KEY_DAY_FOR_INCOMES + " INTEGER, " +
                    KEY_MONTH_FOR_INCOMES + " INTEGER, " +
                    KEY_YEAR_FOR_INCOMES + " INTEGER)";

            String sqlCodeForCategoriesIncome = "CREATE TABLE " + DATABASE_TABLE_CATEGORIES_FOR_INCOME + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CATEGORY_FOR_INCOME_TABLE + " TEXT NOT NULL)";

            String sqlCodeForCategoriesExpense = "CREATE TABLE " + DATABASE_TABLE_CATEGORIES_FOR_EXPENSE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CATEGORY_FOR_EXPENSE_TABLE + " TEXT NOT NULL)";

            sqLiteDatabase.execSQL(sqlCodeForExpense);
            sqLiteDatabase.execSQL(sqlCodeForIncome);
            sqLiteDatabase.execSQL(sqlCodeForCategoriesIncome);
            sqLiteDatabase.execSQL(sqlCodeForCategoriesExpense);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_INCOME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_EXPENSE);
            onCreate(sqLiteDatabase);
        }
    }

    public ExpensesDB open() throws SQLException {
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {

        ourHelper.close();
    }

    public long createEntryIncomeCategory(String category) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CATEGORY_FOR_INCOME_TABLE, category);
        return ourDatabase.insert(DATABASE_TABLE_CATEGORIES_FOR_INCOME, null, cv);
    }

    public ArrayList<String> getAllIncomeCategories() {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<String> incomeCategoriesArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_CATEGORIES_FOR_INCOME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String category = res.getString(res.getColumnIndex(KEY_CATEGORY_FOR_INCOME_TABLE));
            incomeCategoriesArray.add(category);
            res.moveToNext();
        }
        return incomeCategoriesArray;
    }

    public long createEntryExpenseCategory(String category) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CATEGORY_FOR_EXPENSE_TABLE, category);
        return ourDatabase.insert(DATABASE_TABLE_CATEGORIES_FOR_EXPENSE, null, cv);
    }

    public long deleteCategoryExpense(String name) {
        return ourDatabase.delete(DATABASE_TABLE_CATEGORIES_FOR_EXPENSE, KEY_CATEGORY_FOR_EXPENSE_TABLE + "=?", new String[]{name});
    }


    public long deleteCategoryIncome(String name) {
        return ourDatabase.delete(DATABASE_TABLE_CATEGORIES_FOR_INCOME, KEY_CATEGORY_FOR_INCOME_TABLE + "=?", new String[]{name});
    }

    public ArrayList<String> getAllExpenseCategories() {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<String> expenseCategoriesArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_CATEGORIES_FOR_EXPENSE, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String category = res.getString(res.getColumnIndex(KEY_CATEGORY_FOR_EXPENSE_TABLE));
            expenseCategoriesArray.add(category);
            res.moveToNext();
        }
        return expenseCategoriesArray;
    }


    public long createEntryExpense(double price, int dayExpense, String monthExpense, int yearExpense, String notes, String category) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_PRICE, price);
        cv.put(KEY_DAY_FOR_EXPENSES, dayExpense);
        cv.put(KEY_MONTH_FOR_EXPENSES, monthExpense);
        cv.put(KEY_YEAR_FOR_EXPENSES, yearExpense);
        cv.put(KEY_FOR_NOTES_EXPENSES, notes);
        cv.put(KEY_FOR_CATEGORY, category);
        return ourDatabase.insert(DATABASE_TABLE_EXPENSE, null, cv);
    }

    public long createEntryIncome(double sum, int dayIncome, String monthIncome, int yearIncome, String category, String notes) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_SUM, sum);
        cv.put(KEY_DAY_FOR_INCOMES, dayIncome);
        cv.put(KEY_MONTH_FOR_INCOMES, monthIncome);
        cv.put(KEY_YEAR_FOR_INCOMES, yearIncome);
        cv.put(KEY_FOR_CATEGORY_FOR_INCOME, category);
        cv.put(KEY_FOR_NOTES_INCOMES, notes);

        return ourDatabase.insert(DATABASE_TABLE_INCOME, null, cv);
    }


    public long deleteEntryExpense(String id) {
        return ourDatabase.delete(DATABASE_TABLE_EXPENSE, KEY_ROWID + "=?", new String[]{id});
    }

    public long deleteEntryIncome(String id) {
        return ourDatabase.delete(DATABASE_TABLE_INCOME, KEY_ROWID + "=?", new String[]{id});
    }

    public long updateEntryExpense(String rowId, String product, double price, Date dateExpenses) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_PRODUCT, product);
        contentValues.put(KEY_PRICE, price);
        return ourDatabase.update(DATABASE_TABLE_EXPENSE, contentValues, KEY_ROWID + "=?", new String[]{rowId});
    }

//    public long updateEntryIncome(String type, double sum, Date dateIncomes) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(KEY_SUM, sum);
//
//    }


    public ArrayList<Income> getIncomesByDate(String day, String month, String year) {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Income> incomeArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_INCOME + " where " + KEY_DAY_FOR_INCOMES + " =? AND " + KEY_MONTH_FOR_INCOMES + " =? AND "
                + KEY_YEAR_FOR_INCOMES + " =? ", new String[]{day, month, year});
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Double sum = res.getDouble(res.getColumnIndex(KEY_SUM));
            int dayIncome = res.getInt(res.getColumnIndex(KEY_DAY_FOR_INCOMES));
            String monthIncome = res.getString(res.getColumnIndex(KEY_MONTH_FOR_INCOMES));
            int yearIncome = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_INCOMES));
            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY_FOR_INCOME));
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_INCOMES));

            Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
            incomeArray.add(income);
            res.moveToNext();
        }
        return incomeArray;
    }

    public ArrayList<Income> getIncomesByMonthAndYear(String month, String year) {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Income> incomeArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_INCOME + " where " + KEY_MONTH_FOR_INCOMES + " =? AND "
                + KEY_YEAR_FOR_INCOMES + " =? ", new String[]{month, year});
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_INCOMES));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY_FOR_INCOME));
            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            Double sum = res.getDouble(res.getColumnIndex(KEY_SUM));
            int dayIncome = res.getInt(res.getColumnIndex(KEY_DAY_FOR_INCOMES));
            String monthIncome = res.getString(res.getColumnIndex(KEY_MONTH_FOR_INCOMES));
            int yearIncome = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_INCOMES));

            Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
            incomeArray.add(income);
            res.moveToNext();
        }
        return incomeArray;
    }


    public ArrayList<Income> getIncomesByyear(String year) {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Income> incomeArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_INCOME + " where " + KEY_YEAR_FOR_INCOMES + " =? ", new String[]{year});
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Double sum = res.getDouble(res.getColumnIndex(KEY_SUM));
            int dayIncome = res.getInt(res.getColumnIndex(KEY_DAY_FOR_INCOMES));
            String monthIncome = res.getString(res.getColumnIndex(KEY_MONTH_FOR_INCOMES));
            int yearIncome = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_INCOMES));
            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY_FOR_INCOME));
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_INCOMES));


            Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, category, notes);
            incomeArray.add(income);
            res.moveToNext();
        }
        return incomeArray;
    }

    public ArrayList<Expense> getExpensesByDate(String day, String month, String year) {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Expense> expenseArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_EXPENSE + " where " + KEY_DAY_FOR_EXPENSES + " =? AND " + KEY_MONTH_FOR_EXPENSES + " =? AND "
                + KEY_YEAR_FOR_EXPENSES + " =? ", new String[]{day, month, year});
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_EXPENSES));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY));
            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            Double price = res.getDouble(res.getColumnIndex(KEY_PRICE));
            int dayExpense = res.getInt(res.getColumnIndex(KEY_DAY_FOR_EXPENSES));
            String monthExpense = res.getString(res.getColumnIndex(KEY_MONTH_FOR_EXPENSES));
            int yearExpense = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_EXPENSES));

            Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id,notes, category);
            expenseArray.add(expense);
            res.moveToNext();
        }
        return expenseArray;
    }

    public ArrayList<Expense> getExpensesByMonthAndYear(String month, String year) {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Expense> expenseArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_EXPENSE + " where " + KEY_MONTH_FOR_EXPENSES + " =? AND "
                + KEY_YEAR_FOR_EXPENSES + " =? ", new String[]{month, year});
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_EXPENSES));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY));

            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            Double price = res.getDouble(res.getColumnIndex(KEY_PRICE));
            int dayExpense = res.getInt(res.getColumnIndex(KEY_DAY_FOR_EXPENSES));
            String monthExpense = res.getString(res.getColumnIndex(KEY_MONTH_FOR_EXPENSES));
            int yearExpense = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_EXPENSES));

            Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
            expenseArray.add(expense);
            res.moveToNext();
        }
        return expenseArray;
    }

    public ArrayList<Expense> getExpensesByYear(String year) {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Expense> expenseArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_EXPENSE + " where " + KEY_YEAR_FOR_EXPENSES + " =? ", new String[]{year});
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_EXPENSES));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY));
            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            Double price = res.getDouble(res.getColumnIndex(KEY_PRICE));
            int dayExpense = res.getInt(res.getColumnIndex(KEY_DAY_FOR_EXPENSES));
            String monthExpense = res.getString(res.getColumnIndex(KEY_MONTH_FOR_EXPENSES));
            int yearExpense = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_EXPENSES));

            Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
            expenseArray.add(expense);
            res.moveToNext();
        }
        return expenseArray;
    }


    public ArrayList<Income> getAllIncomeValues() throws ParseException {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Income> incomeArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_INCOME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            Double sum = res.getDouble(res.getColumnIndex(KEY_SUM));
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_INCOMES));
            int dayIncome = res.getInt(res.getColumnIndex(KEY_DAY_FOR_INCOMES));
            String monthIncome = res.getString(res.getColumnIndex(KEY_MONTH_FOR_INCOMES));
            int yearIncome = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_INCOMES));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY_FOR_INCOME));


            Income income = new Income(sum, dayIncome, monthIncome, yearIncome, id, notes, category);
            incomeArray.add(income);
            res.moveToNext();
        }
        return incomeArray;
    }

    public ArrayList<Expense> getAllExpenseValues() throws ParseException {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Expense> expenseArray = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_EXPENSE, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String notes = res.getString(res.getColumnIndex(KEY_FOR_NOTES_EXPENSES));
            String category = res.getString(res.getColumnIndex(KEY_FOR_CATEGORY));

            String id = res.getString(res.getColumnIndex(KEY_ROWID));
            Double price = res.getDouble(res.getColumnIndex(KEY_PRICE));
            int dayExpense = res.getInt(res.getColumnIndex(KEY_DAY_FOR_EXPENSES));
            String monthExpense = res.getString(res.getColumnIndex(KEY_MONTH_FOR_EXPENSES));
            int yearExpense = res.getInt(res.getColumnIndex(KEY_YEAR_FOR_EXPENSES));


            Expense expense = new Expense(price, dayExpense, monthExpense, yearExpense, id, notes, category);
            expenseArray.add(expense);
            res.moveToNext();
        }
        return expenseArray;
    }



    public ArrayList<Double> getExpensesValuesByCategory(String category) {
        SQLiteDatabase db = this.ourHelper.getReadableDatabase();
        ArrayList<Double> expenseValues = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_EXPENSE + " where " + KEY_FOR_CATEGORY + " =? ", new String[]{category});
//        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_EXPENSE + " where " + KEY_FOR_CATEGORY + " =? AND "
//                + KEY_YEAR_FOR_EXPENSES + " =? AND " + KEY_MONTH_FOR_EXPENSES +  " =? ", new String[]{month, year});
        res.moveToFirst();
        while (!res.isAfterLast()) {
            double expenseValue = res.getDouble(res.getColumnIndex(KEY_PRICE));
            expenseValues.add(expenseValue);
            res.moveToNext();
        }
        return expenseValues;
    }
}
