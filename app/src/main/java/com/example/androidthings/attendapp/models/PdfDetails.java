package com.example.androidthings.attendapp.models;

import java.util.ArrayList;

public class PdfDetails {

    private String teacherName;
    private String subject;
    private String duration;
    private String date;
    private String batchName;

    public PdfDetails(String teacherName, String batchName, String subject, String duration, String date) {
        this.teacherName = teacherName;
        this.batchName = batchName;
        this.subject = subject;
        this.duration = duration;
        this.date = date;

    }
    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }


    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
