package com.example.assignment4.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment4.R;
import com.example.assignment4.entity.FirebaseSingleton;
import com.example.assignment4.entity.Order;
import com.example.assignment4.entity.Review;
import com.example.assignment4.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ReviewViewHolder> {
    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    private List<User> customerList;
    private Context context;
    public CustomerAdapter(List<User> customerList, Context context) {
        this.customerList = customerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        User user = customerList.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail().replace("-", "."));
        holder.address.setText(user.getAddress().replace("/", " "));

        holder.name.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_customer_orders);

            TextView name = dialog.findViewById(R.id.text_name);
            name.setText("Name: " + user.getName());
            TextView address = dialog.findViewById(R.id.text_address);
            address.setText("Address: " + user.getAddress().replace("/", " "));
            TextView email = dialog.findViewById(R.id.text_email);
            email.setText("Email: " + user.getEmail().replace("-", "."));

            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            ArrayList<Order> ordersList = new ArrayList<>();
            OrdersAdapter ordersAdapter = new OrdersAdapter(ordersList, context, user.getEmail(), true);
            recyclerView.setAdapter(ordersAdapter);

            databaseReference.child("users").child(user.getEmail()).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, snapshot.toString());
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Order order = dataSnapshot.getValue(Order.class);
                        ordersList.add(order);
                    }
                    ordersAdapter.setOrders(ordersList);
                    ordersAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void setUsers(List<User> reviews) {
        this.customerList = reviews;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView name, email, address;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            email = itemView.findViewById(R.id.text_email);
            address = itemView.findViewById(R.id.text_address);
        }
    }
}
