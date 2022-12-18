package com.example.flashfastfood;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.flashfastfood.Adapter.CartViewHolder;
import com.example.flashfastfood.Adapter.DiscountViewHolder;
import com.example.flashfastfood.Data.Cart;
import com.example.flashfastfood.Data.Discount;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;


public class CheckOutActivity extends AppCompatActivity {
    String saveCurrentDate, saveCurrentTime, orderItemQuantity, orderPrice, orderShippingCharges, orderTotalPrice;

    TextView goback, orderAddress, txtOrderItemQuantity, txtOrderPrice, txtOrderTotalPrice, txtOrderShippingCharges, txtItem;

    RelativeLayout btnPickAddress;

    FirebaseRecyclerOptions<Cart> options;
    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    FirebaseRecyclerOptions<Discount> optionsDiscount;
    FirebaseRecyclerAdapter<Discount, DiscountViewHolder> adapterDiscount;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cartRef, discountRef, discountUsedRef;
    String currentUserId, address;
    Cart cart;
    Discount discount;

    RecyclerView rvOrderView, rvDiscount;

    ArrayList<String> arrayList = null;
    String countItemInCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cart = new Cart();
        discount = new Discount();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        cartRef = firebaseDatabase.getReference("Shopping Cart");
        discountRef = firebaseDatabase.getReference("Discount");
        discountUsedRef = firebaseDatabase.getReference("Discount Used");


        rvOrderView = findViewById(R.id.rvOrderItem);
        rvOrderView.setLayoutManager(new LinearLayoutManager(CheckOutActivity.this,LinearLayoutManager.VERTICAL,false));

        rvDiscount = findViewById(R.id.rvDiscount);
        rvDiscount.setLayoutManager(new LinearLayoutManager(CheckOutActivity.this,LinearLayoutManager.HORIZONTAL,false));

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        saveCurrentDate = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm a");
        saveCurrentTime = simpleTimeFormat.format(calendar.getTime());


        orderItemQuantity = getIntent().getStringExtra("orderItemQuantity");
        orderPrice = getIntent().getStringExtra("orderPrice");


        orderAddress = findViewById(R.id.orderAddress);
        address = getIntent().getStringExtra("orderAddress");
        if (address != null){
            orderAddress.setText(address);
        }
//        if (orderAddress.getText().toString().equals("Pick you deliver address")){
//            Toast.makeText(CheckOutActivity.this, "You have to pick deliver address!", Toast.LENGTH_SHORT).show();
//            orderAddress.setError("You have to pick deliver address!");
//            orderAddress.requestFocus();
//        }
        btnPickAddress = findViewById(R.id.relativeAddress);
        btnPickAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutActivity.this,PickAddressActivity.class);
                intent.putExtra("orderItemQuantity",orderItemQuantity);
                intent.putExtra("orderPrice",orderPrice);
                startActivity(intent);
            }
        });


        txtOrderItemQuantity = findViewById(R.id.orderItemQuantity);
        txtOrderPrice = findViewById(R.id.orderPrice);
        txtOrderTotalPrice = findViewById(R.id.orderTotalPrice);
        txtOrderShippingCharges = findViewById(R.id.orderShippingCharges);
        txtItem = findViewById(R.id.txtItem);

        txtOrderItemQuantity.setText(orderItemQuantity);
        if (Integer.parseInt(orderItemQuantity) == 1){
            txtItem.setText("Item");
        }

        txtOrderPrice.setText(orderPrice);
        orderShippingCharges = txtOrderShippingCharges.getText().toString();

        int totalPrice = Integer.parseInt(orderPrice) + Integer.parseInt(orderShippingCharges);
        txtOrderTotalPrice.setText(Integer.toString(totalPrice));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCartView();
        getCartQuantity();
        getDiscountView();
    }

    private void getDiscountView() {
        Query query = discountRef;

        optionsDiscount =new FirebaseRecyclerOptions.Builder<Discount>().setQuery(query,Discount.class).build();

        adapterDiscount = new FirebaseRecyclerAdapter<Discount, DiscountViewHolder>(optionsDiscount) {
            @Override
            protected void onBindViewHolder(@NonNull DiscountViewHolder holder, int position, @NonNull Discount model) {
                String postKey2 = getRef(position).getKey();

                discountRef.child(postKey2).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nameDiscount = snapshot.child("name").getValue().toString();
                        String desDiscount = snapshot.child("des").getValue().toString();
                        String expDayDiscount = snapshot.child("expDay").getValue().toString();
                        String valueDiscount = snapshot.child("value").getValue().toString();
                        String typeDiscount = snapshot.child("type").getValue().toString();

                        holder.discountName.setText(nameDiscount);
                        holder.discountExpDay.setText(expDayDiscount);
                        holder.discountDes.setText(desDiscount);

                        holder.discountUseCheck(postKey2);
                        holder.btnUseorCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (holder.isInMyDiscountUsed){
                                    discountUsedRef.child(currentUserId).child(postKey2).removeValue();
                                }else {
                                    discount.setDes(desDiscount);
                                    discount.setExpDay(expDayDiscount);
                                    discount.setType(typeDiscount);
                                    discount.setValue(Integer.parseInt(valueDiscount));
                                    discount.setName(nameDiscount);
                                    discountUsedRef.child(currentUserId).removeValue();
                                    discountUsedRef.child(currentUserId).child(postKey2).setValue(discount);
                                }
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
            public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount, parent,false);
                DiscountViewHolder viewHolder = new DiscountViewHolder(view);
                return viewHolder;
            }
        };

        rvDiscount.setAdapter(adapterDiscount);
        adapterDiscount.startListening();
    }

    private void loadCartView() {

        Query query = cartRef.child(currentUserId);

        options =new FirebaseRecyclerOptions.Builder<Cart>().setQuery(query,Cart.class).build();

        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                String postKey = getRef(position).getKey();

                cartRef.child(currentUserId).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("itemCartName").getValue().toString();
                        String img = snapshot.child("itemCartImg").getValue().toString();
                        String priceTotal = snapshot.child("itemCartTotalPrice").getValue().toString();
                        String quantity = snapshot.child("itemCartQuantity").getValue().toString();

                        holder.ItemCartname.setText(name);
                        holder.ItemCartPriceTotal.setText(priceTotal);
                        holder.ItemCartQuantity.setText(quantity);
                        Picasso.get().load(img).into(holder.ItemCartimg);

                        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getCartQuantity();

                                int quantity = 0;
                                quantity = Integer.parseInt(txtOrderItemQuantity.getText().toString())-1;
                                if (quantity!=0){
                                    txtOrderItemQuantity.setText(Integer.toString(quantity));
                                }
                                if (quantity==1){
                                    txtItem.setText("Item");
                                }

                                if ((Integer.parseInt(countItemInCart))<2){
                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder
                                                    (CheckOutActivity.this);
                                    View view = LayoutInflater.from(CheckOutActivity.this).inflate(
                                            R.layout.dialog_alert,
                                            (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
                                    );
                                    builder.setView(view);
                                    ((TextView) view.findViewById(R.id.textTitle))
                                            .setText("Delete your cart");
                                    ((TextView) view.findViewById(R.id.textMessage))
                                            .setText("Do you want to delete last item in cart?");
                                    ((Button) view.findViewById(R.id.buttonYes))
                                            .setText("Yes");
                                    ((Button) view.findViewById(R.id.buttonNo))
                                            .setText("No");
                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            cartRef.child(currentUserId).removeValue();
                                            onBackPressed();
                                            alertDialog.dismiss();
                                        }
                                    });
                                    view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    if (alertDialog.getWindow() != null){
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                    }
                                }
                                else {
                                    cartRef.child(currentUserId).child(postKey).removeValue();
                                    getCartQuantity();

                                    int updatecartprice = 0;
                                    updatecartprice = Integer.parseInt(txtOrderPrice.getText().toString()) - Integer.parseInt(priceTotal);
                                    txtOrderPrice.setText(Integer.toString(updatecartprice));
                                    int totalPrice = updatecartprice + Integer.parseInt(orderShippingCharges);
                                    txtOrderTotalPrice.setText(Integer.toString(totalPrice));

                                }
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
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_view, parent,false);
                CartViewHolder viewHolder = new CartViewHolder(view);
                return viewHolder;
            }
        };

        rvOrderView.setAdapter(adapter);
        adapter.startListening();

    }

    private void getCartQuantity() {
        cartRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                countItemInCart = Integer.toString(arrayList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_to_bottom);
    }


}