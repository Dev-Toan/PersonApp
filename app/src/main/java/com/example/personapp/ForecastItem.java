package com.example.personapp;

public class ForecastItem {
    private String dayOfWeek;
    private int iconResource;
    private int minTemp;
    private int maxTemp;
    private double rainfall; // mm

    public ForecastItem(String dayOfWeek, int iconResource, int minTemp, int maxTemp, double rainfall) {
        this.dayOfWeek = dayOfWeek;
        this.iconResource = iconResource;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.rainfall = rainfall;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getIconResource() {
        return iconResource;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public double getRainfall() {
        return rainfall;
    }
}