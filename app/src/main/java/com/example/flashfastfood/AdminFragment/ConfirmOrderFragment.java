package com.example.flashfastfood.AdminFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.flashfastfood.Adapter.OrderViewHolder;
import com.example.flashfastfood.Admin.MainAdminActivity;
import com.example.flashfastfood.Data.Order;
import com.example.flashfastfood.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class ConfirmOrderFragment extends Fragment {

    private String userId;

    private RecyclerView rvConfirmOrder;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference orderRef, userRef;

    private FirebaseRecyclerOptions<Order> options;
    private FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;
    private ArrayList<String> arrayList1 = null;

    private Order order;

    private LottieAnimationView prepareWating;

    public ConfirmOrderFragment() {
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
        return inflater.inflate(R.layout.fragment_confirm_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId =  getActivity().getIntent().getStringExtra("userId");

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        orderRef = firebaseDatabase.getReference("Order");
        userRef = firebaseDatabase.getReference("Registered Users");

        rvConfirmOrder = view.findViewById(R.id.rvConfirmOrder);
        rvConfirmOrder.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void getOrderQuantity(){
        orderRef.child(userId).orderByChild("orderStatus").equalTo("Processing").addValueEventListener(new ValueEventListener() {
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
        Query query = orderRef.child(userId).orderByChild("orderStatus").equalTo("Processing");

        options =new FirebaseRecyclerOptions.Builder<Order>().setQuery(query,Order.class).build();

        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {
                String postKey = getRef(position).getKey();

                orderRef.child(userId).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
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

                        userRef.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userName = snapshot.child("FullName").getValue().toString();
                                String userPhoneNUmber = snapshot.child("PhoneNumber").getValue().toString();

                                holder.orderCustomer.setText(userName);
                                holder.orderPhoneNumber.setText(userPhoneNUmber);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

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
                                                                          orderRef.child(userId).child(postKey).setValue(order);
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

                        holder.btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
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
                                        .setText("Confirm this order");
                                ((TextView) view.findViewById(R.id.textMessage))
                                        .setText("Do you want to add this order to delivery?");
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
                                        order.setOrderStatus("Delivering");
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
                                                                          orderRef.child(userId).child(postKey).setValue(order);
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirm_order, parent,false);
                OrderViewHolder viewHolder = new OrderViewHolder(view);
                return viewHolder;
            }
        };

        rvConfirmOrder.setAdapter(adapter);
        adapter.startListening();

    }
}