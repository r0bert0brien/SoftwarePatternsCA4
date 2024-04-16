package com.example.assignment4.customer;

import com.example.assignment4.entity.Product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface SortStrategy {
    void sort(List<Product> products);
}

// Concrete strategy classes which carries out sorting functionality as applicable
class TitleDescendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getTitle));
        Collections.reverse(products);
    }
}

class TitleAscendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparing(Product::getTitle));
    }
}

class PriceDescendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getPrice())));
        Collections.reverse(products);
    }
}

class PriceAscendingSortStrategy implements SortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products, Comparator.comparingDouble(p -> Double.parseDouble(p.getPrice())));
    }
}
