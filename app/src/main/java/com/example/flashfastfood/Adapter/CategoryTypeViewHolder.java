package com.example.flashfastfood.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.R;


public class CategoryTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cateName;
    public ImageView cateImg;
    public ItemClickListener itemClickListener;
    public CardView cardView;

    public CategoryTypeViewHolder(@NonNull View itemView) {
        super(itemView);
        cateName = (TextView) itemView.findViewById(R.id.categoryName);
        cateImg = (ImageView) itemView.findViewById(R.id.categoryImg);
        cardView = (CardView) itemView.findViewById(R.id.cardViewDash);

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
