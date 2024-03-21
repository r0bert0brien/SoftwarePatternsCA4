package com.example.assignment4;

public class User {
    String name, address, paymentMethod, email;
    Boolean isAdmin;
    public User(String name, String email, String address, String paymentMethod, Boolean isAdmin) {
        this.name = name;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public Boolean getAdmin() {
        return isAdmin;
    }
    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
