package com.example.flashfastfood;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.flashfastfood.Adapter.CartViewHolder;
import com.example.flashfastfood.Adapter.DiscountViewHolder;
import com.example.flashfastfood.Adapter.SpinnerAdapter;
import com.example.flashfastfood.Data.Cart;
import com.example.flashfastfood.Data.Discount;
import com.example.flashfastfood.Data.Order;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.Guest.GuestCheckOutActivity;
import com.example.flashfastfood.Service.FCMSend;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class CheckOutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String saveCurrentDate, saveCurrentTime, orderItemQuantity, orderPrice, orderShippingCharges;

    TextView goback, orderAddress, txtOrderItemQuantity, txtOrderPrice, txtOrderTotalPrice, txtOrderShippingCharges, txtItem,
            txtDiscountName, txtOldPrice, txtFinalPrice;

    RelativeLayout btnPickAddress;

    FirebaseRecyclerOptions<Cart> options;
    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    FirebaseRecyclerOptions<Discount> optionsDiscount;
    FirebaseRecyclerAdapter<Discount, DiscountViewHolder> adapterDiscount;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cartRef, discountRef, discountUsedRef, orderRef;
    String currentUserId, address;
    Cart cart;
    Discount discount;

    RecyclerView rvOrderView, rvDiscount;

    ArrayList<String> arrayList = null;
    ArrayList<String> arrayList2;
    String countItemInCart;
    Order order;

    CardView btnOrder;
    Spinner spinner;
    String[] payMentMethod={"Cash on delivery","Pay with card","Momo","Zalo Pay"};
    int flags[] = {R.drawable.cash, R.drawable.credit_card, R.drawable.momo, R.drawable.zalo};
    int totalPrice2=0, totalPrice1=0;
    String paymentMethodstr;

    String itemIdForCreate;
    String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        txtOrderItemQuantity = findViewById(R.id.orderItemQuantity);
        txtOrderPrice = findViewById(R.id.orderPrice);
        txtOrderTotalPrice = findViewById(R.id.orderTotalPrice);
        txtOrderShippingCharges = findViewById(R.id.orderShippingCharges);
        txtItem = findViewById(R.id.txtItem);

        btnOrder = findViewById(R.id.btnOrder);
        txtDiscountName = findViewById(R.id.txtDiscount);
        txtOldPrice = findViewById(R.id.txtOldPrice);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
        spinner = findViewById(R.id.spinnerPayMethod);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cart = new Cart();
        discount = new Discount();
        order = new Order();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        cartRef = firebaseDatabase.getReference("Shopping Cart");
        discountRef = firebaseDatabase.getReference("Discount");
        discountUsedRef = firebaseDatabase.getReference("Discount Used");
        orderRef = firebaseDatabase.getReference("Order");


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

        txtOrderItemQuantity.setText(orderItemQuantity);
        if (Integer.parseInt(orderItemQuantity) == 1){
            txtItem.setText("Item");
        }

        txtItem.setPaintFlags(txtItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        txtOrderPrice.setText(orderPrice);

        orderShippingCharges = txtOrderShippingCharges.getText().toString();

        totalPrice2 = Integer.parseInt(orderPrice) + Integer.parseInt(orderShippingCharges);
        txtOrderTotalPrice.setText(Integer.toString(totalPrice2));
        txtFinalPrice.setText(Integer.toString(totalPrice2));

        spinner.setOnItemSelectedListener(this);
        SpinnerAdapter spinnerAdapter=new SpinnerAdapter(getApplicationContext(),flags,payMentMethod);
        spinner.setAdapter(spinnerAdapter);

        Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinner.setBackground(spinnerDrawable);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethodstr = payMentMethod[spinner.getSelectedItemPosition()];
                getNewItemKeyForCreate();
                placeOrder(fullName);
            }
        });
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int position,long id) {
        paymentMethodstr = payMentMethod[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void placeOrder(String name){
        if (orderAddress.getText().toString().equals("Pick your deliver address")){
            Toast.makeText(CheckOutActivity.this, "You have to pick deliver address!", Toast.LENGTH_SHORT).show();
            orderAddress.setError("You have to pick deliver address!");
            orderAddress.requestFocus();
        }else if (paymentMethodstr.equals("Pay with card")){
            payWithCard(name);
        }else if (paymentMethodstr.equals("Cash on delivery")){
            payOnCash(name);
        }
        else {
            String text = "Your chosen payment method is not available at the moment";
            Spannable centeredText = new SpannableString(text);
            centeredText.setSpan(
                    new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                    0, text.length() - 1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
            );

            Toast.makeText(CheckOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();

        }
    }

    private void payOnCash(String name){
        order.setOrderDate(saveCurrentDate);
        order.setOrderTime(saveCurrentTime);
        order.setOrderLocation(orderAddress.getText().toString());
        order.setOrderTotalPrice(txtFinalPrice.getText().toString());
        order.setOrderPayment(paymentMethodstr);
        order.setOrderItemQuantity(txtOrderItemQuantity.getText().toString());
        order.setOrderStatus("Processing");

        orderRef.child(currentUserId).child(itemIdForCreate).setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseDatabase.getReference("Admin device token").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String deviceToken = snapshot.child("token").getValue().toString();
                        Dialog dialog = new Dialog(CheckOutActivity.this,R.style.CustomDialog);
                        dialog.setContentView(R.layout.dialog_order_loading);
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                                                      @Override
                                                      public void run() {

                                                          //push notification
                                                          FCMSend.pushNotificationI(
                                                                  CheckOutActivity.this,
                                                                  deviceToken,
                                                                  "New Order",
                                                                  "New order from "+name
                                                          );

                                                          Intent intent = new Intent(CheckOutActivity.this,MainActivity.class);
                                                          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                          int idOrder = 2;
                                                          String IDorder = Integer.toString(idOrder);
                                                          intent.putExtra("Fragment",IDorder);

                                                          //delete cart when create order
                                                          cartRef.child(currentUserId).removeValue();
                                                          dialog.dismiss();
                                                          startActivity(intent);
                                                      }
                                                  }, 5000
                        );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CheckOutActivity.this,"Can't place Order",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void payWithCard(String name){
        Intent intent =new Intent(CheckOutActivity.this, PaymentActivity.class);

        intent.putExtra("orderTime",saveCurrentTime);
        intent.putExtra("orderDate",saveCurrentDate);
        intent.putExtra("orderAddress", orderAddress.getText().toString());
        intent.putExtra("orderPrice",txtFinalPrice.getText().toString());
        intent.putExtra("orderPayment",paymentMethodstr);
        intent.putExtra("orderItemQuantity",txtOrderItemQuantity.getText().toString());
        intent.putExtra("orderStatus","Processing");
        intent.putExtra("orderItem",itemIdForCreate);
        intent.putExtra("username",name);
        startActivity(intent);
    }

    private void getNewItemKeyForCreate() {
        orderRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList2 = new ArrayList<String>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    arrayList2.add(dataSnapshot.getKey());
                }
                //get last itemId in item and create id for new item
                if (arrayList2.size()<1){
                    itemIdForCreate = String.valueOf(1);
                }else {
                    String itemidString = arrayList2.get(arrayList2.size()-1);
                    int itemidInt = Integer.parseInt(itemidString) +1;
                    itemIdForCreate = Integer.toString(itemidInt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        String condition = snapshot.child("condition").getValue().toString();

                        holder.discountName.setText(nameDiscount);
                        holder.discountExpDay.setText(expDayDiscount);
                        holder.discountDes.setText(desDiscount);

                        holder.discountUseCheck(postKey2);
                        holder.btnUseorCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog dialog = new Dialog(CheckOutActivity.this,R.style.CustomDialog);
                                dialog.setContentView(R.layout.dialog_progress);
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                                              @Override
                                                              public void run() {
                                if (Integer.parseInt(condition)<Integer.parseInt(txtOrderTotalPrice.getText().toString())){
                                    if (holder.isInMyDiscountUsed){
                                        discountUsedRef.child(currentUserId).child(postKey2).removeValue();
                                        txtDiscountName.setText("Discount");
                                        txtOldPrice.setText("Total");
                                        txtOldPrice.setForeground(null);
                                        txtFinalPrice.setText(Integer.toString(totalPrice2));
                                    }else {
                                        discount.setDes(desDiscount);
                                        discount.setExpDay(expDayDiscount);
                                        discount.setType(typeDiscount);
                                        discount.setValue(Integer.parseInt(valueDiscount));
                                        discount.setName(nameDiscount);
                                        discount.setCondition(condition);
                                        discountUsedRef.child(currentUserId).removeValue();
                                        discountUsedRef.child(currentUserId).child(postKey2).setValue(discount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                discountUsedRef.child(currentUserId).child(postKey2).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String discountName = snapshot.child("name").getValue().toString();
                                                        txtDiscountName.setText(discountName);
                                                        txtOldPrice.setText(Integer.toString(totalPrice2));
                                                        txtOldPrice.setForeground(getResources().getDrawable(R.drawable.strikethrough_text));

                                                        double totalPrice3 = Double.parseDouble(Integer.toString(totalPrice2));
                                                        String discountType = snapshot.child("type").getValue().toString();
                                                        if (discountType.equals("amount")){
                                                            totalPrice3 = totalPrice3 - Double.parseDouble(snapshot.child("value").getValue().toString());
                                                            int i = (int) totalPrice3;
                                                            txtFinalPrice.setText(Integer.toString(i));
                                                        }else {
                                                            totalPrice3 = totalPrice3 - (totalPrice3*(Double.parseDouble(snapshot.child("value").getValue().toString()))/100);
                                                            txtFinalPrice.setText(Double.toString(totalPrice3));
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
                                            }
                                        });


                                    }
                                }else {
                                    String text = "Your total price is not enough to use this coupon";
                                    Spannable centeredText = new SpannableString(text);
                                    centeredText.setSpan(
                                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                            0, text.length() - 1,
                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                    );
                                    Toast.makeText(CheckOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                                              }
                                                          }, 2000
                                );

                            }
                        });
                        if (holder.isInMyDiscountUsed){
                            discountUsedRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        String discountCondition = dataSnapshot.child("condition").getValue().toString();
                                        String discountName = dataSnapshot.child("name").getValue().toString();

                                        if (Integer.parseInt(txtOrderTotalPrice.getText().toString())<Integer.parseInt(discountCondition)){
                                            discountUsedRef.child(currentUserId).child(postKey2).removeValue();
                                            txtDiscountName.setText("Discount");
                                            txtOldPrice.setText("Total");
                                            txtOldPrice.setForeground(null);
                                            txtFinalPrice.setText(Integer.toString(totalPrice2));
                                        }else {
                                            txtDiscountName.setText(discountName);
                                            txtOldPrice.setText(Integer.toString(totalPrice2));
                                            txtOldPrice.setForeground(getResources().getDrawable(R.drawable.strikethrough_text));

                                            double totalPrice3 = Double.parseDouble(Integer.toString(totalPrice2));
                                            String discountType = dataSnapshot.child("type").getValue().toString();
                                            if (discountType.equals("amount")){
                                                totalPrice3 = totalPrice3 - Double.parseDouble(dataSnapshot.child("value").getValue().toString());
                                                int i = (int) totalPrice3;
                                                txtFinalPrice.setText(Integer.toString(i));
                                            }else {
                                                totalPrice3 = totalPrice3 - (totalPrice3*(Double.parseDouble(dataSnapshot.child("value").getValue().toString()))/100);
                                                txtFinalPrice.setText(Double.toString(totalPrice3));
                                            }
                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else {
                            txtDiscountName.setText("Discount");
                            txtOldPrice.setText("Total");
                            txtOldPrice.setForeground(null);
                            txtFinalPrice.setText(Integer.toString(totalPrice2));
                        }

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
                                    totalPrice1 = updatecartprice + Integer.parseInt(orderShippingCharges);
                                    txtOrderTotalPrice.setText(Integer.toString(totalPrice1));
                                    txtFinalPrice.setText(Integer.toString(totalPrice1));
                                    discountUsedRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                    String discountCondition = dataSnapshot.child("condition").getValue().toString();
                                                    String discountName = dataSnapshot.child("name").getValue().toString();

                                                    if (Integer.parseInt(txtOrderTotalPrice.getText().toString())<Integer.parseInt(discountCondition)){
                                                        discountUsedRef.child(currentUserId).removeValue();
                                                        txtDiscountName.setText("Discount");
                                                        txtOldPrice.setText("Total");
                                                        txtOldPrice.setForeground(null);
                                                        txtFinalPrice.setText(Integer.toString(totalPrice1));
                                                    }else {
                                                        txtDiscountName.setText(discountName);
                                                        txtOldPrice.setText(Integer.toString(totalPrice1));
                                                        txtOldPrice.setForeground(getResources().getDrawable(R.drawable.strikethrough_text));

                                                        double totalPrice3 = Double.parseDouble(Integer.toString(totalPrice1));
                                                        String discountType = dataSnapshot.child("type").getValue().toString();
                                                        if (discountType.equals("amount")){
                                                            totalPrice3 = totalPrice3 - Double.parseDouble(dataSnapshot.child("value").getValue().toString());
                                                            int i = (int) totalPrice3;
                                                            txtFinalPrice.setText(Integer.toString(i));
                                                        }else {
                                                            totalPrice3 = totalPrice3 - (totalPrice3*(Double.parseDouble(dataSnapshot.child("value").getValue().toString()))/100);
                                                            txtFinalPrice.setText(Double.toString(totalPrice3));
                                                        }
                                                    }


                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
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

    @Override
    protected void onStart() {
        super.onStart();
        loadCartView();
        getCartQuantity();
        getDiscountView();
        getNewItemKeyForCreate();
        firebaseDatabase.getReference("Registered Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = snapshot.child("FullName").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCartView();
        getCartQuantity();
        getDiscountView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartView();
        getCartQuantity();
        getDiscountView();
    }


}