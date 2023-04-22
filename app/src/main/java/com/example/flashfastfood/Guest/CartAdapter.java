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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.Data.CartGuest;
import com.example.flashfastfood.Data.CartItemGuest;
import com.example.flashfastfood.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private List<CartItemGuest> itemList;
    private Context context;
    BottomSheetCartGuestFragment bottomSheetCartGuestFragment;

    public CartAdapter(List<CartItemGuest> itemList, Context context,BottomSheetCartGuestFragment  bottomSheetCartGuestFragment) {
        this.itemList = itemList;
        this.context = context;
        this.bottomSheetCartGuestFragment = bottomSheetCartGuestFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemGuest item = itemList.get(position);

        // Set the item details in the UI
        holder.ItemCartname.setText(item.getItemCartName());
        holder.ItemCartprice.setText(item.getItemCartPrice());
        holder.ItemCartQuantity.setText(item.getItemCartQuantity());
        holder.ItemCartPriceTotal.setText(item.getItemCartTotalPrice());
        Picasso.get().load(item.getItemCartImg()).into(holder.ItemCartimg);
        holder.BtnCartMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer newQuantity = Integer.parseInt(item.getItemCartQuantity()) - 1;
                if (newQuantity<=0){
                    itemList.remove(item);
                    removeItem(item);
                }else {
                    item.setItemCartQuantity(String.valueOf(newQuantity));

                    // Update the total price
                    Integer newTotalPrice = Integer.parseInt(item.getItemCartPrice()) * Integer.parseInt(item.getItemCartQuantity());
                    item.setItemCartTotalPrice(String.valueOf(newTotalPrice));

                    // Update the UI
                    holder.ItemCartQuantity.setText(item.getItemCartQuantity());
                    holder.ItemCartPriceTotal.setText(item.getItemCartTotalPrice());
                    updateCartDataInSharedPreferences(item);
                }
                // Notify the adapter that the data has changed
                notifyDataSetChanged();

                bottomSheetCartGuestFragment.getCartTotal();
            }
        });
        holder.BtnCartPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer newQuantity = Integer.parseInt(item.getItemCartQuantity()) + 1;
                item.setItemCartQuantity(String.valueOf(newQuantity));

                // Update the total price
                Integer newTotalPrice = Integer.parseInt(item.getItemCartPrice()) * Integer.parseInt(item.getItemCartQuantity());
                item.setItemCartTotalPrice(String.valueOf(newTotalPrice));

                // Update the UI
                holder.ItemCartQuantity.setText(item.getItemCartQuantity());
                holder.ItemCartPriceTotal.setText(item.getItemCartTotalPrice());

                // Notify the adapter that the data has changed
                notifyDataSetChanged();
                updateCartDataInSharedPreferences(item);
                bottomSheetCartGuestFragment.getCartTotal();

            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ItemCartprice, ItemCartname, ItemCartQuantity, ItemCartPriceTotal;
        public ImageView ItemCartimg;

        public LinearLayout btnDelete;

        public ImageButton BtnCartMinus, BtnCartPlus;

        public ViewHolder(View itemView) {
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
    private void updateCartDataInSharedPreferences(CartItemGuest cartItemGuest) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

        CartItemGuest existingItem = cartItems.get(cartItemGuest.getItemId());
        existingItem.setItemCartQuantity(cartItemGuest.getItemCartQuantity());
        existingItem.setItemCartTotalPrice(cartItemGuest.getItemCartTotalPrice());
        cartItems.put(cartItemGuest.getItemId(), existingItem);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CartItems", gson.toJson(cartItems));
        editor.apply();
    }
    private void removeItem(CartItemGuest item) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

        cartItems.remove(item.getItemId());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CartItems", gson.toJson(cartItems));
        editor.apply();
    }

}
