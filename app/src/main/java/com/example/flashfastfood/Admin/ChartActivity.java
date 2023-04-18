package com.example.flashfastfood.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.flashfastfood.Data.Order;
import com.example.flashfastfood.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {
    private HorizontalBarChart chart;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = findViewById(R.id.chart);

        int year = Integer.parseInt(getIntent().getStringExtra("year"));
        int month = Integer.parseInt(getIntent().getStringExtra("month"));
        int day = Integer.parseInt(getIntent().getStringExtra("day"));
        fetchData(year,month,day);

        textView = findViewById(R.id.text);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fetchData(int year, int month, int day) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Order");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Float> dailyTotalOrders = new HashMap<>(); // Stores total order price for each day
                List<String> dayList = new ArrayList<>(); // Stores list of days

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.MONTH, month); // Replace with your own month
                int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshotChild : orderSnapshot.getChildren()){
                        if (dataSnapshotChild.child("orderStatus").getValue().toString().equals("Successful")){
                            String dateTime = dataSnapshotChild.child("orderDate").getValue().toString();
                            String getMonth = dateTime.split("-")[0];
                            String getYear = dateTime.split("-")[2];
                            String getDay = dateTime.split("-")[1];
                            String moneyString = dataSnapshotChild.child("orderTotalPrice").getValue().toString();

                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.MONTH, month-1);
                            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
                            textView.setText("Statistics from "+ day+" to "+lastDayOfMonth+" of "+monthName+","+year);

                            // Add orderPrice to dailyTotalOrders map
                            if (dailyTotalOrders.containsKey(getDay) && getYear.equals(String.valueOf(year)) && getMonth.equals(String.valueOf(month))) {
                                dailyTotalOrders.put(getDay, dailyTotalOrders.get(Float.parseFloat(getDay))+Float.parseFloat(moneyString));
                            }else if (getYear.equals(String.valueOf(year)) && getMonth.equals(String.valueOf(month))){
                                dailyTotalOrders.put(getDay, Float.parseFloat(moneyString));
                            }

                        }

                    }
                }
                // Create chart data
                List<BarEntry> entries = new ArrayList<>();
                int i = 0;
                for (int dayFor = day; dayFor <= lastDayOfMonth; dayFor++) {
                    String dayStr = String.format("%02d", dayFor);
                    float totalOrders = dailyTotalOrders.getOrDefault(dayStr, 0f);
                    entries.add(new BarEntry(i++, totalOrders));
                    dayList.add(dayStr);
                }

                // Create chart
                BarDataSet dataSet = new BarDataSet(entries, "Total Order Price per Day");
                dataSet.setColor(Color.parseColor("#E25822"));
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(14f);
                dataSet.notifyDataSetChanged();
                BarData barData = new BarData(dataSet);
                chart.setData(barData);

                chart.setDrawBarShadow(false);
                chart.setDrawValueAboveBar(true);
                chart.getDescription().setEnabled(false);
                // if more than 60 entries are displayed in the chart, no values will be
                // drawn
                chart.setMaxVisibleValueCount(60);
                // scaling can now only be done on x- and y-axis separately
                chart.setPinchZoom(false);
                // draw shadows for each bar that show the maximum value
                // chart.setDrawBarShadow(true);
                chart.setDrawGridBackground(false);
                chart.setFitBars(true);

                // Set axis labels
                XAxis xAxis = chart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dayList));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(dayList.size());

                YAxis yl = chart.getAxisLeft();
                yl.setDrawAxisLine(true);
                yl.setDrawGridLines(true);
                yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                YAxis yr = chart.getAxisRight();
                yr.setDrawAxisLine(true);
                yr.setDrawGridLines(false);
                yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                chart.animateY(2500);
                chart.setData(barData);
                chart.notifyDataSetChanged();
                chart.invalidate();

                Legend l = chart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(false);
                l.setFormSize(8f);
                l.setXEntrySpace(2f);
                l.setTextSize(16l);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "fetchData: onCancelled", error.toException());
            }
        });
    }

}