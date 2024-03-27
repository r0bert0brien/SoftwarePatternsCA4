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

import com.example.assignment4.R;
import com.example.assignment4.entity.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewCart extends Fragment implements ProductAdapter.CartUpdateListener {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    String email;
    ProductAdapter productAdapter;
    RecyclerView recyclerView;
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
        productAdapter = new ProductAdapter(new ArrayList<>(), requireContext(), email, this);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productAdapter.setProducts(cart);

        databaseReference.child("users").child(email).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    cart.add(product);
                }
                Log.d(TAG, cart.toString());
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onCartUpdate() {
        databaseReference.child("users").child(email).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cart.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    cart.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
