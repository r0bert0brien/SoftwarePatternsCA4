package com.example.assignment4.entity;

import java.util.ArrayList;

public class Order {

    ArrayList<Product> products;
    String userEmail;
    String date;
    String subtotal;
    public Order(ArrayList<Product> products, String userEmail, String date, String subtotal) {
        this.products = products;
        this.userEmail = userEmail;
        this.date = date;
        this.subtotal = subtotal;
    }
    @Override
    public String toString() {
        return "Order{" +
                "products=" + products +
                ", userEmail='" + userEmail + '\'' +
                ", date='" + date + '\'' +
                ", subtotal='" + subtotal + '\'' +
                '}';
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}
