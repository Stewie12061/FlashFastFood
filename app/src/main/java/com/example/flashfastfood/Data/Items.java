package com.example.flashfastfood.Data;

public class Items {
    String cateId, itemDes, itemImg, itemName, itemPrice, itemPromo, itemRating, itemStatus;

    public Items(){}

    public Items(String cateId, String itemDes, String itemImg, String itemName, String itemPrice, String itemPromo, String itemRating, String itemStatus) {
        this.cateId = cateId;
        this.itemDes = itemDes;
        this.itemImg = itemImg;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemPromo = itemPromo;
        this.itemRating = itemRating;
        this.itemStatus = itemStatus;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemPromo() {
        return itemPromo;
    }

    public void setItemPromo(String itemPromo) {
        this.itemPromo = itemPromo;
    }

    public String getItemRating() {
        return itemRating;
    }

    public void setItemRating(String itemRating) {
        this.itemRating = itemRating;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }
}
