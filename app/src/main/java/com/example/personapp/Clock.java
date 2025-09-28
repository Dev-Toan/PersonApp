package com.example.personapp;

import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;

public class Clock implements Serializable {
    String name;
    Time time;
    boolean isEnabled;
    boolean isRingTheBell;  // rung
    String sound; // tên nhạc
    String repeatDays; // Chuỗi các ngày lặp lại, ví dụ: "2 3 4 5 6 7 cn"


    public Clock() {
        // Constructor mặc định
    }



    public Clock(String name, Time time, boolean isEnabled, String repeatDays, boolean isRingTheBell, String sound) {
        this.name = name;
        this.time = time;
        this.isEnabled = isEnabled;
        this.repeatDays = repeatDays; // Mặc định lặp lại từ thứ 2 đến chủ nhật
        this.isRingTheBell = isRingTheBell;
        this.sound = sound;
    }


    public boolean isRingTheBell() {
        return isRingTheBell;
    }

    public void setRingTheBell(boolean ringTheBell) {
        isRingTheBell = ringTheBell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

}