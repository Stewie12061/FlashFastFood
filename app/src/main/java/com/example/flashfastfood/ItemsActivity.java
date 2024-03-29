package com.example.flashfastfood;

import static a.a.a.a.b.c.i;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Adapter.ItemsViewHolder;
import com.example.flashfastfood.Data.CartItemGuest;
import com.example.flashfastfood.Data.Items;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.Guest.BottomSheetCartGuestFragment;
import com.example.flashfastfood.Guest.BottomSheetListener;
import com.example.flashfastfood.Guest.MainGuestActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ItemsActivity extends AppCompatActivity implements BottomSheetListener {

    Toolbar toolbar;
    TextView goback;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemsRef, cateRef, cartRef;
    FirebaseRecyclerOptions<Items> options;
    FirebaseRecyclerAdapter<Items, ItemsViewHolder> adapter;

    Items items;

    RecyclerView rvAllItems;

    String idCategory,currentUserId;
    ArrayList<String> arrayList = null;
    ArrayList<String> arrayList2 = null;

    String guestFlag;
    CounterFab btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        toolbar = findViewById(R.id.toolbar1);
        goback = toolbar.findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        items = new Items();

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemsRef = firebaseDatabase.getReference("Items");
        cateRef = firebaseDatabase.getReference("Categories");
        cartRef = firebaseDatabase.getReference("Shopping Cart");

        guestFlag = getIntent().getStringExtra("guestFlag");
        if (guestFlag==null){
            guestFlag="user";
        }
        if (!guestFlag.equals("guest")){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            currentUserId = user.getUid();
        }

        rvAllItems = findViewById(R.id.rvShowAllItem);
        rvAllItems.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        idCategory = getIntent().getStringExtra("IdCategory");

        btnCart = findViewById(R.id.itemFabCart);

        if (!guestFlag.equals("guest")){
            btnCart.setVisibility(View.GONE);
            btnCart.setImageResource(R.drawable.ic_cart);
            getCartQuantity();
            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    BottomSheetCartFragment bottomSheetFragment = new BottomSheetCartFragment();
                    bottomSheetFragment.setArguments(bundle);
                    bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());
                }
            });
        }
        else {
            btnCart.setVisibility(View.VISIBLE);
            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("guestFlag","guest");
                    BottomSheetCartGuestFragment bottomSheetFragment = new BottomSheetCartGuestFragment();
                    bottomSheetFragment.setBottomSheetListener(ItemsActivity.this);
                    bottomSheetFragment.setArguments(bundle);
                    bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getItemStatus();
        if (!guestFlag.equals("guest")){
            getCartQuantity();
        }
        else {
            getCartGuestQuantity();
        }
        if (idCategory != null) {
            loadCateItems();
        }else {
            loadAllItems();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getItemStatus();
        if (!guestFlag.equals("guest")){
            getCartQuantity();
        }
        else {
            getCartGuestQuantity();
        }
        if (idCategory != null) {
            loadCateItems();
        }else {
            loadAllItems();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getItemStatus();
        if (!guestFlag.equals("guest")){
            getCartQuantity();
        }
        else {
            getCartGuestQuantity();
        }
        if (idCategory != null) {
            loadCateItems();
        }else {
            loadAllItems();
        }
    }


    public void getCartQuantity(){
        cartRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                String countItemInCart= Integer.toString(arrayList.size());
                int itemInCart = Integer.parseInt(countItemInCart);
                if (itemInCart==0){
                    btnCart.setVisibility(View.GONE);
                }
                else {
                    btnCart.setVisibility(View.VISIBLE);
                    btnCart.setCount(itemInCart);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getCartGuestQuantity() {
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

    public void getItemStatus(){
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList2 = new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if (dataSnapshot.child("itemStatus").getValue().toString().equals("Unavailable")){
                        arrayList2.add(dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCateItems(){
        Query query = itemsRef.orderByChild("cateId").equalTo(idCategory);

        options =new FirebaseRecyclerOptions.Builder<Items>().setQuery(query,Items.class).build();

        adapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Items model) {
                String postKey = getRef(position).getKey();
                getItemStatus();
                if (arrayList2.contains(postKey)){
                    holder.itemView.setVisibility(View.INVISIBLE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
                itemsRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        getItemStatus();
                        String name = snapshot.child("itemName").getValue().toString();
                        String rating = snapshot.child("itemRating").getValue().toString();
                        String price = snapshot.child("itemPrice").getValue().toString();
                        String img = snapshot.child("itemImg").getValue().toString();
                        String cateId = snapshot.child("cateId").getValue().toString();

                        holder.itemname.setText(name);
                        holder.itemprice.setText(price);
                        holder.itemrating.setText(rating);
                        Picasso.get().load(img).into(holder.itemimg);

                        cateRef.child(cateId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.itemcatename.setText(snapshot.child("foodCateName").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(ItemsActivity.this, ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                if (guestFlag.equals("guest")){
                                    intent.putExtra("guestFlag","guest");
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_items, parent,false);
                ItemsViewHolder viewHolder = new ItemsViewHolder(view);
                return viewHolder;
            }
        };

        rvAllItems.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadAllItems(){
        options =new FirebaseRecyclerOptions.Builder<Items>().setQuery(itemsRef,Items.class).build();

        adapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Items model) {
                String postKey = getRef(position).getKey();
                getItemStatus();
                if (arrayList2.contains(postKey)){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                itemsRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        getItemStatus();
                        String name = snapshot.child("itemName").getValue().toString();
                        String rating = snapshot.child("itemRating").getValue().toString();
                        String price = snapshot.child("itemPrice").getValue().toString();
                        String img = snapshot.child("itemImg").getValue().toString();
                        String cateId = snapshot.child("cateId").getValue().toString();

                        holder.itemname.setText(name);
                        holder.itemprice.setText(price);
                        holder.itemrating.setText(rating);
                        Picasso.get().load(img).into(holder.itemimg);

                        cateRef.child(cateId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.itemcatename.setText(snapshot.child("foodCateName").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(ItemsActivity.this, ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                if (guestFlag.equals("guest")){
                                    intent.putExtra("guestFlag","guest");
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_items, parent,false);
                ItemsViewHolder viewHolder = new ItemsViewHolder(view);
                return viewHolder;
            }
        };

        rvAllItems.setAdapter(adapter);
        adapter.startListening();

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