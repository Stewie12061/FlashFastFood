package com.example.flashfastfood.Adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DiscountViewHolder extends RecyclerView.ViewHolder{

    public LinearLayout btnUseorCancel;
    public TextView discountName, discountDes, discountExpDay, discountUseCancelText;
    public DatabaseReference discountUsedRef;
    public FirebaseDatabase firebaseDatabase;
    public boolean isInMyDiscountUsed = true;

    public DiscountViewHolder(@NonNull View itemView) {
        super(itemView);

        discountDes = itemView.findViewById(R.id.discountDes);
        discountExpDay = itemView.findViewById(R.id.discountExpDay);
        discountName = itemView.findViewById(R.id.discountName);
        btnUseorCancel = itemView.findViewById(R.id.btnDiscountUseCancel);
        discountUseCancelText = itemView.findViewById(R.id.discountUseCancelText);
    }

    public void discountUseCheck(String postKey) {
        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        discountUsedRef = firebaseDatabase.getReference("Discount Used");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        discountUsedRef.child(userId).child(postKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyDiscountUsed = snapshot.exists();
                        if (isInMyDiscountUsed){
                            discountUseCancelText.setText("Cancel");
                            discountUseCancelText.setTextColor(Color.parseColor("#FFFFFF"));
                            btnUseorCancel.setBackgroundResource(R.color.orange);
                        }else {
                            discountUseCancelText.setText("Use");
                            discountUseCancelText.setTextColor(Color.parseColor("#E25822"));
                            btnUseorCancel.setBackgroundResource(R.color.white);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}
