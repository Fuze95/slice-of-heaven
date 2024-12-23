package com.sliceofheaven.models;
import java.util.*;

// Builder Pattern - Pizza
public class Pizza {
    private String size;
    private String crustType;
    private List<String> toppings;
    private String sauce;
    private boolean extraCheese;
    private double price;
    private String specialName;

    Pizza(PizzaBuilder builder) {
        this.size = builder.getSize();
        this.crustType = builder.getCrust();
        this.toppings = builder.getToppings();
        this.sauce = builder.getSauce();
        this.extraCheese = builder.hasExtraCheese();
        this.specialName = builder.getSpecialName();
        calculatePrice();
    }

    private void calculatePrice() {
        // Base price based on size
        switch (size.toLowerCase()) {
            case "personal": price = 1000.0; break;
            case "medium": price = 1800.0; break;
            case "large": price = 2400.0; break;
            default: price = 1000.0;
        }
        
        price += toppings.size() * 150.0;
        if (extraCheese) price += 200.0;
    }

    // Getters
    public double getPrice() { return price; }
    public String getSize() { return size; }
    public String getCrust() { return crustType; }
    public String getSauce() { return sauce; }
    public String getSpecialName() { return specialName; }
    public List<String> getToppings() { return new ArrayList<>(toppings); }
    public boolean hasExtraCheese() { return extraCheese; }

    // Setter for special name
    public void setSpecialName(String specialName) {
        this.specialName = specialName;
    }
}