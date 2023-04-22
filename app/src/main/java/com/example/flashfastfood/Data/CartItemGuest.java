package com.example.flashfastfood.Data;

public class CartItemGuest {
    private String itemId;
    private String itemCartImg;
    private String itemCartName;
    private String itemCartPrice;
    private String itemCartQuantity;
    private String itemCartTotalPrice;

    public CartItemGuest(){

    }

    public CartItemGuest(String itemId, String itemCartImg, String itemCartName, String itemCartPrice, String itemCartQuantity, String itemCartTotalPrice) {
        this.itemId = itemId;
        this.itemCartImg = itemCartImg;
        this.itemCartName = itemCartName;
        this.itemCartPrice = itemCartPrice;
        this.itemCartQuantity = itemCartQuantity;
        this.itemCartTotalPrice = itemCartTotalPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemCartImg() {
        return itemCartImg;
    }

    public void setItemCartImg(String itemCartImg) {
        this.itemCartImg = itemCartImg;
    }

    public String getItemCartName() {
        return itemCartName;
    }

    public void setItemCartName(String itemCartName) {
        this.itemCartName = itemCartName;
    }

    public String getItemCartPrice() {
        return itemCartPrice;
    }

    public void setItemCartPrice(String itemCartPrice) {
        this.itemCartPrice = itemCartPrice;
    }

    public String getItemCartQuantity() {
        return itemCartQuantity;
    }

    public void setItemCartQuantity(String itemCartQuantity) {
        this.itemCartQuantity = itemCartQuantity;
    }

    public String getItemCartTotalPrice() {
        return itemCartTotalPrice;
    }

    public void setItemCartTotalPrice(String itemCartTotalPrice) {
        this.itemCartTotalPrice = itemCartTotalPrice;
    }
}
