package com.second.moneymanager;

import java.time.LocalDate;

public class Expense {

    private String id;
    private double price;
    private LocalDate date;
    private int dayExpense;
    private String monthExpense;
    private String notes;
    private String category;
    private int yearExpense;

    public Expense( double price, int dayExpense, String monthExpense, int yearExpense, String id, String notes, String category) {
        this.price = price;
        this.id = id;
        this.dayExpense = dayExpense;
        this.monthExpense = monthExpense;
        this.yearExpense = yearExpense;
        this.notes = notes;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", price=" + price +
                ", date=" + date +
                ", dayExpense=" + dayExpense +
                ", monthExpense='" + monthExpense + '\'' +
                ", notes='" + notes + '\'' +
                ", category='" + category + '\'' +
                ", yearExpense=" + yearExpense +
                '}';
    }

    public int getDayExpense() {
        return dayExpense;
    }

    public void setDayExpense(int dayExpense) {
        this.dayExpense = dayExpense;
    }

    public String getMonthExpense() {
        return monthExpense;
    }

    public void setMonthExpense(String monthExpense) {
        this.monthExpense = monthExpense;
    }

    public int getYearExpense() {
        return yearExpense;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setYearExpense(int yearExpense) {
        this.yearExpense = yearExpense;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Expense( double price, String monthExpense, int yearExpense, String id) {
        this.price = price;
        this.id = id;
        this.monthExpense = monthExpense;
        this.yearExpense = yearExpense;
    }

    public Expense(String product, double price, int yearExpense, String id) {
        this.price = price;
        this.id = id;
        this.yearExpense = yearExpense;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
