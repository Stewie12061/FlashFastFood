package com.example.flashfastfood.Data;

public class Cart {
    String itemCartImg, itemCartName, itemCartPrice, itemCartQuantity, itemCartTotalPrice;

    public Cart(){}

    public Cart(String itemCartImg, String itemCartName, String itemCartPrice, String itemCartQuantity, String itemCartTotalPrice) {
        this.itemCartImg = itemCartImg;
        this.itemCartName = itemCartName;
        this.itemCartPrice = itemCartPrice;
        this.itemCartQuantity = itemCartQuantity;
        this.itemCartTotalPrice = itemCartTotalPrice;
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
