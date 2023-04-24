package com.example.flashfastfood.Guest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.Adapter.CartViewHolder;
import com.example.flashfastfood.Adapter.DiscountViewHolder;
import com.example.flashfastfood.Adapter.SpinnerAdapter;
import com.example.flashfastfood.CheckOutActivity;
import com.example.flashfastfood.Data.Cart;
import com.example.flashfastfood.Data.CartGuest;
import com.example.flashfastfood.Data.CartItemGuest;
import com.example.flashfastfood.Data.Discount;
import com.example.flashfastfood.Data.Order;
import com.example.flashfastfood.MainActivity;
import com.example.flashfastfood.PaymentActivity;
import com.example.flashfastfood.PickAddressActivity;
import com.example.flashfastfood.R;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuestCheckOutActivity extends AppCompatActivity {
    String saveCurrentDate, saveCurrentTime, orderItemQuantity, orderPrice, orderShippingCharges;

    TextView goback, orderAddress, txtOrderItemQuantity, txtOrderPrice, txtOrderTotalPrice, txtOrderShippingCharges, txtItem,
            txtDiscountName, txtOldPrice, txtFinalPrice;
    EditText edtfullname, edtphonenumber;
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
    String[] payMentMethod={"Cash on delivery","Pay with card","Momo","Zalo Pay"};
    int flags[] = {R.drawable.cash, R.drawable.credit_card, R.drawable.momo, R.drawable.zalo};
    int totalPrice2=0, totalPrice1=0;
    String paymentMethodstr;

    String itemIdForCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_check_out);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("userId", "");

        txtOrderItemQuantity = findViewById(R.id.orderItemQuantity);
        txtOrderPrice = findViewById(R.id.orderPrice);
        txtOrderTotalPrice = findViewById(R.id.orderTotalPrice);
        txtOrderShippingCharges = findViewById(R.id.orderShippingCharges);
        txtItem = findViewById(R.id.txtItem);
        edtfullname = findViewById(R.id.edtFullname);
        edtphonenumber = findViewById(R.id.edtPhoneNumber);

        btnOrder = findViewById(R.id.btnOrder);
        txtOldPrice = findViewById(R.id.txtOldPrice);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
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

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        orderRef = firebaseDatabase.getReference("Order");

        rvOrderView = findViewById(R.id.rvOrderItem);
        rvOrderView.setLayoutManager(new LinearLayoutManager(GuestCheckOutActivity.this,LinearLayoutManager.VERTICAL,false));
        CartGuest cartGuest = loadCartGuestView();
        List<CartItemGuest> cartItemGuestList = new ArrayList<>(cartGuest.getItems().values());
        OrderCartAdapter orderCartAdapter = new OrderCartAdapter(cartItemGuestList,getApplicationContext(),GuestCheckOutActivity.this);
        rvOrderView.setAdapter(orderCartAdapter);

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
                Intent intent = new Intent(GuestCheckOutActivity.this, PickAddressActivity.class);
                intent.putExtra("orderItemQuantity",orderItemQuantity);
                intent.putExtra("orderPrice",orderPrice);
                intent.putExtra("guestFlag","guest");
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

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethodstr = "Cash on delivery";
                getNewItemKeyForCreate();
                placeOrder();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Cart", MODE_PRIVATE);
                String cartJson = sharedPreferences.getString("CartItems", "{}");
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
                Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

                cartItems.clear();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("CartItems", gson.toJson(cartItems));
                editor.apply();
            }
        });
    }

    private void placeOrder(){
        String fullname = edtfullname.getText().toString();
        String phonenumber = edtphonenumber.getText().toString();
        //Validate Mobile Number using Matcher and Patter
        String mobileRegex = "(0[3|5|7|8|9])+([0-9]{8})";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(phonenumber);

        if (orderAddress.getText().toString().equals("Pick your deliver address")){
            Toast.makeText(GuestCheckOutActivity.this, "You have to pick deliver address!", Toast.LENGTH_SHORT).show();
            orderAddress.setError("You have to pick deliver address!");
            orderAddress.requestFocus();
        }else if (TextUtils.isEmpty(fullname)){
            edtfullname.setError("You have to fill this information!");
            edtfullname.requestFocus();
        }else if (TextUtils.isEmpty(phonenumber)){
            edtphonenumber.setError("You have to fill this information!");
            edtphonenumber.requestFocus();
        }else if (phonenumber.length() != 10){
            edtphonenumber.setError("Mobile number should be 10 digits!");
            edtphonenumber.requestFocus();
        }else if (!mobileMatcher.find()){
            edtphonenumber.setError("Mobile is not valid!");
            edtphonenumber.requestFocus();
        }else{
            payOnCash();
        }
    }

    private void payOnCash(){
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
                DatabaseReference userRef = firebaseDatabase.getReference("Registered Users");
                Map<String,String> guestInfo = new HashMap<>();
                guestInfo.put("FullName", edtfullname.getText().toString());
                guestInfo.put("PhoneNumber",edtphonenumber.getText().toString());
                guestInfo.put("Email","No info");
                guestInfo.put("Role","User");
                userRef.child(currentUserId).setValue(guestInfo);

                Dialog dialog = new Dialog(GuestCheckOutActivity.this,R.style.CustomDialog);
                dialog.setContentView(R.layout.dialog_order_loading);
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          Intent intent = new Intent(GuestCheckOutActivity.this, MainGuestActivity.class);
                          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          int idOrder = 2;
                          String IDorder = Integer.toString(idOrder);
                          intent.putExtra("Fragment",IDorder);

                          dialog.dismiss();
                          startActivity(intent);
                      }
                  }, 5000
                );
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GuestCheckOutActivity.this,"Can't place Order",Toast.LENGTH_SHORT).show();
            }
        });
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
    private CartGuest loadCartGuestView(){
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

        CartGuest cart = new CartGuest();
        cart.getItems().putAll(cartItems);

        return cart;
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
        countItemInCart = String.valueOf(totalItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_to_bottom);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartQuantity();
        loadCartGuestView();
        getNewItemKeyForCreate();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCartGuestView();
        getCartQuantity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartGuestView();
        getCartQuantity();
    }

    public void updateCartSummary(int totalItem, int totalPrice) {
        // Update total item count
        txtOrderItemQuantity.setText(String.valueOf(totalItem));
        // Update total price
        txtOrderPrice.setText(String.valueOf(totalPrice));

        totalPrice2 = totalPrice + Integer.parseInt(orderShippingCharges);
        txtOrderTotalPrice.setText(Integer.toString(totalPrice2));
        txtFinalPrice.setText(Integer.toString(totalPrice2));
    }
}