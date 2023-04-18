package com.example.flashfastfood.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.flashfastfood.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChartAfterYearActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef, orderRef;
    FirebaseAuth firebaseAuth;
    String userId, adminId;
    double jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec=0;
    ArrayList<Double> lstJan,lstFeb,lstMar,lstApr,lstMay,lstJun,lstJul,lstAug,lstSep,lstOct,lstNov,lstDec;

    Spinner spinnerChart;
    String strYear;

    private HorizontalBarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_after_year);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered Users");
        orderRef = firebaseDatabase.getReference("Order");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        adminId = user.getUid();

        chart = findViewById(R.id.horizontalChart);
        spinnerChart = findViewById(R.id.spinnerChart);
        spinnerChart.setOnItemSelectedListener(this);
        List<String> years = new ArrayList<String>();
        years.add("2022");
        years.add("2023");
        years.add("2024");
        years.add("2025");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.items_custom_spinner_chart, years);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChart.setAdapter(dataAdapter);

        /*String year = getIntent().getStringExtra("year");
        strYear = year;*/
        ResetDate();
        strYear = spinnerChart.getSelectedItem().toString();
    }

    private void getChartData(){
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ResetDate();
                String month = null;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                        if (dataSnapshot2.child("orderStatus").getValue().toString().equals("Successful")){
                            month = dataSnapshot2.child("orderDate").getValue().toString();
                            String getMonth = month.split("-")[0];
                            String getYear = month.split("-")[2];
                            String moneyString = dataSnapshot2.child("orderTotalPrice").getValue().toString();
                            Double moneyDouble = Double.parseDouble(moneyString);

                            if (getYear.equals(strYear)){
                                switch (getMonth){
                                    case "01":
                                        lstJan.add(moneyDouble);
                                        break;
                                    case "02":
                                        lstFeb.add(moneyDouble);
                                        break;
                                    case "03":
                                        lstMar.add(moneyDouble);
                                        break;
                                    case "04":
                                        lstApr.add(moneyDouble);
                                        break;
                                    case "05":
                                        lstMay.add(moneyDouble);
                                        break;
                                    case "06":
                                        lstJun.add(moneyDouble);
                                        break;
                                    case "07":
                                        lstJul.add(moneyDouble);
                                        break;
                                    case "08":
                                        lstAug.add(moneyDouble);
                                        break;
                                    case "09":
                                        lstSep.add(moneyDouble);
                                        break;
                                    case "10":
                                        lstOct.add(moneyDouble);
                                        break;
                                    case "11":
                                        lstNov.add(moneyDouble);
                                        break;
                                    case "12":
                                        lstDec.add(moneyDouble);
                                        break;
                                }
                            }
                        }
                    }
                }
                for (int i=0; i<lstDec.size();i++){
                    dec = dec + lstDec.get(i);
                }
                for (int i=0; i<lstJan.size();i++){
                    jan = jan + lstJan.get(i);
                }
                for (int i=0; i<lstFeb.size();i++){
                    feb = feb + lstFeb.get(i);
                }
                for (int i=0; i<lstApr.size();i++){
                    apr = apr + lstApr.get(i);
                }
                for (int i=0; i<lstMar.size();i++){
                    mar = mar + lstMar.get(i);
                }
                for (int i=0; i<lstMay.size();i++){
                    may = may + lstMay.get(i);
                }
                for (int i=0; i<lstJul.size();i++){
                    jul = jul + lstJul.get(i);
                }
                for (int i=0; i<lstJun.size();i++){
                    jun = jun + lstJun.get(i);
                }
                for (int i=0; i<lstAug.size();i++){
                    aug = aug + lstAug.get(i);
                }
                for (int i=0; i<lstSep.size();i++){
                    sep = sep + lstSep.get(i);
                }
                for (int i=0; i<lstOct.size();i++){
                    oct = oct + lstOct.get(i);
                }
                for (int i=0; i<lstNov.size();i++){
                    nov = nov + lstNov.get(i);
                }
                if (jan==0.0 && feb==0.0 && mar==0.0 && apr == 0.0 && may ==0.0
                        && jun == 0.0 && jul == 0.0 && aug == 0.0 && sep == 0.0 && dec == 0.0
                        && nov == 0.0 && oct == 0.0){
                    Toast.makeText(getApplicationContext(), "No Data Yet", Toast.LENGTH_SHORT).show();
                }

                BarDataSet barDataSet = new BarDataSet(dataChart(),"Profit");
                barDataSet.setColor(Color.parseColor("#E25822"));
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(14f);
                barDataSet.notifyDataSetChanged();

                BarData barData = new BarData();
                barData.addDataSet(barDataSet);

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

                XAxis xl = chart.getXAxis();
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setDrawAxisLine(true);
                xl.setDrawGridLines(false);
                xl.setGranularity(1f);

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

            }
        });
    }

    private ArrayList<BarEntry> dataChart(){
        ArrayList<BarEntry> dataChartValue = new ArrayList<>();
        dataChartValue.add(new BarEntry(1, (float) jan));
        dataChartValue.add(new BarEntry(2, (float) feb));
        dataChartValue.add(new BarEntry(3, (float) mar));
        dataChartValue.add(new BarEntry(4, (float) apr));
        dataChartValue.add(new BarEntry(5, (float) may));
        dataChartValue.add(new BarEntry(6, (float) jun));
        dataChartValue.add(new BarEntry(7, (float) jul));
        dataChartValue.add(new BarEntry(8, (float) aug));
        dataChartValue.add(new BarEntry(9, (float) sep));
        dataChartValue.add(new BarEntry(10, (float) oct));
        dataChartValue.add(new BarEntry(11, (float) nov));
        dataChartValue.add(new BarEntry(12, (float) dec));

        return  dataChartValue;
    }
    void ResetDate(){
        lstDec = new ArrayList<>();
        lstNov = new ArrayList<>();
        lstOct = new ArrayList<>();
        lstSep = new ArrayList<>();
        lstApr = new ArrayList<>();
        lstJul = new ArrayList<>();
        lstJun = new ArrayList<>();
        lstMay = new ArrayList<>();
        lstAug = new ArrayList<>();
        lstMar = new ArrayList<>();
        lstFeb = new ArrayList<>();
        lstJan = new ArrayList<>();
        jan=0;feb=0;mar=0;apr=0;may=0;jun=0;jul=0;aug=0;sep=0;oct=0;nov=0;dec=0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strYear = parent.getItemAtPosition(position).toString();
        getChartData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}