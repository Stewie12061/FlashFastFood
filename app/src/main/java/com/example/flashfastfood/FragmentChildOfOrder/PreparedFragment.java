package com.example.flashfastfood.FragmentChildOfOrder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Adapter.ItemsViewHolder;
import com.example.flashfastfood.Adapter.OrderViewHolder;
import com.example.flashfastfood.Data.Items;
import com.example.flashfastfood.Data.Order;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.ItemDetailActivity;
import com.example.flashfastfood.ItemsActivity;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PreparedFragment extends Fragment {

    String currentUserId;

    RecyclerView rvPrepared;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cartRef, orderRef;

    FirebaseRecyclerOptions<Order> options;
    FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;
    ArrayList<String> arrayList = null;

    CounterFab btnCart;
    LottieAnimationView prepareWating;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PreparedFragment() {
        // Required empty public constructor
    }

    public static PreparedFragment newInstance(String param1, String param2) {
        PreparedFragment fragment = new PreparedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        btnCart = view.findViewById(R.id.itemFabCart);
        btnCart.setVisibility(View.GONE);
        getCartQuantity();

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("callbackkey", "1");
                BottomSheetCartFragment bottomSheetFragment = new BottomSheetCartFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getChildFragmentManager(),bottomSheetFragment.getTag());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getCartQuantity();
        loadAllItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCartQuantity();
        loadAllItems();
    }

    public void getCartQuantity(){
        cartRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    prepareWating.setVisibility(View.GONE);
                }
                else {
                    prepareWating.setVisibility(View.VISIBLE);
                    btnCart.setVisibility(View.VISIBLE);
                    btnCart.setCount(itemInCart);
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



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_items, parent,false);
                OrderViewHolder viewHolder = new OrderViewHolder(view);
                return viewHolder;
            }
        };

        rvPrepared.setAdapter(adapter);
        adapter.startListening();

    }
}