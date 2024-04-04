package com.example.assignment4.entity;

public class Product {

    String key;
    String title;
    String manufacturer;
    String price;
    String size;
    String category;
    String image;
    int stock;

    public Product(String key, String title, String manufacturer, String price, String size, String category, String image, int stock) {
        this.key = key;
        this.title = title;
        this.manufacturer = manufacturer;
        this.price = price;
        this.size = size;
        this.category = category;
        this.image = image;
        this.stock = stock;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    @Override
    public String toString() {
        return "Product{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", price='" + price + '\'' +
                ", size='" + size + '\'' +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                ", stock=" + stock +
                '}';
    }

    public Product() {
        // Required empty public constructor
    }


}
