package com.example.flashfastfood.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.flashfastfood.Adapter.ViewPagerAdapter;
import com.example.flashfastfood.AdminFragment.CompleteOrderFragment;
import com.example.flashfastfood.AdminFragment.ConfirmOrderFragment;
import com.example.flashfastfood.FragmentChildOfOrder.DeliveryFragment;
import com.example.flashfastfood.FragmentChildOfOrder.HistoryFragment;
import com.example.flashfastfood.FragmentChildOfOrder.PreparedFragment;
import com.example.flashfastfood.R;
import com.google.android.material.tabs.TabLayout;

public class ConfirmOrderActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPagerOrderAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        tabLayout = findViewById(R.id.tabLayoutOrderAd);
        viewPagerOrderAd = findViewById(R.id.viewPagerOrderAd);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ConfirmOrderFragment(),"Confirm User Order");
        viewPagerAdapter.addFragment(new CompleteOrderFragment(),"Complete User Order");
        viewPagerOrderAd.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPagerOrderAd);
    }
}