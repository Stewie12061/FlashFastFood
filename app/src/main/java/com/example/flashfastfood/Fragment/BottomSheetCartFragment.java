package com.example.flashfastfood.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.flashfastfood.Adapter.CartViewHolder;
import com.example.flashfastfood.CheckOutActivity;
import com.example.flashfastfood.Data.Cart;
import com.example.flashfastfood.ItemDetailActivity;
import com.example.flashfastfood.ItemsActivity;
import com.example.flashfastfood.MainActivity;
import com.example.flashfastfood.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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


public class BottomSheetCartFragment extends BottomSheetDialogFragment {

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

    public BottomSheetCartFragment() {
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
        return inflater.inflate(R.layout.fragment_bottom_sheet_cart, container, false);
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

        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cart = new Cart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        cartRef = firebaseDatabase.getReference("Shopping Cart");

        rvCartView = view.findViewById(R.id.rvCartView);
        rvCartView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        deleteAllCart = view.findViewById(R.id.deleteAllCart);
        deleteAllCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder
                                (getContext());
                View view = LayoutInflater.from(getContext()).inflate(
                        R.layout.dialog_alert,
                        (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
                );
                builder.setView(view);
                ((TextView) view.findViewById(R.id.textTitle))
                        .setText("Delete All Item In Your Cart");
                ((TextView) view.findViewById(R.id.textMessage))
                        .setText("Do you want to delete all item in cart?");
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
                        getCartQuantity();
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
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CheckOutActivity.class);
                intent.putExtra("orderPrice", cartTotalPrice.getText().toString());
                intent.putExtra("orderItemQuantity", cartQuantity.getText().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_bottom,R.anim.slide_to_top);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        getCartQuantity();
        if (txtNoCart.getText().toString().equals("Your cart is empty")){
            dismiss();
        }else {
            loadCartView();
            getCartTotalPrice();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCartView();
        getCartQuantity();
        getCartTotalPrice();
    }

    private void getCartQuantity() {
        cartRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                String countItemInCart= Integer.toString(arrayList.size());
                int itemInCart = Integer.parseInt(countItemInCart);

                cartQuantity.setText(Integer.toString(itemInCart));
                if (itemInCart>1){
                    txtQuantity.setText("Items");
                }

                if (itemInCart==0){
                    deleteAllCart.setVisibility(View.GONE);
                    whenNoCart.setVisibility(View.VISIBLE);
                    txtNoCart.setText("Your cart is empty");
                    btnCheckout.setVisibility(View.GONE);

                }
                else {
                    deleteAllCart.setVisibility(View.VISIBLE);
                    whenNoCart.setVisibility(View.GONE);
                    txtNoCart.setText("Your Cart");
                    btnCheckout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCartTotalPrice(){
        cartRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String priceTotal2 = null;
                int totalCartPrice = 0;
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    priceTotal2 = dataSnapshot.child("itemCartTotalPrice").getValue().toString();
                    totalCartPrice = totalCartPrice + Integer.parseInt(priceTotal2);

                }
                if (priceTotal2 != null){
                    cartTotalPrice.setText(Integer.toString(totalCartPrice));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        String price = snapshot.child("itemCartPrice").getValue().toString();
                        String img = snapshot.child("itemCartImg").getValue().toString();
                        String priceTotal = snapshot.child("itemCartTotalPrice").getValue().toString();
                        String quantity = snapshot.child("itemCartQuantity").getValue().toString();

                        holder.ItemCartname.setText(name);
                        holder.ItemCartPriceTotal.setText(priceTotal);
                        holder.ItemCartQuantity.setText(quantity);
                        holder.ItemCartprice.setText(price);
                        Picasso.get().load(img).into(holder.ItemCartimg);

                        final int[] totalQuantity = {Integer.parseInt(quantity)};
                        holder.BtnCartPlus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (totalQuantity[0] <10){
                                    totalQuantity[0]++;
                                    holder.ItemCartQuantity.setText(String.valueOf(totalQuantity[0]));
                                    fullprice = (int) (Integer.parseInt(price)*(totalQuantity[0]));
                                    holder.ItemCartPriceTotal.setText(String.valueOf(fullprice));

//                                    cart = new Cart(img,name,price,holder.ItemCartQuantity.getText().toString(),holder.ItemCartPriceTotal.getText().toString());
                                    cartRef.child(currentUserId).child(postKey).child("itemCartQuantity").setValue(holder.ItemCartQuantity.getText().toString());
                                    cartRef.child(currentUserId).child(postKey).child("itemCartTotalPrice").setValue(holder.ItemCartPriceTotal.getText().toString());

                                    getCartTotalPrice();
                                }
                                if (totalQuantity[0] == 10){
                                    Toast.makeText(getContext(),"You can't add more",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        holder.BtnCartMinus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (totalQuantity[0] >0){
                                    totalQuantity[0]--;
                                    holder.ItemCartQuantity.setText(String.valueOf(totalQuantity[0]));
                                    fullprice = (int) (Integer.parseInt(price)*(totalQuantity[0]));
                                    holder.ItemCartPriceTotal.setText(String.valueOf(fullprice));

//                                    cart = new Cart(img,name,price,holder.ItemCartQuantity.getText().toString(),holder.ItemCartPriceTotal.getText().toString());
//                                    cartRef.child(currentUserId).child(postKey).setValue(cart);
                                    cartRef.child(currentUserId).child(postKey).child("itemCartQuantity").setValue(holder.ItemCartQuantity.getText().toString());
                                    cartRef.child(currentUserId).child(postKey).child("itemCartTotalPrice").setValue(holder.ItemCartPriceTotal.getText().toString());

                                    getCartTotalPrice();
                                }
                                if (totalQuantity[0] == 0){
                                    cartRef.child(currentUserId).child(postKey).removeValue();
                                    getCartQuantity();
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_view, parent,false);
                CartViewHolder viewHolder = new CartViewHolder(view);
                return viewHolder;
            }
        };

        rvCartView.setAdapter(adapter);
        adapter.startListening();

    }
}