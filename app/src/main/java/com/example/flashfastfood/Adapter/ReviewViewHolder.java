package com.example.flashfastfood.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.R;

public class ReviewViewHolder extends RecyclerView.ViewHolder {

    public TextView userNameReview, reviewText, reviewDate, reviewTime;

    public ReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameReview = itemView.findViewById(R.id.userNameReview);
        reviewDate = itemView.findViewById(R.id.datePostReview);
        reviewTime = itemView.findViewById(R.id.timePostReview);
        reviewText = itemView.findViewById(R.id.txtReview);
    }
}
