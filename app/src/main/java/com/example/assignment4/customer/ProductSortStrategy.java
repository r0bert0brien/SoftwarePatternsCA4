package com.example.assignment4.customer;

import com.example.assignment4.entity.Product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface ProductSortStrategy {
    void sort(List<Product> products);
}

// Concrete strategy classes which carries out sorting functionality as applicable
class TitleDescendingProductSortStrategy implements ProductSortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getTitle));
        Collections.reverse(products);
    }
}

class TitleAscendingProductSortStrategy implements ProductSortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getTitle));
    }
}

class PriceDescendingProductSortStrategy implements ProductSortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getPrice())));
        Collections.reverse(products);
    }
}

class PriceAscendingProductSortStrategy implements ProductSortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getPrice())));
    }
}
