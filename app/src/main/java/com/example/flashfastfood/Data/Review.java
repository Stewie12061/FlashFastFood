package com.example.flashfastfood.Data;

public class Review {
    String itemReviewText, itemReviewDate, itemReviewTime, itemReviewCurrentUser;

    public Review(){}

    public Review(String itemReviewText, String itemReviewDate, String itemReviewTime, String itemReviewCurrentUser) {
        this.itemReviewText = itemReviewText;
        this.itemReviewDate = itemReviewDate;
        this.itemReviewTime = itemReviewTime;
        this.itemReviewCurrentUser = itemReviewCurrentUser;
    }

    public String getItemReviewText() {
        return itemReviewText;
    }

    public void setItemReviewText(String itemReviewText) {
        this.itemReviewText = itemReviewText;
    }

    public String getItemReviewDate() {
        return itemReviewDate;
    }

    public void setItemReviewDate(String itemReviewDate) {
        this.itemReviewDate = itemReviewDate;
    }

    public String getItemReviewTime() {
        return itemReviewTime;
    }

    public void setItemReviewTime(String itemReviewTime) {
        this.itemReviewTime = itemReviewTime;
    }

    public String getItemReviewCurrentUser() {
        return itemReviewCurrentUser;
    }

    public void setItemReviewCurrentUser(String itemReviewCurrentUser) {
        this.itemReviewCurrentUser = itemReviewCurrentUser;
    }
}
