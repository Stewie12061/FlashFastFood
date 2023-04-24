package com.example.flashfastfood.Guest;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.Data.CartItemGuest;
import com.example.flashfastfood.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class OrderCartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItemGuest> itemList;
    private Context context;
    GuestCheckOutActivity guestCheckOutActivity;

    public OrderCartAdapter(List<CartItemGuest> itemList, Context context,GuestCheckOutActivity guestCheckOutActivity) {
        this.itemList = itemList;
        this.context = context;
        this.guestCheckOutActivity = guestCheckOutActivity;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_view, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItemGuest item = itemList.get(position);

        // Set the item details in the UI
        holder.ItemCartname.setText(item.getItemCartName());
        holder.ItemCartQuantity.setText(item.getItemCartQuantity());
        holder.ItemCartPriceTotal.setText(item.getItemCartTotalPrice());
        Picasso.get().load(item.getItemCartImg()).into(holder.ItemCartimg);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.remove(item);
                removeItem(item);
                notifyDataSetChanged();
            }
        });
        guestCheckOutActivity.getCartQuantity();

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ItemCartname, ItemCartPriceTotal;
        public ImageView ItemCartimg;

        public LinearLayout btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            ItemCartPriceTotal = itemView.findViewById(R.id.itemCartPriceTotal);
            ItemCartname  = itemView.findViewById(R.id.itemCartName);
            ItemCartimg = itemView.findViewById(R.id.itemCartImg);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void removeItem(CartItemGuest item) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

        cartItems.remove(item.getItemId());

        int totalItem = 0;
        int totalPrice = 0;
        for (CartItemGuest cartItemGuest : cartItems.values()) {
            totalItem += Integer.parseInt(cartItemGuest.getItemCartQuantity());
            totalPrice += Integer.parseInt(cartItemGuest.getItemCartTotalPrice());
        }
        guestCheckOutActivity.updateCartSummary(totalItem, totalPrice);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CartItems", gson.toJson(cartItems));
        editor.apply();
    }
}
