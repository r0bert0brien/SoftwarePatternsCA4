package com.example.assignment4.customer;

import com.example.assignment4.entity.Order;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

interface OrderSortingStrategy {
    void sort(List<Order> orders);
}

class DateDescendingSortingStrategy implements OrderSortingStrategy {
    @Override
    public void sort(List<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o1.getDate().compareToIgnoreCase(o2.getDate());
            }
        });
    }
}

class DateAscendingSortingStrategy implements OrderSortingStrategy {
    @Override
    public void sort(List<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getDate().compareToIgnoreCase(o1.getDate());
            }
        });
    }
}

class PriceDescendingSortingStrategy implements OrderSortingStrategy {
    @Override
    public void sort(List<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                double price1 = Double.parseDouble(o1.getSubtotal());
                double price2 = Double.parseDouble(o2.getSubtotal());
                return Double.compare(price2, price1);
            }
        });
    }
}

class PriceAscendingSortingStrategy implements OrderSortingStrategy {
    @Override
    public void sort(List<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                double price1 = Double.parseDouble(o1.getSubtotal());
                double price2 = Double.parseDouble(o2.getSubtotal());
                return Double.compare(price1, price2);
            }
        });
    }
}