package com.example.assignment4.admin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment4.R;
import com.example.assignment4.customer.ProductAdapter;
import com.example.assignment4.entity.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapterAdmin extends RecyclerView.Adapter<ProductAdapterAdmin.ProductViewHolder> implements ProductAdapter.CartUpdateListener {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    private List<Product> productList;
    private Context context;
    String email;
    private ProductAdapter.CartUpdateListener cartUpdateListener;

    public ProductAdapterAdmin(List<Product> productList, Context context, String email, ProductAdapter.CartUpdateListener cartUpdateListener) {
        this.productList = productList;
        this.context = context;
        this.email = email;
        this.cartUpdateListener = cartUpdateListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.manufacturer.setText(product.getManufacturer());
        holder.price.setText("€" + product.getPrice());
        Picasso.get().load(product.getImage()).into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Log.d(TAG, "Clicked");
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_view_product_admin);

            ImageView imageView = dialog.findViewById(R.id.productImage);
            TextView title = dialog.findViewById(R.id.productTitle);
            TextView manufacturer = dialog.findViewById(R.id.productManufacturer);
            EditText price = dialog.findViewById(R.id.productPrice);
            EditText stock = dialog.findViewById(R.id.productStock);
            Button save = dialog.findViewById(R.id.saveButton);
            RatingBar rating = dialog.findViewById(R.id.productRating);

            //For Rating Functionality
            /*databaseReference.child("product").child(product.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                rating.setRating(4.5f);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/

            Picasso.get().load(product.getImage()).into(imageView);
            title.setText(product.getTitle());
            manufacturer.setText(product.getManufacturer());
            price.setText("€" + product.getPrice());
            stock.setText(String.valueOf(product.getStock()));

            save.setOnClickListener(v1 -> {
                product.setPrice(price.getText().toString().replace("€", "").trim());
                product.setStock(Integer.parseInt(String.valueOf(stock.getText())));
                databaseReference.child("product").child(product.getKey()).setValue(product);
                notifyItemChanged(position);
            });
            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
    }

    @Override
    public void onCartUpdate() {

    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, manufacturer, price;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            manufacturer = itemView.findViewById(R.id.text_manufacturer);
            price = itemView.findViewById(R.id.text_price);
        }
    }
}
