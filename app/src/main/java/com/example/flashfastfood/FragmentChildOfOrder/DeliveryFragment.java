package com.example.flashfastfood.FragmentChildOfOrder;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class DeliveryFragment extends Fragment {

    private String currentUserId;

    private RecyclerView rvDelivery;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference orderRef;

    private FirebaseRecyclerOptions<Order> options;
    private FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;
    private ArrayList<String> arrayList2 = null;

    private LottieAnimationView deliveryWaiting;


    public DeliveryFragment() {
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
        return inflater.inflate(R.layout.fragment_delivery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        orderRef = firebaseDatabase.getReference("Order");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        rvDelivery = view.findViewById(R.id.rvDelivery);
        rvDelivery.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        deliveryWaiting = view.findViewById(R.id.deliveryWaiting);
        deliveryWaiting.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayList2 = new ArrayList<>();
        loadDeliveringItems();
//        String countItemInCart= Integer.toString(arrayList2.size());
//        int itemInOrderDe = Integer.parseInt(countItemInCart);
//        if (itemInOrderDe==0){
//            deliveryWaiting.setVisibility(View.VISIBLE);
//        }
//        else {
//            deliveryWaiting.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayList2 = new ArrayList<>();
        loadDeliveringItems();
//        String countItemInCart= Integer.toString(arrayList2.size());
//        int itemInOrder = Integer.parseInt(countItemInCart);
//        if (itemInOrder==0){
//            deliveryWaiting.setVisibility(View.VISIBLE);
//        }
//        else {
//            deliveryWaiting.setVisibility(View.GONE);
//        }
    }

    private void loadDeliveringItems(){
        Query query = orderRef.child(currentUserId).orderByChild("orderStatus").equalTo("Delivering");

        options =new FirebaseRecyclerOptions.Builder<Order>().setQuery(query,Order.class).build();

        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {
                String postKey = getRef(position).getKey();

                orderRef.child(currentUserId).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            arrayList2.add(dataSnapshot.getKey());
                        }

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

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_delivery, parent,false);
                OrderViewHolder viewHolder = new OrderViewHolder(view);
                return viewHolder;
            }
        };

        rvDelivery.setAdapter(adapter);
        adapter.startListening();

    }
}