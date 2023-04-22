package com.example.flashfastfood.Data;

import java.util.HashMap;
import java.util.Map;

public class CartGuest {
    private Map<String, CartItemGuest> items;

    public CartGuest() {
        items = new HashMap<>();
    }

    public Map<String, CartItemGuest> getItems() {
        return items;
    }

    public void addItem(CartItemGuest item) {
        items.put(item.getItemId(), item);
    }

    public void removeItem(CartItemGuest item) {
        items.remove(item.getItemId());
    }

    public boolean containsItem(CartItemGuest item) {
        return items.containsKey(item.getItemId());
    }

    public void updateItem(CartItemGuest item) {
        items.put(item.getItemId(), item);
    }
}
