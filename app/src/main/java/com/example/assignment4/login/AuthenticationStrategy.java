package com.example.assignment4.login;

public interface AuthenticationStrategy {
    void authenticate(String email, String password);
}

