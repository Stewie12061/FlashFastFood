package com.example.flashfastfood.Guest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flashfastfood.Adapter.ViewPagerAdapter;
import com.example.flashfastfood.FragmentChildOfOrder.DeliveryFragment;
import com.example.flashfastfood.FragmentChildOfOrder.HistoryFragment;
import com.example.flashfastfood.FragmentChildOfOrder.PreparedFragment;
import com.example.flashfastfood.R;
import com.google.android.material.tabs.TabLayout;

public class OrderGuestFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;


    public OrderGuestFragment() {
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
        return inflater.inflate(R.layout.fragment_order_guest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new PreparedFragment(), "Preparing Your Order");
        viewPagerAdapter.addFragment(new DeliveryFragment(), "Delivering Your Order");
        viewPagerAdapter.addFragment(new HistoryFragment(), "Your Order History");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}