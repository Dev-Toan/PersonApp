package com.example.personapp;

import java.io.Serializable;
import java.sql.Time;
import java.time.Month;
import java.time.Year;
import java.util.Date;

public class Event implements Serializable {
    String tieude;
    String noidung;
    Date ngay;

    Time startTime;
    Time endTime;
    boolean activity = true;
    boolean status = true;
    boolean isExpanded = false;
    boolean isNotification;




    public Event(Time endTime, Date ngay, String noidung, Time startTime, boolean status, String tieude, boolean activity, boolean isNotification) {
        this.endTime = endTime;
        this.ngay = ngay;
        this.noidung = noidung;
        this.startTime = startTime;
        this.status = status;
        this.tieude = tieude;
        this.activity = activity;
        this.isNotification = isNotification;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Date getNgay() {
        return ngay;
    }

    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }


    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTieude() {
        return tieude;
    }

    public void setTieude(String tieude) {
        this.tieude = tieude;
    }

    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }


    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }
}