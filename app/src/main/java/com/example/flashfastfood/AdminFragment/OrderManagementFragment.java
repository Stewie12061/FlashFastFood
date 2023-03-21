package com.example.flashfastfood.AdminFragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.AdapterAdmin.UserListViewHolder;
import com.example.flashfastfood.Admin.ConfirmOrderActivity;
import com.example.flashfastfood.Admin.MessageAdminActivity;
import com.example.flashfastfood.Data.UserProfile;
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

import java.util.ArrayList;


public class OrderManagementFragment extends Fragment {

    String countItemInCart, countItemChat;
    int itemInCart, itemInChat;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef, orderRef, chatRef, chatNotifiRef;
    FirebaseRecyclerAdapter<UserProfile, UserListViewHolder> adapter;
    String userId, adminId;
    ArrayList<String> arrayList = null;
    ArrayList<String> arrayListUser = null;

    RecyclerView rvCustomerList;

    int countMess;

    public OrderManagementFragment() {
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
        return inflater.inflate(R.layout.fragment_order_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered Users");
        orderRef = firebaseDatabase.getReference("Order");
        chatRef = firebaseDatabase.getReference("Chats");
        chatNotifiRef = firebaseDatabase.getReference("Chats Notification");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        adminId = user.getUid();

        rvCustomerList = view.findViewById(R.id.rvUserList);
        rvCustomerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public void onStart() {
        super.onStart();
        getUserList();
    }

    private void getUserList() {
        Query query = userRef.orderByChild("Role").equalTo("User");
        FirebaseRecyclerOptions<UserProfile> options = new FirebaseRecyclerOptions.Builder<UserProfile>().setQuery(query,UserProfile.class).build();
        adapter = new FirebaseRecyclerAdapter<UserProfile, UserListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserListViewHolder holder, int position, @NonNull UserProfile model) {
                String id = getRef(position).getKey();
                userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.child("FullName").getValue().toString();
                        String userMail = snapshot.child("Email").getValue().toString();
                        String userPhone = snapshot.child("PhoneNumber").getValue().toString();

                        holder.userMail.setText(userMail);
                        holder.userName.setText(userName);
                        holder.userPhone.setText(userPhone);

                        orderRef.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                arrayList = new ArrayList<>();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    if (dataSnapshot.child("orderStatus").getValue().toString().contentEquals("Canceled")|dataSnapshot.child("orderStatus").getValue().toString().contentEquals("Successful")){
                                        arrayList.remove(dataSnapshot.getKey());
                                    }else {
                                        arrayList.add(dataSnapshot.getKey());
                                    }
                                }
                                countItemInCart= Integer.toString(arrayList.size());
                                itemInCart = Integer.parseInt(countItemInCart);
                                holder.btnItemFabCartUser.setCount(itemInCart);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        chatNotifiRef.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                countMess = (int) snapshot.getChildrenCount();
                                holder.btnItemFabMessage.setCount(countMess);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.btnItemFabCartUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ConfirmOrderActivity.class);
                                intent.putExtra("userId",id);
                                startActivity(intent);
                            }
                        });

                        holder.btnItemFabMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chatNotifiRef.child(id).removeValue();

                                Intent intent = new Intent(getContext(), MessageAdminActivity.class);
                                intent.putExtra("userId",id);
                                startActivity(intent);
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
            public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list,parent,false);
                UserListViewHolder viewHolder = new UserListViewHolder(view);
                return viewHolder;
            }
        };
        rvCustomerList.setAdapter(adapter);
        adapter.startListening();
    }
}