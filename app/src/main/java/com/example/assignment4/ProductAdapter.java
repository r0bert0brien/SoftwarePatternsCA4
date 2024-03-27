package com.example.assignment4;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment4.entity.Product;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
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

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            manufacturer = itemView.findViewById(R.id.text_manufacturer);
            price = itemView.findViewById(R.id.text_price);
        }
    }
}
