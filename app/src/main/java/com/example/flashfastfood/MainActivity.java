package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.andremion.counterfab.CounterFab;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.Fragment.FavoriteFragment;
import com.example.flashfastfood.Fragment.HomeFragment;
import com.example.flashfastfood.Fragment.NotificationFragment;
import com.example.flashfastfood.Fragment.ProfileFragment;
import com.example.flashfastfood.Fragment.OrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    String idIntent = null, currentUserId,countItemInCart;

    ChipNavigationBar chipNavigationBar;
    Fragment fragment = null;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    CounterFab btnCart;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cartRef;
    ArrayList<String> arrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        cartRef = firebaseDatabase.getReference("Shopping Cart");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        idIntent = getIntent().getStringExtra("Fragment");

        chipNavigationBar = findViewById(R.id.chipNavBar);
        chipNavigationBar.setItemSelected(R.id.mnuHome,true);

        if (fragment == null){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, new HomeFragment());
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
                bundle.putString("callbackkey", "0");
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

    public void getCartQuantity(){
        cartRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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