package com.example.flashfastfood;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.Fragment.FavoriteFragment;
import com.example.flashfastfood.Fragment.HomeFragment;
import com.example.flashfastfood.Fragment.OrderFragment;
import com.example.flashfastfood.Fragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String idIntent = null, currentUserId,countItemInCart;

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment = null;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private CounterFab btnCart;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cartRef;
    ArrayList<String> arrayList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        cartRef = firebaseDatabase.getReference("Shopping Cart");

        idIntent = getIntent().getStringExtra("Fragment");

        chipNavigationBar = findViewById(R.id.chipNavBarUser);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        if (idIntent==null){
            chipNavigationBar.setItemSelected(R.id.mnuHome,true);
        }else {
            int ID = Integer.parseInt(idIntent);
            if (ID==2){
                chipNavigationBar.setItemSelected(R.id.mnuOrder,true);
                fragment = new OrderFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.commit();
                idIntent = null;
            }else if (ID==1){
                chipNavigationBar.setItemSelected(R.id.mnuHome,true);
                fragment = new HomeFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.commit();
                idIntent = null;
            }

        }


        if (fragment == null){
            fragment = new HomeFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, fragment);
            fragmentTransaction.commit();
        }
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.mnuHome:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mnuOrder:
                        fragment = new OrderFragment();
                        break;
                    case R.id.mnuFavorite:
                        fragment = new FavoriteFragment();
                        break;
                    case R.id.mnuProfile:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.commit();
            }

        });

        btnCart = findViewById(R.id.fabCart);

        getCartQuantity();
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("guestFlag","user");
                BottomSheetCartFragment bottomSheetFragment = new BottomSheetCartFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());
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

    private void getCartQuantity(){
        cartRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        arrayList.add(dataSnapshot.getKey());
                    }
                    countItemInCart= Integer.toString(arrayList.size());
                    int itemInCart = Integer.parseInt(countItemInCart);
                    btnCart.setCount(itemInCart);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}