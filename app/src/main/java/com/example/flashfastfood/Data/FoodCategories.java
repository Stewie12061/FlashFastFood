package com.example.flashfastfood.Data;

public class FoodCategories {
    private String foodCateImg, foodCateName;

    public FoodCategories(){}

    public FoodCategories(String foodCateImg, String foodCateName) {
        this.foodCateImg = foodCateImg;
        this.foodCateName = foodCateName;
    }

    public String getFoodCateImg() {
        return foodCateImg;
    }

    public void setFoodCateImg(String foodCateImg) {
        this.foodCateImg = foodCateImg;
    }

    public String getFoodCateName() {
        return foodCateName;
    }

    public void setFoodCateName(String foodCateName) {
        this.foodCateName = foodCateName;
    }
}
