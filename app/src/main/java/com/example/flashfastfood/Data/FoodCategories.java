package com.example.flashfastfood.Data;

public class FoodCategories {
    private String FoodCateImg, FoodCateName;

    public FoodCategories(){}

    public FoodCategories(String foodCateImg, String foodCateName) {
        FoodCateImg = foodCateImg;
        FoodCateName = foodCateName;
    }

    public String getFoodCateImg() {
        return FoodCateImg;
    }

    public void setFoodCateImg(String foodCateImg) {
        FoodCateImg = foodCateImg;
    }

    public String getFoodCateName() {
        return FoodCateName;
    }

    public void setFoodCateName(String foodCateName) {
        FoodCateName = foodCateName;
    }
}
