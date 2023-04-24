package com.example.flashfastfood.Guest;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.flashfastfood.Adapter.CartViewHolder;
import com.example.flashfastfood.CheckOutActivity;
import com.example.flashfastfood.Data.Cart;
import com.example.flashfastfood.Data.CartGuest;
import com.example.flashfastfood.Data.CartItemGuest;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BottomSheetCartGuestFragment extends BottomSheetDialogFragment{

    TextView btnCancel, deleteAllCart;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cartRef;
    String currentUserId;
    FirebaseRecyclerOptions<Cart> options;
    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    Cart cart;

    RecyclerView rvCartView;

    int fullprice;
    ArrayList<String> arrayList = null;

    String callbackkey;
    LottieAnimationView whenNoCart;
    TextView txtNoCart, cartQuantity, cartTotalPrice, txtQuantity;

    RelativeLayout btnCheckout;

    private BottomSheetListener mListener;

    public BottomSheetCartGuestFragment() {
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
        return inflater.inflate(R.layout.fragment_bottom_sheet_cart_guest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callbackkey = getArguments().getString("callbackkey");

        whenNoCart = view.findViewById(R.id.whenNoCart);
        whenNoCart.setVisibility(View.GONE);
        txtNoCart = view.findViewById(R.id.txt);
        btnCheckout = view.findViewById(R.id.btnCheckOut);
        cartQuantity = view.findViewById(R.id.cartQuantity);
        cartTotalPrice = view.findViewById(R.id.cartTotalPrice);
        txtQuantity = view.findViewById(R.id.txtQuantity);
        deleteAllCart = view.findViewById(R.id.deleteAllCart);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rvCartView = view.findViewById(R.id.rvCartView);
        rvCartView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        CartGuest cartGuest = getCart();
        List<CartItemGuest> cartItemGuestList = new ArrayList<>(cartGuest.getItems().values());
        CartAdapter cartAdapter = new CartAdapter(cartItemGuestList,getContext(),this);
        rvCartView.setAdapter(cartAdapter);
        getCartTotal();

        deleteAllCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("Cart", MODE_PRIVATE);
                String cartJson = sharedPreferences.getString("CartItems", "{}");
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
                Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

                cartItems.clear();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("CartItems", gson.toJson(cartItems));
                editor.apply();
                cartItemGuestList.clear();
                cartAdapter.notifyDataSetChanged();
                getCartTotal();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GuestCheckOutActivity.class);
                intent.putExtra("orderPrice", cartTotalPrice.getText().toString());
                intent.putExtra("orderItemQuantity", cartQuantity.getText().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_bottom,R.anim.slide_to_top);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getCart();
        getCartTotal();
    }

    @Override
    public void onResume() {
        super.onResume();
        CartGuest cartGuest = getCart();
        List<CartItemGuest> cartItemGuestList = new ArrayList<>(cartGuest.getItems().values());
        CartAdapter cartAdapter = new CartAdapter(cartItemGuestList,getContext(),this);
        rvCartView.setAdapter(cartAdapter);
        getCart();
        getCartTotal();
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        mListener = listener;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDismiss(getContext());
        }
    }

    private CartGuest getCart() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

        CartGuest cart = new CartGuest();
        cart.getItems().putAll(cartItems);

        return cart;
    }

    public void getCartTotal(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);
        if (cartItems.size()>0){
            int totalPrice=0;
            int totalQuantity = 0;
            for (String key : cartItems.keySet()){
                CartItemGuest cartItemGuest = cartItems.get(key);
                String price = cartItemGuest.getItemCartTotalPrice();
                String quantity = cartItemGuest.getItemCartQuantity();
                totalPrice += Integer.parseInt(price);
                totalQuantity += Integer.parseInt(quantity);
            }
            cartQuantity.setText(String.valueOf(totalQuantity));
            cartTotalPrice.setText(String.valueOf(totalPrice));

            deleteAllCart.setVisibility(View.VISIBLE);
            whenNoCart.setVisibility(View.GONE);
            txtNoCart.setText("Your Cart");
            btnCheckout.setVisibility(View.VISIBLE);
        }else {
            deleteAllCart.setVisibility(View.GONE);
            whenNoCart.setVisibility(View.VISIBLE);
            txtNoCart.setText("Your cart is empty");
            btnCheckout.setVisibility(View.GONE);
        }
    }
}