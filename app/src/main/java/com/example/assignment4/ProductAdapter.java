package com.example.assignment4;

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
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment4.entity.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    private List<Product> productList;
    private Context context;
    String email;
    public ProductAdapter(List<Product> productList, Context context, String email) {
        this.productList = productList;
        this.context = context;
        this.email = email;
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
            Button addToCart = dialog.findViewById(R.id.addToCart);
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
            if(holder.inCart) {
                addToCart.setText("Remove From Cart");
            }else{
                addToCart.setText("Add To Cart");
            }

            addToCart.setOnClickListener(v1 -> {
                if(!holder.inCart){
                    addToCart.setText("Remove From Cart");
                    databaseReference.child("users").child(email).child("shoppingCart").push().setValue(product);
                    holder.inCart = true;
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
