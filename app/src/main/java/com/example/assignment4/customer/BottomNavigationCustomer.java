package com.example.assignment4.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.assignment4.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationCustomer extends AppCompatActivity {

    private ProductBrowse productBrowse;
    private ViewCart viewCart;
    private MyOrders myOrders;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav_customer);
        email = getIntent().getStringExtra("EMAIL");
        productBrowse = new ProductBrowse(email, false);
        viewCart = new ViewCart(email);
        myOrders = new MyOrders(email);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                productBrowse).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.product_browse) {
                        selectedFragment = productBrowse;
                    } else if (item.getItemId() == R.id.view_cart) {
                        selectedFragment = viewCart;
                    } else if (item.getItemId() == R.id.my_orders) {
                        selectedFragment = myOrders;
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        return true;
                    } else {
                        return false;
                    }
                }
            };
}

