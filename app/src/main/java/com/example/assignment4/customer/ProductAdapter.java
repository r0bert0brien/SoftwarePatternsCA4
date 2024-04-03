package com.example.assignment4.customer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment4.Login;
import com.example.assignment4.R;
import com.example.assignment4.entity.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public interface CartUpdateListener {
        void onCartUpdate();
    }
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    private List<Product> productList;
    private Context context;
    String email;
    private CartUpdateListener cartUpdateListener;

    public ProductAdapter(List<Product> productList, Context context, String email, CartUpdateListener cartUpdateListener) {
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

        databaseReference.child("users").child(email).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        String productKey = productSnapshot.child("key").getValue(String.class);
                        if (product.getKey().equalsIgnoreCase(productKey)) {
                            holder.inCart = true;
                            break;
                        }else{
                            holder.inCart = false;
                        }
                    }
                    Log.d(TAG, "InCart Status: " + holder.inCart.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.imageView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_view_product);

            ImageView imageView = dialog.findViewById(R.id.productImage);
            TextView title = dialog.findViewById(R.id.productTitle);
            TextView manufacturer = dialog.findViewById(R.id.productManufacturer);
            TextView price = dialog.findViewById(R.id.productPrice);
            Button increment = dialog.findViewById(R.id.incrementButton);
            Button decrement = dialog.findViewById(R.id.decrementButton);
            TextView quantity = dialog.findViewById(R.id.quantityTextView);
            Button addToCart = dialog.findViewById(R.id.addToCart);
            RatingBar rating = dialog.findViewById(R.id.productRating);

            increment.setOnClickListener(v1 -> {
                int quantityAmount = Integer.parseInt(String.valueOf(quantity.getText()));
                quantityAmount++;
                if (quantityAmount <= product.getStock()){
                    quantity.setText(String.valueOf(quantityAmount));
                }
            });

            decrement.setOnClickListener(v1 -> {
                int quantityAmount = Integer.parseInt(String.valueOf(quantity.getText()));
                quantityAmount--;
                if(quantityAmount >= 1) {
                    quantity.setText(String.valueOf(quantityAmount));
                }else{
                    quantityAmount = 1;
                }
            });

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
            if(holder.inCart) {
                addToCart.setText("Remove From Cart");
                increment.setVisibility(View.GONE);
                decrement.setVisibility(View.GONE);
                quantity.setVisibility(View.GONE);
            }else{
                addToCart.setText("Add To Cart");
                increment.setVisibility(View.VISIBLE);
                decrement.setVisibility(View.VISIBLE);
                quantity.setVisibility(View.VISIBLE);
            }

            addToCart.setOnClickListener(v1 -> {
                if(!holder.inCart){
                    if (Integer.parseInt(String.valueOf(quantity.getText())) > product.getStock()){
                        Toast.makeText(context, "Error, maximum quantity exceeded - max quantity is: " + String.valueOf(product.getStock()), Toast.LENGTH_SHORT).show();
                    }else{
                    addToCart.setText("Remove From Cart");
                    product.setStock(Integer.parseInt(String.valueOf(quantity.getText())));
                    databaseReference.child("users").child(email).child("shoppingCart").push().setValue(product);
                    holder.inCart = true;
                    increment.setVisibility(View.GONE);
                    decrement.setVisibility(View.GONE);
                    quantity.setVisibility(View.GONE);
                }
                }else {
                    addToCart.setText("Add To Cart");
                    databaseReference.child("users").child(email).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Product cartProduct = dataSnapshot.getValue(Product.class);
                                if (cartProduct.getKey().equals(product.getKey())) {
                                    dataSnapshot.getRef().removeValue();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    holder.inCart = false;
                }
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

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, manufacturer, price;
        Boolean inCart = false;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            manufacturer = itemView.findViewById(R.id.text_manufacturer);
            price = itemView.findViewById(R.id.text_price);
        }
    }
}
