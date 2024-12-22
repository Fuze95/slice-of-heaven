package com.sliceofheaven.models;

import java.util.*;

// Customer class implementing Observer
public class Customer implements OrderObserver {
    private String name;
    private String email;
    private String mobileNumber;
    private String address;
    private List<Pizza> savedPizzas;
    private int loyaltyPoints;

    public Customer(String name, String email, String mobile, String address) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobile;
        this.address = address;
        this.savedPizzas = new ArrayList<>();
        this.loyaltyPoints = 0;
    }

    @Override
    public void update(String message) {
        System.out.println("SMS sent to " + mobileNumber + ": " + message);
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    public String getName() { return name; }
    public int getLoyaltyPoints() { return loyaltyPoints; }

    public String getEmail() { 
        return email; 
    }

    public void savePizza(Pizza pizza) {
        if (pizza != null) {
            savedPizzas.add(pizza);
        }
    }

    public List<Pizza> getSavedPizzas() {
        return savedPizzas;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
