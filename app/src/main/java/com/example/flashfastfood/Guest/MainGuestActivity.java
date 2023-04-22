package com.example.flashfastfood.Guest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.Fragment.FavoriteFragment;
import com.example.flashfastfood.Fragment.HomeFragment;
import com.example.flashfastfood.Fragment.OrderFragment;
import com.example.flashfastfood.Fragment.ProfileFragment;
import com.example.flashfastfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainGuestActivity extends AppCompatActivity {
    private ChipNavigationBar chipNavigationBarGuest;
    private String guestFlag=null;

    private Fragment fragmentGuest = null;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private CounterFab btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_guest);

        guestFlag = getIntent().getStringExtra("guestFlag");
        if (guestFlag==null){
            guestFlag="user";
        }

        chipNavigationBarGuest = findViewById(R.id.chipNavBarGuest);
        chipNavigationBarGuest.setItemSelected(R.id.mnuHomeGuest,true);
        if (fragmentGuest == null){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contentGuest, new HomeFragment());
            fragmentTransaction.commit();
        }
        chipNavigationBarGuest.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.mnuHomeGuest:
                        fragmentGuest = new HomeFragment();
                        break;
                    case R.id.mnuOrderGuest:
                        fragmentGuest = new OrderGuestFragment();
                        break;
                    case R.id.mnuProfileGuest:
                        fragmentGuest = new GuestProfileFragment();
                        break;
                }
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentGuest, fragmentGuest);
                fragmentTransaction.commit();
            }

        });




        btnCart = findViewById(R.id.fabCartGuest);

        getCartQuantity();
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("guestFlag","guest");
                BottomSheetCartGuestFragment bottomSheetFragment2 = new BottomSheetCartGuestFragment();
                bottomSheetFragment2.setArguments(bundle);
                bottomSheetFragment2.show(getSupportFragmentManager(),bottomSheetFragment2.getTag());
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartQuantity();
    }

    private void getCartQuantity(){

    }

}