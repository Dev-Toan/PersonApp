package com.example.personapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RainfallChartView extends View {

    private List<Double> rainfallData = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private Paint barPaint;
    private Paint textPaint;
    private Paint gridPaint;

    public RainfallChartView(Context context) {
        super(context);
        init();
    }

    public RainfallChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#90CAF9"));
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#4A658A"));
        gridPaint.setStrokeWidth(2f);
        gridPaint.setAntiAlias(true);
    }

    public void setData(List<Double> rainfall, List<String> dayLabels) {
        this.rainfallData = rainfall;
        this.labels = dayLabels;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (rainfallData.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int bottomPadding = 60;

        // Tìm giá trị max để scale
        double maxRainfall = 0;
        for (double val : rainfallData) {
            if (val > maxRainfall) maxRainfall = val;
        }
        if (maxRainfall == 0) maxRainfall = 1;

        int chartHeight = height - padding - bottomPadding;
        int barWidth = (width - 2 * padding) / rainfallData.size();
        int actualBarWidth = (int) (barWidth * 0.7);

        // Vẽ các thanh
        for (int i = 0; i < rainfallData.size(); i++) {
            double value = rainfallData.get(i);
            int barHeight = (int) ((value / maxRainfall) * chartHeight);

            int left = padding + i * barWidth + (barWidth - actualBarWidth) / 2;
            int top = height - bottomPadding - barHeight;
            int right = left + actualBarWidth;
            int bottom = height - bottomPadding;

            // Vẽ thanh cột
            canvas.drawRect(left, top, right, bottom, barPaint);

            // Vẽ giá trị trên thanh
            if (value > 0) {
                String valueText = String.format("%.1f", value);
                canvas.drawText(valueText, left + actualBarWidth / 2f, top - 10, textPaint);
            }

            // Vẽ label ngày
            if (i < labels.size()) {
                canvas.drawText(labels.get(i), left + actualBarWidth / 2f,
                        height - bottomPadding + 40, textPaint);
            }
        }

        // Vẽ đường baseline
        canvas.drawLine(padding, height - bottomPadding,
                width - padding, height - bottomPadding, gridPaint);
    }
}