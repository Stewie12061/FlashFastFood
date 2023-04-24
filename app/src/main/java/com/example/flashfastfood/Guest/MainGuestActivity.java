package com.example.flashfastfood.Guest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Data.CartItemGuest;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.Fragment.FavoriteFragment;
import com.example.flashfastfood.Fragment.HomeFragment;
import com.example.flashfastfood.Fragment.OrderFragment;
import com.example.flashfastfood.Fragment.ProfileFragment;
import com.example.flashfastfood.ItemDetailActivity;
import com.example.flashfastfood.ItemsActivity;
import com.example.flashfastfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class MainGuestActivity extends AppCompatActivity implements BottomSheetListener {
    private ChipNavigationBar chipNavigationBarGuest;
    private String guestFlag = null;

    private Fragment fragmentGuest = null;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private CounterFab btnCart;

    private String idIntent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_guest);

        idIntent = getIntent().getStringExtra("Fragment");

        guestFlag = getIntent().getStringExtra("guestFlag");
        if (guestFlag == null) {
            guestFlag = "user";
        }

        chipNavigationBarGuest = findViewById(R.id.chipNavBarGuest);

        if (idIntent==null){
            chipNavigationBarGuest.setItemSelected(R.id.mnuHome,true);
        }else {
            int ID = Integer.parseInt(idIntent);
            if (ID==2){
                chipNavigationBarGuest.setItemSelected(R.id.mnuOrderGuest,true);
                fragmentGuest = new OrderGuestFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentGuest, fragmentGuest);
                fragmentTransaction.commit();
                idIntent = null;
            }else if (ID==1){
                chipNavigationBarGuest.setItemSelected(R.id.mnuHome,true);
                fragmentGuest = new HomeFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentGuest, fragmentGuest);
                fragmentTransaction.commit();
                idIntent = null;
            }

        }
        if (fragmentGuest == null) {
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
                bundle.putString("guestFlag", "guest");
                BottomSheetCartGuestFragment bottomSheetFragment2 = new BottomSheetCartGuestFragment();
                bottomSheetFragment2.setBottomSheetListener(MainGuestActivity.this);
                bottomSheetFragment2.setArguments(bundle);
                bottomSheetFragment2.show(getSupportFragmentManager(), bottomSheetFragment2.getTag());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartQuantity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartQuantity();
    }

    public void getCartQuantity() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>() {
        }.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);
        int totalItem= 0;
        for (String key : cartItems.keySet()) {
            totalItem++;
        }
        btnCart.setCount(totalItem);
    }

    @Override
    public void onDismiss(Context context) {
        if (context instanceof ItemDetailActivity) {
            ((ItemDetailActivity) context).getCartGuestQuantity();
        } else if (context instanceof MainGuestActivity) {
            ((MainGuestActivity) context).getCartQuantity();
        }else if (context instanceof ItemsActivity) {
            ((ItemsActivity) context).getCartGuestQuantity();
        }
    }
}