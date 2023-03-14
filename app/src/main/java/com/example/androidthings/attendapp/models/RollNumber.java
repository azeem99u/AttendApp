package com.example.androidthings.attendapp.models;

import java.util.Objects;

public class RollNumber {

    private String rollNumber;
    private String attendance;

    public RollNumber(String rollNumber, String attendance) {
        this.rollNumber = rollNumber;
        this.attendance = attendance;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RollNumber that = (RollNumber) o;
        return Objects.equals(rollNumber, that.rollNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rollNumber);
    }
}
