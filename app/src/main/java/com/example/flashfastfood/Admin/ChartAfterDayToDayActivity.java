package com.example.flashfastfood.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChartAfterDayToDayActivity extends AppCompatActivity {

    private HorizontalBarChart chart;
    private TextView textView;
    String dateStart, dateEnd, dateStartQuery, dateEndQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_after_day_to_day);

        chart = findViewById(R.id.chart);

        dateStart = getIntent().getStringExtra("start");
        dateEnd = getIntent().getStringExtra("end");

        String getStarMonth = dateStart.split("-")[1];
        String getStarYear = dateStart.split("-")[2];
        String getStarDay = dateStart.split("-")[0];

        dateStartQuery = getIntent().getStringExtra("startQ");
        dateEndQuery = getIntent().getStringExtra("endQ");

        fetchData(Integer.parseInt(getStarYear),Integer.parseInt(getStarMonth),Integer.parseInt(getStarDay));

        textView = findViewById(R.id.text);
    }

    private void fetchData(int year, int month, int day) {
        Date startDate;
        Date endDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        try {
            startDate = dateFormat.parse(dateStartQuery);
            endDate = dateFormat.parse(dateEndQuery);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        DatabaseReference ordersRef = FirebaseDatabase
                .getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Order");
        Query query = ordersRef.orderByChild("orderDate");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Double> dailyTotalPrizes = new HashMap<>();
                final List<String> daysWithValues = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        if (orderSnapshot.child("orderStatus").getValue().toString().equals("Successful")) {
                            String orderDateStr = orderSnapshot.child("orderDate").getValue(String.class);
                            String getMonth = orderDateStr.split("-")[0];
                            String getYear = orderDateStr.split("-")[2];
                            String getDay = orderDateStr.split("-")[1];
                            String orderTotalPrice = orderSnapshot.child("orderTotalPrice").getValue().toString();

                            // Parse the orderDate string into a Date object
                            Date orderDate = null;
                            try {
                                orderDate = dateFormat.parse(orderDateStr);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            if (orderDate.compareTo(startDate) >= 0 && orderDate.compareTo(endDate) <= 0) {
                                textView.setText("Statistics from " + dateStart + " to " + dateEnd);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(orderDate);
                                String dayKey = dateFormat.format(cal.getTime());

                                // Add the orderTotalPrice to the dailyTotalPrizes HashMap
                                if (dailyTotalPrizes.containsKey(dayKey)) {
                                    double currentTotal = dailyTotalPrizes.get(dayKey);
                                    dailyTotalPrizes.put(dayKey, currentTotal + Double.parseDouble(orderTotalPrice));
                                } else {
                                    dailyTotalPrizes.put(dayKey, Double.parseDouble(orderTotalPrice));
                                    daysWithValues.add(dayKey);
                                }
                            }

                        }

                    }
                }

                // Create a List of BarEntry objects to hold the chart data
                List<BarEntry> entries = new ArrayList<>();
                // Loop through the dailyTotalPrizes HashMap and add each entry to the List
                int i = 0;
                for (Map.Entry<String, Double> entry : dailyTotalPrizes.entrySet()) {
                    String dayKey = entry.getKey();
                    double totalPrize = entry.getValue();

                    // Create a BarEntry object with the day and total prize values
                    entries.add(new BarEntry(i, new float[]{(float)totalPrize}));
                    i++;
                }

                DateFormat dateMonthFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());
                DateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                Date startDate, endDate;
                try {
                    startDate = dateMonthFormat.parse(dateStart);
                    endDate = dateMonthFormat.parse(dateEnd);
                    calendar.setTime(startDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                // Create List of Dates
                ArrayList<String> dates = new ArrayList<>();
                while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
                    String dateMonthString = dateMonthFormat.format(calendar.getTime());
                    dates.add(dateMonthString);
                    calendar.add(Calendar.DATE, 1);
                }

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
                xAxis.setValueFormatter(new IndexAxisValueFormatter(daysWithValues));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(daysWithValues.size());
//                String[] xLabels = new String[daysWithValues.size()];
//                for (int i2 = 0; i < daysWithValues.size(); i2++) {
//                    xLabels[i2] = daysWithValues.get(i2);
//                }

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
                chart.setFitBars(true);

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