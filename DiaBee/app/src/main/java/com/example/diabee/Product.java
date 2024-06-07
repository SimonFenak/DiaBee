package com.example.diabee;

public class Product {

    private String name;
    private String value;
    private boolean favorite;

    public Product(String name, String value, boolean favorite) {
        this.name = name;
        this.value = value;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
