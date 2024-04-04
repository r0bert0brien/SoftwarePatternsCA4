package com.example.assignment4.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment4.R;
import com.example.assignment4.entity.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    private List<Order> orderList;
    private Context context;
    String email;
    public OrdersAdapter(List<Order> productList, Context context, String email) {
        this.orderList = productList;
        this.context = context;
        this.email = email;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.date.setText(order.getDate());
        holder.itemCount.setText(order.getProducts().size()+ " item(s)");
        holder.subtotal.setText("€" + order.getSubtotal());

        holder.imageView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_view_order);
            TextView orderDate = dialog.findViewById(R.id.orderDate);
            TextView orderSubtotal = dialog.findViewById(R.id.orderSubtotal);

            ProductAdapter productAdapter = new ProductAdapter(order.getProducts(), context, order.getUserEmail(), null);
            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(productAdapter);
            productAdapter.setProducts(order.getProducts());
            productAdapter.notifyDataSetChanged();

            orderDate.setText("Subtotal: " + holder.date.getText());
            orderSubtotal.setText(holder.subtotal.getText());

            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders;
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView date, itemCount, subtotal;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            date = itemView.findViewById(R.id.date);
            itemCount = itemView.findViewById(R.id.item_count);
            subtotal = itemView.findViewById(R.id.price);
        }
    }
}
