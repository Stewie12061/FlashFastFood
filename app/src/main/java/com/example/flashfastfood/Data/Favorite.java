package com.example.flashfastfood.Data;

public class Favorite {
    String favoriteDescription, favoriteImage, favoriteId, favoriteName, favoritePrice, favoriteRating;

    public Favorite(){
    }

    public Favorite(String favoriteDescription, String favoriteId, String favoriteImage, String favoriteName, String favoritePrice, String favoriteRating){
        this.favoriteDescription = favoriteDescription;
        this.favoriteId = favoriteId;
        this.favoriteImage = favoriteImage;
        this.favoriteName = favoriteName;
        this.favoritePrice = favoritePrice;
        this.favoriteRating = favoriteRating;
    }


    public String getfavoriteDescription() {
        return favoriteDescription;
    }

    public void setfavoriteDescription(String favoriteDescription) {
        this.favoriteDescription = favoriteDescription;
    }

    public String getfavoriteImage() {
        return favoriteImage;
    }

    public void setfavoriteImage(String favoriteImage) {
        this.favoriteImage = favoriteImage;
    }

    public String getfavoriteId() {
        return favoriteId;
    }

    public void setfavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    public String getfavoriteName() {
        return favoriteName;
    }

    public void setfavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }


    public String getfavoritePrice() {
        return favoritePrice;
    }

    public void setfavoritePrice(String favoritePrice) {
        this.favoritePrice = favoritePrice;
    }

    public String getfavoriteRating() {
        return favoriteRating;
    }

    public void setfavoriteRating(String favoriteRating) {
        this.favoriteRating = favoriteRating;
    }
}
