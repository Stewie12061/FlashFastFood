package com.example.flashfastfood.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.flashfastfood.Data.Cart;
import com.example.flashfastfood.Data.Items;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class CartGuestHelper {
    private static final String PREF_CART_ITEMS = "cart_items";
    private static final String PREFS_NAME = "app_prefs";


    public static void addItem(Context context, Cart cart) {
        // get current cart items
        List<Cart> cartItems = getCartItems(context);

        // add item to cart
        cartItems.add(cart);

        // save updated cart items
        saveCartItems(context, cartItems);
    }

    public static void updateCartItem(Context context, Cart cart, String quantity) {
        List<Cart> cartItems = getCartItems(context);

        for (int i = 0; i < cartItems.size(); i++) {
            Cart cartItem = cartItems.get(i);
            if (cartItem.getItemCartName() == cart.getItemCartName()) {
                if (Integer.parseInt(quantity) > 0) {
                    cartItem.setItemCartQuantity(quantity);
                } else {
                    cartItems.remove(i);
                }
                break;
            }
        }

        saveCartItems(context, cartItems);
}
    public static List<Cart> getCartItems(Context context) {
        // get cart items from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String cartItemsJson = prefs.getString(PREF_CART_ITEMS, "[]");
        Type type = new TypeToken<List<Cart>>(){}.getType();
        return new Gson().fromJson(cartItemsJson, type);
    }

    public static void saveCartItems(Context context, List<Cart> cartItems) {
        // save cart items to SharedPreferences
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        String cartItemsJson = new Gson().toJson(cartItems);
        editor.putString(PREF_CART_ITEMS, cartItemsJson);
        editor.apply();
    }
    public static void removeItem(Context context, Cart cart) {
        List<Cart> cartItems = getCartItems(context);

        Iterator<Cart> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            Cart cartItem = iterator.next();
            if (cartItem.getItemCartName() == cart.getItemCartName()) {
                iterator.remove();
                break;
            }
        }

        saveCartItems(context, cartItems);
    }

}
