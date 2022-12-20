package com.example.flashfastfood.AdapterAdmin;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class CateAdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView cateadName;
    public RoundedImageView cateadImg;
    private ItemClickListener itemClickListener;
    public LinearLayout btnmodify, btndelete;
    public CateAdminViewHolder(@NonNull View itemView) {
        super(itemView);
        cateadImg = itemView.findViewById(R.id.cateAdImg);
        cateadName = itemView.findViewById(R.id.cateAdName);
        btnmodify = itemView.findViewById(R.id.btnModify);
        btndelete = itemView.findViewById(R.id.btnDelete);
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
