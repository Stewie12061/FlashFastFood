package com.example.flashfastfood.AdapterAdmin;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.R;

public class ItemAdminVIewHolder extends RecyclerView.ViewHolder{

    public TextView ItemAdName, ItemAdStatus, ItemAdPrice, ItemAdRating ;
    public ImageView ItemAdImg;
    public LinearLayout btnDeleteItem, btnModifyItem;

    public ItemAdminVIewHolder(@NonNull View itemView) {
        super(itemView);

        ItemAdName = itemView.findViewById(R.id.itemAdName);
        ItemAdStatus = itemView.findViewById(R.id.itemAdStatus);
        ItemAdPrice = itemView.findViewById(R.id.itemAdPrice);
        ItemAdRating = itemView.findViewById(R.id.itemAdRating);
        ItemAdImg = itemView.findViewById(R.id.itemAdImg);

        btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        btnModifyItem =  itemView.findViewById(R.id.btnModifyItem);

    }
}
