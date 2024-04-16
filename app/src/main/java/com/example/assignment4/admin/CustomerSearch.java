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

import com.example.assignment4.adapter.CustomerAdapter;
import com.example.assignment4.R;
import com.example.assignment4.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerSearch extends Fragment {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    String email;
    Boolean isAdmin;
    AutoCompleteTextView searchBar;
    ImageButton searchButton;
    RecyclerView recyclerView;
    ArrayAdapter arrayAdapter;
    CustomerAdapter customerAdapter;
    ArrayList<String> searchField = new ArrayList<>();
    ArrayList<User> searchResults = new ArrayList<>();

    public CustomerSearch(){
        // Required empty public constructor
    }

    public CustomerSearch(String email){
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_search, container, false);
        populateSuggestions();
        customerAdapter = new CustomerAdapter(searchResults, requireContext());
        searchBar = view.findViewById(R.id.searchBar);
        searchButton = view.findViewById(R.id.searchButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(customerAdapter);

        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, searchField);
        searchBar.setAdapter(arrayAdapter);

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            searchResults.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);
                                Log.d(TAG, user.toString());
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);
                                Log.d(TAG, name  + " " + email);
                                if (selectedOption.equalsIgnoreCase(user.getName()) || selectedOption.replace(".", "-").equalsIgnoreCase(user.getEmail()) && !user.getAdmin()) {
                                    searchResults.add(user);
                                    Log.d(TAG, "New Customer Added: " + searchResults.toString());
                                }
                            }
                            customerAdapter.setUsers(searchResults);
                            customerAdapter.notifyDataSetChanged();
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
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String potentialEmail = dataSnapshot.getKey();
                        String potentialName = dataSnapshot.child("name").getValue(String.class);
                        Log.d(TAG, potentialName +  " " + potentialEmail);
                        Boolean isAdmin = dataSnapshot.child("admin").getValue(Boolean.class);
                        if (isAdmin){
                            break;
                        }else if (searchField != null) {
                            if (!searchField.contains(potentialName)) {
                                searchField.add(potentialName);
                            }
                            if (!searchField.contains(potentialEmail)) {
                                searchField.add(potentialEmail.replace("-", "."));
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
}
