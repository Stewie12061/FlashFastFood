package com.example.flashfastfood.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView orderPrice, orderPayment, orderDate, orderTime, orderLocation, orderStatus, orderItemQuantity;
    public CardView btnCancelOrder;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        orderPrice = itemView.findViewById(R.id.orderPriceorder);
        orderPayment = itemView.findViewById(R.id.orderPayment);
        orderDate = itemView.findViewById(R.id.orderDate);
        orderTime = itemView.findViewById(R.id.orderTime);
        orderLocation = itemView.findViewById(R.id.orderLocation);
        orderStatus = itemView.findViewById(R.id.orderStatus);
        orderItemQuantity = itemView.findViewById(R.id.orderQuantity);
        btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);

    }

}
