package com.example.javaapp;

public class Record {
    String audience;
    String teacher;
    String  date;
    String time;

    public Record(String audience, String teacher, String date, String time) {
        this.audience = audience;
        this.teacher = teacher;
        this.date = date;
        this.time = time;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
