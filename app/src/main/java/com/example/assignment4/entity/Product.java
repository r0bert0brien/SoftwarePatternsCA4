package com.example.assignment4.entity;

public class Product {

    String key;
    String title;
    String manufacturer;
    String price;
    String category;
    String image;
    int stock;

    @Override
    public String toString() {
        return "Product{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", price='" + price + '\'' +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public Product(String key, String title, String manufacturer, String price, String category, String image, int Stock) {
        this.key = key;
        this.title = title;
        this.manufacturer = manufacturer;
        this.price = price;
        this.category = category;
        this.image = image;
        this.stock = stock;
    }

    public Product(){
        // Required empty public constructor
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
