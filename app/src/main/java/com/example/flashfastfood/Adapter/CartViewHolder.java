package com.example.flashfastfood.Adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class CartViewHolder extends RecyclerView.ViewHolder{

    public TextView ItemCartprice, ItemCartname, ItemCartQuantity, ItemCartPriceTotal;
    public ImageView ItemCartimg;

    public LinearLayout btnDelete;

    public ImageButton BtnCartMinus, BtnCartPlus;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        ItemCartPriceTotal = itemView.findViewById(R.id.itemCartPriceTotal);
        ItemCartprice = itemView.findViewById(R.id.itemCartPrice);
        ItemCartname  = itemView.findViewById(R.id.itemCartName);
        ItemCartimg = itemView.findViewById(R.id.itemCartImg);
        ItemCartQuantity = itemView.findViewById(R.id.itemCartQuantity);
        BtnCartMinus = itemView.findViewById(R.id.btnCartMinus);
        BtnCartPlus = itemView.findViewById(R.id.btnCartPlus);

        btnDelete = itemView.findViewById(R.id.btnDelete);

    }


}
