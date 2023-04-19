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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
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
    String dateStart, dateEnd;

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


        fetchData(Integer.parseInt(getStarYear),Integer.parseInt(getStarMonth),Integer.parseInt(getStarDay));

        textView = findViewById(R.id.text);
    }

    private void fetchData(int year, int month, int day) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Order");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Float> dailyTotalOrders = new HashMap<>(); // Stores total order price for each day

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
                            textView.setText("Statistics from "+ dateStart+" to "+dateEnd);

                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.set(year, month - 1, day); // Month is zero-based, so subtract 1

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
                            String monthString = dateFormat.format(calendar2.getTime());
                            // Add orderPrice to dailyTotalOrders map
                            if (dailyTotalOrders.containsKey(getDay) && getYear.equals(String.valueOf(year)) && getMonth.equals(monthString)) {
                                dailyTotalOrders.put(getDay, dailyTotalOrders.get(Float.parseFloat(getDay))+Float.parseFloat(moneyString));
                            }else if (getYear.equals(String.valueOf(year)) && getMonth.equals(monthString)){
                                dailyTotalOrders.put(getDay, Float.parseFloat(moneyString));
                            }

                        }

                    }
                }
                int differenceDates=0;
                try {
                    SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");
                    Date dateStartd = dates.parse(dateStart);
                    Date dateEndd = dates.parse(dateEnd);
                    long dateStartInMs = dateStartd.getTime();
                    long dateEndInMs = dateEndd.getTime();
                    long difference = dateEndInMs - dateStartInMs;
                    differenceDates = (int) (difference / (24 * 60 * 60 * 1000));
                }catch (Exception e) {
                    e.printStackTrace();
                }
                // Create chart data
                List<BarEntry> entries = new ArrayList<>();
                int i = 0;
                for (int dayFor = day; dayFor <= day+differenceDates; dayFor++) {
                    String dayStr = String.format("%02d", dayFor);
                    float totalOrders = dailyTotalOrders.getOrDefault(dayStr, 0f);
                    entries.add(new BarEntry(i++, totalOrders));
                }

                DateFormat dateFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                Date startDate, endDate;
                try {
                    startDate = dateFormat.parse(dateStart);
                    endDate = dateFormat.parse(dateEnd);
                    calendar.setTime(startDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                // Create List of Dates
                ArrayList<String> dates = new ArrayList<>();
                while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
                    String dateString = dateFormat.format(calendar.getTime());
                    dates.add(dateString);
                    calendar.add(Calendar.DATE, 1);
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
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(dates.size());

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