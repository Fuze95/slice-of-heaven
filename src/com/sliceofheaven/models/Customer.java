package com.sliceofheaven.models;

import java.util.*;
import com.sliceofheaven.observers.OrderObserver;

/*
 * Customer class that implements the Observer pattern to receive order updates.
 * Manages customer information, loyalty points, and saved pizza preferences.
 */
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

    /*
     * Implementation of Observer pattern's update method.
     * Called when order status changes to notify customer via SMS.
     * @param message The notification message to be sent
     */
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

    //Saves a pizza to customer's list of favorite/saved pizzas
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
