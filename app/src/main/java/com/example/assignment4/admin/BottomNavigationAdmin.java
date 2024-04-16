package com.example.assignment4.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.assignment4.R;
import com.example.assignment4.customer.ProductBrowse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BottomNavigationAdmin extends AppCompatActivity {
    private AddStock addStock;
    private ProductBrowse productBrowse;
    private CustomerSearch customerSearch;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav_admin);
        email = getIntent().getStringExtra("EMAIL");
        addStock = new AddStock(email);
        productBrowse = new ProductBrowse(email,true);
        customerSearch = new CustomerSearch(email);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                addStock).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.navigation_add_stock) {
                        selectedFragment = addStock;
                    } if (item.getItemId() == R.id.navigation_product_maintenance) {
                        selectedFragment = productBrowse;
                    } else if (item.getItemId() == R.id.navigation_customer_search) {
                        selectedFragment = customerSearch;
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

