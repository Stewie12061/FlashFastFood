package com.example.flashfastfood.AdapterAdmin;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DiscountAdminViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout btnDeleteDiscount;
    public TextView discountName, discountDes, discountExpDay;

    public DiscountAdminViewHolder(@NonNull View itemView) {
        super(itemView);
        discountDes = itemView.findViewById(R.id.discountDes);
        discountExpDay = itemView.findViewById(R.id.discountExpDay);
        discountName = itemView.findViewById(R.id.discountName);
        btnDeleteDiscount = itemView.findViewById(R.id.btnDeleteDiscount);

    }
}
