package com.example.flashfastfood.FragmentChildOfOrder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Adapter.ItemsViewHolder;
import com.example.flashfastfood.Adapter.OrderViewHolder;
import com.example.flashfastfood.CheckOutActivity;
import com.example.flashfastfood.Data.Items;
import com.example.flashfastfood.Data.Order;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.ItemDetailActivity;
import com.example.flashfastfood.ItemsActivity;
import com.example.flashfastfood.MainActivity;
import com.example.flashfastfood.PaymentActivity;
import com.example.flashfastfood.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.util.ArrayList;

public class PreparedFragment extends Fragment {

    private String currentUserId;

    private RecyclerView rvPrepared;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference cartRef, orderRef;

    private FirebaseRecyclerOptions<Order> options;
    private FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;
    private ArrayList<String> arrayList1 = null;

    private Order order;

    private LottieAnimationView prepareWating;
    public PreparedFragment() {
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
        return inflater.inflate(R.layout.fragment_prepared, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        orderRef = firebaseDatabase.getReference("Order");
        cartRef = firebaseDatabase.getReference("Shopping Cart");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        rvPrepared = view.findViewById(R.id.rvPrepared);
        rvPrepared.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        prepareWating = view.findViewById(R.id.preparedWaiting);
        prepareWating.setVisibility(View.GONE);

    }

    @Override
    public void onStart() {
        super.onStart();
        getOrderQuantity();
        loadAllItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderQuantity();
        loadAllItems();
    }

    public void getOrderQuantity(){
        orderRef.child(currentUserId).orderByChild("orderStatus").equalTo("Processing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList1 = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList1.add(dataSnapshot.getKey());
                }
                String countItemInCart= Integer.toString(arrayList1.size());
                int itemInOrder = Integer.parseInt(countItemInCart);
                if (itemInOrder==0){
                    prepareWating.setVisibility(View.VISIBLE);
                }
                else {
                    prepareWating.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadAllItems(){
        Query query = orderRef.child(currentUserId).orderByChild("orderStatus").equalTo("Processing");

        options =new FirebaseRecyclerOptions.Builder<Order>().setQuery(query,Order.class).build();

        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {
                String postKey = getRef(position).getKey();

                orderRef.child(currentUserId).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String orderDate = snapshot.child("orderDate").getValue().toString();
                        String orderTime = snapshot.child("orderTime").getValue().toString();
                        String orderPrice = snapshot.child("orderTotalPrice").getValue().toString();
                        String orderPayment = snapshot.child("orderPayment").getValue().toString();
                        String orderItemQuantity = snapshot.child("orderItemQuantity").getValue().toString();
                        String orderLocation = snapshot.child("orderLocation").getValue().toString();
                        String orderStatus = snapshot.child("orderStatus").getValue().toString();

                        holder.orderItemQuantity.setText(orderItemQuantity);
                        holder.orderPrice.setText(orderPrice);
                        holder.orderLocation.setText(orderLocation);
                        holder.orderStatus.setText(orderStatus);
                        holder.orderDate.setText(orderDate);
                        holder.orderTime.setText(orderTime);
                        holder.orderPayment.setText(orderPayment);

                        if (orderPayment.equals("Cash on delivery")){
                            holder.orderPayment.setTextColor(Color.parseColor("#CB1B11"));
                        }else {
                            holder.orderPayment.setTextColor(Color.parseColor("#4CAF50"));
                        }

                        holder.btnCancelOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder
                                                (getContext());
                                View view = LayoutInflater.from(getContext()).inflate(
                                        R.layout.dialog_alert,
                                        (ConstraintLayout) getActivity().findViewById(R.id.layoutDialogContainer)
                                );
                                builder.setView(view);
                                ((TextView) view.findViewById(R.id.textTitle))
                                        .setText("Cancel this order");
                                ((TextView) view.findViewById(R.id.textMessage))
                                        .setText("Do you really want to cancel this order?");
                                ((Button) view.findViewById(R.id.buttonYes))
                                        .setText("Yes");
                                ((Button) view.findViewById(R.id.buttonNo))
                                        .setText("No");
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        order = new Order();
                                        order.setOrderTotalPrice(orderPrice);
                                        order.setOrderStatus("Canceled");
                                        order.setOrderPayment(orderPayment);
                                        order.setOrderTime(orderTime);
                                        order.setOrderLocation(orderLocation);
                                        order.setOrderItemQuantity(orderItemQuantity);
                                        order.setOrderDate(orderDate);
                                        alertDialog.dismiss();
                                        Dialog dialog = new Dialog(getContext(),R.style.CustomDialog);
                                        dialog.setContentView(R.layout.dialog_order_loading);
                                        dialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          orderRef.child(currentUserId).child(postKey).setValue(order);
                                                                          dialog.dismiss();
                                                                      }
                                                                  }, 5000
                                        );

                                    }
                                });
                                view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                    }
                                });

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
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent,false);
                OrderViewHolder viewHolder = new OrderViewHolder(view);
                return viewHolder;
            }
        };

        rvPrepared.setAdapter(adapter);
        adapter.startListening();

    }
}