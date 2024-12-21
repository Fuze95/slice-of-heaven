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

    private Pizza(PizzaBuilder builder) {
        this.size = builder.size;
        this.crustType = builder.crustType;
        this.toppings = builder.toppings;
        this.sauce = builder.sauce;
        this.extraCheese = builder.extraCheese;
        this.specialName = builder.specialName;
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

    // Setter for special name
    public void setSpecialName(String specialName) {
        this.specialName = specialName;
    }

    public static class PizzaBuilder {
        private String size;
        private String crustType;
        private List<String> toppings;
        private String sauce;
        private boolean extraCheese;
        private String specialName;

        public PizzaBuilder() {
            this.toppings = new ArrayList<>();
        }

        public PizzaBuilder setSize(String size) {
            this.size = size;
            return this;
        }

        public PizzaBuilder setCrust(String type) {
            this.crustType = type;
            return this;
        }

        public PizzaBuilder addTopping(String topping) {
            this.toppings.add(topping);
            return this;
        }

        public PizzaBuilder setSauce(String sauce) {
            this.sauce = sauce;
            return this;
        }

        public PizzaBuilder setExtraCheese(boolean extra) {
            this.extraCheese = extra;
            return this;
        }

        public PizzaBuilder setSpecialName(String specialName) {
            this.specialName = specialName;
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}