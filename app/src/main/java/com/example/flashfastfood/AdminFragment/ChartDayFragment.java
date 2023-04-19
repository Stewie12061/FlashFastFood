package com.example.flashfastfood.AdminFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.flashfastfood.Admin.ChartAfterDayToDayActivity;
import com.example.flashfastfood.Admin.ChartAfterDayToEndMonthActivity;
import com.example.flashfastfood.Admin.ChartAfterYearActivity;
import com.example.flashfastfood.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChartDayFragment extends Fragment {

    private DatePicker datePicker;
    private Button showChartButton, showChartYearButton;
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
                Intent intent = new Intent(getActivity(), ChartAfterDayToEndMonthActivity.class);
                intent.putExtra("year", String.valueOf(year));
                intent.putExtra("month", String.valueOf(month));
                intent.putExtra("day",String.valueOf(day));
                startActivity(intent);
            }
        });

        showChartYearButton = view.findViewById(R.id.showChartYearButton);
        showChartYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1; // month is 0-indexed
                int day = datePicker.getDayOfMonth();
                Intent intent = new Intent(getActivity(), ChartAfterYearActivity.class);
                intent.putExtra("year",String.valueOf(year));
                startActivity(intent);
            }
        });


        Button btnDateRangePicker = view.findViewById(R.id.btn_date_range_picker);
        btnDateRangePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<androidx.core.util.Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new androidx.core.util.Pair<>(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                )).build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        String dateStart = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                        String dateEnd = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));

                        Intent intent = new Intent(getActivity(), ChartAfterDayToDayActivity.class);
                        intent.putExtra("start",dateStart);
                        intent.putExtra("end",dateEnd);
                        startActivity(intent);
                    }
                });

                materialDatePicker.show(getActivity().getSupportFragmentManager(),"tag");
            }
        });
    }
}