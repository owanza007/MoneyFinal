package com.second.moneymanager;

import android.os.Parcel;
import android.os.Parcelable;


public class Income implements Parcelable {
    private double sum;
    private int dayIncome;
    private String monthIncome;
    private int yearIncome;
    private String id;
    private String notes;
    private String category;

    public Income(double sum, int dayIncome, String monthIncome, int yearIncome, String id, String category, String notes) {
        this.sum = sum;
        this.dayIncome = dayIncome;
        this.monthIncome = monthIncome;
        this.yearIncome = yearIncome;
        this.id = id;
        this.notes = notes;
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Income(double sum, String monthIncome, int yearIncome, String id) {
        this.sum = sum;
        this.monthIncome = monthIncome;
        this.yearIncome = yearIncome;
        this.id = id;
    }


    public Income(double sum,  int yearIncome, String id) {
        this.sum = sum;
        this.yearIncome = yearIncome;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    protected Income(Parcel in) {
        sum = in.readDouble();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(sum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Income> CREATOR = new Creator<Income>() {
        @Override
        public Income createFromParcel(Parcel in) {
            return new Income(in);
        }

        @Override
        public Income[] newArray(int size) {
            return new Income[size];
        }
    };

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }




    @Override
    public String toString() {
        return "Income{" +
                "sum=" + sum +
                ", dayIncome=" + dayIncome +
                ", monthIncome=" + monthIncome +
                ", yearIncome=" + yearIncome +
                '}';
    }

    public int getDayIncome() {
        return dayIncome;
    }

    public void setDayIncome(int dayIncome) {
        this.dayIncome = dayIncome;
    }

    public String getMonthIncome() {
        return monthIncome;
    }

    public void setMonthIncome(String monthIncome) {
        this.monthIncome = monthIncome;
    }

    public int getYearIncome() {
        return yearIncome;
    }

    public void setYearIncome(int yearIncome) {
        this.yearIncome = yearIncome;
    }


}
