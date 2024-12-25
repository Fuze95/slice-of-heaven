package com.sliceofheaven;

import com.sliceofheaven.models.Customer;
import java.util.*;

/*
 * Admin class implementing the Singleton pattern to manage customer and delivery information.
 * This ensures only one instance of Admin exists throughout the application lifecycle.
 */
public class Admin {
    private static Admin instance;
    private List<String> towns;
    private Map<String, Customer> customers;
    private Map<String, Double> deliveryFees;
    private final String adminUsername = "admin";
    private final String adminPassword = "admin123";

    private Admin() {
        towns = new ArrayList<>(Arrays.asList("Bandarawela", "Diyatalawa", "Ella"));
        customers = new HashMap<>();

        //Initialize and set default delivery fees for each town
        deliveryFees = new HashMap<>();
        deliveryFees.put("Bandarawela", 150.0);
        deliveryFees.put("Diyatalawa", 200.0);
        deliveryFees.put("Ella", 250.0);
    }

    /*
     * Gets the singleton instance of Admin class.
     * Creates new instance if none exists.
     * return The single instance of Admin
     */
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

    //Return delivery fee for the town, returns 1000 if town not found
    public double getDeliveryFee(String town) {
        return deliveryFees.getOrDefault(town, 1000.0);
    }
}