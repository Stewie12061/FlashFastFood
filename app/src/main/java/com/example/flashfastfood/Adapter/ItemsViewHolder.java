package com.example.flashfastfood.Adapter;

import android.view.View;
import android.widget.ImageView;
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
import com.varunest.sparkbutton.SparkButton;

public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView itemprice, itemname, itemrating, itemcatename;
    public ImageView itemimg;

    private ItemClickListener itemClickListener;

    public ItemsViewHolder(@NonNull View itemView) {
        super(itemView);

        itemprice = itemView.findViewById(R.id.itemPrice);
        itemname  = itemView.findViewById(R.id.itemName);
        itemrating = itemView.findViewById(R.id.itemRating);
        itemimg = itemView.findViewById(R.id.itemImg);

        itemcatename = itemView.findViewById(R.id.itemCateName);
        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getBindingAdapterPosition(),false);
    }

}
