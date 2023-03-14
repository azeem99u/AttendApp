package com.example.androidthings.attendapp;

import android.text.InputFilter;
import android.text.Spanned;

class MinMaxFilter implements InputFilter {

    private int intMin;
    private int intMax;


    public MinMaxFilter(int intMin, int intMax) {
        this.intMin = intMin;
        this.intMax = intMax;

    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {

        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(intMin, intMax, input)) {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}