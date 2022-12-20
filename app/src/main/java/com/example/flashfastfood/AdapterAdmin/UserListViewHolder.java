package com.example.flashfastfood.AdapterAdmin;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.R;


public class UserListViewHolder extends RecyclerView.ViewHolder{

    public TextView userName, userMail, userPhone;
    public CounterFab btnItemFabMessage, btnItemFabCartUser;

    public UserListViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.itemUserName);
        userMail = itemView.findViewById(R.id.itemUserEmail);
        userPhone = itemView.findViewById(R.id.itemUserPhonenumber);
        btnItemFabMessage = itemView.findViewById(R.id.itemFabMessage);
        btnItemFabCartUser = itemView.findViewById(R.id.itemFabCartUser);

    }


}
