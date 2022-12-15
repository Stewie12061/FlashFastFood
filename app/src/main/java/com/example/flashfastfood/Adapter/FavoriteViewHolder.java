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

public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView FavoriteName, FavoritePrice, FavoriteRating ;
    public ImageView FavoriteImg;
    public SparkButton sparkButton;
    public DatabaseReference favoriteRef;
    public FirebaseDatabase firebaseDatabase;
    public boolean isInMyFavorite = false;
    private ItemClickListener itemClickListener;


    public FavoriteViewHolder(@NonNull View itemView) {
        super(itemView);
        FavoriteName = itemView.findViewById(R.id.itemFavName);
        FavoriteRating = itemView.findViewById(R.id.itemFavRating);
        FavoritePrice = itemView.findViewById(R.id.itemFavPrice);
        FavoriteImg = itemView.findViewById(R.id.itemFavImg);
        sparkButton = itemView.findViewById(R.id.favFav);
        itemView.setOnClickListener(this);
    }

    public void favoriteCheck(String postKey) {
        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        favoriteRef = firebaseDatabase.getReference("Favorite");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        favoriteRef.child(userId).child(postKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        if (isInMyFavorite){
                            sparkButton.setChecked(true);
                        }else {
                            sparkButton.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void onClick(View v) {
        itemClickListener.onClick(v, getBindingAdapterPosition(),false);
    }

}
