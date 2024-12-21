package com.sliceofheaven;

import com.sliceofheaven.models.Customer;
import java.util.*;

// Singleton Pattern - Admin
public class Admin {
    private static Admin instance;
    private List<String> towns;
    private Map<String, Customer> customers;

    private Admin() {
        towns = new ArrayList<>(Arrays.asList("Colombo", "Kandy", "Galle"));
        customers = new HashMap<>();
    }

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
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
}