package com.example.flashfastfood.AdminFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.flashfastfood.Admin.ChartActivity;
import com.example.flashfastfood.R;

public class ChartDayFragment extends Fragment {

    private DatePicker datePicker;
    private Button showChartButton;
    public ChartDayFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        datePicker = view.findViewById(R.id.datePicker);
        showChartButton = view.findViewById(R.id.showChartButton);
        showChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1; // month is 0-indexed
                int day = datePicker.getDayOfMonth();
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                intent.putExtra("year", String.valueOf(year));
                intent.putExtra("month", String.valueOf(month));
                intent.putExtra("day",String.valueOf(day));
                startActivity(intent);
            }
        });
    }
}