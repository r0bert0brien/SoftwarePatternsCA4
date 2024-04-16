package com.example.assignment4.customer;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.assignment4.R;
import com.example.assignment4.adapter.ProductAdapter;
import com.example.assignment4.entity.FirebaseSingleton;
import com.example.assignment4.entity.Order;
import com.example.assignment4.entity.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewCart extends Fragment implements ProductAdapter.CartUpdateListener {

    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    String email;
    ProductAdapter productAdapter;
    RecyclerView recyclerView;
    Button checkoutButton;
    TextView discountPresent;
    Double subtotal;
    ArrayList<Product> cart = new ArrayList<Product>();

    public ViewCart(){
        // Required empty public constructor
    }

    public ViewCart(String email){
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_cart, container, false);
        cart.clear();
        recyclerView = view.findViewById(R.id.recyclerView);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        discountPresent = view.findViewById(R.id.discountPresent);
        productAdapter = new ProductAdapter(new ArrayList<>(), requireContext(), email, false, this);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        populateCart();
        checkoutButton.setOnClickListener(v -> {
            for(Product product : cart){
                databaseReference.child("product").child(product.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int stock = snapshot.child("stock").getValue(Integer.class);
                        stock = stock - product.getStock();
                        databaseReference.child("product").child(product.getKey()).child("stock").setValue(stock);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            databaseReference.child("users").child(email).child("orders").push().setValue(new Order(cart, email, getCurrentDateTime(), String.valueOf(subtotal)));
            databaseReference.child("users").child(email).child("shoppingCart").removeValue();
            cart.clear();
            productAdapter.notifyDataSetChanged();
            checkoutButton.setText("Checkout");
        });

        return view;
    }

    public void populateCart(){
        databaseReference.child("users").child(email).child("shoppingCart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subtotal = 0.0;
                cart.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    if (!product.getTitle().contains("x") || !product.getTitle().matches(".*\\d.*")) {
                        product.setTitle(product.getTitle() + " x " + product.getStock());
                    }
                    subtotal = subtotal + product.getStock()*Double.parseDouble(product.getPrice());
                    Log.d(TAG, String.valueOf(product.getTitle()) + " Calculation: Ordered Amount = " + String.valueOf(product.getStock()) + "* Price €" + String.valueOf(product.getPrice()));
                    cart.add(product);
                }
                Log.d(TAG, cart.toString());
                productAdapter.setProducts(cart);
                productAdapter.notifyDataSetChanged();
                checkoutButton.setText("Checkout Subtotal €"+String.valueOf(subtotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read cart data.", error.toException());
            }
        });

        databaseReference.child("discounts").child("repeatCustomers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, snapshot.toString());
                if (snapshot.exists()) {
                        String count = snapshot.child("count").getValue(String.class);
                        String discount = snapshot.child("discount").getValue(String.class);
                        Log.d(TAG, "Discount: " + discount.toString());
                        databaseReference.child("users").child(email).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getChildrenCount() >= Integer.parseInt(count)){
                                    Double discountAmount = subtotal / 100 * Integer.parseInt(discount);
                                    subtotal = subtotal - discountAmount;
                                    discountPresent.setText("Discount Present! You Saved €" + String.valueOf(discountAmount) + " for being a Repeat Customer");
                                    discountPresent.setVisibility(View.VISIBLE);
                                    checkoutButton.setText("Checkout Subtotal €"+String.valueOf(subtotal));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String formattedDateTime = sdf.format(now);

        return formattedDateTime;
    }

    @Override
    public void onCartUpdate() {
        Log.d(TAG, "PopulateCart being called");
        populateCart();
    }
}
