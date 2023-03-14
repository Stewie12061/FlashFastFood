package com.example.flashfastfood.FragmentChildOfOrder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.flashfastfood.Adapter.OrderViewHolder;
import com.example.flashfastfood.Data.Order;
import com.example.flashfastfood.R;
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

import java.util.ArrayList;


public class HistoryFragment extends Fragment {

    private String currentUserId;

    private RecyclerView rvHistory, rvHistoryCancel;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference orderRef;

    private FirebaseRecyclerOptions<Order> options;
    private FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;
    private ArrayList<String> arrayList3 = null;

    private LottieAnimationView historyWating, historyWating2;

    public HistoryFragment() {
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        orderRef = firebaseDatabase.getReference("Order");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        rvHistoryCancel = view.findViewById(R.id.rvHistoryCanceled);
        rvHistoryCancel.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        historyWating = view.findViewById(R.id.historyWaiting);
        historyWating.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getOrderQuantity();
        loadHistoryCompleted();
        loadHistoryCanceled();

    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderQuantity();
        loadHistoryCompleted();
        loadHistoryCanceled();

    }
    public void getOrderQuantity(){
        arrayList3 = new ArrayList<>();
        orderRef.child(currentUserId).orderByChild("orderStatus").equalTo("Canceled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList3.add(dataSnapshot.getKey());
                }
                String countItemInCart= Integer.toString(arrayList3.size());
                int itemInOrder = Integer.parseInt(countItemInCart);
                if (itemInOrder==0){
                    historyWating.setVisibility(View.VISIBLE);
                }
                else {
                    historyWating.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        orderRef.child(currentUserId).orderByChild("orderStatus").equalTo("Successful").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList3.add(dataSnapshot.getKey());
                }
                String countItemInCart= Integer.toString(arrayList3.size());
                int itemInOrder = Integer.parseInt(countItemInCart);
                if (itemInOrder==0){
                    historyWating.setVisibility(View.VISIBLE);
                }
                else {
                    historyWating.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadHistoryCanceled(){
        Query query = orderRef.child(currentUserId).orderByChild("orderStatus").equalTo("Canceled");

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

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder
                                                (getContext());
                                View view = LayoutInflater.from(getContext()).inflate(
                                        R.layout.dialog_alert,
                                        (ConstraintLayout) getActivity().findViewById(R.id.layoutDialogContainer)
                                );
                                builder.setView(view);
                                ((TextView) view.findViewById(R.id.textTitle))
                                        .setText("Delete Order History");
                                ((TextView) view.findViewById(R.id.textMessage))
                                        .setText("Do you really want to delete this order?");
                                ((Button) view.findViewById(R.id.buttonYes))
                                        .setText("Yes");
                                ((Button) view.findViewById(R.id.buttonNo))
                                        .setText("No");
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        Dialog dialog = new Dialog(getContext(),R.style.CustomDialog);
                                        dialog.setContentView(R.layout.dialog_order_loading);
                                        dialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          orderRef.child(currentUserId).child(postKey).removeValue();
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
                                return false;
                            }
                        });

                        if (orderPayment.equals("Cash on delivery")){
                            holder.orderPayment.setTextColor(Color.parseColor("#CB1B11"));
                        }else {
                            holder.orderPayment.setTextColor(Color.parseColor("#4CAF50"));
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent,false);
                OrderViewHolder viewHolder = new OrderViewHolder(view);
                return viewHolder;
            }
        };

        rvHistoryCancel.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadHistoryCompleted(){
        Query query = orderRef.child(currentUserId).orderByChild("orderStatus").equalTo("Successful");

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

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder
                                                (getContext());
                                View view = LayoutInflater.from(getContext()).inflate(
                                        R.layout.dialog_alert,
                                        (ConstraintLayout) getActivity().findViewById(R.id.layoutDialogContainer)
                                );
                                builder.setView(view);
                                ((TextView) view.findViewById(R.id.textTitle))
                                        .setText("Delete Order History");
                                ((TextView) view.findViewById(R.id.textMessage))
                                        .setText("Do you really want to delete this order?");
                                ((Button) view.findViewById(R.id.buttonYes))
                                        .setText("Yes");
                                ((Button) view.findViewById(R.id.buttonNo))
                                        .setText("No");
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        Dialog dialog = new Dialog(getContext(),R.style.CustomDialog);
                                        dialog.setContentView(R.layout.dialog_order_loading);
                                        dialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          orderRef.child(currentUserId).child(postKey).removeValue();
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
                                return false;
                            }
                        });

                        if (orderPayment.equals("Cash on delivery")){
                            holder.orderPayment.setTextColor(Color.parseColor("#CB1B11"));
                        }else {
                            holder.orderPayment.setTextColor(Color.parseColor("#4CAF50"));
                        }

                        if (orderStatus.equals("Successful")){
                            holder.orderStatus.setTextColor(Color.parseColor("#4CAF50"));
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent,false);
                OrderViewHolder viewHolder = new OrderViewHolder(view);
                return viewHolder;
            }
        };

        rvHistory.setAdapter(adapter);
        adapter.startListening();

    }
}