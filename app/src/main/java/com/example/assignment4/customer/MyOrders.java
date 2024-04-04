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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.assignment4.R;
import com.example.assignment4.adapter.OrdersAdapter;
import com.example.assignment4.entity.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyOrders extends Fragment {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    String email;
    Spinner sortBy;
    RecyclerView recyclerView;
    OrdersAdapter ordersAdapter;
    ArrayList<Order> orders = new ArrayList<>();

    public MyOrders(){
        // Required empty public constructor
    }

    public MyOrders(String email){
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        orders.clear();
        sortBy = view.findViewById(R.id.sortBy);
        recyclerView = view.findViewById(R.id.recyclerView);
        ordersAdapter = new OrdersAdapter(new ArrayList<>(), requireContext(), email);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(ordersAdapter);

        populateRecyclerView();
        List<String> paymentMethods = new ArrayList<>();
        paymentMethods.add("Sort By");
        paymentMethods.add("Date - Descending");
        paymentMethods.add("Date - Ascending");
        paymentMethods.add("Price - Descending");
        paymentMethods.add("Price - Ascending");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBy.setAdapter(adapter);

        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                sortResults(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public void populateRecyclerView(){
        databaseReference.child("users").child(email).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, snapshot.toString());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Order order = dataSnapshot.getValue(Order.class);
                    orders.add(order);
                }
                Log.d(TAG, orders.toString());
                ordersAdapter.setOrders(orders);
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sortResults(String selectedOption) {
        if (selectedOption.equals("Date - Descending")) {
            Collections.sort(orders, new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    return o1.getDate().compareToIgnoreCase(o2.getDate());
                }
            });
        } else if (selectedOption.equals("Date - Ascending")) {
            Collections.sort(orders, new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    return o2.getDate().compareToIgnoreCase(o1.getDate());
                }
            });
        } else if (selectedOption.equals("Price - Descending")) {
            Collections.sort(orders, new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    double price1 = Double.parseDouble(o1.getSubtotal());
                    double price2 = Double.parseDouble(o2.getSubtotal());
                    return Double.compare(price2, price1);
                }
            });
        } else if (selectedOption.equals("Price - Ascending")) {
            Collections.sort(orders, new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    double price1 = Double.parseDouble(o1.getSubtotal());
                    double price2 = Double.parseDouble(o2.getSubtotal());
                    return Double.compare(price1, price2);
                }
            });
        }
        ordersAdapter.notifyDataSetChanged();
    }
}
