package com.example.flashfastfood.AdminFragment;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Bar;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.HoverMode;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.enums.TooltipPositionMode;
import com.example.flashfastfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChartAdminFragment extends Fragment{

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef, orderRef;
    FirebaseAuth firebaseAuth;
    String userId, adminId;
    double jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec=0;
    ArrayList<Double> lstJan,lstFeb,lstMar,lstApr,lstMay,lstJun,lstJul,lstAug,lstSep,lstOct,lstNov,lstDec;
    List<DataEntry> data = new ArrayList<>();
    Cartesian vertical;
    Set set;
    AnyChartView anyChartView;

    public ChartAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered Users");
        orderRef = firebaseDatabase.getReference("Order");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        adminId = user.getUid();

        anyChartView = view.findViewById(R.id.horizontalChart);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        vertical = AnyChart.vertical();

        vertical.animation(true)
                .title("Revenue statistics in 2022").title().fontColor("#E25822").fontSize(20).fontWeight(700);
        ResetDate();
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                            if (getYear.equals("2022")){
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
                data.add(new CustomDataEntry("Jan", jan));
                data.add(new CustomDataEntry("Feb", feb));
                data.add(new CustomDataEntry("Mar", mar));
                data.add(new CustomDataEntry("Apr", apr));
                data.add(new CustomDataEntry("May", may));
                data.add(new CustomDataEntry("Jun", jun));
                data.add(new CustomDataEntry("Jul", jul));
                data.add(new CustomDataEntry("Aug", aug));
                data.add(new CustomDataEntry("Sep", sep));
                data.add(new CustomDataEntry("Oct", oct));
                data.add(new CustomDataEntry("Nov", nov));
                data.add(new CustomDataEntry("Dec", dec));

                set = Set.instantiate();
                set.data(data);
                Mapping barData = set.mapAs("{ x: 'x', value: 'value' }");
                Bar bar = vertical.bar(barData);
                bar.fill("#FFFFFF");
                bar.stroke("#FFFFFF");

                bar.labels().format("${%Value}").fontColor("#000000").fontSize(15).fontWeight(700);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        vertical.yScale().minimum(0d);

        vertical.labels(true);

        vertical.tooltip().fontColor("#FFFFFF").fontSize(16).fontWeight(500)
                .format("Total: ${%value}")
                .displayMode(TooltipDisplayMode.SINGLE)
                .positionMode(TooltipPositionMode.POINT)
                .unionFormat();

        vertical.interactivity().hoverMode(HoverMode.BY_X);
        vertical.xAxis(true);
        vertical.yAxis(true);
        vertical.yAxis(0).labels().format("${%Value}").fontColor("#E25822").fontSize(14).fontWeight(500);
        vertical.xAxis(0).labels().fontColor("#E25822").fontSize(14).fontWeight(500);

        vertical.yGrid(0).fill("#E25822");
        vertical.yGrid(0).stroke("#90E25822");
        anyChartView.setChart(vertical);
        anyChartView.invalidate();

    }
    private class CustomDataEntry extends ValueDataEntry {
        public CustomDataEntry(String x, Double value) {
            super(x, value);
        }
    }
    void ResetDate(){
        data = new ArrayList<>();
        data.add(new CustomDataEntry(null,null));
        set = Set.instantiate();
        set.data(data);
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

}