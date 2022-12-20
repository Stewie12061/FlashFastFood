package com.example.flashfastfood.Data;

public class Items {
    String itemDes, itemImg, cateId,  itemName, itemStatus, itemPrice, itemRating;


    public Items(){}

    public Items(String itemDes, String itemImg, String cateId, String itemName, String itemStatus, String itemPrice, String itemRating) {
        this.itemDes = itemDes;
        this.itemImg = itemImg;
        this.cateId = cateId;
        this.itemName = itemName;
        this.itemStatus = itemStatus;
        this.itemPrice = itemPrice;
        this.itemRating = itemRating;
    }

    public String getItemDes() {
        return itemDes;
    }

    public void setItemDes(String itemDes) {
        this.itemDes = itemDes;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemRating() {
        return itemRating;
    }

    public void setItemRating(String itemRating) {
        this.itemRating = itemRating;
    }
}
