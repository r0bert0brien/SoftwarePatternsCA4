package com.example.assignment4.admin;

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
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.assignment4.admin.ProductAdapterAdmin;
import com.example.assignment4.R;
import com.example.assignment4.customer.ProductAdapter;
import com.example.assignment4.entity.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductBrowseAdmin extends Fragment  implements ProductAdapter.CartUpdateListener{

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    String email;
    Boolean isAdmin;
    AutoCompleteTextView searchBar;
    ImageButton searchButton;
    Spinner sortBy;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    ProductAdapterAdmin productAdapterAdmin;
    ArrayAdapter arrayAdapter;
    ArrayList<String> searchField = new ArrayList<>();
    ArrayList<Product> searchResults = new ArrayList<>();

    public ProductBrowseAdmin(){
        // Required empty public constructor
    }

    public ProductBrowseAdmin(String email, Boolean isAdmin){
        this.email = email;
        this.isAdmin = isAdmin;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_browse, container, false);
        populateSuggestions();
        searchBar = view.findViewById(R.id.searchBar);
        searchButton = view.findViewById(R.id.searchButton);
        sortBy = view.findViewById(R.id.sortBy);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productAdapterAdmin = new ProductAdapterAdmin(new ArrayList<>(), requireContext(), email, this);
        recyclerView.setAdapter(productAdapterAdmin);

        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, searchField);
        searchBar.setAdapter(arrayAdapter);

        List<String> paymentMethods = new ArrayList<>();
        paymentMethods.add("Sort By");
        paymentMethods.add("Title - Descending");
        paymentMethods.add("Title - Ascending");
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

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                databaseReference.child("product").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            searchResults.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Product product = dataSnapshot.getValue(Product.class);
                                Log.d(TAG, product.toString());
                                String category = dataSnapshot.child("category").getValue(String.class);
                                String manufacturer = dataSnapshot.child("manufacturer").getValue(String.class);
                                String title = dataSnapshot.child("title").getValue(String.class);
                                Log.d(TAG, category  + " " + manufacturer + " " + title);
                                if (selectedOption.equalsIgnoreCase(product.getCategory()) || selectedOption.equalsIgnoreCase(product.getManufacturer()) ||
                                        selectedOption.equalsIgnoreCase(product.getTitle())) {
                                    searchResults.add(product);
                                    Log.d(TAG, "New Product Added: " + searchResults.toString());
                                }
                            }

                            productAdapterAdmin.setProducts(searchResults);
                            productAdapterAdmin.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        return view;
    }
    private void populateSuggestions(){
        databaseReference.child("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String potentialCategory = dataSnapshot.child("category").getValue(String.class);
                        String potentialManufacturer = dataSnapshot.child("manufacturer").getValue(String.class);
                        String potentialTitle = dataSnapshot.child("title").getValue(String.class);
                        Log.d(TAG, potentialCategory  + " " + potentialManufacturer + " " + potentialTitle);
                        if (searchField != null) {
                            if (!searchField.contains(potentialCategory)) {
                                searchField.add(potentialCategory);
                            }
                            if (!searchField.contains(potentialManufacturer)) {
                                searchField.add(potentialManufacturer);
                            }
                            if (!searchField.contains(potentialTitle)) {
                                searchField.add(potentialTitle);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sortResults(String selectedOption) {
        if (selectedOption.equals("Title - Descending")) {
            Collections.sort(searchResults, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    return p1.getTitle().compareToIgnoreCase(p2.getTitle());
                }
            });
        } else if (selectedOption.equals("Title - Ascending")) {
            Collections.sort(searchResults, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    return p2.getTitle().compareToIgnoreCase(p1.getTitle());
                }
            });
        } else if (selectedOption.equals("Price - Descending")) {
            Collections.sort(searchResults, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    double price1 = Double.parseDouble(p1.getPrice());
                    double price2 = Double.parseDouble(p2.getPrice());
                    return Double.compare(price2, price1);
                }
            });
        } else if (selectedOption.equals("Price - Ascending")) {
            Collections.sort(searchResults, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    double price1 = Double.parseDouble(p1.getPrice());
                    double price2 = Double.parseDouble(p2.getPrice());
                    return Double.compare(price1, price2);
                }
            });
        }
        productAdapterAdmin.notifyDataSetChanged();
    }

    @Override
    public void onCartUpdate() {

    }
}
