package com.sliceofheaven;

import com.sliceofheaven.models.Customer;
import java.util.*;

// Singleton Pattern - Admin
public class Admin {
    private static Admin instance;
    private List<String> towns;
    private Map<String, Customer> customers;
    private Map<String, Double> deliveryFees;
    private final String adminUsername = "admin";
    private final String adminPassword = "admin123";

    private Admin() {
        towns = new ArrayList<>(Arrays.asList("Bandarawela", "Badulla", "Diyatalawa"));
        customers = new HashMap<>();

        deliveryFees = new HashMap<>();
        deliveryFees.put("Bandarawela", 200.0);
        deliveryFees.put("Badulla", 450.0);
        deliveryFees.put("Diyatalawa", 250.0);
    }

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        return adminUsername.equals(username) && adminPassword.equals(password);
    }

    public void createCustomer(String name, String email, String mobile, String address) {
        Customer customer = new Customer(name, email, mobile, address);
        customers.put(mobile, customer);
    }

    public Customer getCustomer(String mobile) {
        return customers.get(mobile);
    }

    public List<String> getTowns() {
        return towns;
    }

    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public double getDeliveryFee(String town) {
        return deliveryFees.getOrDefault(town, 1000.0);
    }
}